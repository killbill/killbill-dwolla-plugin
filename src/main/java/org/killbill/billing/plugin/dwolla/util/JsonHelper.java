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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JsonHelper {

    /**
     * Convert json string into a java object
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T getObjectFromRequest(final String jsonString, final Class<T> clazz) throws PaymentPluginApiException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new PaymentPluginApiException("Exception during generation of the Object from JSON", e.getCause());
        }
    }

    /**
     * Convert servlet request (in json string format) into a java object
     *
     * @param request
     * @param clazz
     * @return
     */
    public static <T> T getObjectFromRequest(HttpServletRequest request, Class<T> clazz) throws PaymentPluginApiException {
        String jsonString = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            if(br != null){
                while ((line = br.readLine()) != null) {
                    jsonString += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaymentPluginApiException("Exception during generation of the Object from JSON", e.getCause());
        }
        return getObjectFromRequest(jsonString, clazz);
    }

    public static String getIdFromUrl(final String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
