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

import com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v2.TeaserEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v2.Teaser;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TeaserIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.teaser.v1.TeaserIT {

    protected Teaser teaser;

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
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinksTab();
        editDialog.setLinkURL(testPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(teaser.isTitleHidden(), "Title and Link should not be displayed");
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
}
