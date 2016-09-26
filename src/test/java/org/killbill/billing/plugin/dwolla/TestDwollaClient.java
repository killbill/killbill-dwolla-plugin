
package org.killbill.billing.plugin.dwolla;

import com.dwolla.java.sdk.responses.TokenResponse;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AccountsApi;
import io.swagger.client.api.CustomersApi;
import io.swagger.client.api.FundingsourcesApi;
import io.swagger.client.api.TransfersApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.model.*;
import org.killbill.billing.plugin.dwolla.client.DwollaClient;
import org.killbill.billing.plugin.dwolla.client.DwollaConfigProperties;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Properties;

public class TestDwollaClient {

    private static final String PROPERTY_PREFIX = "org.killbill.billing.plugin.dwolla.";

    public final static String SCOPES = "Send|AccountInfoFull|Funding";
    public final static String REDIRECT_URI = "http://localhost:4567/callback";
    public final static String CLIENT_ID = "SC9t2DfUDxx0JFI80y9DeOL1ZduXEEXZbC9qCdJDr5XGhfLKcr";
    public final static String CLIENT_SECRET = "xPfIZUxEJWviFoVuyzPNTgbmYtjLBacOzwki4yNGeHczNK2xh5";
    public final static String SENDER_PIN = "1234";

    public final static String ACCESS_TOKEN = "SYLlmJjpkPBMMi2d1RUhGmpXKLAHQ0D6ewX7TqpWI2LYLUGUYm";
    public final static String URL = "https://api-uat.dwolla.com";

    private DwollaClient client;

    @BeforeTest
    public void beforeMethod() {

        Properties props = new Properties();
        props.setProperty(PROPERTY_PREFIX + "baseUrl", URL);
        props.setProperty(PROPERTY_PREFIX + "baseOAuthUrl", "https://uat.dwolla.com/oauth/v2");
        props.setProperty(PROPERTY_PREFIX + "clientId", CLIENT_ID);
        props.setProperty(PROPERTY_PREFIX + "clientSecret", CLIENT_SECRET);
        props.setProperty(PROPERTY_PREFIX + "refreshToken", "2VAYKXwCrlh6RlINcYriHYd73JNec3fLNtxVGs3iZdamd1fxVg");
//        props.put("redirectUrl", "");
//        props.put("redirectUrl", "");
//        props.put("redirectUrl", "");

        DwollaConfigProperties properties = new DwollaConfigProperties(props);

        client = new DwollaClient(properties);
    }

    @Test(groups = "slow")
    public void testNewClient() throws ApiException {
        final TokenResponse token = client.getApplicationToken();
        Assert.assertNotNull(token);
    }

    @Test(groups = "slow")
    public void testClient() throws ApiException {

        ApiClient a = new ApiClient();
        a.setBasePath(URL);
        a.setAccessToken(ACCESS_TOKEN);



        CustomersApi c = new CustomersApi(a);
        final CustomerListResponse list = c.list(null, null, null);
        Assert.assertNotNull(list);

        AccountsApi api = new AccountsApi(a);
        final AccountInfo accountInfo = api.id("4218a933-95c0-433c-9d66-efe833061a8d");
        Assert.assertNotNull(accountInfo);

//        CreateCustomer myNewCust = new CreateCustomer();
//        myNewCust.setEmail("name@mail.com");
//        myNewCust.setFirstName("First");
//        myNewCust.setLastName("Last");

//        Unit$ r = c.create(myNewCust);
//
//        System.out.println(r.getLocationHeader());

//        TransfersApi transfersApi;
//        TransferRequestBody body;
//        transfersApi.create(body);

        FundingsourcesApi fundingsourcesApi = new FundingsourcesApi(a);
        final FundingSource fundingSource = fundingsourcesApi.id("");

    }
}
