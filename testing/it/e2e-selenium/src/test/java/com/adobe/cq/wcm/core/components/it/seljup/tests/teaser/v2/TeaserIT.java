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

package com.adobe.cq.wcm.core.components.it.seljup.tests.teaser.v2;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v2.Teaser;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v2.TeaserEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static com.adobe.cq.testing.selenium.utils.ElementUtils.clickableClick;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TeaserIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.teaser.v1.TeaserIT {

    protected Teaser teaser;

    protected void setupResources() {
        super.setupResources();
        teaserRT = Commons.RT_TEASER_V2;
        clientlibs = Commons.CLIENTLIBS_TEASER_V2;
        teaser = new Teaser();
    }

    /**
     * Before Test Case
     **/
    @BeforeEach
    public void setupBeforeEach() throws ClientException, InterruptedException {
        setupResources();
        setup();
    }
    /**
     * Test: Links to elements for Teaser
     *
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws ClientException
     */
    @Test
    @DisplayName("Test: Links to elements for Teaser")
    public void testLinksToElementsTeaser() throws TimeoutException, InterruptedException {

        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinksTab();
        editDialog.setLinkURL(testPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(!teaser.isTitleLinkPresent(testPage, title),"Title link should not be present");
    }

    /**
     * Test: Fully Configured Teaser
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Fully configured Teaser")
    public void testFullyConfiguredTeaser() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinksTab();
        editDialog.setLinkURL(testPage);
        editDialog.clickLinkTarget();
        editDialog.openTextTab();
        editDialog.setPreTitle(preTitle);
        assertTrue(editDialog.isTitleFromPagePresent());
        editDialog.clickTitleFromPage();
        editDialog.setTitle(title);
        assertTrue(editDialog.isDescriptionFromPagePresent());
        editDialog.clickDescriptionFromPage();
        editDialog.setDescription(description);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(teaser.isPreTitlePresent(preTitle), "PreTitle should be present");

        assertTrue(teaser.isTeaserLinkPresentWithTarget(testPage, title, "_blank"),"Teaser link should be present with target");
        assertTrue(teaser.isDescriptionPresent(description),"Description should be present");
        assertTrue(!teaser.isImageLinkPresent(),"The image should not be linked");
    }

    /**
     * Test: Teaser with link and image and not title and description
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Teaser with link and image and inherited title and description")
    public void testWithLinkAndImageTeaser() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinksTab();
        editDialog.setLinkURL(testPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(!teaser.isImageLinkPresent(), "The image should not be linked");
        assertTrue(teaser.isTitlePresent(), "Teaser title should be present");
        assertTrue(teaser.isDescriptionPresent(), "Teaser description should be present");
    }

    /**
     * Teaser with External Actions
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Teaser with External Actions")
    public void testWithExternalActionsTeaser() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinksTab();
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(actionExternalLink);
        editDialog.setActionText(actionExternalText);
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(secondTestPage);
        editDialog.clickLActionLinkTarget();
        editDialog.setActionText(actionText2);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isTitleHidden(), "Title and Link should not be displayed");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(!teaser.isImageLinkPresent(), "Image should not be linked");
        assertTrue(!teaser.isTitleLinkPresent(), "Title link should not be present");
        assertTrue(teaser.isDescriptionPresent(), "Teaser description should be present");
        assertTrue(teaser.isActionLinkPresent(actionExternalText), actionExternalLink + " action link should be present");
        assertTrue(teaser.isActionLinkPresentWithTarget(actionText2, "_blank"), actionText2 + " action link should be present");
    }

    /**
     * Test: Inherited Properties Teaser
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Teaser with inherited properties")
    public void testInheritedPropertiesTeaser() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinksTab();
        editDialog.setLinkURL(testPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(teaser.isTeaserLinkPresent(testPage, pageTitle),"Teaser link should be present");
        assertTrue(teaser.isDescriptionPresent(pageDescription),"Description from page should be present");
    }

    /**
     * Teaser with Actions
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Teaser with Actions")
    public void testWithActionsTeaser() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openTextTab();
        editDialog.openLinksTab();
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(testPage);
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(secondTestPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(!teaser.isImageLinkPresent(), "Image should not be linked");
        assertTrue(teaser.isTitleHidden(), "Title and Link should not be displayed");
        assertTrue(!teaser.isTitleLinkPresent(testPage, pageTitle),"Page title should not be present as title link ");
        assertTrue(teaser.isDescriptionPresent(pageDescription),"Description from page should be present");
        assertTrue(teaser.isActionLinkPresent(pageTitle), "Test Page action link should be present");
        assertTrue(teaser.isActionLinkPresent(secondPageTitle), "Second Test Page action link should be present");
    }

    /**
     * Test: Checkbox-Textfield Tuple
     *
     * 1. open the edit dialog
     * 2. switch to the 'Text' tab
     * 3. populate the title tuple textfield
     * 4. open the 'Linka' tab
     * 5. add a link
     * 6. open the 'Text' tab
     * 7. verify the title tuple textfield value has not changed and that the textfield is not disabled
     * 8. set 'Get title from linked page' checkbox, checked
     * 9. verify the title value and disabled state
     * 10. set 'Get title from linked page' checkbox, unchecked
     * 11. verify the title has reverted to its previous user-input value
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Checkbox-Textfield Tuple")
    public void testCheckboxTextfieldTuple() throws TimeoutException, InterruptedException {
        // 1.
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();

        // 2.
        editDialog.openTextTab();

        // 3.
        editDialog.clickTitleFromPage();
        editDialog.setTitle(title);

        // 4.
        editDialog.openLinksTab();

        // 5.
        editDialog.setLinkURL(testPage);

        // 6.
        editDialog.openTextTab();

        // 7.
        assertTrue(editDialog.getTitleValue().equals(title) && editDialog.isTitleEnabled(),
            "Title should be enabled and should be set to " + title);
        // 8.
        editDialog.clickTitleFromPage();

        // 9.
        assertTrue(editDialog.getTitleValue().equals(pageTitle) && !editDialog.isTitleEnabled(),
            "Title should be disabled and should not be set");

        // 10.
        editDialog.clickTitleFromPage();

        // 11.
        assertTrue(editDialog.getTitleValue().equals(title) && editDialog.isTitleEnabled(),
            "Title should be enabled and should be set to " + title);
    }

    @Test
    @DisplayName("Test: Inherit image from current page")
    public void testInheritImageFromCurrentPage() throws ClientException, InterruptedException {
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, climbingAssetAltText),"image should be rendered with alt text: " + climbingAssetAltText);
        assertTrue(teaser.isImagePresentWithFileName(climbingAssetFormatted),"image should be rendered with file name: " + climbingAssetFormatted);
    }

    @Test
    @DisplayName("Test: Inherit image from current page and no alt text")
    public void testInheritImageFromCurrentPage_isDecorative() throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        // set image to decorative
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkInheritAltFromPage();
        editDialog.setAltText("");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS * 5);
        editDialog.scrollToIsDecorativeCheckbox();
        editDialog.checkIsDecorative();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS * 5);
        assertTrue(editDialog.isDecorativeChecked());
        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS * 5);
        assertTrue(teaser.isImagePresentWithFileName(climbingAssetFormatted), "image should be rendered with file name: " + climbingAssetFormatted);
        assertTrue(teaser.isImagePresentWithEmptyAltAttribute(testPage), "image should be rendered with alt attribute empty");
    }

    @Test
    @DisplayName("Test: Inherit image from linked page")
    public void testInheritImageFromLinkedPage() throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the linked page
        setPageImage(secondTestPage, surfingAsset);
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        // set the link URL
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openLinksTab();
        editDialog.setLinkURL(secondTestPage);
        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, surfingAssetAltText),"image should be rendered with alt text: " + surfingAssetAltText);
        assertTrue(teaser.isImagePresentWithFileName(surfingAssetFormatted),"image should be rendered with file name: " + surfingAssetFormatted);
    }

    @Test
    @DisplayName("Test: Inherit image from linked page with alt defined in the dialog")
    public void testInheritImageFromLinkedPage_altNotInherited() throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the linked page
        setPageImage(secondTestPage, surfingAsset);
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        // set the link URL
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openLinksTab();
        editDialog.setLinkURL(secondTestPage);

        // define alt on the resource
        editDialog.openAssetsTab();
        editDialog.checkInheritAltFromPage();
        editDialog.setAltText(alt);

        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, alt),"image should be rendered with alt text: " + alt);
        assertTrue(teaser.isImagePresentWithFileName(surfingAssetFormatted),"image should be rendered with file name: " + surfingAssetFormatted);
    }

    @Test
    @DisplayName("Test: Inherit image from action page")
    public void testInheritImageFromAction() throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the action page
        setPageImage(thirdTestPage, skiingAsset);
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        // set the action URL
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openLinksTab();
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(thirdTestPage);
        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, skiingAssetAltText),"image should be rendered with alt text: " + skiingAssetAltText);
        assertTrue(teaser.isImagePresentWithFileName(skiingAssetFormatted),"image should be rendered with file name: " + skiingAssetFormatted);
    }

    /**
     * Test: Teaser with title, description and without image and link
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Teaser with title, description and without image and link")
    public void testNoImageTeaser() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openTextTab();
        editDialog.clickTitleFromPage();
        editDialog.setTitle(title);
        editDialog.clickDescriptionFromPage();
        editDialog.setDescription(description);
        editDialog.openLinkAndActionsTab();
        Commons.saveConfigureDialog();
        assertEquals(editDialog.getAssetWithoutDescriptionErrorMessage(), "Error: Please provide an asset which has a description that can be used as alt text.");
        editDialog.checkImageFromPageImage();
        editDialog.checkAltTextFromAssetDescription();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(!teaser.isImagePresent(testPage), "Image should not be present");
        assertTrue(teaser.isTitlePresent(title),"Title link should be present");
        assertTrue(teaser.isDescriptionPresent(description),"Description should be present");
    }

    /**
     * Hide elements for Teaser
     *
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws ClientException
     */
    @Test
    @DisplayName("Test: Hide elements for Teaser")
    @Override
    public void testHideElementsTeaser() throws TimeoutException, InterruptedException, ClientException {
        createComponentPolicy("/teaser-v2", new HashMap<String, String>() {{
            put("titleHidden", "true");
            put("descriptionHidden", "true");
        }});

        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openTextTab();
        assertTrue(!editDialog.isDescriptionFromPagePresent(), "Description from Page checkbox should not be present");
        assertTrue(!editDialog.isTitleFromPagePresent(), "Title from Page checkbox should not be present");
    }

    /**
     * Disable Actions for Teaser
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Disable Actions for Teaser")
    public void testDisableActionsTeaser() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy("/teaser-v2", new HashMap<String, String>() {{
            put("actionsDisabled", "true");
        }});

        Commons.openSidePanel();
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();

        editDialog.openLinksTab();
        assertTrue(!editDialog.isActionsPresent(), "Actions should not be present");
    }

    @Test
    @DisplayName("Test: Inherit image from action page with alt defined in the dialog")
    public void testInheritImageFromAction_altNotInherited() throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the action page
        setPageImage(thirdTestPage, skiingAsset);
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        // set the action URL
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openLinksTab();
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(thirdTestPage);

        // define alt on the resource
        editDialog.openAssetsTab();
        editDialog.checkInheritAltFromPage();
        editDialog.setAltText(alt);

        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, alt),"image should be rendered with alt text: " + alt);
        assertTrue(teaser.isImagePresentWithFileName(skiingAssetFormatted),"image should be rendered with file name: " + skiingAssetFormatted);
    }

    // ----------------------------------------------------------
    // private stuff
    // ----------------------------------------------------------

    /**
     * Sets the featured image for a page.
     */
    private void setPageImage(String page, String asset) throws ClientException, InterruptedException {
        // set page resource type to page v3
        authorClient.setPageProperty(page, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        PropertiesPage pageProperties = new PropertiesPage(page);
        pageProperties.open();
        $("coral-tab[data-foundation-tracking-event*='images']").click();
        $(".cq-FileUpload-picker").click();
        $("*[data-foundation-collection-item-id='/content/dam/core-components']").click();

        try {
            $("*[data-foundation-collection-item-id='/content/dam/core-components/" + asset + "'] coral-checkbox").click();
        } catch (Throwable t) {
            // Fallback for AEM 6.5
            $("*[data-foundation-collection-item-id='/content/dam/core-components/" + asset + "'] coral-columnview-item-thumbnail").click();
        }

        clickableClick($(".granite-pickerdialog-submit"));

        // inherit alt text from DAM
        String altValueFromDAMSelector = ".cq-siteadmin-admin-properties coral-checkbox[name='./cq:featuredimage/altValueFromDAM']";
        CoralCheckbox altValueFromDAMCheckbox = new CoralCheckbox(altValueFromDAMSelector);
        altValueFromDAMCheckbox.click();

        pageProperties.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editorPage.open();
    }
}
