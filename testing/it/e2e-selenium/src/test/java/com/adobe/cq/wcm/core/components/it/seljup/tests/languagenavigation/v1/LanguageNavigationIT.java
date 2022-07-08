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

package com.adobe.cq.wcm.core.components.it.seljup.tests.languagenavigation.v1;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.languagenavigation.LanguageNavigationEditConfig;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.languagenavigation.v1.LanguageNavigation;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class LanguageNavigationIT extends AuthorBaseUITest {

    protected String siteRoot;
    protected String compPath;
    protected String noStructure;
    protected EditorPage editorPage;
    protected LanguageNavigation languageNavigation;
    protected String languageNavigationRT;

    private void setupResources() {
        languageNavigationRT = Commons.RT_LANGUAGE_NAVIGATION_V1;
    }

    protected void setup() throws ClientException {
        // site root
        siteRoot = authorClient.createPage("site_root", "site_root", rootPage, defaultPageTemplate).getSlingPath();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "Site Root");
        Commons.editNodeProperties(authorClient, siteRoot, data);

        // 1
        String locale1 = authorClient.createPage("LOCALE_1", "LOCALE_1", siteRoot, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "LOCALE 1");
        Commons.editNodeProperties(authorClient, locale1, data);

        // 1.1
        String locale31 = authorClient.createPage("LOCALE_3", "LOCALE_3", locale1, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "LOCALE 3 1");
        Commons.editNodeProperties(authorClient, locale31, data);

        // 1.2
        String locale4 = authorClient.createPage("LOCALE_4", "LOCALE_4", locale1, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "LOCALE 4");
        data.put("./jcr:content/sling:vanityPath", "/LOCALE_4_vanity");
        Commons.editNodeProperties(authorClient, locale4, data);

        // 1.1.1
        String about1 = authorClient.createPage("about", "about", locale31, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "About Us");
        Commons.editNodeProperties(authorClient, about1, data);

        // 2
        String locale2 = authorClient.createPage("LOCALE_2", "LOCALE_2", siteRoot, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "LOCALE 2");
        Commons.editNodeProperties(authorClient, locale2, data);

        // 2.1
        String locale32 = authorClient.createPage("LOCALE_3", "LOCALE_3", locale2, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "LOCALE 3 2");
        Commons.editNodeProperties(authorClient, locale32, data);

        // 2.2
        String locale5 = authorClient.createPage("LOCALE_5", "LOCALE_5", locale2, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "LOCALE 5");
        Commons.editNodeProperties(authorClient, locale5, data);

        // 2.2.1
        String about2 = authorClient.createPage("about", "about", locale32, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "About Us");
        Commons.editNodeProperties(authorClient, about2, data);

        // 3
        String hideInNav = authorClient.createPage("hideInNav", "hideInNav", siteRoot, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/hideInNav", "true");
        Commons.editNodeProperties(authorClient, hideInNav, data);

        // no structure
        noStructure = authorClient.createPage("no_structure", "no_structure", rootPage, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "No Structure");
        Commons.editNodeProperties(authorClient, noStructure, data);

        compPath = Commons.addComponentWithRetry(authorClient, languageNavigationRT, about1 + Commons.relParentCompPath, "languagenavigation");

        editorPage = new PageEditorPage(about1);
        editorPage.open();

        languageNavigation = new LanguageNavigation();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(siteRoot, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }


    /**
     * Test: Default configuration (depth 1)
     */
    @Test
    @DisplayName("Test: Default configuration (depth 1)")
    public void testDefaultConfiguration() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, compPath);
        LanguageNavigationEditConfig editConfig = languageNavigation.getEditDialog();
        editConfig.setNavigationRoot(siteRoot);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(languageNavigation.isLevel0ItemActiveContainValue("LOCALE 1"),"active Level 0 item should be LOCALE 1");
        assertTrue(languageNavigation.isLevel0ItemPresentContainValue("LOCALE 2"), "LOCALE 2 item should be present at Level 0");
        assertTrue(!languageNavigation.isLevel0ItemPresentContainValue("hideInNav"), "hideInNav item should not be present at Level 0");
        assertTrue(!languageNavigation.isItemPresentContainValue("LOCALE 3 1"), "Item should contain LOCALE 3 1");
    }

    /**
     * Test: Change Structure Depth (depth 2)
     */
    @Test
    @DisplayName("Test: Change Structure Depth (depth 2)")
    public void testChangeStructureDepth() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, compPath);
        LanguageNavigationEditConfig editConfig = languageNavigation.getEditDialog();
        editConfig.setNavigationRoot(siteRoot);
        editConfig.setStructureDepth("2");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(languageNavigation.isLevel0ItemActiveContainValue("LOCALE 1"),"active Level 0 item should be LOCALE 1");
        assertTrue(languageNavigation.isLevel0ItemPresentContainValue("LOCALE 2"), "LOCALE 2 item should be present at Level 0");
        assertTrue(!languageNavigation.isLevel0ItemPresentContainValue("hideInNav"), "hideInNav item should not be present at Level 0");
        assertTrue(!languageNavigation.isLinkItemPresent(),"Link item should not present");
        assertTrue(languageNavigation.isLevel1ItemActiveContainValue("LOCALE 3 1"),"active Level 1 item should be LOCALE 3 1");
        assertTrue(languageNavigation.isLevel1ItemPresentContainValue("LOCALE 3 2"), "LOCALE 3 2 item should be present at Level 1");
        assertTrue(languageNavigation.isLevel1ItemPresentContainValue("LOCALE 4"), "LOCALE 4 item should be present at Level 1");
        assertTrue(languageNavigation.isLevel1ItemPresentContainValue("LOCALE 5"), "LOCALE 5 item should be present at Level 1");
        assertTrue(!languageNavigation.isItemPresentContainValue("About Us"), "Item should not contain About Us");
    }

    /**
     * Test: Change Structure Depth to zero - invalid input, no dialog submission
     */
    @Test
    @DisplayName("Test: Change Structure Depth to zero - invalid input, no dialog submission")
    public void testSetStructureDepthZero() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, compPath);
        LanguageNavigationEditConfig editConfig = languageNavigation.getEditDialog();
        editConfig.setNavigationRoot(siteRoot);
        editConfig.setStructureDepth("0");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(!Commons.iseditDialogVisible(),"Edit Dialog should not be closed since Structure depth is set to 0");
    }

    /**
     * Test: Navigation Root with no structure - no items, placeholder displayed
     */
    @Test
    @DisplayName("Test: Navigation Root with no structure - no items, placeholder displayed")
    public void testNavigationRootNoStructure() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, compPath);
        LanguageNavigationEditConfig editConfig = languageNavigation.getEditDialog();
        editConfig.setNavigationRoot(noStructure);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(!languageNavigation.isItemPresent(), "Navigation item should not be present");
        assertTrue(languageNavigation.isPlaceholderItemPresent(), "Placeholder item should be present");
    }

}
