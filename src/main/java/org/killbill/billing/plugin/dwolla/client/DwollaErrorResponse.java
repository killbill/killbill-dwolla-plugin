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

import java.util.List;
import java.util.Map;

public class DwollaErrorResponse {

    private String code;
    private String message;
    private Map<String, List<Map<String, Object>>> _embedded;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<Map<String, Object>>> get_embedded() {
        return _embedded;
    }

    public void set_embedded(Map<String, List<Map<String, Object>>> _embedded) {
        this._embedded = _embedded;
    }

    public class DwollaErrorDetail {
        private String code;
        private String message;
        private String path;
        private Object _links;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Object get_links() {
            return _links;
        }

        public void set_links(Object _links) {
            this._links = _links;
        }
    }

}
