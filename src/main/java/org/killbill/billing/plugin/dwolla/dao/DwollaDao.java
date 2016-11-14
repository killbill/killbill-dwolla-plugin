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

package org.killbill.billing.plugin.dwolla.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.client.model.Transfer;
import io.swagger.client.model.TransferFailure;
import io.swagger.client.model.Webhook;
import org.joda.time.DateTime;
import org.jooq.Configuration;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.plugin.dao.payment.PluginPaymentDao;
import org.killbill.billing.plugin.dwolla.api.DwollaPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.client.TransferStatus;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaTokensRecord;
import org.killbill.billing.plugin.dwolla.util.JsonHelper;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.killbill.billing.plugin.dwolla.dao.gen.Tables.*;
import static org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES;

public class DwollaDao extends PluginPaymentDao<DwollaResponsesRecord, DwollaResponses, DwollaPaymentMethodsRecord, DwollaPaymentMethods> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public DwollaDao(final DataSource dataSource) throws SQLException {
        super(DWOLLA_RESPONSES, DwollaPaymentMethods.DWOLLA_PAYMENT_METHODS, dataSource);
    }

    @Override
    public void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId, final boolean isDefault, Map<String, String> properties, final DateTime utcNow, final UUID kbTenantId) throws SQLException {
        /* Clone our properties, what we have been given might be unmodifiable */
        final Map<String, String> clonedProperties = new HashMap<String, String>(properties);

        /* Extract and remove known values from the properties map that will become "additional data" */
        final String customerId            = clonedProperties.remove(DwollaPaymentPluginApi.PROPERTY_CUSTOMER_ID);
        final String accountId             = clonedProperties.remove(DwollaPaymentPluginApi.PROPERTY_ACCOUNT_ID);
        final String fundingSource         = clonedProperties.remove(DwollaPaymentPluginApi.PROPERTY_FUNDING_SOURCE_ID);

        /* Calculate the additional data to store */
        final String additionalData = asString(clonedProperties);

        /* Store computed data */
        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .insertInto(paymentMethodsTable,
                                        DWOLLA_PAYMENT_METHODS.KB_ACCOUNT_ID,
                                        DWOLLA_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID,
                                        DWOLLA_PAYMENT_METHODS.FUNDING_SOURCE,
                                        DWOLLA_PAYMENT_METHODS.CUSTOMER_ID,
                                        DWOLLA_PAYMENT_METHODS.ACCOUNT_ID,
                                        DWOLLA_PAYMENT_METHODS.IS_DEFAULT,
                                        DWOLLA_PAYMENT_METHODS.IS_DELETED,
                                        DWOLLA_PAYMENT_METHODS.ADDITIONAL_DATA,
                                        DWOLLA_PAYMENT_METHODS.CREATED_DATE,
                                        DWOLLA_PAYMENT_METHODS.UPDATED_DATE,
                                        DWOLLA_PAYMENT_METHODS.KB_TENANT_ID)
                                .values(kbAccountId.toString(),
                                        kbPaymentMethodId.toString(),
                                        fundingSource,
                                        customerId,
                                        accountId,
                                        fromBoolean(isDefault),
                                        FALSE,
                                        additionalData,
                                        toTimestamp(utcNow),
                                        toTimestamp(utcNow),
                                        kbTenantId.toString())
                                .execute();
                        return null;
                    }
                });
    }

    public void addTokens(final String accessToken,
                          final String refreshToken,
                          final String dwAccountId,
                          final UUID tenantId) throws SQLException {

        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .insertInto(DWOLLA_TOKENS,
                                        DWOLLA_TOKENS.ACCESS_TOKEN,
                                        DWOLLA_TOKENS.REFRESH_TOKEN,
                                        DWOLLA_TOKENS.CREATED_DATE,
                                        DWOLLA_TOKENS.UPDATED_DATE,
                                        DWOLLA_TOKENS.ACCOUNT_ID,
                                        DWOLLA_TOKENS.KB_TENANT_ID)
                                .values(accessToken,
                                        refreshToken,
                                        toTimestamp(DateTime.now()),
                                        toTimestamp(DateTime.now()),
                                        dwAccountId,
                                        tenantId.toString())
                                .execute();
                        return null;
                    }
                });
    }

    public DwollaTokensRecord getTokens(final String dwAccountId, final UUID kbTenantId) throws SQLException {
        return execute(dataSource.getConnection(),
                new WithConnectionCallback<DwollaTokensRecord>() {
                    @Override
                    public DwollaTokensRecord withConnection(final Connection conn) throws SQLException {
                        return DSL.using(conn, dialect, settings)
                                .selectFrom(DWOLLA_TOKENS)
                                .where(DWOLLA_TOKENS.KB_TENANT_ID.equal(kbTenantId.toString()))
                                .and(DWOLLA_TOKENS.ACCOUNT_ID.equal(dwAccountId))
                                .orderBy(DWOLLA_TOKENS.RECORD_ID.desc())
                                .fetchOne();
                    }
                });
    }

    public void updateTokens(final String accessToken, final String refreshToken, final String dwAccountId, final UUID kbTenantId) throws SQLException {
        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .transaction(new TransactionalRunnable() {
                                    @Override
                                    public void run(final Configuration configuration) throws Exception {
                                        DSL.using(conn, dialect, settings)
                                                .update(DWOLLA_TOKENS)
                                                .set(DWOLLA_TOKENS.ACCESS_TOKEN, accessToken)
                                                .set(DWOLLA_TOKENS.REFRESH_TOKEN, refreshToken)
                                                .where(DWOLLA_TOKENS.ACCOUNT_ID.equal(dwAccountId))
                                                .and(DWOLLA_TOKENS.KB_TENANT_ID.equal(kbTenantId.toString()))
                                                .execute();
                                    }
                                });
                        return null;
                    }
                });
    }

    public void addResponse(final UUID kbAccountId,
                                 final UUID kbPaymentId,
                                 final UUID kbPaymentTransactionId,
                                 final TransactionType transactionType,
                                 @Nullable final BigDecimal amount,
                                 @Nullable final Currency currency,
                                 final Transfer transfer,
                                 final TransferFailure transferFailure,
                                 final Iterable<PluginProperty> properties, final DateTime utcNow,
                                 final UUID kbTenantId) throws SQLException {

        final String additionalData = getAdditionalData(transfer, transferFailure, properties);
        final String errorCode = (transferFailure != null) ? transferFailure.getCode() : null;

        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .insertInto(DWOLLA_RESPONSES,
                                        DWOLLA_RESPONSES.KB_ACCOUNT_ID,
                                        DWOLLA_RESPONSES.KB_PAYMENT_ID,
                                        DWOLLA_RESPONSES.KB_PAYMENT_TRANSACTION_ID,
                                        DWOLLA_RESPONSES.TRANSACTION_TYPE,
                                        DWOLLA_RESPONSES.AMOUNT,
                                        DWOLLA_RESPONSES.CURRENCY,
                                        DWOLLA_RESPONSES.TRANSFER_ID,
                                        DWOLLA_RESPONSES.TRANSFER_STATUS,
                                        DWOLLA_RESPONSES.ERROR_CODE,
                                        DWOLLA_RESPONSES.ADDITIONAL_DATA,
                                        DWOLLA_RESPONSES.CREATED_DATE,
                                        DWOLLA_RESPONSES.KB_TENANT_ID)
                                .values(kbAccountId.toString(),
                                        kbPaymentId.toString(),
                                        kbPaymentTransactionId.toString(),
                                        transactionType.toString(),
                                        amount,
                                        currency.toString(),
                                        transfer.getId(),
                                        transfer.getStatus(),
                                        errorCode,
                                        additionalData,
                                        toTimestamp(utcNow),
                                        kbTenantId.toString())
                                .execute();
                        return null;
                    }
                });
    }

    public void addErrorResponse(final UUID kbAccountId,
                                 final UUID kbPaymentId,
                                 final UUID kbPaymentTransactionId,
                                 final TransactionType transactionType,
                                 @Nullable final BigDecimal amount,
                                 @Nullable final Currency currency,
                                 final String errorCode,
                                 final String errorDescription,
                                 final DateTime utcNow,
                                 final UUID kbTenantId) throws SQLException {

        final String additionalData = "errorDescription: " + errorDescription;

        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .insertInto(DWOLLA_RESPONSES,
                                        DWOLLA_RESPONSES.KB_ACCOUNT_ID,
                                        DWOLLA_RESPONSES.KB_PAYMENT_ID,
                                        DWOLLA_RESPONSES.KB_PAYMENT_TRANSACTION_ID,
                                        DWOLLA_RESPONSES.TRANSACTION_TYPE,
                                        DWOLLA_RESPONSES.AMOUNT,
                                        DWOLLA_RESPONSES.CURRENCY,
                                        DWOLLA_RESPONSES.TRANSFER_ID,
                                        DWOLLA_RESPONSES.TRANSFER_STATUS,
                                        DWOLLA_RESPONSES.ERROR_CODE,
                                        DWOLLA_RESPONSES.ADDITIONAL_DATA,
                                        DWOLLA_RESPONSES.CREATED_DATE,
                                        DWOLLA_RESPONSES.KB_TENANT_ID)
                                .values(kbAccountId.toString(),
                                        kbPaymentId.toString(),
                                        kbPaymentTransactionId.toString(),
                                        transactionType.toString(),
                                        amount,
                                        currency.toString(),
                                        null, // transfer id
                                        TransferStatus.FAILED.toString(),
                                        errorCode,
                                        additionalData,
                                        toTimestamp(utcNow),
                                        kbTenantId.toString())
                                .execute();
                        return null;
                    }
                });
    }

    public void updateResponseStatus(final String newStatus, final String transferId, final UUID tenantId) throws SQLException {
        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .transaction(new TransactionalRunnable() {
                                    @Override
                                    public void run(final Configuration configuration) throws Exception {
                                        DSL.using(conn, dialect, settings)
                                                .update(DWOLLA_RESPONSES)
                                                .set(DWOLLA_RESPONSES.TRANSFER_STATUS, newStatus)
                                                .where(DWOLLA_RESPONSES.KB_TENANT_ID.equal(tenantId.toString()))
                                                .and(DWOLLA_RESPONSES.TRANSFER_ID.equal(transferId))
                                                .execute();
                                    }
                                });
                        return null;
                    }
                });
    }

    public void addNotification(final Webhook webhook,
                                final DateTime utcNow,
                                final UUID tenantId) throws SQLException {

        final String self = webhook.getLinks().get(DwollaPaymentPluginApi.SELF).getHref();

        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                                .insertInto(DWOLLA_NOTIFICATIONS,
                                        DWOLLA_NOTIFICATIONS.SELF,
                                        DWOLLA_NOTIFICATIONS.ID,
                                        DWOLLA_NOTIFICATIONS.ACCOUNT_ID,
                                        DWOLLA_NOTIFICATIONS.EVENT_ID,
                                        DWOLLA_NOTIFICATIONS.SUBSCRIPTION_ID,
                                        DWOLLA_NOTIFICATIONS.TOPIC,
                                        DWOLLA_NOTIFICATIONS.ADDITIONAL_DATA,
                                        DWOLLA_NOTIFICATIONS.CREATED_DATE,
                                        DWOLLA_NOTIFICATIONS.KB_TENANT_ID)
                                .values(JsonHelper.getIdFromUrl(self),
                                        webhook.getId(),
                                        webhook.getAccountId(),
                                        webhook.getEventId(),
                                        webhook.getSubscriptionId(),
                                        webhook.getTopic(),
                                        getAdditionalData(webhook),
                                        toTimestamp(utcNow),
                                        tenantId.toString())
                                .execute();
                        return null;
                    }
                });
    }

    public DwollaResponsesRecord getResponseByTransferId(final String transferId, final UUID kbTenantId) throws SQLException {
        return execute(dataSource.getConnection(),
                new WithConnectionCallback<DwollaResponsesRecord>() {
                    @Override
                    public DwollaResponsesRecord withConnection(final Connection conn) throws SQLException {
                        return DSL.using(conn, dialect, settings)
                                .selectFrom(DWOLLA_RESPONSES)
                                .where(DWOLLA_RESPONSES.TRANSFER_ID.equal(transferId))
                                .and(DWOLLA_RESPONSES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                .fetchOne();
                    }
                });
    }

    private String getAdditionalData(final Transfer result, final TransferFailure transferFailure, final Iterable<PluginProperty> properties) throws SQLException {
        Map<String, String> additionalData = new HashMap<String, String>();
        for (PluginProperty prop : properties) {
            additionalData.put(prop.getKey(), prop.getValue().toString());
        }
        additionalData.put("transferId", result.getId());
        additionalData.put("status", result.getStatus());
        if (transferFailure != null) {
            additionalData.put("errorDescription",  transferFailure.getDescription());
        }
        try {
            additionalData.put("links", objectMapper.writeValueAsString(result.getLinks()));
            additionalData.put("embedded", objectMapper.writeValueAsString(result.getEmbedded()));
            additionalData.put("metadata", objectMapper.writeValueAsString(result.getMetadata()));
            return objectMapper.writeValueAsString(additionalData);
        } catch (final JsonProcessingException e) {
            throw new SQLException(e.getMessage());
        }
    }

    private String getAdditionalData(final Webhook webhook) throws SQLException {
        Map<String, String> additionalData = new HashMap<String, String>();
        try {
            additionalData.put("links", objectMapper.writeValueAsString(webhook.getLinks()));
            additionalData.put("embedded", objectMapper.writeValueAsString(webhook.getEmbedded()));
            additionalData.put("attempts", objectMapper.writeValueAsString(webhook.getAttempts()));
            return objectMapper.writeValueAsString(additionalData);
        } catch (final JsonProcessingException e) {
            throw new SQLException(e.getMessage());
        }
    }
}
