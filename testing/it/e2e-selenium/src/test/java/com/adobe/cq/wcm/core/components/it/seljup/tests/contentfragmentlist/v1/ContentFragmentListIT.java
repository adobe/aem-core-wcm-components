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

package com.adobe.cq.wcm.core.components.it.seljup.tests.contentfragmentlist.v1;

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
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragment.v1.ContentFragment;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragmentlist.ContentFragmentListEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragmentlist.v1.ContentFragmentList;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.ElementsCollection;


import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_CONTENTFRAGMENTLIST_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class ContentFragmentListIT extends AuthorBaseUITest {
    private static String PN_MODEL_PATH = "./modelPath";
    private static String PN_ELEMENT_NAMES = "./elementNames";
    private static String DESCRIPTION = "component-description";
    private static String LATEST_VERSION = "component-latest-version";
    private static String TITLE = "component-title";
    private static String TYPE = "component-type";
    private static String modelPath = "/conf/core-components/settings/dam/cfm/models/core-component-model";
    private static String parentPath = "/content/dam/core-components/contentfragments-tests";
    private static String tagName = "core-components/component-type/basic";

    protected String testPage;
    protected String cmpPath;
    protected EditorPage editorPage;
    protected ContentFragment contentFragment;
    protected ContentFragmentList contentFragmentList;
    protected String contentFragmentListRT;

    protected void setupResources() {
        contentFragmentListRT = RT_CONTENTFRAGMENTLIST_V1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the contentfragmentlist component
        cmpPath = Commons.addComponentWithRetry(authorClient, contentFragmentListRT, testPage + Commons.relParentCompPath, "contentfragmentlist");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        contentFragment = new ContentFragment();
        contentFragmentList = new ContentFragmentList();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Set the parent path
     */
    @Test
    @DisplayName("Set the parent path")
    public void testSetParentPath() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        Commons.useDialogSelect(PN_MODEL_PATH, modelPath);
        Commons.selectInAutocomplete(contentFragmentList.getEditDialog().getParentPath(),parentPath);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection cfList = contentFragmentList.getContentFragmentList();
        ElementsCollection contentFragments = contentFragment.getContentFragments();
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElements = contentFragment.getElementTitle();

        boolean titlesMatch = false;

        for(int i = 0; i < contentFragmentTitle.size(); i++) {
            String title = contentFragmentTitle.get(i).getText();
            titlesMatch = title.equals("Image Fragment") || title.equals("Text Fragment") || title.equals("Carousel Fragment");
        }

        assertTrue(cfList.size() == 1,"There should be 1 content fragment list present");
        assertTrue(contentFragments.size() == 3, "There should be 3 content fragments present");
        assertTrue(contentFragmentElements.size() == 12, "There should be 12 content fragments elements present");
        assertTrue(titlesMatch == true, "Content Fragment Titles should match");
    }

    /**
     * Set the tag names
     *
     * Note: this test is ignored on 6.3 because it fails on 6.3 for the following reason:
     * the expected tags location is "/etc/tags" for 6.3 and "/content/cq:tags" for versions > 6.3
     */
    @Test
    @DisplayName("Set the tag names")
    public void testSetTagNames() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        Commons.useDialogSelect(PN_MODEL_PATH, modelPath);
        Commons.selectInAutocomplete(contentFragmentList.getEditDialog().getParentPath(),parentPath);
        Commons.selectInTags(contentFragmentList.getEditDialog().getTagNames(), tagName);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection cfList = contentFragmentList.getContentFragmentList();
        ElementsCollection contentFragments = contentFragment.getContentFragments();
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElements = contentFragment.getElementTitle();

        boolean titlesMatch = false;

        for(int i = 0; i < contentFragmentTitle.size(); i++) {
            String title = contentFragmentTitle.get(i).getText();
            titlesMatch = title.equals("Image Fragment") || title.equals("Text Fragment");
        }

        assertTrue(cfList.size() == 1,"There should be 1 content fragment list present");
        assertTrue(contentFragments.size() == 2, "There should be 2 content fragments present");
        assertTrue(contentFragmentElements.size() == 8, "There should be 8 content fragments elements present");
        assertTrue(titlesMatch == true, "Content Fragment Titles should match");
    }

    /**
     * Set the element names
     */
    @Test
    @DisplayName("Set the element names")
    public void testSetElementNames() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        Commons.useDialogSelect(PN_MODEL_PATH, modelPath);
        Commons.selectInAutocomplete(contentFragmentList.getEditDialog().getParentPath(),parentPath);
        ContentFragmentListEditDialog editDialog = contentFragmentList.getEditDialog();
        editDialog.openElementTab();
        editDialog.addElement(TITLE);
        editDialog.addElement(TYPE);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection cfList = contentFragmentList.getContentFragmentList();
        ElementsCollection contentFragments = contentFragment.getContentFragments();
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElements = contentFragment.getElementTitle();


        boolean titlesMatch = false;

        for(int i = 0; i < contentFragmentTitle.size(); i++) {
            String title = contentFragmentTitle.get(i).getText();
            titlesMatch = title.equals("Image Fragment") || title.equals("Text Fragment") || title.equals("Carousel Fragment");
        }

        assertTrue(cfList.size() == 1,"There should be 1 content fragment list present");
        assertTrue(contentFragments.size() == 3, "There should be 3 content fragments present");
        assertTrue(contentFragmentElements.size() == 6, "There should be 6 content fragments elements present");
        assertTrue(titlesMatch == true, "Content Fragment Titles should match");
    }
}
