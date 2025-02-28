package com.adobe.cq.wcm.core.components.it.http;

import org.apache.sling.testing.clients.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Assert;
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
