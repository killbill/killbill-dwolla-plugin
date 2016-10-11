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

import com.dwolla.java.sdk.Consts;
import com.dwolla.java.sdk.DwollaTypedBytes;
import com.dwolla.java.sdk.OAuthServiceSync;
import com.dwolla.java.sdk.requests.TokenRequest;
import com.dwolla.java.sdk.responses.TokenResponse;
import com.google.gson.Gson;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.RootApi;
import io.swagger.client.model.CatalogResponse;
import retrofit.RestAdapter;

public class DwollaClient {

    private final DwollaConfigProperties configProperties;
    private final ApiClient client;

    public DwollaClient(final DwollaConfigProperties properties) {
        this.configProperties = properties;
        final TokenResponse accessToken = getApplicationToken();

        client = new ApiClient();
        client.setBasePath(configProperties.getBaseUrl());
    }

    public TokenResponse refreshToken(final String refreshToken) {
        OAuthServiceSync oAuth = createOAuthService();
        return oAuth.refreshToken(new DwollaTypedBytes(new Gson(),
                new TokenRequest(configProperties.getClientId(), configProperties.getClientSecret(), Consts.Api.REFRESH_TOKEN, refreshToken)));
    }

    public TokenResponse getApplicationToken() {
        OAuthServiceSync oAuth = createOAuthService();
        return oAuth.getToken(new DwollaTypedBytes(new Gson(),
                new TokenRequest(configProperties.getClientId(), configProperties.getClientSecret(), "client_credentials", null)));
    }

    public CatalogResponse getRootInfo() throws ApiException {
        final RootApi rootApi = new RootApi(client);
        return rootApi.root();
    }

    protected OAuthServiceSync createOAuthService() {
        return new RestAdapter
                .Builder()
                .setEndpoint(configProperties.getBaseOAuthUrl())
                .build()
                .create(OAuthServiceSync.class);
    }

    public ApiClient getClient() {
        return client;
    }

    public DwollaConfigProperties getConfigProperties() {
        return configProperties;
    }
}
