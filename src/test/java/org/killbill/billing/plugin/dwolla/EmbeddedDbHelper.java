/*
 * Copyright 2015 Groupon, Inc
 *
 * Groupon licenses this file to you under the Apache License, version 2.0
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

package org.killbill.billing.plugin.dwolla;

import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.dwolla.dao.DwollaDao;
import org.killbill.commons.embeddeddb.mysql.MySQLStandaloneDB;

public class EmbeddedDbHelper {

    private static final String DDL_FILE_NAME = "ddl.sql";

    private static final EmbeddedDbHelper INSTANCE = new EmbeddedDbHelper();
    private MySQLStandaloneDB embeddedDB;

    public static EmbeddedDbHelper instance() {
        return INSTANCE;
    }

    public DwollaDao startDb() throws Exception {

        embeddedDB = new MySQLStandaloneDB("killbill");
        embeddedDB.initialize();
        embeddedDB.start();

        final String ddl = TestUtils.toString(DDL_FILE_NAME);
        embeddedDB.executeScript(ddl);
        embeddedDB.refreshTableNames();

        return new DwollaDao(embeddedDB.getDataSource());
    }

    public void resetDB() throws Exception {
        embeddedDB.cleanupAllTables();
    }

    public void stopDB() throws Exception {
        embeddedDB.stop();
    }
}
