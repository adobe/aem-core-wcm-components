/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025 Adobe
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

import org.apache.sling.testing.clients.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TeaserIT {
    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static CQClient adminPublish;

    @BeforeClass
    public static void beforeClass() {
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testNoNestedLinkElements() throws ClientException {
        String content = adminPublish.doGet("/content/core-components/teaser/teaser-links.html", 200).getContent();
        Elements html = Jsoup.parse(content).select("html");
        assertNotNull(html);
        // Expect no nesting of <a> tags
        assertEquals("Expected no nested links", 0, html.select("a a").size());
        // Expect only teaser with not CTAs and not content to have image link
        assertEquals("Mismatched image links", 1, html.select("a > img").size());
        // Expect only teaser with no CTAs and content to have global link
        assertEquals("Mismatched teaser content links", 1, html.select("a .cmp-teaser__content").size());
        // Expect teasers with CTAs to have CTA links
        assertEquals("Mismatched teaser content links", 2, html.select("a.cmp-teaser__action-link").size());
    }
}
