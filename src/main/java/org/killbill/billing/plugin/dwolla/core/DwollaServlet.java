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

import com.google.common.base.Preconditions;
import org.killbill.billing.osgi.api.OSGIKillbill;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.plugin.core.PluginServlet;
import org.killbill.billing.plugin.dwolla.client.DwollaConfigProperties;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.billing.plugin.dwolla.util.JsonHelper;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.billing.tenant.api.TenantApiException;
import org.killbill.billing.tenant.api.TenantUserApi;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class DwollaServlet extends PluginServlet {

    private static final Logger logger = LoggerFactory.getLogger(DwollaServlet.class);

    public static final String X_KILLBILL_API_KEY = "X-Killbill-ApiKey";

    private DwollaDao dao;
    private DwollaConfigProperties dwollaConfigProperties;
    private OSGIKillbillLogService logService;
    private OSGIKillbill osgiKillbillAPI;

    public DwollaServlet(final DwollaDao dao, final DwollaConfigProperties dwollaConfigProperties, final OSGIKillbillLogService logService, final OSGIKillbill osgiKillbillAPI) {
        this.dao = dao;
        this.dwollaConfigProperties = dwollaConfigProperties;
        this.logService = logService;
        this.osgiKillbillAPI = osgiKillbillAPI;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("DWOLLA plugin running");
        buildOKResponse(null, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final DwollaTokenPair tokenPair = JsonHelper.getObjectFromRequest(req, DwollaTokenPair.class);

            Preconditions.checkNotNull(tokenPair, "Body must not be null");
            Preconditions.checkNotNull(tokenPair.getAccessToken(), "accessToken value must not be null");
            Preconditions.checkNotNull(tokenPair.getRefreshToken(), "refreshToken value must not be null");

            final String apiKey = req.getHeader(X_KILLBILL_API_KEY);
            final UUID tenantId = getTenantId(apiKey);
            dao.addTokens(tokenPair.getAccessToken(), tokenPair.getRefreshToken(), dwollaConfigProperties.getAccountId(), tenantId);
            buildOKResponse(null, resp);

        } catch (Exception e) {
            logService.log(LogService.LOG_ERROR, "Exception saving new token pair. Cause: " + e.getMessage());
            buildErrorResponse(e, resp);
        }
    }

    @Override
    protected void doPut(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final DwollaTokenPair tokenPair = JsonHelper.getObjectFromRequest(req, DwollaTokenPair.class);

            Preconditions.checkNotNull(tokenPair, "Body must not be null");
            Preconditions.checkNotNull(tokenPair.getAccessToken(), "accessToken value must not be null");
            Preconditions.checkNotNull(tokenPair.getRefreshToken(), "refreshToken value must not be null");

            final String apiKey = req.getHeader(X_KILLBILL_API_KEY);
            final UUID tenantId = getTenantId(apiKey);
            dao.updateTokens(tokenPair.getAccessToken(), tokenPair.getRefreshToken(), dwollaConfigProperties.getAccountId(), tenantId);
            buildOKResponse(null, resp);

        } catch (Exception e) {
            logService.log(LogService.LOG_ERROR, "Exception updating token pair. Cause: " + e.getMessage());
            buildErrorResponse(e, resp);
        }
    }

    public UUID getTenantId(String apiKey) throws PaymentPluginApiException {
        logService.log(LogService.LOG_INFO, "Accesing osgiKillbillAPI to get the TenantUserApi");
        TenantUserApi tenantUserApi = osgiKillbillAPI.getTenantUserApi();
        if (null != tenantUserApi && !apiKey.isEmpty()) {
            Tenant tenant = null;
            try {
                logService.log(LogService.LOG_INFO, "Accessing the TenantUserApi to get the Tenant using the ApiKey: " + apiKey);
                tenant = tenantUserApi.getTenantByApiKey(apiKey);
                if (null != tenant) {
                    logService.log(LogService.LOG_INFO, "Returning Tenant Id as result");
                    return tenant.getId();
                }
                else logService.log(LogService.LOG_ERROR, "Tenant object is null");
            } catch (TenantApiException e) {
                logService.log(LogService.LOG_ERROR, "TenantApiException. Cause: " + e.getMessage());
                e.printStackTrace();
                throw new PaymentPluginApiException("TenantApiException when trying to get Tenant by ApiKey", e.getCause());
            }
        }
        else logService.log(LogService.LOG_ERROR, "TenantUserApi is null or apiKey is Empty");
        throw new PaymentPluginApiException("Either TenantUserApi is null or apiKey is Empty", new RuntimeException());
    }
}
