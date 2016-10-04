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

import com.dwolla.java.sdk.responses.TokenResponse;
import com.google.common.collect.Lists;
import io.swagger.client.ApiException;
import io.swagger.client.api.FundingsourcesApi;
import io.swagger.client.api.TransfersApi;
import io.swagger.client.model.*;
import org.joda.time.DateTime;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.*;
import org.killbill.billing.plugin.api.payment.PluginPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.client.DwollaClient;
import org.killbill.billing.plugin.dwolla.client.TransferStatus;
import org.killbill.billing.plugin.dwolla.core.DwollaNotificationHandler;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaTokensRecord;
import org.killbill.billing.plugin.dwolla.util.JsonHelper;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DwollaPaymentPluginApi extends PluginPaymentPluginApi<DwollaResponsesRecord, DwollaResponses, DwollaPaymentMethodsRecord, DwollaPaymentMethods> {

    private static final Logger logger = LoggerFactory.getLogger(DwollaPaymentPluginApi.class);

    public static final String EXPIRED_ACCESS_TOKEN_ERROR_CODE = "ExpiredAccessToken";
    public static final String INVALID_ACCESS_TOKEN_ERROR_CODE = "InvalidAccessToken";

    // properties
    public static final String PROPERTY_CUSTOMER_ID = "customerId";
    public static final String PROPERTY_FUNDING_SOURCE_ID = "fundingSource";
    public static final String SELF = "self";
    public static final String SOURCE = "source";
    public static final String RESOURCE = "resource";
    public static final String DESTINATION = "destination";

    private final DwollaDao dao;
    private final DwollaClient client;
    private final DwollaNotificationHandler notificationHandler;

    public DwollaPaymentPluginApi(final OSGIKillbillAPI killbillApi,
                                  final OSGIConfigPropertiesService osgiConfigPropertiesService,
                                  final OSGIKillbillLogService logService,
                                  final Clock clock,
                                  final DwollaDao dao,
                                  final DwollaClient client,
                                  final DwollaNotificationHandler notificationHandler) {
        super(killbillApi, osgiConfigPropertiesService, logService, clock, dao);
        this.dao = dao;
        this.client = client;
        this.notificationHandler = notificationHandler;
    }

    @Override
    protected PaymentTransactionInfoPlugin buildPaymentTransactionInfoPlugin(DwollaResponsesRecord record) {
        return new DwollaPaymentTransactionInfoPlugin(record);
    }

    @Override
    protected PaymentMethodPlugin buildPaymentMethodPlugin(DwollaPaymentMethodsRecord record) {
        return new DwollaPaymentMethodPlugin(record);
    }

    @Override
    protected PaymentMethodInfoPlugin buildPaymentMethodInfoPlugin(DwollaPaymentMethodsRecord record) {
        return new DwollaPaymentMethodInfoPlugin(record);
    }

    @Override
    protected String getPaymentMethodId(DwollaPaymentMethodsRecord input) {
        return input.getKbPaymentMethodId();
    }

    @Override
    public PaymentTransactionInfoPlugin authorizePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return new DwollaPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, TransactionType.AUTHORIZE, amount, currency.toString(), Lists.newArrayList(properties));
    }

    @Override
    public PaymentTransactionInfoPlugin capturePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return new DwollaPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, TransactionType.CAPTURE, amount, currency.toString(), Lists.newArrayList(properties));
    }

    @Override
    public PaymentTransactionInfoPlugin purchasePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        checkValidAccessToken(context);
        DwollaPaymentMethodsRecord paymentMethod = null;
        try {
            paymentMethod = dao.getPaymentMethod(kbPaymentMethodId, context.getTenantId());
        } catch (SQLException e) {
            throw new PaymentPluginApiException("There was an error trying to load Dwolla payment method for KillBill payment method " + kbPaymentMethodId, e);
        }

        if (paymentMethod == null) {
            throw new PaymentPluginApiException(null, "No Dwolla payment method was found for killbill payment method " + kbPaymentMethodId);
        }

        TransferRequestBody body = new TransferRequestBody();
        Amount amountBody = new Amount();
        amountBody.setCurrency(currency.toString());
        amountBody.setValue(amount.toString());
        body.setAmount(amountBody);

        Transfer transfer = null;

        try {
            FundingsourcesApi fundingsourcesApi = new FundingsourcesApi(client.getClient());
            final FundingSource fundingSource = fundingsourcesApi.id(paymentMethod.getFundingSource());
            final HalLink source = fundingSource.getLinks().get(SELF);

            Map<String, HalLink> links = new HashMap<String, HalLink>();
            links.put(SOURCE, source);
            links.put(DESTINATION, getDwollaAccount());
            body.setLinks(links);

            TransfersApi transfersApi = new TransfersApi(client.getClient());
            final Unit$ transferResponse = transfersApi.create(body);
            final String transferHref = transferResponse.getLocationHeader();


            transfer = transfersApi.byId(transferHref);
            final TransferFailure transferFailure;
            if (TransferStatus.FAILED.toString().equals(transfer.getStatus().toUpperCase())) {
                transferFailure = transfersApi.failureById(transferHref);
            } else {
                transferFailure = null;
            }

            dao.addResponse(kbAccountId, kbPaymentId, kbTransactionId, TransactionType.PURCHASE, amount, currency,
                    transfer, transferFailure, properties, DateTime.now(), context.getTenantId());

            return new DwollaPaymentTransactionInfoPlugin(
                    transfer,
                    kbPaymentId,
                    kbTransactionId,
                    TransactionType.PURCHASE,
                    transfer.getId(), // firstPaymentReferenceId
                    "", // secondPaymentReferenceId
                    Lists.newArrayList(properties)
            );

        } catch (ApiException e) {
            // TODO parse error code
            final String errorCode = "";
            final String errorMsg = e.getMessage();

            try {
                logService.log(LogService.LOG_WARNING, e.getMessage());
                dao.addErrorResponse(kbAccountId, kbPaymentId, kbTransactionId, TransactionType.PURCHASE, amount, currency, errorCode, errorMsg, DateTime.now(), context.getTenantId());
            } catch (SQLException sqle) {
                logService.log(LogService.LOG_ERROR, sqle.getMessage());
            }
            return new DwollaPaymentTransactionInfoPlugin(
                    kbPaymentId,
                    kbTransactionId,
                    TransactionType.PURCHASE,
                    amount,
                    currency.toString(),
                    errorMsg, // gatewayError
                    errorCode, // gatewayErrorCode
                    Lists.newArrayList(properties)
            );
        } catch (final SQLException e) {
            logService.log(LogService.LOG_ERROR, e.getMessage());
            throw new PaymentPluginApiException("Payment went through, but we encountered a database error. Payment details: " + (transfer.toString()), e);
        }
    }

    @Override
    public PaymentTransactionInfoPlugin voidPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        // TODO Should implement cancel operation?
        // Please see: https://docsv2.dwolla.com/#cancel-a-transfer
        return new DwollaPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, TransactionType.VOID, null, null, Lists.newArrayList(properties));
    }

    @Override
    public PaymentTransactionInfoPlugin creditPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return new DwollaPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, TransactionType.CREDIT, amount, currency.toString(), Lists.newArrayList(properties));
    }

    @Override
    public PaymentTransactionInfoPlugin refundPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        // TODO send money to customer
        // Please see: https://developers.dwolla.com/guides/send-money/
        return new DwollaPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, TransactionType.REFUND, amount, currency.toString(), Lists.newArrayList(properties));
    }

    @Override
    public HostedPaymentPageFormDescriptor buildFormDescriptor(UUID kbAccountId, Iterable<PluginProperty> customFields, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public GatewayNotification processNotification(String notification, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        notificationHandler.processNotification(notification, context.getTenantId());
        return new DwollaGatewayNotification(notification);
    }

    public HalLink getDwollaAccount() throws PaymentPluginApiException {
        try {
            final CatalogResponse root = client.getRootInfo();
            return root.getLinks().get("account");
        } catch (ApiException e) {
            throw new PaymentPluginApiException("There was an error loading merchant account info.", e);
        }
    }

    private void checkValidAccessToken(final CallContext context) throws PaymentPluginApiException {
        try {
            final DwollaTokensRecord tokens = dao.getTokens(context.getTenantId());
            if (tokens == null) {
                throw new PaymentPluginApiException(null, "Dwolla tokens not found for tenant " + context.getTenantId());
            }
            client.getClient().setAccessToken(tokens.getAccessToken());

            // check if token is expired with a root call. Dwolla does not have an endpoint to validate it.
            client.getRootInfo();

        } catch (ApiException e) {
            if (e.getResponseBody().contains(EXPIRED_ACCESS_TOKEN_ERROR_CODE) ||
                    e.getResponseBody().contains(INVALID_ACCESS_TOKEN_ERROR_CODE)) {
                refreshClientTokens(context);
            } else {
                throw new PaymentPluginApiException("There was an error validating Dwolla access token.", e);
            }
        } catch (SQLException e) {
            throw new PaymentPluginApiException("There was an error loading Dwolla token pair from database.", e);
        }
    }

    private synchronized void refreshClientTokens(CallContext context) throws PaymentPluginApiException {
        try {
            final DwollaTokensRecord tokens = dao.getTokens(context.getTenantId());
            if (tokens == null) {
                throw new PaymentPluginApiException(null, "Dwolla tokens not found for tenant " + context.getTenantId());
            }

            final TokenResponse tokenResponse = client.refreshToken(tokens.getRefreshToken());
            if (tokenResponse.error != null) {
                throw new PaymentPluginApiException(tokenResponse.error,
                        "There was an error refreshing Dwolla tokens. " + tokenResponse.error_description);
            }

            client.getClient().setAccessToken(tokenResponse.access_token);
            if (!tokens.getAccessToken().equals(tokenResponse.access_token)) {
                dao.updateTokens(tokenResponse.access_token, tokenResponse.refresh_token, context.getTenantId());
            }

        } catch (SQLException e) {
            throw new PaymentPluginApiException("There was an error loading Dwolla token pair from database.", e);
        }
    }

}
