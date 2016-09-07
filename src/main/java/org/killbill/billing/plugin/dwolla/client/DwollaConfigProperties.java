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

package org.killbill.billing.plugin.dwolla.client;

import java.util.Properties;

public class DwollaConfigProperties {

    private static final String PROPERTY_PREFIX = "org.killbill.billing.plugin.dwolla.";

    private final String baseUrl;
    private final String baseOAuthUrl;
    private final String redirectUrl;
    private final String scopes;

    private final String clientId;
    private final String clientSecret;
    private final String senderPin;

    private String accessToken;
    private final String refreshToken;

    public DwollaConfigProperties(final Properties properties) {
        this.baseUrl = properties.getProperty(PROPERTY_PREFIX + "baseUrl");
        this.baseOAuthUrl = properties.getProperty(PROPERTY_PREFIX + "baseOAuthUrl");
        this.redirectUrl = properties.getProperty(PROPERTY_PREFIX + "redirectUrl");
        this.scopes = properties.getProperty(PROPERTY_PREFIX + "scopes", "Send|AccountInfoFull|Funding");

        this.clientId = properties.getProperty(PROPERTY_PREFIX + "clientId");
        this.clientSecret = properties.getProperty(PROPERTY_PREFIX + "clientSecret");
        this.senderPin = properties.getProperty(PROPERTY_PREFIX + "senderPin");

        this.accessToken = properties.getProperty(PROPERTY_PREFIX + "accessToken");
        this.refreshToken = properties.getProperty(PROPERTY_PREFIX + "refreshToken");
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBaseOAuthUrl() {
        return baseOAuthUrl;
    }

    public String getScopes() {
        return scopes;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getSenderPin() {
        return senderPin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
