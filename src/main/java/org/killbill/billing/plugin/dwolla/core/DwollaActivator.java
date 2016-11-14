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

import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.billing.payment.plugin.api.PaymentPluginApi;
import org.killbill.billing.plugin.dwolla.api.DwollaPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.client.DwollaClient;
import org.killbill.billing.plugin.dwolla.client.DwollaConfigProperties;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.osgi.framework.BundleContext;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.util.Hashtable;

public class DwollaActivator extends KillbillActivatorBase {

    public static final String PLUGIN_NAME = "killbill-dwolla";

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        final Clock clock = new DefaultClock();
        final DwollaDao dao = new DwollaDao(dataSource.getDataSource());
        final DwollaClient client = new DwollaClient(new DwollaConfigProperties(configProperties.getProperties()));
        final DwollaNotificationHandler notificationHandler = new DwollaNotificationHandler(dao, client, killbillAPI, clock);

        // Register the payment plugin
        final DwollaPaymentPluginApi pluginApi = new DwollaPaymentPluginApi(killbillAPI, configProperties, logService, clock, dao, client, notificationHandler);
        registerPaymentPluginApi(context, pluginApi);

        // Register the servlet
        final DwollaServlet dwollaServlet = new DwollaServlet(dao, client.getConfigProperties(), logService, killbillAPI);
        registerServlet(context, dwollaServlet);
    }

    private void registerServlet(final BundleContext context, final HttpServlet servlet) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, Servlet.class, servlet, props);
    }

    private void registerPaymentPluginApi(final BundleContext context, final PaymentPluginApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, PaymentPluginApi.class, api, props);
    }
}
