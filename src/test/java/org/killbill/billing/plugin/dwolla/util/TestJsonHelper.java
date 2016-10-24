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

import org.killbill.billing.plugin.dwolla.client.DwollaErrorResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class TestJsonHelper {

    private final static String apiErrorResponse =
            "{" +
                "\"code\":\"ValidationError\"," +
                "\"message\":\"Validation error(s) present. See embedded errors list for more details.\"," +
                "\"_embedded\":{" +
                                "\"errors\":[{" +
                                            "\"code\":\"Invalid\"," +
                                            "\"message\":\"Receiver cannot be the owner of the source funding source.\"," +
                                            "\"path\":\"/_links/destination/href\"," +
                                            "\"_links\":{}" +
                                            "}]" +
                                "}" +
            "}";

    @Test(groups = "fast")
    public void testParseApiErrorResponse() throws Exception {
        final DwollaErrorResponse request = JsonHelper.getObjectFromRequest(apiErrorResponse, DwollaErrorResponse.class);

        Assert.assertNotNull(request.get_embedded());

        final List<Map<String, Object>> errors = request.get_embedded().get("errors");
        Assert.assertNotNull(errors);
        Assert.assertEquals(errors.size(), 1);
        Assert.assertEquals(errors.get(0).get("code"), "Invalid");
        Assert.assertEquals(errors.get(0).get("message").toString(), "Receiver cannot be the owner of the source funding source.");
    }

}
