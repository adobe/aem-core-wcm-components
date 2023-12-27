/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.Header;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({ IgnoreOnCloud.class })
public class TableOfContentsFilterIT {

    @ClassRule
    public static final CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static CQClient author;
    static OsgiConsoleClient authorOsgiConsole;

    static Multimap<OsgiConsoleClient, String> osgiConfigurationsToDelete = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
    static String mappingEntry;
    static String cp;
    static int authorPort;

    @BeforeClass
    public static void beforeClass() throws ClientException, InterruptedException, TimeoutException {
        author = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        authorOsgiConsole = cqBaseClassRule.authorRule.getAdminClient(OsgiConsoleClient.class);

        // get the context path
        cp = cqBaseClassRule.authorRule.getConfiguration().getUrl().getPath() + "/";
        authorPort = cqBaseClassRule.authorRule.getConfiguration().getUrl().getPort();

        // enable the TableOfContentsFilter
        osgiConfigurationsToDelete.put(authorOsgiConsole, authorOsgiConsole.editConfiguration(
            "com.adobe.cq.wcm.core.components.internal.servlets.TableOfContentsFilter", null,
            Collections.singletonMap("enabled", Boolean.TRUE)));
        authorOsgiConsole.waitComponentRegistered(
            "com.adobe.cq.wcm.core.components.internal.servlets.TableOfContentsFilter",
            5000,
            1000);
    }

    @AfterClass
    public static void afterClass() throws ClientException {
        for (Map.Entry<OsgiConsoleClient, String> entry : osgiConfigurationsToDelete.entries()) {
            entry.getKey().deleteConfiguration(entry.getValue());
        }
    }

    /**
     * This test verifies that the TableOfContentsFilter does not interfere with the resourceType-based rewriter pipeline selection.
     * <p>
     * It creates a rewriter pipeline configuration specific to the {@code "core/wcm/tests/components/test-page"} resourceType adding the
     * {@link com.adobe.cq.wcm.core.components.it.support.TestTransformerFactory} to the pipeline. If everything works this transformer sets
     * a response header which the test assets.
     *
     * @throws ClientException
     */
    @Test
    public void testResourceTypeEnabledRewriterPipelineUsed() throws ClientException {
        String pipelineConfig = "/apps/core/config/rewriter/core-components-test-transformer";
        author.createNode(pipelineConfig, "nt:unstructured");

        try {
            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put("contentTypes", new String[] { "text/html" });
            properties.put("enabled", "true");
            properties.put("order", "2000");
            properties.put("generatorType", "htmlparser");
            properties.put("paths", new String[] { "/content" });
            properties.put("serializerType", "htmlwriter");
            // include the test transformer
            properties.put("transformerTypes", new String[] { "core-components-test-transformer" });
            // limit the rewriter pipeline to the test-page resourceType
            properties.put("resourceTypes", new String[] { "core/wcm/tests/components/test-page" });

            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String[]) {
                    author.setPropertyStringArray(pipelineConfig, entry.getKey(), Arrays.asList((String[]) value), 200);
                } else {
                    author.setPropertyString(pipelineConfig, entry.getKey(), (String) value, 200);
                }
            }

            // if the pipeline is used the response to GET /content/core-components/core-components-page should contain the
            // X-CoreComponents-TestTransformer response header
            SlingHttpResponse resp = author.doGet("/content/core-components/core-components-page.html", 200);
            Header expectedHeader = resp.getFirstHeader("X-CoreComponents-TestTransformer");
            assertNotNull(expectedHeader);
            assertEquals("true", expectedHeader.getValue());
        } finally {
            author.deletePath(pipelineConfig);
        }
    }

}
