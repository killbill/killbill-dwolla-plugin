/*
 * Copyright 2014-2016 Groupon, Inc
 * Copyright 2014-2016 The Billing Project, LLC
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

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.plugin.api.*;
import org.killbill.billing.plugin.api.payment.PluginPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.util.UUID;

public class DwollaPaymentPluginApi extends PluginPaymentPluginApi<DwollaResponsesRecord, DwollaResponses, DwollaPaymentMethodsRecord, DwollaPaymentMethods> {

    // properties
    public static final String PROPERTY_CUSTOMER_ID = "customerId";
    public static final String PROPERTY_FUNDING_SOURCE_ID = "fundingSource";

    private final DwollaDao dao;

    public DwollaPaymentPluginApi(final OSGIKillbillAPI killbillApi,
                                  final OSGIConfigPropertiesService osgiConfigPropertiesService,
                                  final OSGIKillbillLogService logService,
                                  final Clock clock,
                                  final DwollaDao dao) throws JAXBException {
        super(killbillApi, osgiConfigPropertiesService, logService, clock, dao);
        this.dao = dao;
    }

    @Override
    protected PaymentTransactionInfoPlugin buildPaymentTransactionInfoPlugin(DwollaResponsesRecord record) {
        return null;
    }

    @Override
    protected PaymentMethodPlugin buildPaymentMethodPlugin(DwollaPaymentMethodsRecord record) {
        return null;
    }

    @Override
    protected PaymentMethodInfoPlugin buildPaymentMethodInfoPlugin(DwollaPaymentMethodsRecord record) {
        return null;
    }

    @Override
    protected String getPaymentMethodId(DwollaPaymentMethodsRecord input) {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin authorizePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin capturePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin purchasePayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin voidPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin creditPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public PaymentTransactionInfoPlugin refundPayment(UUID kbAccountId, UUID kbPaymentId, UUID kbTransactionId, UUID kbPaymentMethodId, BigDecimal amount, Currency currency, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public HostedPaymentPageFormDescriptor buildFormDescriptor(UUID kbAccountId, Iterable<PluginProperty> customFields, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

    @Override
    public GatewayNotification processNotification(String notification, Iterable<PluginProperty> properties, CallContext context) throws PaymentPluginApiException {
        return null;
    }

}
