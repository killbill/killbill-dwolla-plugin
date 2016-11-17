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

package org.killbill.billing.plugin.dwolla.notification;


import io.swagger.client.model.Webhook;
import org.joda.time.DateTime;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.payment.api.Payment;
import org.killbill.billing.payment.api.PaymentTransaction;
import org.killbill.billing.payment.api.TransactionStatus;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.dwolla.client.DwollaClient;
import org.killbill.billing.plugin.dwolla.core.DwollaActivator;
import org.killbill.billing.plugin.dwolla.core.DwollaNotificationHandler;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class TestDwollaNotifications {

    private static final String PAYMENT_COMPLETED_NOTIFICATION =
            "{" +
                "  \"id\": \"9ece9660-aa34-41eb-80d7-0125d53b45e8\",\n" +
                "  \"topic\": \"customer_transfer_completed\",\n" +
                "  \"accountId\": \"ca32853c-48fa-40be-ae75-77b37504581b\",\n" +
                "  \"eventId\": \"03c7e14c-7f15-44a2-bcf7-83f2f7e95d50\",\n" +
                "  \"subscriptionId\": \"a0943041-7a5c-4e8f-92de-b55711ef3a83\"," +
                "  \"_links\": {\n" +
                    "    \"self\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/webhooks/9ece9660-aa34-41eb-80d7-0125d53b45e8\"\n" +
                    "    },\n" +
                    "    \"subscription\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/webhook-subscriptions/a0943041-7a5c-4e8f-92de-b55711ef3a83\"\n" +
                    "    },\n" +
                    "    \"resource\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/transfers/9ece9660-aa34-41eb-80d7-0125d53b45e8\"\n" +
                    "    },\n" +
                    "    \"event\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/events/03c7e14c-7f15-44a2-bcf7-83f2f7e95d50\"\n" +
                    "    }\n" +
                    "  }" +
            "}";

    private static final String PAYMENT_FAILED_NOTIFICATION =
            "{" +
                    "  \"id\": \"9ece9660-aa34-41eb-80d7-0125d53b45e8\",\n" +
                    "  \"topic\": \"customer_transfer_failed\",\n" +
                    "  \"accountId\": \"ca32853c-48fa-40be-ae75-77b37504581b\",\n" +
                    "  \"eventId\": \"03c7e14c-7f15-44a2-bcf7-83f2f7e95d50\",\n" +
                    "  \"subscriptionId\": \"a0943041-7a5c-4e8f-92de-b55711ef3a83\"," +
                    "  \"_links\": {\n" +
                    "    \"self\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/webhooks/9ece9660-aa34-41eb-80d7-0125d53b45e8\"\n" +
                    "    },\n" +
                    "    \"subscription\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/webhook-subscriptions/a0943041-7a5c-4e8f-92de-b55711ef3a83\"\n" +
                    "    },\n" +
                    "    \"resource\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/transfers/9ece9660-aa34-41eb-80d7-0125d53b45e8\"\n" +
                    "    },\n" +
                    "    \"event\": {\n" +
                    "      \"href\": \"https://api.dwolla.com/events/03c7e14c-7f15-44a2-bcf7-83f2f7e95d50\"\n" +
                    "    }\n" +
                    "  }" +
                    "}";

    private DwollaNotificationHandler handler;
    private UUID tenantid;

    private DwollaDao dao;
    private DwollaClient client;
    private OSGIKillbillAPI killbillApi;
    private Clock clock;

    private Account account;
    private Payment payment;
    private PaymentTransaction transaction;

    @BeforeMethod(groups = "fast")
    public void setUp() throws Exception {
        tenantid = UUID.randomUUID();

        account = TestUtils.buildAccount(Currency.USD, "US");
        killbillApi = TestUtils.buildOSGIKillbillAPI(account);

        TestUtils.buildPaymentMethod(account.getId(), account.getPaymentMethodId(), DwollaActivator.PLUGIN_NAME, killbillApi);
        payment = TestUtils.buildPayment(account.getId(), account.getPaymentMethodId(), account.getCurrency(), killbillApi);

        transaction = Mockito.mock(PaymentTransaction.class);
        Mockito.when(transaction.getId()).thenReturn(UUID.randomUUID());
        Mockito.when(transaction.getTransactionType()).thenReturn(TransactionType.PURCHASE);
        Mockito.when(transaction.getTransactionStatus()).thenReturn(TransactionStatus.PENDING);
        Mockito.when(payment.getTransactions()).thenReturn(Arrays.asList(transaction));

        dao = Mockito.mock(DwollaDao.class);
        client = Mockito.mock(DwollaClient.class);
        clock = Mockito.mock(Clock.class);
        handler = new DwollaNotificationHandler(dao, client, killbillApi, clock);

        Mockito.when(killbillApi.getPaymentApi().getPayment(Mockito.eq(payment.getId()), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(Iterable.class), Mockito.any(CallContext.class))).thenReturn(payment);
        Mockito.when(killbillApi.getAccountUserApi().getAccountById(Mockito.eq(account.getId()), Mockito.any(CallContext.class))).thenReturn(account);
        Mockito.when(killbillApi.getPaymentApi().notifyPendingTransactionOfStateChanged(Mockito.eq(account), Mockito.any(UUID.class), Mockito.anyBoolean(), Mockito.any(CallContext.class))).thenReturn(payment);

        DwollaResponsesRecord response = Mockito.mock(DwollaResponsesRecord.class);
        final String accountId = account.getId().toString();
        Mockito.when(response.getKbAccountId()).thenReturn(accountId);
        final String paymentId = payment.getId().toString();
        Mockito.when(response.getKbPaymentId()).thenReturn(paymentId);
        final String transactionId = transaction.getId().toString();
        Mockito.when(response.getKbPaymentTransactionId()).thenReturn(transactionId);

        Mockito.when(dao.getResponseByTransferId(Mockito.anyString(), Mockito.eq(tenantid))).thenReturn(response);
    }

    @Test(groups = "fast")
    public void testHandleTransactionCompletedNotification() throws Exception {
        handler.processNotification(PAYMENT_COMPLETED_NOTIFICATION, tenantid);

        Mockito.verify(dao, times(1)).addNotification(Mockito.any(Webhook.class), Mockito.any(DateTime.class), Mockito.eq(tenantid));
        Mockito.verify(dao, times(1)).getResponseByTransferId(Mockito.anyString(), Mockito.eq(tenantid));
        Mockito.verify(dao, times(1)).updateResponseStatus(Mockito.eq("processed"), Mockito.anyString(), Mockito.eq(tenantid));

        final UUID transactionId = transaction.getId();
        final boolean processed = true;
        Mockito.verify(killbillApi.getPaymentApi(), Mockito.times(1)).notifyPendingTransactionOfStateChanged(Mockito.eq(account), Mockito.eq(transactionId), Mockito.eq(processed), Mockito.any(CallContext.class));
    }

    @Test(groups = "fast")
    public void testHandleTransactionFailedNotification() throws Exception {
        handler.processNotification(PAYMENT_FAILED_NOTIFICATION, tenantid);

        Mockito.verify(dao, times(1)).addNotification(Mockito.any(Webhook.class), Mockito.any(DateTime.class), Mockito.eq(tenantid));
        Mockito.verify(dao, times(1)).getResponseByTransferId(Mockito.anyString(), Mockito.eq(tenantid));
        Mockito.verify(dao, times(1)).updateResponseStatus(Mockito.eq("failed"), Mockito.anyString(), Mockito.eq(tenantid));

        final UUID transactionId = transaction.getId();
        final boolean processed = false;
        Mockito.verify(killbillApi.getPaymentApi(), Mockito.times(1)).notifyPendingTransactionOfStateChanged(Mockito.eq(account), Mockito.eq(transactionId), Mockito.eq(processed), Mockito.any(CallContext.class));
    }

    @Test(groups = "fast")
    public void testAlreadyExistTransactionNotification() throws Exception {

        SQLException exception = new SQLException(new SQLIntegrityConstraintViolationException());
        Mockito.doThrow(exception).when(dao).addNotification(Mockito.any(Webhook.class), Mockito.any(DateTime.class), Mockito.eq(tenantid));
        handler.processNotification(PAYMENT_COMPLETED_NOTIFICATION, tenantid);

        Mockito.verify(dao, times(1)).addNotification(Mockito.any(Webhook.class), Mockito.any(DateTime.class), Mockito.eq(tenantid));
        Mockito.verify(dao, never()).getResponseByTransferId(Mockito.anyString(), Mockito.eq(tenantid));
        Mockito.verify(dao, never()).updateResponseStatus(Mockito.eq("failed"), Mockito.anyString(), Mockito.eq(tenantid));

        final UUID transactionId = transaction.getId();
        final boolean processed = false;
        Mockito.verify(killbillApi.getPaymentApi(), never()).notifyPendingTransactionOfStateChanged(Mockito.eq(account), Mockito.eq(transactionId), Mockito.eq(processed), Mockito.any(CallContext.class));
    }

    @Test(groups = "fast", expectedExceptions = PaymentPluginApiException.class)
    public void testErrorProcessingTransactionNotification() throws Exception {
        SQLException exception = new SQLException(new SQLDataException());
        Mockito.doThrow(exception).when(dao).addNotification(Mockito.any(Webhook.class), Mockito.any(DateTime.class), Mockito.eq(tenantid));
        handler.processNotification(PAYMENT_COMPLETED_NOTIFICATION, tenantid);
    }

}
