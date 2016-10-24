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
package org.killbill.billing.plugin.dwolla.payment;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.Payment;
import org.killbill.billing.payment.api.PaymentApiException;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PaymentTransaction;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.dwolla.TestRemoteBase;
import org.killbill.billing.plugin.dwolla.api.DwollaPaymentMethodPlugin;
import org.killbill.billing.plugin.dwolla.api.DwollaPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

public class TestDwollaPaymentPluginApi extends TestRemoteBase {

    private final static String FUNDING_SOURCE_ID = UUID.randomUUID().toString();
    private final static String CODE = UUID.randomUUID().toString();
    private InOrder inOrder;

    @BeforeClass
    @Override
    public void setUpBeforeClass() throws Exception {
        super.setUpBeforeClass();
        inOrder = Mockito.inOrder(client.getClient());
    }

    @Test(groups = "slow")
    public void testPurchaseAndRefundWhiteLabelSolution() throws Exception {
        paymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), dWollaEmptyPaymentMethodPlugin(), true,
                PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of(DwollaPaymentPluginApi.PROPERTY_FUNDING_SOURCE_ID, FUNDING_SOURCE_ID)), context);

        verifySetAccessTokenNumberOfCalls(never(), never());

        final Iterable<PluginProperty> properties = PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of());
        final Payment payment = doPurchase(BigDecimal.TEN, account.getCurrency(), properties);

        verifySetAccessTokenNumberOfCalls(times(1), never());

        doRefund(payment, BigDecimal.TEN, properties);

        verifySetAccessTokenNumberOfCalls(times(1), never());
    }

    @Test(groups = "slow")
    public void testPurchaseAndRefundDwollaDirectSolution() throws Exception {
        paymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), dWollaEmptyPaymentMethodPlugin(), true,
                PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of(DwollaPaymentPluginApi.PROPERTY_CODE, CODE)), context);

        verifySetAccessTokenNumberOfCalls(never(), times(1));

        final Iterable<PluginProperty> properties = PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of());
        final Payment payment = doPurchase(BigDecimal.TEN, account.getCurrency(), properties);

        verifySetAccessTokenNumberOfCalls(times(1), times(1));
        verifySetAccessTokenNumberOfCalls(times(1), never());

        doRefund(payment, BigDecimal.TEN, properties);

        verifySetAccessTokenNumberOfCalls(times(1), times(1));
        verifySetAccessTokenNumberOfCalls(times(1), never());
    }

    private void verifySetAccessTokenNumberOfCalls(final VerificationMode whiteLabelTimes, final VerificationMode dwollaDirectTimes) {
        // verify how many time White Label access token is set
        inOrder.verify(client.getClient(), whiteLabelTimes).setAccessToken(Mockito.eq(WL_ACCESS_TOKEN));

        // verify how many time Dwolla Direct access token is set
        inOrder.verify(client.getClient(), dwollaDirectTimes).setAccessToken(Mockito.eq(DD_ACCESS_TOKEN));
    }

    private PaymentMethodPlugin dWollaEmptyPaymentMethodPlugin() {
        return dwollaPaymentMethodPlugin(account.getPaymentMethodId().toString(), null);
    }

    private static PaymentMethodPlugin dwollaPaymentMethodPlugin(final String paymentMethodId, final String additionalData) {
        final DwollaPaymentMethodsRecord record = new DwollaPaymentMethodsRecord();
        record.setKbPaymentMethodId(paymentMethodId);
        record.setIsDefault(DwollaDao.TRUE);
        if (!Strings.isNullOrEmpty(additionalData)) {
            record.setAdditionalData(additionalData);
        }
        return new DwollaPaymentMethodPlugin(record);
    }

    private Payment doPurchase(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException, PaymentApiException {
        return doPluginCall(amount,
                currency,
                pluginProperties,
                new PluginCall() {
                    @Override
                    public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                        return paymentPluginApi.purchasePayment(account.getId(),
                                payment.getId(),
                                paymentTransaction.getId(),
                                payment.getPaymentMethodId(),
                                paymentTransaction.getAmount(),
                                paymentTransaction.getCurrency(),
                                pluginProperties,
                                context);
                    }
                });
    }

    private Payment doRefund(final Payment payment, final BigDecimal amount, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
        return doPluginCall(payment,
                amount,
                pluginProperties,
                new PluginCall() {
                    @Override
                    public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                        return paymentPluginApi.refundPayment(account.getId(),
                                payment.getId(),
                                paymentTransaction.getId(),
                                payment.getPaymentMethodId(),
                                paymentTransaction.getAmount(),
                                paymentTransaction.getCurrency(),
                                pluginProperties,
                                context);
                    }
                });
    }

    private Payment doPluginCall(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties, final PluginCall pluginCall) throws PaymentPluginApiException, PaymentApiException {
        final Payment payment = TestUtils.buildPayment(account.getId(), account.getPaymentMethodId(), currency, killbillApi);
        return doPluginCall(payment, amount, pluginProperties, pluginCall);
    }

    private Payment doPluginCall(final Payment payment, final BigDecimal amount, final Iterable<PluginProperty> pluginProperties, final PluginCall pluginCall) throws PaymentPluginApiException {
        final PaymentTransaction paymentTransaction = TestUtils.buildPaymentTransaction(payment, null, amount, payment.getCurrency());

        final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin = pluginCall.execute(payment, paymentTransaction, pluginProperties);
        TestUtils.updatePaymentTransaction(paymentTransaction, paymentTransactionInfoPlugin);

        verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPlugin);

        assertEquals(PaymentPluginStatus.PENDING, paymentTransactionInfoPlugin.getStatus());
        assertEquals(PaymentPluginStatus.PENDING, paymentTransaction.getPaymentInfoPlugin().getStatus());

        return payment;
    }

    private void verifyPaymentTransactionInfoPlugin(final Payment payment, final PaymentTransaction paymentTransaction, final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin) throws PaymentPluginApiException {
        verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPlugin, true);

        // Verify we can fetch the details
        final List<PaymentTransactionInfoPlugin> paymentTransactionInfoPlugins = paymentPluginApi.getPaymentInfo(account.getId(), paymentTransactionInfoPlugin.getKbPaymentId(), ImmutableList.<PluginProperty>of(), context);
        final PaymentTransactionInfoPlugin paymentTransactionInfoPluginFetched = Iterables.<PaymentTransactionInfoPlugin>find(Lists.<PaymentTransactionInfoPlugin>reverse(paymentTransactionInfoPlugins),
                new Predicate<PaymentTransactionInfoPlugin>() {
                    @Override
                    public boolean apply(final PaymentTransactionInfoPlugin input) {
                        return input.getKbTransactionPaymentId().equals(paymentTransaction.getId());
                    }
                });
        verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPluginFetched, true);
    }

    private void verifyPaymentTransactionInfoPlugin(final Payment payment, final PaymentTransaction paymentTransaction, final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin, final boolean authorizedProcessed) {
        Assert.assertEquals(paymentTransactionInfoPlugin.getKbPaymentId(), payment.getId());
        Assert.assertEquals(paymentTransactionInfoPlugin.getKbTransactionPaymentId(), paymentTransaction.getId());
        Assert.assertEquals(paymentTransactionInfoPlugin.getTransactionType(), paymentTransaction.getTransactionType());
        assertNotNull(paymentTransactionInfoPlugin.getCreatedDate());
        assertNotNull(paymentTransactionInfoPlugin.getEffectiveDate());
        assertNotNull(paymentTransactionInfoPlugin.getFirstPaymentReferenceId());
    }

    private interface PluginCall {
        PaymentTransactionInfoPlugin execute(Payment payment, PaymentTransaction paymentTransaction, Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException;
    }

}
