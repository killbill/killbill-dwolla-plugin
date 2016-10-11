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

package org.killbill.billing.plugin.dwolla.client;

public enum EventTopic {

    // https://docsv2.dwolla.com/#events

    // account events
    ACCOUNT_TRANSFER_CREATED("transfer_created"),
    ACCOUNT_TRANSFER_CANCELLED("transfer_cancelled"),
    ACCOUNT_TRANSFER_FAILED("transfer_failed"),
    ACCOUNT_TRANSFER_RECLAIMED("transfer_reclaimed"),
    ACCOUNT_TRANSFER_COMPLETED("transfer_completed"),

    // customer events
    CUSTOMER_TRANSFER_CREATED("customer_transfer_created"),
    CUSTOMER_TRANSFER_CANCELLED("customer_transfer_cancelled"),
    CUSTOMER_TRANSFER_FAILED("customer_transfer_failed"),
    CUSTOMER_TRANSFER_COMPLETED("customer_transfer_completed");

    private final String topic;

    EventTopic(final String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return topic;
    }

}
