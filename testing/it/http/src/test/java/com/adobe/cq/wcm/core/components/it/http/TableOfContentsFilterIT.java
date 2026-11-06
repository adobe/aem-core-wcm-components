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
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.http.Header;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.VersioningClient;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
            Collections.singletonMap("enabled", "true")));
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

    @Test
    public void testTableOfContentsRenderedInVersionHistory() throws ClientException {
        String page = author.createPage("toc-" + UUID.randomUUID(), "Table of contents test", "/content/core-components",
            "/conf/core-components/settings/wcm/templates/simple-template").getSlingPath();
        String previewPage = null;
        try {
            String container = page + "/jcr:content/root/container";
            String toc = container + "/toc";
            author.createNode(toc, "nt:unstructured");
            author.setPropertyString(toc, "sling:resourceType", "core/wcm/components/tableofcontents/v1/tableofcontents");
            author.setPropertyString(toc, "startLevel", "h2");
            author.setPropertyString(toc, "stopLevel", "h2");

            String title = container + "/title";
            author.createNode(title, "nt:unstructured");
            author.setPropertyString(title, "sling:resourceType", "core/wcm/components/title/v3/title");
            author.setPropertyString(title, "jcr:title", "Versioned heading");
            author.setPropertyString(title, "type", "h2");

            assertTableOfContents(page, "Versioned heading");

            author.createVersion(page, "Table of contents regression test", "toc-test");
            VersioningClient versioning = cqBaseClassRule.authorRule.getAdminClient(VersioningClient.class);
            String versionId = versioning.getPageVersions(page).get(0).path("id").asText();
            assertTrue("Expected a saved page version", !versionId.isEmpty());

            author.setPropertyString(title, "jcr:title", "Current heading");
            assertTableOfContents(page, "Current heading");

            String previewPath = author.doPost("/mnt/overlay/wcm/core/content/sites/versionhistory/_jcr_content.txt",
                FormEntityBuilder.create().addParameter("versionId", versionId).build(), 200).getContent().trim();
            assertTrue("Expected a version-history preview page: " + previewPath, previewPath.startsWith("/tmp/versionhistory/"));
            previewPage = previewPath;
            assertTableOfContents(previewPage, "Versioned heading");
        } finally {
            try {
                if (previewPage != null) {
                    author.deletePath(previewPage);
                }
            } finally {
                author.deletePage(new String[] {page}, true, false);
            }
        }
    }

    private void assertTableOfContents(String page, String expectedHeading) throws ClientException {
        Document document = Jsoup.parse(author.doGet(page + ".html", 200).getContent());
        assertEquals("The filter must replace the TOC placeholder", 0, document.select(".cmp-toc__placeholder").size());
        assertEquals("Expected one generated table of contents", 1, document.select(".cmp-toc__content").size());
        assertEquals("Expected one TOC heading link", 1, document.select(".cmp-toc__content a").size());
        Element link = document.select(".cmp-toc__content a").first();
        assertEquals(expectedHeading, link.text());
        assertTrue("Expected a non-empty fragment link", link.attr("href").startsWith("#") && link.attr("href").length() > 1);
        Element heading = document.getElementById(link.attr("href").substring(1));
        assertNotNull("The TOC link must point to a rendered heading", heading);
        assertEquals("h2", heading.tagName());
        assertEquals(expectedHeading, heading.text());
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
