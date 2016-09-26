/*
 * Copyright 2016 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.plugin.dwolla.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.swagger.client.model.HalLink;
import io.swagger.client.model.Webhook;
import org.joda.time.DateTime;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.account.api.AccountApiException;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.payment.api.*;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.plugin.dwolla.api.DwollaCallContext;
import org.killbill.billing.plugin.dwolla.client.DwollaClient;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;
import org.killbill.billing.plugin.dwolla.util.JsonHelper;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

import static org.killbill.billing.plugin.dwolla.client.EventTopic.valueOf;

public class DwollaNotificationHandler {

    private static final Logger logger = LoggerFactory.getLogger(DwollaNotificationHandler.class);

    private final DwollaDao dao;
    private final DwollaClient client;
    private final OSGIKillbillAPI osgiKillbillAPI;
    private final Clock clock;

    public DwollaNotificationHandler(final DwollaDao dao, final DwollaClient client, final OSGIKillbillAPI osgiKillbillAPI, final Clock clock) {
        this.dao = dao;
        this.client = client;
        this.osgiKillbillAPI = osgiKillbillAPI;
        this.clock = clock;
    }

    public void processNotification(Webhook webhook, final UUID tenantId) throws PaymentPluginApiException {
        try {
            dao.addNotification(webhook, clock.getUTCNow(), tenantId);
        } catch (Exception e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                logger.debug("Dwolla notification was already processed");
                return;
            } else {
                logger.error("Error saving webhook in database", e);
                throw new PaymentPluginApiException("Error saving webhook in database", e);
            }
        }

        PaymentPluginStatus status = getTransferStatusUpdated(webhook.getTopic());
        final DwollaResponsesRecord dwollaResponse;
        if (status !=  null) {
            final HalLink resource = webhook.getLinks().get("resource");
            final String transferId = JsonHelper.getIdFromUrl(resource.getHref());
            try {
                dwollaResponse = dao.getResponseByTransferId(transferId, tenantId);

                updateKillbill(UUID.fromString(dwollaResponse.getKbAccountId()),
                        UUID.fromString(dwollaResponse.getKbPaymentId()),
                        UUID.fromString(dwollaResponse.getKbPaymentTransactionId()),
                        status, TransactionType.PURCHASE,
                        clock.getUTCNow(), tenantId);

                // Update the plugin tables
                dao.updateResponseStatus(webhook.getTopic(), transferId, tenantId);


            } catch (SQLException e) {
                logger.error("Error processing existing transfer response", e);
                throw new PaymentPluginApiException("Error saving webhook in database", e);
            }

        }

    }

    private PaymentPluginStatus getTransferStatusUpdated(final String topic) {
        switch (valueOf(topic.toUpperCase())) {
            case ACCOUNT_TRANSFER_COMPLETED:
            case CUSTOMER_TRANSFER_COMPLETED:
                return PaymentPluginStatus.PROCESSED;
            case ACCOUNT_TRANSFER_FAILED:
            case CUSTOMER_TRANSFER_FAILED:
                return PaymentPluginStatus.ERROR;
            case ACCOUNT_TRANSFER_CANCELLED:
            case CUSTOMER_TRANSFER_CANCELLED:
                return PaymentPluginStatus.CANCELED;
        }
        return null;
    }

    private Payment updateKillbill(@Nullable final UUID kbAccountId,
                                   @Nullable final UUID kbPaymentId,
                                   @Nullable final UUID kbPaymentTransactionId,
                                   final PaymentPluginStatus paymentPluginStatus,
                                   @Nullable final TransactionType expectedTransactionType,
                                   final DateTime utcNow,
                                   @Nullable final UUID kbTenantId) {
        if (kbPaymentId != null) {
            Preconditions.checkNotNull(kbTenantId, String.format("kbTenantId null for kbPaymentId='%s'", kbPaymentId));
            final CallContext context = new DwollaCallContext(utcNow, kbTenantId);

            final Payment payment = getPayment(kbPaymentId, context);

            Preconditions.checkArgument(payment.getAccountId().equals(kbAccountId), String.format("kbAccountId='%s' doesn't match payment#accountId='%s'", kbAccountId, payment.getAccountId()));
            final Account account = getAccount(kbAccountId, context);

            PaymentTransaction paymentTransaction = filterForTransaction(payment, kbPaymentTransactionId);
            Preconditions.checkNotNull(paymentTransaction, String.format("kbPaymentTransactionId='%s' not found for kbPaymentId='%s'", kbPaymentTransactionId, kbPaymentId));
            Preconditions.checkArgument(expectedTransactionType == null || paymentTransaction.getTransactionType() == expectedTransactionType, String.format("transactionType='%s' doesn't match expectedTransactionType='%s'", paymentTransaction.getTransactionType(), expectedTransactionType));

            // Update Kill Bill
            if (PaymentPluginStatus.UNDEFINED.equals(paymentPluginStatus)) {
                // We cannot do anything
                return payment;
            } else if (paymentTransaction != null && TransactionStatus.PENDING.equals(paymentTransaction.getTransactionStatus())) {
                return transitionPendingTransaction(account, kbPaymentTransactionId, paymentPluginStatus, context);
            } else {
                // Payment in Kill Bill has the latest state, nothing to do (we simply updated our plugin tables in case Adyen had extra information for us)
                return payment;
            }
        } else {
            // API payment unknown to Kill Bill, does it belong to a different system?
            // Note that we could decide to record a new payment here, this would be useful to migrate data for instance
            return null;
        }
    }

    private Account getAccount(final UUID kbAccountId, final TenantContext context) {
        try {
            return osgiKillbillAPI.getAccountUserApi().getAccountById(kbAccountId, context);
        } catch (final AccountApiException e) {
            // Have Adyen retry
            throw new RuntimeException(String.format("Failed to retrieve kbAccountId='%s'", kbAccountId), e);
        }
    }

    private Payment getPayment(final UUID kbPaymentId, final TenantContext context) {
        try {
            return osgiKillbillAPI.getPaymentApi().getPayment(kbPaymentId, true, false, ImmutableList.<PluginProperty>of(), context);
        } catch (final PaymentApiException e) {
            // Have Adyen retry
            throw new RuntimeException(String.format("Failed to retrieve kbPaymentId='%s'", kbPaymentId), e);
        }
    }

    private PaymentTransaction filterForTransaction(final Payment payment, final UUID kbTransactionId) {
        for (final PaymentTransaction paymentTransaction : payment.getTransactions()) {
            if (paymentTransaction.getId().equals(kbTransactionId)) {
                return paymentTransaction;
            }
        }
        return null;
    }

    private Payment transitionPendingTransaction(final Account account, final UUID kbPaymentTransactionId, final PaymentPluginStatus paymentPluginStatus, final CallContext context) {
        try {
            return osgiKillbillAPI.getPaymentApi().notifyPendingTransactionOfStateChanged(account, kbPaymentTransactionId, paymentPluginStatus == PaymentPluginStatus.PROCESSED, context);
        } catch (final PaymentApiException e) {
            // Have Adyen retry
            throw new RuntimeException(String.format("Failed to transition pending transaction kbPaymentTransactionId='%s'", kbPaymentTransactionId), e);
        }
    }
}
