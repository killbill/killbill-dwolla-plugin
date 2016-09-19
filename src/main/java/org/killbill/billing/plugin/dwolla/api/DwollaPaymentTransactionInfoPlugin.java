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

package org.killbill.billing.plugin.dwolla.api;

import com.google.common.base.Strings;
import io.swagger.client.model.Transfer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.plugin.api.payment.PluginPaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.killbill.billing.plugin.dwolla.client.TransferStatus.valueOf;

public class DwollaPaymentTransactionInfoPlugin extends PluginPaymentTransactionInfoPlugin {

    public DwollaPaymentTransactionInfoPlugin(final Transfer transfer,
                                              final UUID kbPaymentId, final UUID kbTransactionPaymentPaymentId,
                                              final TransactionType transactionType,
                                              final String firstPaymentReferenceId, final String secondPaymentReferenceId,
                                              final List<PluginProperty> properties) {
        super(kbPaymentId,
                kbTransactionPaymentPaymentId,
                transactionType,
                new BigDecimal(transfer.getAmount().getValue()),
                Currency.valueOf(transfer.getAmount().getCurrency().toUpperCase()),
                getPaymentPluginStatus(transfer.getStatus()),
                null,
                null,
                firstPaymentReferenceId,
                secondPaymentReferenceId,
                new DateTime(transfer.getCreated()),
                new DateTime(transfer.getCreated()),
                properties);
    }

    public DwollaPaymentTransactionInfoPlugin(final UUID kbPaymentId, final UUID kbTransactionPaymentPaymentId,
                                              final TransactionType transactionType,
                                              final BigDecimal amount, final String currency,
                                              final String gatewayError, final String gatewayErrorCode,
                                              final List<PluginProperty> properties) {
        super(kbPaymentId,
                kbTransactionPaymentPaymentId,
                transactionType,
                amount,
                Currency.valueOf(currency),
                PaymentPluginStatus.ERROR,
                gatewayError,
                gatewayErrorCode,
                null,
                null,
                DateTime.now(),
                DateTime.now(),
                properties);
    }

    public DwollaPaymentTransactionInfoPlugin(final UUID kbPaymentId, final UUID kbTransactionPaymentPaymentId,
                                              final TransactionType transactionType,
                                              final BigDecimal amount, final String currency,
                                              final List<PluginProperty> properties) {
        super(kbPaymentId,
                kbTransactionPaymentPaymentId,
                transactionType,
                amount,
                Currency.valueOf(currency),
                PaymentPluginStatus.CANCELED,
                "Not supported",
                null,
                null,
                null,
                DateTime.now(),
                DateTime.now(),
                properties);
    }

    public DwollaPaymentTransactionInfoPlugin(final DwollaResponsesRecord record) {
        super(UUID.fromString(record.getKbPaymentId()),
                UUID.fromString(record.getKbPaymentTransactionId()),
                TransactionType.valueOf(record.getTransactionType()),
                record.getAmount(),
                Strings.isNullOrEmpty(record.getCurrency()) ? null : Currency.valueOf(record.getCurrency()),
                getPaymentPluginStatus(record),
                record.getErrorCodes(),
                getGatewayError(record),
                record.getTransferId(),
                null,
                new DateTime(record.getCreatedDate(), DateTimeZone.UTC),
                new DateTime(record.getCreatedDate(), DateTimeZone.UTC),
                DwollaModelPluginBase.buildPluginProperties(record.getAdditionalData()));
    }

    private static PaymentPluginStatus getPaymentPluginStatus(final String transferStatus) {
        switch (valueOf(transferStatus.toUpperCase())) {
            case PENDING:
                return PaymentPluginStatus.PENDING;
            case PROCESSED:
                return PaymentPluginStatus.PROCESSED;
            case CANCELLED:
                return PaymentPluginStatus.CANCELED;
            case FAILED:
                return PaymentPluginStatus.ERROR;
            case RECLAIMED:
            default:
                return PaymentPluginStatus.UNDEFINED;
        }
    }

    private static PaymentPluginStatus getPaymentPluginStatus(final DwollaResponsesRecord record) {
        if (Strings.isNullOrEmpty(record.getTransferStatus())) {
            return PaymentPluginStatus.UNDEFINED;
        } else {
            return getPaymentPluginStatus(record.getTransferStatus());
        }
    }

    private static String truncate(@Nullable final String string) {
//        if (string == null) {
//            return null;
//        } else if (string.length() <= ERROR_CODE_MAX_LENGTH) {
//            return string;
//        } else {
//            return string.substring(0, ERROR_CODE_MAX_LENGTH);
//        }
        return string;
    }

    private static String getGatewayError(final DwollaResponsesRecord record) {
        return ""; // TODO
    }

}
