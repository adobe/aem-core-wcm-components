/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import static org.junit.Assert.assertEquals;

@Category({IgnoreOnCloud.class})
public class SeoIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static CQClient publish;
    static OsgiConsoleClient publishOsgiConsole;

    static Multimap<OsgiConsoleClient, String> osgiConfigurationsToDelete = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
    static String mappingEntry;
    static String cp;
    static int publisherPort;

    @BeforeClass
    public static void beforeClass() throws ClientException, InterruptedException, TimeoutException {
        publish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
        publishOsgiConsole = cqBaseClassRule.publishRule.getAdminClient(OsgiConsoleClient.class);

        // get the context path
        cp = cqBaseClassRule.publishRule.getConfiguration().getUrl().getPath() + "/";
        publisherPort = cqBaseClassRule.publishRule.getConfiguration().getUrl().getPort();

        // enable the LanguageNavigationSiteRootSelectionStrategy
        osgiConfigurationsToDelete.put(publishOsgiConsole, publishOsgiConsole.editConfiguration(
            "com.adobe.cq.wcm.core.components.internal.services.seo.LanguageNavigationSiteRootSelectionStrategy", null,
            Collections.singletonMap("configured", Boolean.TRUE)));
        publishOsgiConsole.waitComponentRegistered(
            "com.adobe.cq.wcm.core.components.internal.services.seo.LanguageNavigationSiteRootSelectionStrategy",
            5000,
            1000);

        // enable sitemap scheduler to build sitemaps every 10s
        osgiConfigurationsToDelete.put(publishOsgiConsole, publishOsgiConsole.editConfiguration(
            "[Temporary PID replaced by real PID upon save]",
            "org.apache.sling.sitemap.impl.SitemapScheduler",
            ImmutableMap.of(
                "scheduler.name", "test",
                "scheduler.expression", "*/10 * * * * ?"
            )));

        // create a mapping on publish to validate proper externalisation
        mappingEntry = "/etc/map/http/integrationtest.local." + cqBaseClassRule.publishRule.getConfiguration().getUrl().getPort();
        publish.createNode(mappingEntry,"nt:unstructured");
        publish.setPropertyStringArray(
            mappingEntry,
            "sling:internalRedirect",
            Arrays.asList("/content/core-components/seo-site", "/"),
            HttpStatus.SC_OK);
    }

    @AfterClass
    public static void afterClass() throws ClientException {
        for (Map.Entry<OsgiConsoleClient, String> entry : osgiConfigurationsToDelete.entries()) {
            entry.getKey().deleteConfiguration(entry.getValue());
        }
        publish.deletePath(mappingEntry, HttpStatus.SC_OK);
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testRobotsTagRenderedToPage() throws ClientException {
        String content = publish.doGet("/content/core-components/seo-site/gb/en/child.html", 200).getContent();
        MatcherAssert.assertThat(content, CoreMatchers.not(CoreMatchers.containsString("<meta name=\"robots\"")));
        content = publish.doGet("/content/core-components/seo-site/gb/en/noindex-child.html", 200).getContent();
        MatcherAssert.assertThat(content, CoreMatchers.containsString("<meta name=\"robots\" content=\"noindex\"/>"));
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testCanonicalLinkRenderedToPage() throws ClientException {
        String content = publish.doGet("/content/core-components/seo-site/gb/en/child.html", 200).getContent();
        MatcherAssert.assertThat(content, CoreMatchers.containsString(
            "<link rel=\"canonical\" href=\"http://integrationtest.local:" + publisherPort + cp + "gb/en/child.html\"/>"));
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testCanonicalLinkNotRenderedToPageNoIndex() throws ClientException {
        String content = publish.doGet("/content/core-components/seo-site/gb/en/noindex-child.html", 200).getContent();
        MatcherAssert.assertThat(content, CoreMatchers.not(CoreMatchers.containsString("<link rel=\"canonical\"")));
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testCustomCanonicalLinkNotRenderedToPage() throws ClientException {
        try {
            publish.setPageProperty("/content/core-components/seo-site/gb/en/child.html", "cq:canonicalUrl", "https://example.com", 200);
            String content = publish.doGet("/content/core-components/seo-site/gb/en/child.html", 200).getContent();
            MatcherAssert.assertThat(content, CoreMatchers.containsString("<link rel=\"canonical\" href=\"https://example.com\"/>"));
        } finally {
            publish.setPageProperty("/content/core-components/seo-site/gb/en/child.html", "cq:canonicalUrl", "", 200);
        }
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testLanguageAlternatesRenderedToPage() throws ClientException {
        String content = publish.doGet("/content/core-components/seo-site/gb/en/child.html", 200).getContent();
        MatcherAssert.assertThat(content, CoreMatchers.not(CoreMatchers.containsString(
            "<link rel=\"alternate\" hreflang=\"en\" href=\"http://integrationtest.local:" + publisherPort + cp + "master/en/child.html\"/>")));
        MatcherAssert.assertThat(content, CoreMatchers.containsString(
            "<link rel=\"alternate\" hreflang=\"en-GB\" href=\"http://integrationtest.local:" + publisherPort + cp + "gb/en/child.html\"/>"));
        MatcherAssert.assertThat(content, CoreMatchers.containsString(
            "<link rel=\"alternate\" hreflang=\"en-US\" href=\"http://integrationtest.local:" + publisherPort + cp + "us/en/child.html\"/>"));
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testLanguageAlternatesNotRenderedToPageNotCanonical() throws ClientException {
        try {
            publish.setPageProperty("/content/core-components/seo-site/gb/en/child.html", "cq:canonicalUrl", "https://example.com", 200);
            String content = publish.doGet("/content/core-components/seo-site/gb/en/child.html", 200).getContent();
            MatcherAssert.assertThat(content, CoreMatchers.not(CoreMatchers.containsString("<link rel=\"alternate\"")));
        } finally {
            publish.setPageProperty("/content/core-components/seo-site/gb/en/child.html", "cq:canonicalUrl", "", 200);
        }
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testSitemapAndSitemapIndexGeneration() throws ClientException, InterruptedException, TimeoutException {
        try {
            publish.setPageProperty("/content/core-components/seo-site/gb/en", "sling:sitemapRoot", "true", HttpStatus.SC_OK);
            publish.waitExists("/var/sitemaps/content/core-components/seo-site/gb/en/sitemap.xml", 30000, 5000);

            String timeRegex = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})Z";
            String expectedSitemapIndex = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
                + "<sitemap>"
                + "<loc>http://integrationtest.local:" + publisherPort + cp + "gb/en.sitemap.xml</loc>"
                + "<lastmod>test</lastmod>"
                + "</sitemap>"
                + "</sitemapindex>";
            String index = publish.doGet("/content/core-components/seo-site/gb/en.sitemap-index.xml", HttpStatus.SC_OK)
                .getContent()
                .replaceAll(timeRegex, "test");
            assertEquals(expectedSitemapIndex, index);

            String xmlnsRegex = "\\s?xmlns(:[^=]+)?=\"[^\"]+\"";
            String expectedSitemap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<urlset>"
                + "<url>"
                + "<loc>http://integrationtest.local:" + publisherPort + cp + "gb/en.html</loc>"
                + "<xhtml:link rel=\"alternate\" hreflang=\"en-GB\" href=\"http://integrationtest.local:" + publisherPort + cp + "gb/en.html\"/>"
                + "<xhtml:link rel=\"alternate\" hreflang=\"en-US\" href=\"http://integrationtest.local:" + publisherPort + cp + "us/en.html\"/>"
                + "</url>"
                + "<url>"
                + "<loc>http://integrationtest.local:" + publisherPort + cp + "gb/en/child.html</loc>"
                + "<xhtml:link rel=\"alternate\" hreflang=\"en-GB\" href=\"http://integrationtest.local:" + publisherPort + cp + "gb/en/child.html\"/>"
                + "<xhtml:link rel=\"alternate\" hreflang=\"en-US\" href=\"http://integrationtest.local:" + publisherPort + cp + "us/en" +
                    "/child.html\"/>"
                + "</url>"
                + "</urlset>";
            String sitemap = publish.doGet("/content/core-components/seo-site/gb/en.sitemap.xml", HttpStatus.SC_OK)
                .getContent()
                .replaceAll(xmlnsRegex,"");
            assertEquals(expectedSitemap, sitemap);
        } finally {
            try {
                publish.setPageProperty("/content/core-components/seo-site/gb/en", "sling:sitemapRoot", "false", HttpStatus.SC_OK);
                publish.deletePath("/var/sitemaps/content/core-components/seo-site/gb/en/sitemap.xml");
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
