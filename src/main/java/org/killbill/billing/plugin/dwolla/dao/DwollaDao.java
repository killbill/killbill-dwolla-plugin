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

package org.killbill.billing.plugin.dwolla.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;
import org.jooq.impl.DSL;
import org.killbill.billing.plugin.dao.payment.PluginPaymentDao;
import org.killbill.billing.plugin.dwolla.api.DwollaPaymentPluginApi;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DwollaDao extends PluginPaymentDao<DwollaResponsesRecord, DwollaResponses, DwollaPaymentMethodsRecord, DwollaPaymentMethods> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Joiner JOINER = Joiner.on(",");

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String FUNDING_SOURCE = "FUNDING_SOURCE";

    public DwollaDao(final DataSource dataSource) throws SQLException {
        super(DwollaResponses.DWOLLA_RESPONSES, DwollaPaymentMethods.DWOLLA_PAYMENT_METHODS, dataSource);
    }

    @Override
    public void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId, final boolean isDefault, Map<String, String> properties, final DateTime utcNow, final UUID kbTenantId) throws SQLException {
        /* Clone our properties, what we have been given might be unmodifiable */
        final Map<String, String> clonedProperties = new HashMap<String, String>(properties);

        /* Extract and remove known values from the properties map that will become "additional data" */
        final String customerId            = clonedProperties.remove(DwollaPaymentPluginApi.PROPERTY_CUSTOMER_ID);
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
                                        DSL.field(paymentMethodsTable.getName() + "." + KB_ACCOUNT_ID),
                                        DSL.field(paymentMethodsTable.getName() + "." + KB_PAYMENT_METHOD_ID),
                                        DSL.field(paymentMethodsTable.getName() + "." + FUNDING_SOURCE),
                                        DSL.field(paymentMethodsTable.getName() + "." + CUSTOMER_ID),
                                        DSL.field(paymentMethodsTable.getName() + "." + IS_DEFAULT),
                                        DSL.field(paymentMethodsTable.getName() + "." + IS_DELETED),
                                        DSL.field(paymentMethodsTable.getName() + "." + ADDITIONAL_DATA),
                                        DSL.field(paymentMethodsTable.getName() + "." + CREATED_DATE),
                                        DSL.field(paymentMethodsTable.getName() + "." + UPDATED_DATE),
                                        DSL.field(paymentMethodsTable.getName() + "." + KB_TENANT_ID))
                                .values(kbAccountId.toString(),
                                        kbPaymentMethodId.toString(),
                                        fundingSource,
                                        customerId,
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
}
