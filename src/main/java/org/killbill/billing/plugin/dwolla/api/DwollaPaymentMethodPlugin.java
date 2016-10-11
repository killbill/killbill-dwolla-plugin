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

import org.killbill.billing.plugin.api.payment.PluginPaymentMethodPlugin;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord;

import java.util.UUID;

public class DwollaPaymentMethodPlugin extends PluginPaymentMethodPlugin {

    public DwollaPaymentMethodPlugin(final DwollaPaymentMethodsRecord record) {
        super(record.getKbPaymentMethodId() == null ? null : UUID.fromString(record.getKbPaymentMethodId()),
                record.getFundingSource(),
                (record.getIsDefault() != null) && DwollaDao.TRUE == record.getIsDefault(),
                DwollaModelPluginBase.buildPluginProperties(record.getAdditionalData()));
    }
}
