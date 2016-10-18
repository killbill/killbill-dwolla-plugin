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

package org.killbill.billing.plugin.dwolla;

import com.google.common.collect.Lists;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.model.*;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.TestWithEmbeddedDBBase;
import org.killbill.billing.plugin.dwolla.api.DwollaPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.client.DwollaClient;
import org.killbill.billing.plugin.dwolla.client.DwollaConfigProperties;
import org.killbill.billing.plugin.dwolla.core.DwollaActivator;
import org.killbill.billing.plugin.dwolla.core.DwollaNotificationHandler;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class TestRemoteBase extends TestWithEmbeddedDBBase {

    public static final String DEFAULT_COUNTRY = "US";
    public static final Currency DEFAULT_CURRENCY = Currency.USD;
    public static final UUID TENANT_ID = UUID.randomUUID();
    public static final String  ACCOUNT_ID = UUID.randomUUID().toString();
    public static final String ACCESS_TOKEN = "US";
    public static final String REFRESH_TOKEN = "US";

    protected CallContext context;
    protected Account account;
    protected DwollaPaymentPluginApi paymentPluginApi;
    protected OSGIKillbillAPI killbillApi;
    protected DwollaDao dao;
    protected DwollaClient client;
    protected DwollaNotificationHandler notificationHandler;

    @BeforeClass(groups = "slow")
    public void setUpBeforeClass() throws Exception {
        super.setUpBeforeClass();

        final Clock clock = new DefaultClock();
        mockDwollaClient();

        // mock on each test case
        // apiClient.invokeAPI(path, "POST", queryParams, postBody, headerParams, formParams, accept, contentType, this.authNames);
        // apiClient.deserialize(ex1, "", Transfer.class):null

        context = Mockito.mock(CallContext.class);
        Mockito.when(context.getTenantId()).thenReturn(TENANT_ID);

        account = TestUtils.buildAccount(DEFAULT_CURRENCY, DEFAULT_COUNTRY);
        killbillApi = TestUtils.buildOSGIKillbillAPI(account);

        TestUtils.buildPaymentMethod(account.getId(), account.getPaymentMethodId(), DwollaActivator.PLUGIN_NAME, killbillApi);

        dao = new DwollaDao(embeddedDB.getDataSource());

        final OSGIKillbillLogService logService = TestUtils.buildLogService();

        notificationHandler = new DwollaNotificationHandler(dao, client, killbillApi, clock);

        final OSGIConfigPropertiesService configPropertiesService = Mockito.mock(OSGIConfigPropertiesService.class);
        paymentPluginApi = new DwollaPaymentPluginApi(killbillApi, configPropertiesService, logService, clock, dao, client, notificationHandler);
    }

    private void mockDwollaClient() throws ApiException {
        client = Mockito.mock(DwollaClient.class);
        final ApiClient apiClient = Mockito.mock(ApiClient.class);
        Mockito.when(client.getClient()).thenReturn(apiClient);
        Mockito.when(apiClient.parameterToString(Mockito.anyObject())).thenReturn("");
        Mockito.when(apiClient.escapeString(Mockito.anyString())).thenReturn("");
        Mockito.when(apiClient.selectHeaderAccept(Mockito.any(String[].class))).thenReturn("");
        Mockito.when(apiClient.selectHeaderContentType(Mockito.any(String[].class))).thenReturn("");

        Mockito.when(apiClient.invokeAPI(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(Map.class),
                Mockito.anyObject(),
                Mockito.any(Map.class),
                Mockito.any(Map.class),
                Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class)
        )).thenReturn("{}");


        final DwollaConfigProperties configProperties = Mockito.mock(DwollaConfigProperties.class);
        Mockito.when(configProperties.getAccountId()).thenReturn(ACCOUNT_ID);
        Mockito.when(client.getConfigProperties()).thenReturn(configProperties);

        final FundingSource fundingSource = new FundingSource();
        fundingSource.setId("692486f8-29f6-4516-a6a5-c69fd2ce854c");
        fundingSource.setStatus("verified");
        final Map<String, HalLink> links = new HashMap<String, HalLink>();
        final HalLink self = new HalLink();
        self.setHref("https://api.dwolla.com/funding-sources/692486f8-29f6-4516-a6a5-c69fd2ce854c");
        links.put("self", self);
        fundingSource.setLinks(links);
        Mockito.when(apiClient.deserialize(Mockito.anyString(), Mockito.anyString(), Mockito.eq(FundingSource.class))).thenReturn(fundingSource);

        final CatalogResponse rootInfo = new CatalogResponse();
        final Map<String, HalLink> accountLinks = new HashMap<String, HalLink>();
        final HalLink accountLink = new HalLink();
        accountLink.setHref("https://api.dwolla.com/accounts/662956f8-29f6-4516-a6a5-b5a672ce854c");
        accountLinks.put("account", accountLink);
        rootInfo.setLinks(accountLinks);
        Mockito.when(client.getRootInfo()).thenReturn(rootInfo);

        final Unit$ unitTransfer = new Unit$();
        unitTransfer.setLocationHeader(Lists.newArrayList("https://api.dwolla.com/transfers/a123486f8-29f6-4516-a6a5-c69fd2ce854c"));
        Mockito.when(apiClient.deserialize(Mockito.anyString(), Mockito.anyString(), Mockito.eq(Unit$.class))).thenReturn(unitTransfer);

        final Transfer transfer = new Transfer();
        transfer.setId("692486f8-29f6-4516-a6a5-c69fd2ce854c");
        transfer.setStatus("pending");
        final Money amount = new Money();
        amount.setValue("10");
        amount.setCurrency("USD");
        transfer.setAmount(amount);

        Mockito.when(apiClient.deserialize(Mockito.anyString(), Mockito.anyString(), Mockito.eq(Transfer.class))).thenReturn(transfer);

    }

    @BeforeMethod
    @Override
    public void setUpBeforeMethod() throws Exception {
        super.setUpBeforeMethod();
        dao.addTokens(ACCESS_TOKEN, REFRESH_TOKEN, ACCOUNT_ID, TENANT_ID);
    }
}
