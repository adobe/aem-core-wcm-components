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

package com.adobe.cq.wcm.core.components.it.seljup.tests.contentfragment.v1;


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
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragment.ContentFragmentEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragment.v1.ContentFragment;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.ElementsCollection;

import java.util.concurrent.TimeoutException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_CONTENTFRAGMENT_V1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class ContentFragmentIT extends AuthorBaseUITest {
    private static String PN_VARIATION_NAME = "./variationName";
    private static String PN_ELEMENT_NAMES = "./elementNames";
    private static String DESCRIPTION = "component-description";
    private static String LATEST_VERSION = "component-latest-version";
    private static String TITLE = "component-title";
    private static String TYPE = "component-type";
    private static String SINGLE = "singleText";
    private static String fragmentPath1 = "/content/dam/core-components/contentfragments-tests/simple-fragment";
    private static String fragmentPath2 = "/content/dam/core-components/contentfragments-tests/image-fragment";
    private static String variationName1 = "short";

    private String testPage;
    private String cmpPath;
    private EditorPage editorPage;
    private ContentFragment contentFragment;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the content fragment component
        cmpPath = Commons.addComponentWithRetry(authorClient, RT_CONTENTFRAGMENT_V1,testPage + Commons.relParentCompPath, "contentfragment");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        contentFragment = new ContentFragment();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Set the fragment path
     */
    @Test
    @DisplayName("Set the fragment path")
    public void testSetFragmentPath() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        contentFragment.getEditDialog().setFragmentPath(fragmentPath1);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElementTitles = contentFragment.getElementTitle();
        ElementsCollection contentFragmentElementValues = contentFragment.getElementValue();

        assertTrue(contentFragmentTitle.size() == 1 && contentFragmentTitle.get(0).isDisplayed()
            && contentFragmentTitle.get(0).innerHtml().trim().equals("Simple Fragment"), "Content Fragment title should be displayed");
        assertTrue(contentFragmentElementTitles.size() == 1 && contentFragmentElementTitles.get(0).isDisplayed()
            && contentFragmentElementTitles.get(0).innerHtml().trim().equals("Main"),"Content Fragment element title should be displayed");
        assertTrue(contentFragmentElementValues.size() == 1 && contentFragmentElementValues.get(0).isDisplayed()
            && contentFragmentElementValues.get(0).$("h2").innerHtml().trim().equals("Master variation"),"Content Fragment element value should be displayed");
    }

    /**
     * Set the variation name
     */
    @Test
    @DisplayName("Set the variation name")
    public void testSetVariationName() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        contentFragment.getEditDialog().setFragmentPath(fragmentPath1);
        Commons.useDialogSelect(PN_VARIATION_NAME, variationName1);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElementTitles = contentFragment.getElementTitle();
        ElementsCollection contentFragmentElementValues = contentFragment.getElementValue();

        assertTrue(contentFragmentTitle.size() == 1 && contentFragmentTitle.get(0).isDisplayed()
            && contentFragmentTitle.get(0).innerHtml().trim().equals("Simple Fragment"), "Content Fragment title should be displayed");
        assertTrue(contentFragmentElementTitles.size() == 1 && contentFragmentElementTitles.get(0).isDisplayed()
            && contentFragmentElementTitles.get(0).innerHtml().trim().equals("Main"),"Content Fragment element title should be displayed");
        assertTrue(contentFragmentElementValues.size() == 1 && contentFragmentElementValues.get(0).isDisplayed()
            && contentFragmentElementValues.get(0).$("h2").innerHtml().trim().equals("Short variation"),"Content Fragment element value should be displayed");

    }

    /**
     * Set a structured content fragment
     */
    @Test
    @DisplayName("Set a structured content fragment")
    public void testSetStructuredContentFragment() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        contentFragment.getEditDialog().setFragmentPath(fragmentPath2);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElementTitles = contentFragment.getElementTitle();
        ElementsCollection contentFragmentElementValues = contentFragment.getElementValue();

        assertTrue(contentFragmentTitle.size() == 1 && contentFragmentTitle.get(0).isDisplayed()
            && contentFragmentTitle.get(0).innerHtml().trim().equals("Image Fragment"), "Content Fragment title should be displayed");
        assertTrue(contentFragmentElementTitles.size() == 4 && contentFragmentElementValues.size() == 4,
            "There should be 4 elements set");
        assertTrue(contentFragmentElementTitles.get(0).isDisplayed() && contentFragmentElementTitles.get(0).innerHtml().trim().equals("Title"),
            "Content Fragment first element title should be correctly displayed");
        assertTrue(contentFragmentElementValues.get(0).isDisplayed() && contentFragmentElementValues.get(0).innerHtml().trim().equals("Image"),
            "Content Fragment first element value should be correctly displayed");
        assertTrue(contentFragmentElementTitles.get(1).isDisplayed() && contentFragmentElementTitles.get(1).innerHtml().trim().equals("Description"),
            "Content Fragment Second element title should be correctly displayed");
        assertTrue(contentFragmentElementTitles.get(2).isDisplayed() && contentFragmentElementTitles.get(2).innerHtml().trim().equals("Latest Version"),
            "Content Fragment Third element title should be correctly displayed");
        assertTrue(contentFragmentElementValues.get(2).isDisplayed() && contentFragmentElementValues.get(2).innerHtml().trim().equals("2"),
            "Content Fragment first element value should be correctly displayed");
        assertTrue(contentFragmentElementTitles.get(3).isDisplayed() && contentFragmentElementTitles.get(3).innerHtml().trim().equals("Type"),
            "Content Fragment Fourth element title should be correctly displayed");
    }

    /**
     * Set the element names
     */
    @Test
    @DisplayName("Set the element names")
    public void testSetElementNames() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        contentFragment.getEditDialog().setFragmentPath(fragmentPath2);
        Commons.saveConfigureDialog();
        Commons.openEditDialog(editorPage, cmpPath);
        ContentFragmentEditDialog editDialog = contentFragment.getEditDialog();
        editDialog.addElement(TITLE);
        editDialog.addElement(TYPE);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        Commons.switchContext("ContentFrame");
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElementTitles = contentFragment.getElementTitle();
        ElementsCollection contentFragmentElementValues = contentFragment.getElementValue();

        assertTrue(contentFragmentTitle.size() == 1 && contentFragmentTitle.get(0).isDisplayed()
            && contentFragmentTitle.get(0).innerHtml().trim().equals("Image Fragment"), "Content Fragment title should be displayed");
        assertTrue(contentFragmentElementTitles.size() == 2 && contentFragmentElementValues.size() == 2,
            "There should be 2 elements set");
        assertTrue(contentFragmentElementTitles.get(0).isDisplayed() && contentFragmentElementTitles.get(0).innerHtml().trim().equals("Title"),
            "Content Fragment first element title should be correctly displayed");
        assertTrue(contentFragmentElementValues.get(0).isDisplayed() && contentFragmentElementValues.get(0).innerHtml().trim().equals("Image"),
            "Content Fragment first element value should be correctly displayed");
        assertTrue(contentFragmentElementTitles.get(1).isDisplayed() && contentFragmentElementTitles.get(1).innerHtml().trim().equals("Type"),
            "Content Fragment Fourth element title should be correctly displayed");
    }

    @Test
    @DisplayName("Set single display Mode")
    public void testSetSingleElement() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        ContentFragmentEditDialog editDialog = contentFragment.getEditDialog();
        editDialog.setFragmentPath(fragmentPath1);
        editDialog.setDisplayMode(SINGLE);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editDialog.setFragmentPath(fragmentPath2);
        editDialog.confirmReplacingFragment();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        ElementsCollection errorLabels = editDialog.getErrorLabels();
        assertEquals(1, errorLabels.size());
        editDialog = contentFragment.getEditDialog();
        editDialog.setElement(TITLE);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        
        Commons.switchContext("ContentFrame");
        ElementsCollection contentFragmentTitle = contentFragment.getTitle();
        ElementsCollection contentFragmentElementTitles = contentFragment.getElementTitle();
        ElementsCollection contentFragmentElementValues = contentFragment.getElementValue();

        assertTrue(contentFragmentTitle.size() == 1 && contentFragmentTitle.get(0).isDisplayed()
            && contentFragmentTitle.get(0).innerHtml().trim().equals("Image Fragment"), "Content Fragment title should be displayed");
        assertTrue(contentFragmentElementTitles.get(0).isDisplayed() && contentFragmentElementTitles.get(0).innerHtml().trim().equals("Title"),
            "Content Fragment first element title should be correctly displayed");
        assertTrue(contentFragmentElementValues.get(0).isDisplayed() && contentFragmentElementValues.get(0).innerHtml().trim().equals("Image"),
            "Content Fragment first element value should be correctly displayed");
    }

    /**
     * Visual Content Fragment (VCF) display mode should reveal the HTML template selector in the edit dialog.
     */
    @Test
    @DisplayName("Switching to VCF clears single-text element validation after failed save")
    public void testVcfClearsSingleTextElementValidationError() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        ContentFragmentEditDialog editDialog = contentFragment.getEditDialog();
        editDialog.setFragmentPath(fragmentPath2);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editDialog.setDisplayMode(SINGLE);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertEquals(1, editDialog.getErrorLabels().size(), "Saving without a required element should show one validation error");

        editDialog.setDisplayMode("vcf");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertEquals(0, editDialog.getErrorLabels().size(),
            "Validation error should clear when Visual Content Fragment mode hides the element field");
    }

    @Test
    @DisplayName("VCF display mode shows VCF template field")
    public void testVcfDisplayModeShowsVcfTemplateField() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        contentFragment.getEditDialog().setFragmentPath(fragmentPath1);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        contentFragment.getEditDialog().setDisplayMode("vcf");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        $("coral-radio[name='./displayMode'][value='vcf']").shouldBe(visible);
        $("coral-select[name='./vcfTemplate']").shouldBe(visible);
    }

    /**
     * After saving with VCF display mode, the page preview should render the VCF wrapper (author preview uses
     * {@code vcfRenderer.js}; published preview uses {@code vcf.js} when a preview URL is available).
     */
    @Test
    @DisplayName("VCF display mode renders wrapper in preview")
    public void testVcfDisplayModeRendersWrapperInPreview() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, cmpPath);
        contentFragment.getEditDialog().setFragmentPath(fragmentPath1);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        contentFragment.getEditDialog().setDisplayMode("vcf");
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        editorPage.enterPreviewMode();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchContext("ContentFrame");

        $(".cmp-contentfragment--vcf").shouldBe(visible);
    }
}
