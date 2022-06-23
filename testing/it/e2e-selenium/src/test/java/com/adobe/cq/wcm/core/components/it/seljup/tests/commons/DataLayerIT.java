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

package com.adobe.cq.wcm.core.components.it.seljup.tests.commons;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("group2")
public class DataLayerIT extends AuthorBaseUITest {

    protected String testPage;
    protected EditorPage editorPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        testPage = adminClient.createPage("testPage-" + System.currentTimeMillis(), "Test Page Title", "/content/core-components", "/conf/core-components/settings/wcm/templates/simple-template").getSlingPath();
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @Test
    public void testDataLayerInitialized() {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        Object adobeDataLayer = ((JavascriptExecutor) webDriver).executeScript("return window.adobeDataLayer;");
        assertNotNull(adobeDataLayer, "adobeDataLayer is undefined!");
        try {
            // check if adobeDataLayer is initialized i.e. getState() function is defined
            Object state = ((JavascriptExecutor) webDriver).executeScript("return window.adobeDataLayer.getState();");
            // check if returned state object is not empty
            assertNotNull(state, "adobeDataLayer.getState(): returned state object is null!");
            Map<?, ?> stateMap = (Map) (state);
            // check some basic properties of the state object
            assertNotNull(stateMap.get("page"), "returned State is missing 'page' property!");
            assertNotNull(stateMap.get("component"), "returned State is missing 'component' property!");
            Map<?, ?> components = (Map) stateMap.get("component");
            assertEquals(2, components.size(), "returned state.components should have 2 entries!");
        } catch (JavascriptException e) {
            // this would return 'window.adobeDataLayer.getState() is not a function'
            fail(e);
        } catch (ClassCastException e) {
            fail("returned State object is not properly initialized!");
        }
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        adminClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
    }
}
