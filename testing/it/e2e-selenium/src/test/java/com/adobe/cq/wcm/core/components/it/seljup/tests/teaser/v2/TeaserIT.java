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

import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.wcm.core.components.it.seljup.components.teaser.v2.TeaserEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.teaser.v2.Teaser;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TeaserIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.teaser.v1.TeaserIT {

    protected Teaser teaser;

    protected void setup() throws ClientException {
        super.setup(Commons.rtImage_v3);
    }

    protected void setupResources() {
        super.setupResources();
        teaserRT = Commons.rtTeaser_v2;
        clientlibs = "core.wcm.components.teaser.v1";
        teaser = new Teaser();
    }

    /**
     * Before Test Case
     **/
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
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
    public void testLinksToElementsTeaser() throws TimeoutException, InterruptedException, ClientException {
        String policySuffix = "/teaser/new_policy";
        HashMap<String, String> data = new HashMap<String, String>();
        data.clear();
        data.put("jcr:title", "New Policy");
        data.put("sling:resourceType", "wcm/core/components/policy/policy");
        data.put("titleLinkHidden", "true");
        data.put("imageLinkHidden", "true");
        String policyPath1 = "/conf/"+ label + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, data , policyPath1);

        // add a policy for teaser component
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content/root/responsivegrid/core-component/components";
        data.clear();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient,"/teaser",data, policyAssignmentPath, 200, 201);

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
        editDialog.openTextTab();
        editDialog.setPreTitle(preTitle);
        editDialog.setTitle(title);
        editDialog.setDescription(description);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(teaser.isPreTitlePresent(preTitle), "PreTitle should be present");
        assertTrue(teaser.isTitleLinkPresent(testPage, title),"Title link should be present");
        assertTrue(teaser.isDescriptionPresent(description),"Description should be present");
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
        editDialog.clickActionEnabled();
        editDialog.setActionLinkUrl(actionExternalLink);
        editDialog.setActionText(actionExternalText);
        editDialog.addActionLinkUrl(secondTestPage);
        editDialog.setActionText(actionText2);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isTitleHidden(), "Title and Link should not be displayed");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(!teaser.isTitleLinkPresent(), "Title link should not be present");
        assertTrue(!teaser.isDescriptionPresent(), "Teaser description should not be present");
        assertTrue(teaser.isActionLinkPresent(actionExternalText), actionExternalLink + " action link should be present");
        assertTrue(teaser.isActionLinkPresent(actionText2), actionText2 + " action link should be present");
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
        editDialog.openTextTab();
        editDialog.clickTitleFromPage();
        editDialog.clickDescriptionFromPage();
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(teaser.isTitleLinkPresent(testPage, pageTitle),"Page title should be present as title link ");
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
        editDialog.clickTitleFromPage();
        editDialog.clickDescriptionFromPage();
        editDialog.openLinksTab();
        editDialog.clickActionEnabled();
        editDialog.setActionLinkUrl(testPage);
        editDialog.addActionLinkUrl(secondTestPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(teaser.isTitleHidden(), "Title and Link should not be displayed");
        assertTrue(teaser.isTitleLinkPresent(testPage, pageTitle),"Page title should be present as title link ");
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

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Inherit image from current page")
    public void testInheritImageFromCurrentPage() throws ClientException, InterruptedException {
        testInheritImageFromCurrentPage(false);
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): Inherit image from current page")
    public void testInheritImageFromCurrentPage65() throws ClientException, InterruptedException {
        testInheritImageFromCurrentPage(true);
    }

    private void testInheritImageFromCurrentPage(boolean aem65) throws ClientException, InterruptedException {
        // set the page image for the current page
        setPageImage(testPage, climbingAsset);

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, climbingAssetAltText),"image should be rendered with alt text: " + climbingAssetAltText);
        assertTrue(teaser.isImagePresentWithFileName(climbingAssetFormatted),"image should be rendered with file name: " + climbingAssetFormatted);
    }

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Inherit image from current page and no alt text")
    public void testInheritImageFromCurrentPage_isDecorative() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromCurrentPage_isDecorative(false);
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): Inherit image from current page and no alt text")
    public void testInheritImageFromCurrentPage_isDecorative65() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromCurrentPage_isDecorative(true);
    }

    private void testInheritImageFromCurrentPage_isDecorative(boolean aem65) throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the current page
        setPageImage(aem65, testPage, climbingAsset);

        // set image to decorative
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openAssetsTab();
        editDialog.checkInheritAltFromPage();
        editDialog.setAltText(alt);
        editDialog.checkIsDecorative();
        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, ""),"image should be rendered without alt text");
        assertTrue(teaser.isImagePresentWithFileName(climbingAssetFormatted),"image should be rendered with file name: " + climbingAssetFormatted);
    }

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Inherit image from linked page")
    public void testInheritImageFromLinkedPage() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromLinkedPage(false);
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): Inherit image from linked page")
    public void testInheritImageFromLinkedPage65() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromLinkedPage(true);
    }

    private void testInheritImageFromLinkedPage(boolean aem65) throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the linked page
        setPageImage(aem65, secondTestPage, surfingAsset);
        // set the page image for the current page
        setPageImage(aem65, testPage, climbingAsset);

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

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Inherit image from linked page with alt defined in the dialog")
    public void testInheritImageFromLinkedPage_altNotInherited() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromLinkedPage_altNotInherited(false);
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): Inherit image from linked page with alt defined in the dialog")
    public void testInheritImageFromLinkedPage_altNotInherited65() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromLinkedPage_altNotInherited(true);
    }

    private void testInheritImageFromLinkedPage_altNotInherited(boolean aem65) throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the linked page
        setPageImage(aem65, secondTestPage, surfingAsset);
        // set the page image for the current page
        setPageImage(aem65, testPage, climbingAsset);

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

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Inherit image from action page")
    public void testInheritImageFromAction() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromAction(false);
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): Inherit image from action page")
    public void testInheritImageFromAction65() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromAction(true);
    }

    private void testInheritImageFromAction(boolean aem65) throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the action page
        setPageImage(aem65, thirdTestPage, skiingAsset);
        // set the page image for the current page
        setPageImage(aem65, testPage, climbingAsset);

        // set the action URL
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openLinksTab();
        editDialog.clickActionEnabled();
        editDialog.setActionLinkUrl(thirdTestPage);
        Commons.saveConfigureDialog();

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresentWithAltText(testPage, skiingAssetAltText),"image should be rendered with alt text: " + skiingAssetAltText);
        assertTrue(teaser.isImagePresentWithFileName(skiingAssetFormatted),"image should be rendered with file name: " + skiingAssetFormatted);
    }

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Inherit image from action page with alt defined in the dialog")
    public void testInheritImageFromAction_altNotInherited() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromAction_altNotInherited(false);
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): Inherit image from action page with alt defined in the dialog")
    public void testInheritImageFromAction_altNotInherited65() throws ClientException, InterruptedException, TimeoutException {
        testInheritImageFromAction_altNotInherited(true);
    }

    private void testInheritImageFromAction_altNotInherited(boolean aem65) throws ClientException, InterruptedException, TimeoutException {
        // set the page image for the action page
        setPageImage(aem65, thirdTestPage, skiingAsset);
        // set the page image for the current page
        setPageImage(aem65, testPage, climbingAsset);

        // set the action URL
        Commons.openEditDialog(editorPage,cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openLinksTab();
        editDialog.clickActionEnabled();
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
    private void setPageImage(boolean aem65, String page, String asset) throws ClientException, InterruptedException {
        String assetSelector;
        if (aem65) {
            assetSelector = "*[data-foundation-collection-item-id='/content/dam/core-components/" + asset + "'] coral-columnview-item-thumbnail";
        } else {
            assetSelector = "*[data-foundation-collection-item-id='/content/dam/core-components/" + asset + "'] coral-checkbox";
        }
        // set page resource type to page v3
        adminClient.setPageProperty(page, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        PropertiesPage pageProperties = new PropertiesPage(page);
        pageProperties.open();
        $("coral-tab[data-foundation-tracking-event*='images']").click();
        $(".cq-FileUpload-picker").click();
        $("*[data-foundation-collection-item-id='/content/dam/core-components']").click();
        $(assetSelector).click();
        $(".granite-pickerdialog-submit").click();

        // inherit alt text from DAM
        String altValueFromDAMSelector = ".cq-siteadmin-admin-properties coral-checkbox[name='./cq:featuredimage/altValueFromDAM']";
        CoralCheckbox altValueFromDAMCheckbox = new CoralCheckbox(altValueFromDAMSelector);
        altValueFromDAMCheckbox.click();

        pageProperties.saveAndClose();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        editorPage.open();
    }



}
