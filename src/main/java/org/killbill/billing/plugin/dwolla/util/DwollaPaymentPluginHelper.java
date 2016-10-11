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

package org.killbill.billing.plugin.dwolla.util;

import com.google.common.base.Strings;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.plugin.dwolla.client.EventTopic;
import org.killbill.billing.plugin.dwolla.client.TransferStatus;
import org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord;

import static org.killbill.billing.plugin.dwolla.client.TransferStatus.valueOf;

public class DwollaPaymentPluginHelper {

    public static PaymentPluginStatus getPaymentPluginStatusFromTransfer(final String transferStatus) {
        switch (valueOf(transferStatus.toUpperCase())) {
            case PENDING:
                return PaymentPluginStatus.PENDING;
            case PROCESSED:
                return PaymentPluginStatus.PROCESSED;
            case CANCELLED:
                return PaymentPluginStatus.CANCELED;
            case FAILED:
                return PaymentPluginStatus.ERROR;
            case RECLAIMED:
            default:
                return PaymentPluginStatus.UNDEFINED;
        }
    }

    public static PaymentPluginStatus getPaymentPluginStatus(final DwollaResponsesRecord record) {
        if (Strings.isNullOrEmpty(record.getTransferStatus())) {
            return PaymentPluginStatus.UNDEFINED;
        } else {
            return getPaymentPluginStatusFromTransfer(record.getTransferStatus());
        }
    }

    public static TransferStatus getTransferStatusFromNotification(final String notificationStatus) {
        switch (EventTopic.valueOf(notificationStatus.toUpperCase())) {
            case ACCOUNT_TRANSFER_CREATED:
            case CUSTOMER_TRANSFER_CREATED:
                return TransferStatus.PENDING;
            case ACCOUNT_TRANSFER_COMPLETED:
            case CUSTOMER_TRANSFER_COMPLETED:
                return TransferStatus.PROCESSED;
            case ACCOUNT_TRANSFER_CANCELLED:
            case ACCOUNT_TRANSFER_FAILED:
            case CUSTOMER_TRANSFER_CANCELLED:
            case CUSTOMER_TRANSFER_FAILED:
                return TransferStatus.FAILED;
            default:
                return TransferStatus.CANCELLED;
        }
    }

    public static String getGatewayError(final DwollaResponsesRecord record) {
        return ""; // TODO
    }

}
