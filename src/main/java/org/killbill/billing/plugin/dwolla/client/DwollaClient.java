package org.killbill.billing.plugin.dwolla.client;

import com.dwolla.java.sdk.Consts;
import com.dwolla.java.sdk.DwollaTypedBytes;
import com.dwolla.java.sdk.OAuthServiceSync;
import com.dwolla.java.sdk.requests.TokenRequest;
import com.dwolla.java.sdk.responses.TokenResponse;
import com.google.gson.Gson;
import io.swagger.client.ApiClient;
import retrofit.RestAdapter;

public class DwollaClient {

    private final DwollaConfigProperties configProperties;
    private final ApiClient client;

    public DwollaClient(final DwollaConfigProperties properties) {
        this.configProperties = properties;
        final TokenResponse accessToken = getApplicationToken();

        client = new ApiClient();
        client.setBasePath(configProperties.getBaseUrl());
        client.setAccessToken(accessToken.access_token);
    }

    public TokenResponse getUserToken(String code) {
        OAuthServiceSync oAuth = createOAuthService();
        return oAuth.getToken(new DwollaTypedBytes(new Gson(),
                new TokenRequest(configProperties.getClientId(), configProperties.getClientSecret(), Consts.Api.AUTHORIZATION_CODE, configProperties.getRedirectUrl(), code)));
    }

    public TokenResponse refreshToken() {
        OAuthServiceSync oAuth = createOAuthService();
        return oAuth.refreshToken(new DwollaTypedBytes(new Gson(),
                new TokenRequest(configProperties.getClientId(), configProperties.getClientSecret(), Consts.Api.REFRESH_TOKEN, configProperties.getRefreshToken())));
    }

    public TokenResponse getApplicationToken() {
        OAuthServiceSync oAuth = createOAuthService();
        return oAuth.getToken(new DwollaTypedBytes(new Gson(),
                new TokenRequest(configProperties.getClientId(), configProperties.getClientSecret(), "client_credentials", null)));
    }

    protected OAuthServiceSync createOAuthService() {
        return new RestAdapter
                .Builder()
                .setEndpoint(configProperties.getBaseOAuthUrl())
                .build()
                .create(OAuthServiceSync.class);
    }

}
