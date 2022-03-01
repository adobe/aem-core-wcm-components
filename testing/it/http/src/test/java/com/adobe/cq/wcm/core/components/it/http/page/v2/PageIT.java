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
package com.adobe.cq.wcm.core.components.it.http.page.v2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.testing.clients.ClientException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.assertion.GraniteAssert;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.wcm.core.components.it.http.IgnoreOn64;
import com.adobe.cq.wcm.core.components.it.http.IgnoreOn65;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PageIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static CQClient adminAuthor;
    static CQClient adminPublish;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testBrandSlug() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/simple-page.html", 200).getContent();
        GraniteAssert.assertRegExFind(content, "<title>Simple Page \\| Core Components</title>");
        // validate that the sub page has also the brand slug without specify it.
        content = adminAuthor.doGet("/content/core-components/simple-page/simple-subpage.html", 200).getContent();
        GraniteAssert.assertRegExFind(content, "<title>Simple SubPage \\| Core Components</title>");
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testPWAProperties() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/simple-page.html", 200).getContent();
        // Manifest
        GraniteAssert.assertRegExFind(content, "<link rel=\"manifest\" href=\".*/content/foo/us/en/manifest.webmanifest\" crossorigin=\"use-credentials\"/>");
        // Theme Color
        GraniteAssert.assertRegExFind(content, "<meta name=\"theme-color\" content=\"#FF851B\"/>");
        // Apple Touch Icon
        GraniteAssert.assertRegExFind(content, "<link rel=\"apple-touch-icon\" href=\".*/content/dam/foo/pwa-logo.png\"/>");
        // Style sheet for messages
        GraniteAssert.assertRegExFind(content, "<link rel=\"stylesheet\" href=\".*/etc.clientlibs/core/wcm/components/page/v2/page/clientlibs/site/pwa.*.css\" type=\"text/css\">");
        // Path to service worker
        GraniteAssert.assertRegExFind(content, "<meta name=\"cq:sw_path\" content=\".*/core-components.simple-pagesw.js\"/>");
        // Reference to script that registers the service worker
        GraniteAssert.assertRegExFind(content, "<script src=\".*/etc.clientlibs/core/wcm/components/page/v2/page/clientlibs/site/pwa.*.js\"></script>");

        // validate that the sub page also has the PWA properties based on the parent.
        content = adminAuthor.doGet("/content/core-components/simple-page/simple-subpage.html", 200).getContent();
        GraniteAssert.assertRegExFind(content, "<link rel=\"manifest\" href=\".*/content/foo/us/en/manifest.webmanifest\" crossorigin=\"use-credentials\"/>");
        GraniteAssert.assertRegExFind(content, "<meta name=\"theme-color\" content=\"#FF851B\"/>");
        GraniteAssert.assertRegExFind(content, "<link rel=\"apple-touch-icon\" href=\".*/content/dam/foo/pwa-logo.png\"/>");
        GraniteAssert.assertRegExFind(content, "<link rel=\"stylesheet\" href=\".*/etc.clientlibs/core/wcm/components/page/v2/page/clientlibs/site/pwa.*.css\" type=\"text/css\">");
        GraniteAssert.assertRegExFind(content, "<meta name=\"cq:sw_path\" content=\".*/core-components.simple-pagesw.js\"/>");
        GraniteAssert.assertRegExFind(content, "<script src=\".*/etc.clientlibs/core/wcm/components/page/v2/page/clientlibs/site/pwa.*.js\"></script>");
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testServiceWorkerConfiguration() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/simple-page.sw.js", 200).getContent();
        Pattern swconfigPattern = Pattern.compile("const swconfig = (?<swconfigjson>\\{.*})", Pattern.MULTILINE);
        Matcher swconfigMatcher= swconfigPattern.matcher(content);
        assertTrue(swconfigMatcher.find());
        Gson gson = new Gson();
        JsonObject swconfigJson = gson.fromJson(swconfigMatcher.group("swconfigjson"), JsonObject.class);
        assertNotNull(swconfigJson);

        // Check caching strategy
        assertEquals("staleWhileRevalidate", swconfigJson.get("pwaCachestrategy").getAsString());

        // Check precache / warm cache items
        JsonArray pwaPrecache = swconfigJson.get("pwaPrecache").getAsJsonArray();
        assertNotNull(pwaPrecache);
        assertEquals(3, pwaPrecache.size());
        assertTrue(containsMatchingRegex(pwaPrecache, "/content/dam/foo/pwa-logo.png"));
        assertTrue(containsMatchingRegex(pwaPrecache, "/content/foo/us/en.html"));
        assertTrue(containsMatchingRegex(pwaPrecache, "/content/foo/us/en/manifest.webmanifest"));

        // Check for cache on first use items
        assertEquals("http://fonts.gstatic.com", swconfigJson.get("pwaCachingpaths").getAsJsonArray().getAsString());

        // Check for clientlibs and static resources
        JsonArray pwaOfflineClientlibs = swconfigJson.get("pwaOfflineClientlibs").getAsJsonArray();
        assertNotNull(pwaOfflineClientlibs);
        assertEquals(2, pwaOfflineClientlibs.size());
        assertTrue(containsMatchingRegex(pwaOfflineClientlibs, ".*/etc.clientlibs/core/wcm/components/page/v2/page/clientlibs/site/pwa.*.css"));
        assertTrue(containsMatchingRegex(pwaOfflineClientlibs, ".*/etc.clientlibs/core/wcm/components/page/v2/page/clientlibs/site/pwa.*.js"));
    }

    boolean containsMatchingRegex(JsonArray jsonArray, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        for (JsonElement jsonElement : jsonArray) {
            if (pattern.matcher(jsonElement.getAsString()).find()) {
                return true;
            }
        }
        return false;
    }
}
