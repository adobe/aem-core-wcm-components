/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ComponentsIT {

    @ClassRule
    public static final CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();

    static CQClient adminAuthor;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testTeaser() throws ClientException, IOException {
        String content = adminAuthor.doGet("/content/core-components/teaser.html", 200).getContent();
        Document document = parse(content);

        testComponent(document, ".teaser.teaser-v1", 0, "/components/teaser-v1-with-link-to-asset.html");
        testComponent(document, ".teaser.teaser-v1", 1, "/components/teaser-v1-with-cta-to-asset.html");
        testComponent(document, ".teaser.teaser-v2", 0, "/components/teaser-v2-with-link-to-asset.html");
        testComponent(document, ".teaser.teaser-v2", 1, "/components/teaser-v2-with-cta-to-asset.html");
    }

    private void testComponent(Document actualDocument, String selector, int selectorSetIndex, String expectation) throws IOException {
        String expected = IOUtils.resourceToString(expectation, StandardCharsets.UTF_8);
        Document expectedDocument = parse(expected);

        Element expectedElement = expectedDocument.body().children().first();
        Element actualElement = actualDocument.select(selector).get(selectorSetIndex);

        assertNotNull(selector + " did not match any element in the page", actualElement);
        assertEquals(selector + " does not match " + expectation, expectedElement.toString(), actualElement.toString());
    }

    private Document parse(String content) {
        // normalize date times
        content = content.replaceAll("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(.\\d{2,4})?", "0000-00-00T00:00:00");

        Document document = Jsoup.parse(content);
        removeNoise(document.select("html").first());
        return document;
    }

    private void removeNoise(Node element) {
        for (int i = 0; i < element.childNodeSize(); ) {
            Node child = element.childNode(i);
            // remove nodes that are: comments, cq tags
            if (StringUtils.equalsAny(child.nodeName(), "#comment", "cq")) {
                child.remove();
                continue;
            }
            // remove empty text nodes
            if (StringUtils.equals(child.nodeName(), "#text") && StringUtils.isBlank(((TextNode) child).text())) {
                child.remove();
                continue;
            }
            // normalize img src attributes
            if (StringUtils.equals(child.nodeName(), "img") || StringUtils.equals(child.attr("data-cmp-is"), "image")) {
                for (String attr : new String[] { "src", "data-cmp-src" }) {
                    String src = child.attr(attr);
                    if (StringUtils.isNotEmpty(src)) {
                        src = src.replaceAll("/\\d+/", "/0/");
                        child.attr(attr, src);
                    }
                }
            }

            // recurse
            removeNoise(child);
            i++;
        }
    }
}
