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
import java.util.Map;
import java.util.TreeMap;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ComponentsIT {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentsIT.class);
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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

    /**
     * Removes noise from a given Node by traversing the structure and removing or processing attributes so that a reproducible output is
     * generated.
     *
     * @param element
     */
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
                        value = value.replaceAll("/\\d+/", "/0/");
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
    private Map<String, Object> removeNoise(Map<String, Object> jsonMap) {
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
                    for (String attr : new String[] { "xdm:linkURL" }) {
                        if (key.equals(attr)
                            && StringUtils.startsWith((String) value, "/")
                            && !StringUtils.startsWith((String) value, cp + '/')) {
                            value = cp + value;
                        }
                    }
                }
            }

            copy.put(key, value);
        }

        return copy;
    }
}
