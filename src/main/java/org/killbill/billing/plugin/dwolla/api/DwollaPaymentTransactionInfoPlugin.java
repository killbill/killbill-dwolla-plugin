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
import org.killbill.billing.plugin.dwolla.util.DwollaPaymentPluginHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
                DwollaPaymentPluginHelper.getPaymentPluginStatusFromTransfer(transfer.getStatus()),
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
                DwollaPaymentPluginHelper.getPaymentPluginStatus(record),
                record.getErrorCode(),
                DwollaPaymentPluginHelper.getGatewayError(record),
                record.getTransferId(),
                null,
                new DateTime(record.getCreatedDate(), DateTimeZone.UTC),
                new DateTime(record.getCreatedDate(), DateTimeZone.UTC),
                DwollaModelPluginBase.buildPluginProperties(record.getAdditionalData()));
    }

}
