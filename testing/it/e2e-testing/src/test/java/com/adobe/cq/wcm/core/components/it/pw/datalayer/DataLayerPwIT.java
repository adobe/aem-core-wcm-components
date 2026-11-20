/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.pw.datalayer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.PlaywrightAuthorBaseTest;
import com.adobe.cq.wcm.core.components.it.util.RequestConstants;
import com.microsoft.playwright.Frame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group2")
public class DataLayerPwIT extends PlaywrightAuthorBaseTest {

    private static final String DATA_LAYER_CONFIG =
        "/conf/core-components/sling:configs/com.adobe.cq.wcm.core.components.internal.DataLayerConfig";
    private static final String USE_ACDL3 = "useAcdl3";
    private static final String TEMPLATES = "/conf/core-components/settings/wcm/templates/";
    // page v2
    private static final String SIMPLE_TEMPLATE = TEMPLATES + "simple-template";
    // page v2, without page policy (no page client libraries)
    private static final String SIMPLE_TEMPLATE_NO_CLIENTLIBS = TEMPLATES + "simple-template-no-clientlibs";
    // page v3, without page policy
    private static final String SIMPLE_TEMPLATE_V3 = TEMPLATES + "simple-template-v3";
    // e.g. /etc.clientlibs/.../core.wcm.components.commons.datalayer.v1.acdl3.lc-<hash>-lc.min.js
    private static final Pattern DATA_LAYER_SCRIPT =
        Pattern.compile("/(core\\.wcm\\.components\\.commons\\.datalayer\\.[^/]+?)"
            + "(?:\\.lc-[0-9a-f]+-lc)?(?:\\.min)?\\.js$");

    private String testPage;
    private boolean acdl3Configured;

    @Test
    public void testDataLayerInitialized() throws Exception {
        // page v2, with core.wcm.components.commons.datalayer.v1 embedded in the page clientlibs (cmp-test.base.forms),
        // so the page component does not output it again
        Map<?, ?> state = openTestPage(SIMPLE_TEMPLATE, "2.", "component");
        assertComponents(state);
    }

    @Test
    public void testDataLayerInitializedNoPageClientlibs() throws Exception {
        Map<?, ?> state = openTestPage(SIMPLE_TEMPLATE_NO_CLIENTLIBS, "2.", "component",
            "core.wcm.components.commons.datalayer.v1");
        assertComponents(state);
    }

    @Test
    public void testDataLayerInitializedNoPageClientlibsAcdl3() throws Exception {
        configureAcdl3();
        Map<?, ?> state = openTestPage(SIMPLE_TEMPLATE_NO_CLIENTLIBS, "3.", "component",
            "core.wcm.components.commons.datalayer.v1.acdl3", "core.wcm.components.commons.datalayer.acdl3");
        assertComponents(state);
    }

    @Test
    public void testDataLayerInitializedPageV3() throws Exception {
        Map<?, ?> state = openTestPage(SIMPLE_TEMPLATE_V3, "2.", "page",
            "core.wcm.components.commons.datalayer.v2", "core.wcm.components.commons.datalayer.acdl");
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
    }

    @Test
    public void testDataLayerInitializedPageV3Acdl3() throws Exception {
        configureAcdl3();
        Map<?, ?> state = openTestPage(SIMPLE_TEMPLATE_V3, "3.", "page",
            "core.wcm.components.commons.datalayer.v2", "core.wcm.components.commons.datalayer.acdl3");
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
    }

    private void assertComponents(Map<?, ?> state) {
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
        assertNotNull(state.get("component"), "returned state is missing 'component' property!");
        assertEquals(2, ((Map<?, ?>) state.get("component")).size(),
            "returned state.components should have 2 entries!");
    }

    private void configureAcdl3() throws Exception {
        List<NameValuePair> properties = Arrays.asList(
            new BasicNameValuePair(USE_ACDL3, "true"),
            new BasicNameValuePair(USE_ACDL3 + "@TypeHint", "Boolean"));
        adminClient.setPropertiesString(DATA_LAYER_CONFIG, properties, HttpStatus.SC_OK);
        acdl3Configured = true;
    }

    /**
     * Opens a page in preview mode and checks the data layer.
     *
     * @param template the template of the page
     * @param expectedVersionPrefix the expected ACDL version prefix
     * @param stateProperty a property of the data layer state to wait for
     * @param expectedCategories the data layer client library categories that the page must output as scripts
     * @return the data layer state
     */
    private Map<?, ?> openTestPage(String template, String expectedVersionPrefix, String stateProperty,
                                   String... expectedCategories) throws Exception {
        testPage = adminClient.createPage("testPage-" + System.currentTimeMillis(), "Test Page Title",
            "/content/core-components", template)
            .getSlingPath();

        openEditor(testPage);
        enterPreviewMode();
        Frame frame = contentFrame().owner().elementHandle().contentFrame();
        // the content frame may reload when switching to preview mode, so wait until the data layer is initialized
        frame.waitForFunction("() => !!window.adobeDataLayer && typeof window.adobeDataLayer.getState === 'function'"
            + " && !!window.adobeDataLayer.getState()." + stateProperty);
        assertNotNull(frame.evaluate("() => window.adobeDataLayer"), "adobeDataLayer is undefined!");
        String version = (String) frame.evaluate("() => window.adobeDataLayer.version");
        assertTrue(version != null && version.startsWith(expectedVersionPrefix),
            "expected Adobe Client Data Layer " + expectedVersionPrefix + "x, got: " + version);
        assertDataLayerScriptsServed(frame, expectedCategories);
        Map<?, ?> state = (Map<?, ?>) frame.evaluate("() => window.adobeDataLayer.getState()");
        assertNotNull(state, "adobeDataLayer.getState(): returned state object is null!");
        return state;
    }

    /**
     * Checks that the data layer scripts output by the page are exactly the ones of the expected client libraries,
     * and that they are served. The category of a script is taken from its URL, which contains the name of the client
     * library folder: this relies on the data layer client library folders being named after their category (see
     * content/clientlib.config.js).
     */
    private void assertDataLayerScriptsServed(Frame frame, String... expectedCategories) {
        List<?> scripts = (List<?>) frame.evaluate("() => Promise.all(Array.from("
            + "document.querySelectorAll('script[src*=\"/core.wcm.components.commons.datalayer.\"]'))"
            + ".map(s => fetch(s.src).then(r => ({src: s.getAttribute('src'), status: r.status}))))");
        Set<String> categories = new TreeSet<>();
        for (Object script : scripts) {
            Map<?, ?> s = (Map<?, ?>) script;
            String src = (String) s.get("src");
            assertEquals(200, ((Number) s.get("status")).intValue(), "data layer script not served: " + src);
            Matcher matcher = DATA_LAYER_SCRIPT.matcher(src);
            assertTrue(matcher.find(), "unexpected data layer script URL: " + src);
            categories.add(matcher.group(1));
        }
        assertEquals(new TreeSet<>(Arrays.asList(expectedCategories)), categories,
            "unexpected data layer scripts: " + scripts);
    }

    @AfterEach
    void cleanupDataLayerPage() throws Exception {
        if (acdl3Configured) {
            adminClient.setPropertyString(DATA_LAYER_CONFIG, USE_ACDL3 + "@Delete", "", HttpStatus.SC_OK);
            acdl3Configured = false;
        }
        if (testPage != null) {
            adminClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS,
                RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
            testPage = null;
        }
    }
}
