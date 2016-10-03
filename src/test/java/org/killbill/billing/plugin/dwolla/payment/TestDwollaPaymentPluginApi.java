package org.killbill.billing.plugin.dwolla.payment;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.*;
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
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;

public class TestDwollaPaymentPluginApi extends TestRemoteBase {

    private final static String FUNDING_SOURCE_ID = UUID.randomUUID().toString();

    @Test(groups = "slow")
    public void testPurchaseAndRefund() throws Exception {
        paymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), dWollaEmptyPaymentMethodPlugin(), true,
                PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of(DwollaPaymentPluginApi.PROPERTY_FUNDING_SOURCE_ID, FUNDING_SOURCE_ID)), context);

        final Payment payment = doPurchase(BigDecimal.TEN, account.getCurrency(), PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of()));
        // TODO doRefund(payment, BigDecimal.TEN);
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

    private Payment doPluginCall(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties, final PluginCall pluginCall) throws PaymentPluginApiException, PaymentApiException {
        final Payment payment = TestUtils.buildPayment(account.getId(), account.getPaymentMethodId(), currency, killbillApi);
        return doPluginCall(payment, amount, pluginProperties, pluginCall);
    }

    private Payment doPluginCall(final Payment payment, final BigDecimal amount, final Iterable<PluginProperty> pluginProperties, final PluginCall pluginCall) throws PaymentPluginApiException {
        final PaymentTransaction paymentTransaction = TestUtils.buildPaymentTransaction(payment, null, amount, payment.getCurrency());

        final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin = pluginCall.execute(payment, paymentTransaction, pluginProperties);
        TestUtils.updatePaymentTransaction(paymentTransaction, paymentTransactionInfoPlugin);

        // TODO verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPlugin);

        assertEquals(PaymentPluginStatus.PENDING, paymentTransactionInfoPlugin.getStatus());
        assertEquals(PaymentPluginStatus.PENDING, paymentTransaction.getPaymentInfoPlugin().getStatus());

        return payment;
    }

    private interface PluginCall {
        PaymentTransactionInfoPlugin execute(Payment payment, PaymentTransaction paymentTransaction, Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException;
    }

}
