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

package com.adobe.cq.wcm.core.components.it.seljup.tests.teaser.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.*;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.AssetFinder;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v1.Teaser;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v1.TeaserEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("group3")
public class TeaserIT extends AuthorBaseUITest {
    protected static String  testAssetsPath                   = "/content/dam/core-components";
    protected static String  testImagePath                    = testAssetsPath + "/core-comp-test-image.jpg";
    protected static String  preTitle                         = "Teaser PreTitle";
    protected static String  title                            = "Teaser Title";
    protected static String  description                      = "Teaser Description";
    protected static String  alt                              = "Teaser alt text";
    private static String  pageName                         = "teaser-page";
    protected static String  pageTitle                        = "teaser_page";
    private static String  secondPageName                   = "teaser-second-page";
    protected static String  secondPageTitle                  = "teaser_second_page";
    private static String  thirdPageName                   = "teaser-third-page";
    protected static String  thirdPageTitle                  = "teaser_third_page";
    public static String  pageDescription                  = "teaser page description";
    protected static String  actionText2                      = "Action Text 2";
    protected static String  actionExternalLink               = "http://www.adobe.com";
    protected static String  actionExternalText               = "Adobe";
    private static String componentName                     = "teaser";
    protected static String climbingAsset                   = "AdobeStock_140634652_climbing.jpeg";
    protected static String climbingAssetAltText            = "Rock Climbing and Bouldering above the lake and mountains";
    protected static String climbingAssetFormatted          = format(climbingAsset);
    protected static String surfingAsset                    = "AdobeStock_175749320_surfing.jpg";
    protected static String surfingAssetAltText             = "Surfers. Balangan beach. Bali, Indonesia.";
    protected static String surfingAssetFormatted           = format(surfingAsset).replace("jpg", "jpeg");
    protected static String skiingAsset                     = "AdobeStock_185234795_skiing.jpeg";
    protected static String skiingAssetAltText              = "A skier does action skiing at the Rolle Pass in the Dolomites, Italy.";
    protected static String skiingAssetFormatted            = format(skiingAsset);

    protected String clientlibs;
    protected String teaserRT;
    protected String testPage;
    protected String secondTestPage;
    protected String thirdTestPage;
    protected String cmpPath;
    protected EditorPage editorPage;
    protected Teaser teaser;
    protected AssetFinder assetFinder;

    protected void setupResources() {
        teaserRT = Commons.RT_TEASER_V1;
        clientlibs = Commons.CLIENTLIBS_TEASER_V1;
        teaser = new Teaser();
    }

