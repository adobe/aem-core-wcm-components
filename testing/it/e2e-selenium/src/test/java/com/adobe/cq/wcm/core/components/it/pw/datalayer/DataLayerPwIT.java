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

import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.PlaywrightAuthorBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("playwright-group2")
public class DataLayerPwIT extends PlaywrightAuthorBaseTest {

    private String testPage;

    @Test
    public void testDataLayerInitialized() throws Exception {
        testPage = adminClient.createPage("testPage-" + System.currentTimeMillis(), "Test Page Title",
            "/content/core-components", "/conf/core-components/settings/wcm/templates/simple-template")
            .getSlingPath();

        page.navigate(baseUrl + testPage + ".html");
        Map<?, ?> state = (Map<?, ?>) page.evaluate(
            "() => window.adobeDataLayer && window.adobeDataLayer.getState()");
        assertNotNull(state, "adobeDataLayer.getState(): returned state object is null!");
        assertNotNull(state.get("page"), "returned state is missing 'page' property!");
        assertNotNull(state.get("component"), "returned state is missing 'component' property!");
        assertEquals(2, ((Map<?, ?>) state.get("component")).size(),
            "returned state.components should have 2 entries!");
    }

    @AfterEach
    void cleanupDataLayerPage() throws Exception {
        if (testPage != null) {
            adminClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS,
                RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
            testPage = null;
        }
    }
}
