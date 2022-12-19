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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ComponentsIT {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentsIT.class);
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final List<NameValuePair> WCMMODE_DISABLED = Collections.singletonList(new BasicNameValuePair("wcmmode", "disabled"));

    @ClassRule
    public static final CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();

    static CQClient adminAuthor;
    static String cp;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        // get the context path
        cp = cqBaseClassRule.authorRule.getConfiguration().getUrl().getPath();
        // configure the JSON_MAPPER to serialize json data attributes
        JSON_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    @Test
    public void testTeaser() throws ClientException, IOException {
        String content = adminAuthor.doGet("/content/core-components/teaser.html", 200).getContent();
        Document document = parse(content);

        new ComponentTest(document)
            .select(".teaser.teaser-v1", 0).expect("teaser-v1-with-link-to-asset.html")
            .select(".teaser.teaser-v1", 1).expect("teaser-v1-with-cta-to-asset.html")
            .select(".teaser.teaser-v2", 0).expect("teaser-v2-with-link-to-asset.html")
            .select(".teaser.teaser-v2", 1).expect("teaser-v2-with-cta-to-asset.html");
    }

    @Test
    public void testEmbed() throws ClientException, IOException {
        String content = adminAuthor.doGet("/content/core-components/embed.html", WCMMODE_DISABLED, 200).getContent();
        Document document = parse(content);

        new ComponentTest(document)
            // fix the origin parameter in the expected documents by setting them to the adminAuthor url
            .withExpectationProcessor(expectedDocument -> {
                BasicNameValuePair origin = new BasicNameValuePair("origin", StringUtils.removeEnd(adminAuthor.getUrl().toString(), "/"));
                String encodedOrigin = URLEncodedUtils.format(Collections.singleton(origin), StandardCharsets.UTF_8);
                for (Element iframe : expectedDocument.select("iframe")) {
                    String src = iframe.attr("src");
                    src = src.replace("origin=http%3A%2F%2Flocalhost%3A4502", encodedOrigin);
                    iframe.attr("src", src);
                }
            })
            .select(".embed.embed-v1", 0).expect("embed-v1-youtube-defaults.html")
            .select(".embed.embed-v1", 1).expect("embed-v1-youtube-fixed.html")
            .select(".embed.embed-v1", 2).expect("embed-v1-youtube-responsive.html")
            .select(".embed.embed-v1", 3).expect("embed-v1-url-youtube.html")
            .select(".embed.embed-v1", 4).expect("embed-v1-url-trailing-whitespace.html")
            .select(".embed.embed-v2", 0).expect("embed-v2-youtube-defaults.html")
            .select(".embed.embed-v2", 1).expect("embed-v2-youtube-fixed.html")
            .select(".embed.embed-v2", 2).expect("embed-v2-youtube-responsive.html")
            .select(".embed.embed-v2", 3).expect("embed-v2-url-youtube.html")
            .select(".embed.embed-v2", 4).expect("embed-v2-url-trailing-whitespace.html");
    }

    @Test
    public void testPdfViewer() throws ClientException, IOException {
        String content = adminAuthor.doGet("/content/core-components/pdfviewer.html", 200).getContent();
        Document document = parse(content);

        new ComponentTest(document)
            .select(".pdfviewer.pdfviewer-v1", 0).expect("pdfviewer-v1-empty.html")
            .select(".pdfviewer.pdfviewer-v1", 1).expect("pdfviewer-v1-defaults.html");
    }

    @Test
    public void testList() throws ClientException, IOException {
        String content = adminAuthor.doGet("/content/core-components/list.html", 200).getContent();
        Document document = parse(content);

        new ComponentTest(document)
            .select(".list.list-v3", 0).expect("list-v3-mixed-pages.html")
            .select(".list.list-v3", 1).expect("list-v3-mixed-pages-linked.html")
            .select(".list.list-v3", 2).expect("list-v3-mixed-pages-and-links.html")
            .select(".list.list-v3", 3).expect("list-v3-mixed-pages-linked-description-modified.html")
            .select(".list.list-v3", 4).expect("list-v3-mixed-pages-linked-description-modifieddate-teaser.html")
            .select(".list.list-v3", 5).expect("list-v3-mixed-pages-empty.html");
    }

    @Test
    public void testSeparator() throws ClientException, IOException {
        String content = adminAuthor.doGet("/content/core-components/separator.html", 200).getContent();
        Document document = parse(content);

        new ComponentTest(document)
            .select(".separator.separator-v1", 0).expect("separator-v1-defaults.html")
            .select(".separator.separator-v1", 1).expect("separator-v1-decorative.html");
    }

    private class ComponentTest {

        private Document actualDocument;
        private Consumer<Document> expectationProcessor;
        private String selector;
        private int selectorSetIndex;

        ComponentTest(Document actualDocument) {
            this.actualDocument = actualDocument;
        }

        ComponentTest withExpectationProcessor(Consumer<Document> processor) {
            this.expectationProcessor = processor;
            return this;
        }

        ComponentTest select(String selector, int selectorSetIndex) {
            this.selector = selector;
            this.selectorSetIndex = selectorSetIndex;
            return this;
        }

        ComponentTest expect(String expectation) throws IOException {
            String expected = IOUtils.resourceToString("/components/" + expectation, StandardCharsets.UTF_8);
            Document expectedDocument = parse(expected);

            if (expectationProcessor != null) {
                expectationProcessor.accept(expectedDocument);
            }

            Element expectedElement = expectedDocument.body().children().first();
            Element actualElement = actualDocument.select(selector).get(selectorSetIndex);
            assertNotNull(selector + " did not match any element in the page", actualElement.toString());
            assertEquals(selector + " does not match " + expectation, expectedElement.toString(), actualElement.toString());
            return this;
        }
    }

    private static Document parse(String content) {
        // normalize date times
        content = content.replaceAll("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(.\\d{2,4})?", "0000-00-00T00:00:00");

        Document document = Jsoup.parse(content);
        removeNoise(document.select("html").first());
        return document;
    }

    /**
     * Removes noise from a given Node by traversing the structure and removing or processing attributes so that a reproducible output is
     * generated.
     *
     * @param element
     */
    private static void removeNoise(Node element) {
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
            // remove timestamps from img src
            for (String attr : new String[] { "src", "data-cmp-src" }) {
                String value = child.attr(attr);
                if (StringUtils.isNotEmpty(value)) {
                    value = value.replaceAll("/\\d+/", "/0/");
                    child.attr(attr, value);
                }
            }
            // prepend context path if needed
            if (StringUtils.isNotEmpty(cp)) {
                for (String attr : new String[] { "src", "data-cmp-src", "href" }) {
                    String value = child.attr(attr);
                    if (StringUtils.startsWith(value, "/") && !StringUtils.startsWith(value, cp + '/')) {
                        value = cp + value;
                        child.attr(attr, value);
                    }
                }
            }
            // normalize json data attributes
            for (String attr : new String[] { "data-cmp-data-layer" }) {
                String stringValue = child.attr(attr);
                if (StringUtils.isNotEmpty(stringValue)) {
                    try {
                        JsonNode treeNode = JSON_MAPPER.readTree(stringValue);
                        Map<String, Object> value = JSON_MAPPER.treeToValue(treeNode, Map.class);
                        value = removeNoise(value);
                        String processedJson = JSON_MAPPER.writeValueAsString(value);
                        child.attr(attr, processedJson);
                    } catch (IOException ex) {
                        LOG.warn("Failed to normalize json attribute: {}", stringValue, ex);
                    }
                }
            }

            if (!StringUtils.startsWith(child.nodeName(), "#")) {
                sortAttributes(child);
            }

            // recurse
            removeNoise(child);
            i++;
        }
    }

    /**
     * Creates a deep copy of a Map parsed from a json object. It uses a {@link TreeMap} internally in order to produce a reproducible
     * output. Some properties will be processed to remove noise to ensure the test outcome is reproducible.
     *
     * @param jsonMap
     * @return
     */
    private static Map<String, Object> removeNoise(Map<String, Object> jsonMap) {
        // use a tree map as copy to enforce a natural ordering of the keys
        Map<String, Object> copy = new TreeMap<>();

        for (Map.Entry<String, ?> entry : jsonMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                value = removeNoise((Map<String, Object>) value);
            } else if (value instanceof String) {
                // prepend context path if needed
                if (StringUtils.isNotEmpty(cp)) {
                    if (ImmutableSet.of("xdm:linkURL").contains(key)
                        && StringUtils.startsWith((String) value, "/")
                        && !StringUtils.startsWith((String) value, cp + '/')) {
                        value = cp + value;
                    }
                }
            }

            copy.put(key, value);
        }

        return copy;
    }

    /**
     * For a reproducible parsing outcome we have to sort the attributes in a specific order. This is method is based on the implementation
     * detail that the node.attributes().dataset() uses a {@link java.util.LinkedHashMap} internally.
     *
     * @param node
     */
    private static void sortAttributes(Node node) {
        Attributes attributes = node.attributes();
        if (attributes.size() > 1) {
            TreeMap<String, String> sortedAttributes = new TreeMap<>();
            attributes.asList().forEach(attribute -> sortedAttributes.put(attribute.getKey(), attribute.getValue()));

            // clear the original attributes and re-add the sorted ones
            sortedAttributes.forEach((key, value) -> attributes.remove(key));
            sortedAttributes.forEach(attributes::put);
        }
    }
}