    protected void setup() throws ClientException, InterruptedException {
        testPage = authorClient.createPage(pageName, pageTitle, rootPage, defaultPageTemplate).getSlingPath();
        secondTestPage = authorClient.createPage(secondPageName, secondPageTitle, rootPage, defaultPageTemplate).getSlingPath();
        thirdTestPage = authorClient.createPage(thirdPageName, thirdPageTitle, rootPage, defaultPageTemplate).getSlingPath();

        //Update test page description
        java.util.List<NameValuePair> props = new ArrayList();
        props.add(new BasicNameValuePair("jcr:description",pageDescription));
        Commons.setPageProperties(authorClient, testPage, props, 200, 201);

        createPagePolicy(new HashMap<String, String>() {{put("clientlibs", clientlibs);}});

        cmpPath = Commons.addComponentWithRetry(authorClient, teaserRT,testPage + Commons.relParentCompPath, componentName, null,
                RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpServletResponse.SC_OK, HttpServletResponse.SC_CREATED);

        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        assetFinder = new AssetFinder();
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
     * After Test Case
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        authorClient.deletePageWithRetry(secondTestPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
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
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinkAndActionsTab();
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
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinkAndActionsTab();
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
        editDialog.setTitle(title);
        editDialog.setDescription(description);
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
    public void testHideElementsTeaser() throws TimeoutException, InterruptedException, ClientException {
        createComponentPolicy("/teaser-v1", new HashMap<String, String>() {{
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
     * Test: Links to elements for Teaser
     *
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws ClientException
     */
    @Test
    @DisplayName("Test: Links to elements for Teaser")
    public void testLinksToElementsTeaser() throws TimeoutException, InterruptedException, ClientException {

        createComponentPolicy("/teaser-v1", new HashMap<String, String>() {{
            put("titleLinkHidden", "true");
            put("imageLinkHidden", "true");
        }});

        Commons.openSidePanel();
        assetFinder.setFiltersPath(testAssetsPath);
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinkAndActionsTab();
        editDialog.setLinkURL(testPage);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(teaser.isImagePresent(testPage), "Image should be present");
        assertTrue(!teaser.isTitleLinkPresent(testPage, title),"Title link should not be present");
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
        createComponentPolicy("/teaser-v1", new HashMap<String, String>() {{
            put("actionsDisabled", "true");
        }});

        Commons.openSidePanel();
        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();

        editDialog.openLinkAndActionsTab();
        assertTrue(editDialog.isActionEnabledCheckDisabled() && !editDialog.isActionEnabledChecked(),
            "ActionEnabled checkbox should be disabled and unchecked");
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
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openTextTab();
        editDialog.clickTitleFromPage();
        editDialog.clickDescriptionFromPage();
        editDialog.openLinkAndActionsTab();
        editDialog.clickActionEnabled();
        editDialog.setActionLinkUrl(testPage);
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(secondTestPage);
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
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.openLinkAndActionsTab();
        editDialog.clickActionEnabled();
        editDialog.setActionLinkUrl(actionExternalLink);
        editDialog.setActionText(actionExternalText);
        editDialog.addActionLink();
        editDialog.setActionLinkUrl(secondTestPage);
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
     * Test: Checkbox-Textfield Tuple
     *
     * 1. open the edit dialog
     * 2. switch to the 'Text' tab
     * 3. populate the title tuple textfield
     * 4. open the 'Link & Actions' tab
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
        editDialog.openLinkAndActionsTab();

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

    /**
     * Test: Check the title type select dropdown to not be displayed when showTitleType is set to false in a policy.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the title type select dropdown to not be displayed when showTitleType is set to false in a policy.")
    public void testNoTitleTypeSelectDropdownDisplayed() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(teaserRT.substring(teaserRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("titleType", "h4"));
            add(new BasicNameValuePair("showTitleType", "false"));
            add(new BasicNameValuePair("allowedTypes", "h1"));
            add(new BasicNameValuePair("allowedTypes", "h2"));
            add(new BasicNameValuePair("allowedTypes", "h3"));
            add(new BasicNameValuePair("allowedTypes", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h6"));
            add(new BasicNameValuePair("allowedTypes@TypeHint", "String[]"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openTextTab();
        assertFalse(editDialog.isTitleTypeSelectDropdownDisplayed());
    }

    /**
     * Test: Check the default option selected in the title type select dropdown if the default type set in a policy is a valid option.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the default option selected in the title type select dropdown if the default type set in a policy is a valid option.")
    public void testTitleTypeSelectDropdownValueUsingValidOption() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(teaserRT.substring(teaserRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("titleType", "h4"));
            add(new BasicNameValuePair("showTitleType", "true"));
            add(new BasicNameValuePair("allowedTypes", "h1"));
            add(new BasicNameValuePair("allowedTypes", "h2"));
            add(new BasicNameValuePair("allowedTypes", "h3"));
            add(new BasicNameValuePair("allowedTypes", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h6"));
            add(new BasicNameValuePair("allowedTypes@TypeHint", "String[]"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openTextTab();
        assertTrue(editDialog.isTitleTypeSelectDropdownDisplayed());
        assertEquals(editDialog.getTitleTypeSelectDropdownDefaultSelectedText(), "h4");
    }

    /**
     * Test: Check the default option selected in the title type select dropdown if the default type set in a policy is an invalid option.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the default option selected in the title type select dropdown if the default type set in a policy is an invalid option.")
    public void testTitleTypeSelectDropdownValueUsingInvalidOption() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(teaserRT.substring(teaserRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("titleType", "h5"));
            add(new BasicNameValuePair("showTitleType", "true"));
            add(new BasicNameValuePair("allowedTypes", "h3"));
            add(new BasicNameValuePair("allowedTypes", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h6"));
            add(new BasicNameValuePair("allowedTypes@TypeHint", "String[]"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openTextTab();
        assertTrue(editDialog.isTitleTypeSelectDropdownDisplayed());
        assertEquals(editDialog.getTitleTypeSelectDropdownDefaultSelectedText(), "h3");
    }

    @Test
    @DisplayName("Test: Check the title type selection when no allowed types are defined (backwards compatibility mode).")
    public void testTypeTypeSelectDropdownNoAllowedTypes() throws ClientException, InterruptedException, TimeoutException {
        createComponentPolicy(teaserRT.substring(teaserRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("showTitleType", "true"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TeaserEditDialog editDialog = teaser.getEditDialog();
        editDialog.openTextTab();
        assertTrue(editDialog.isTitleTypeSelectDropdownDisplayed());
        assertEquals("(default)", editDialog.getTitleTypeSelectDropdownDefaultSelectedText());
    }
    // ----------------------------------------------------------
    // private stuff
    // ----------------------------------------------------------

    private static String format(String name) {
        return StringUtils.lowerCase(name).replace("_", "-");
    }

}
