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

    private String testPage;
    private boolean acdl3Configured;

    @Test
    public void testDataLayerInitialized() throws Exception {
        // page v2, with core.wcm.components.commons.datalayer.v1 embedded in the page clientlibs (cmp-test.base.forms)
        Map<?, ?> state = openTestPage("/conf/core-components/settings/wcm/templates/simple-template", "2.", "component");
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
        assertNotNull(state.get("component"), "returned state is missing 'component' property!");
        assertEquals(2, ((Map<?, ?>) state.get("component")).size(),
            "returned state.components should have 2 entries!");
    }

    @Test
    public void testDataLayerInitializedPageV3() throws Exception {
        Map<?, ?> state = openTestPage("/conf/core-components/settings/wcm/templates/simple-template-v3", "2.", "page");
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
    }

    @Test
    public void testDataLayerInitializedPageV3Acdl3() throws Exception {
        List<NameValuePair> properties = Arrays.asList(
            new BasicNameValuePair(USE_ACDL3, "true"),
            new BasicNameValuePair(USE_ACDL3 + "@TypeHint", "Boolean"));
        adminClient.setPropertiesString(DATA_LAYER_CONFIG, properties, HttpStatus.SC_OK);
        acdl3Configured = true;
        Map<?, ?> state = openTestPage("/conf/core-components/settings/wcm/templates/simple-template-v3", "3.", "page");
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
    }

    private Map<?, ?> openTestPage(String template, String expectedVersionPrefix, String stateProperty) throws Exception {
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
        Map<?, ?> state = (Map<?, ?>) frame.evaluate("() => window.adobeDataLayer.getState()");
        assertNotNull(state, "adobeDataLayer.getState(): returned state object is null!");
        return state;
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
