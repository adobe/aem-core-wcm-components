/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.http;

import java.util.LinkedList;
import java.util.List;

import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.util.JsonUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class SanityCheckTest {

    private static final String URL_HC_INACTIVE_BUNDLES = "/system/sling/monitoring/mbeans/org/apache/sling/healthcheck/HealthCheck/inactiveBundles";
    private static final String URL_PACKAGE_LIST = "/crx/packmgr/list.jsp";

    @ClassRule
    public static CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    static CQClient adminAuthor;
    static CQClient adminPublish;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testBundlesAuthor() throws ClientException {
        testBundlesInternal(adminAuthor);
    }

    @Test
    public void testPackagesAuthor() throws ClientException {
        testPackagesInternal(adminAuthor);
    }

    @Test
    public void testBundlesPublish() throws ClientException {
        testBundlesInternal(adminPublish);
    }

    @Test
    public void testPackagesPublish() throws ClientException {
        testPackagesInternal(adminPublish);
    }

    private void testBundlesInternal(CQClient adminClient) throws ClientException {
        JsonNode inactiveBundles = adminClient.doGetJson(URL_HC_INACTIVE_BUNDLES, HTTP_OK);
        if (!inactiveBundles.get("ok").getBooleanValue()) {
            fail("Expected all bundles ok\n" + inactiveBundles.get("log"));
        }
    }

    private void testPackagesInternal(CQClient adminClient) throws ClientException {
        JsonNode packages = JsonUtils.getJsonNodeFromString(adminClient.doGet(URL_PACKAGE_LIST, HTTP_OK).getContent());
        List<String> unresolvedPackages = new LinkedList<>();
        packages.get("results").getElements().forEachRemaining(res -> {
            if (!res.get("resolved").getBooleanValue()) {
                unresolvedPackages.add(res.get("pid").getTextValue());
            }
        });
        if (unresolvedPackages.size() > 0) {
            fail("Expected all packages are resolved\nUnresolved packages: " + unresolvedPackages);
        }
    }
}
