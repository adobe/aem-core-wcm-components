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

package com.adobe.cq.wcm.core.components.it.seljup.tests.image;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.image.BaseImage;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.image.ImageEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static com.adobe.cq.testing.selenium.utils.ElementUtils.clickableClick;
import static com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest.adminClient;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.*;

public class ImageTests {

    private static String testAssetsPath = "/content/dam/core-components";
    private static String testImagePath = testAssetsPath + "/core-comp-test-image.jpg";
    private static String testImageWithoutDescriptionPath = testAssetsPath + "/Adobe_Systems_logo_and_wordmark.png";
    private static String altText = "Return to Arkham";
    private static String captionText = "The Last Guardian";
    private static String originalDamTitle = "Beach house";
    private static String originalDamDescription = "House on a beach with blue sky";
    private static String logoNodeName = "Adobe_Systems_logo_and_wordmark.png";
    private static String logoFileName = "adobe-systems-logo-and-wordmark.png";
    private static String imageFileName = "core-comp-test-image.jpeg";
    private static String pageImageAlt = "page image alt";
    private static String climbingAsset = "AdobeStock_140634652_climbing.jpeg";
    private static String climbingAssetFormatted = StringUtils.lowerCase(climbingAsset).replace("_", "-");
    private static String climbingAssetAltText = "Rock Climbing and Bouldering above the lake and mountains";

    private String testPage;
    private String proxyPath;
    private String compPath;
    private String policyPath;
    private PageEditorPage editorPage;
    private BaseImage image;
    private String redirectPage;
    private PropertiesPage propertiesPage;
    private String contextPath;

    public String getProxyPath() {
        return proxyPath;
    }

    public void setup(CQClient client, String contextPath, String label, String imageRT, String rootPage,
                      String defaultPageTemplate, String clientlibs, BaseImage image) throws ClientException {
        // 1.
        testPage = client.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        redirectPage = client.createPage("redirectPage", "Redirect Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        propertiesPage = new PropertiesPage(testPage);

        // 2.
        policyPath = Commons.createPagePolicy(client, defaultPageTemplate, label, new HashMap<String, String>() {{
           put("clientlibs", clientlibs);
        }});

        // 4.
        proxyPath = imageRT;

        // 6.
        compPath = Commons.addComponentWithRetry(client, proxyPath,testPage + Commons.relParentCompPath, "image", null,
                RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK, HttpStatus.SC_CREATED);

        // 7.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        this.image = image;

        this.contextPath = contextPath;

    }

    public void cleanup(CQClient client) throws ClientException, InterruptedException {
        client.deletePageWithRetry(testPage, true,false,
                RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
    }

    public void setMinimalProps() throws InterruptedException, TimeoutException {
        Commons.selectInAutocomplete(image.getAssetPath(), testAssetsPath);
        Commons.openEditDialog(editorPage, compPath);
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.uploadImageFromSidePanel(testImagePath);
        editDialog.setAltText(altText);

    }

    public void testAddImageAndAltText() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        setMinimalProps();
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImageSet(testPage),"Image component should be present");
        assertTrue(image.isAltTextSet(altText),"Alternate text should be set");
    }

    public void testSetLink() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        setMinimalProps();
        image.getEditDialog().setLinkURL(redirectPage);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        image.imageClick();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(Commons.getCurrentUrl().endsWith(redirectPage+".html"),"Current page should be link URL set after redirection");
    }

    public void testSetCaption() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        setMinimalProps();
        image.getEditDialog().setTitle(captionText);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isTitleSet(captionText),"Title should be set");
    }

    public void testSetCaptionAsPopup() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        setMinimalProps();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.setTitle(captionText);
        editDialog.checkCaptionAsPopUp();
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImageWithTitle(captionText),"Title should be set");
    }

    public void testSetImageAsDecorative() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        setMinimalProps();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.setTitle(captionText);
        editDialog.checkDecorative();
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(!image.isImageWithAltText(),"image should be rendered without alt text");
    }

    public void testAddImage() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltTextAndTitle(testPage, originalDamDescription, originalDamTitle), "Image should be present with alt text " + originalDamDescription
                + " and title " + originalDamTitle);
    }

    public void testAddAltTextAndTitle() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        editDialog.checkAltValueFromDAM();
        editDialog.checkTitleValueFromDAM();
        editDialog.setAltText(altText);
        editDialog.setTitle(captionText);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltTextAndTitle(testPage, altText, captionText), "Image should be present with alt text " + altText
                + " and title " + captionText);
    }

    public void testSetAssetWithoutDescription() throws InterruptedException, TimeoutException {
        Commons.openSidePanel();
        dragImageWithoutDescription();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        Commons.saveConfigureDialog();
        String assetWithoutDescriptionErrorMessageSelector = "coral-tooltip[variant='error'] coral-tooltip-content";
        assertEquals("Error: Please provide an asset which has a description that can be used as alt text.", $(assetWithoutDescriptionErrorMessageSelector).innerText());
    }

    public void testSetAssetWithoutDescriptionV3() throws InterruptedException, TimeoutException {
        Commons.openSidePanel();
        dragImageWithoutDescription();
        Commons.saveConfigureDialog();
        String assetWithoutDescriptionErrorMessageSelector = "coral-tooltip[variant='error'] coral-tooltip-content";
        String errorIcon = "input[name='./alt'] + coral-icon[icon='alert']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", $(errorIcon));
        assertEquals("Error: Please provide an asset which has a description that can be used as alt text.", $(assetWithoutDescriptionErrorMessageSelector).innerText());
    }

    public void testAddAltTextAndTitleV3() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.checkAltValueFromDAM();
        editDialog.setAltText(altText);
        editDialog.openMetadataTab();
        editDialog.checkTitleValueFromDAM();
        editDialog.setTitle(captionText);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltTextAndTitle(testPage, altText, captionText), "Image should be present with alt text " + altText
                + " and title " + captionText);
    }

    public void testDisableCaptionAsPopup() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        editDialog.checkCaptionAsPopUp();
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, originalDamDescription), "Image should be present with alt text " + originalDamDescription);
        assertTrue(image.isTitleSet(originalDamTitle),"Title should be set");
    }

    public void testSetImageAsDecorativeV2() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        editDialog.setLinkURL(redirectPage);
        editDialog.checkDecorative();
        assertTrue(!image.isImageWithAltText(), "Image with alt text should not be present");
        assertTrue(!image.isLinkSet(), "Image link should not be set");
    }

    public void testSetImageAsDecorativeV3() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        editDialog.setLinkURL(redirectPage);
        editDialog.openAssetTab();
        editDialog.checkDecorative();
        assertTrue(!image.isImageWithAltText(), "Image with alt text should not be present");
        assertTrue(!image.isLinkSet(), "Image link should not be set");
    }

    public void testSetLinkV2() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        image.getEditDialog().setLinkURL(redirectPage);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        image.imageClick();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(Commons.getCurrentUrl().endsWith(redirectPage+".html"),"Current page should be link URL set after redirection");
    }

    public void testCheckMapAreaNavigationAndResponsiveResize(CQClient client) throws ClientException, TimeoutException, InterruptedException {
        // persist a test image map with a single map area
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("imageMap", "[rect(0,0,226,230)\""+redirectPage+"\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]");
        data.put("fileReference", testImagePath);
        Commons.editNodeProperties(client, compPath, data, 200);

        // refresh the component
        editorPage.refresh();

        // verify the map area is available
        Commons.switchContext("ContentFrame");
        assertTrue(image.isAreaElementPresent(), "Area element should be present");
        Commons.switchToDefaultContext();

        // switch to the content frame, click the area link and verify navigation
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        image.clickAreaElement();
        assertTrue(Commons.getCurrentUrl().contains(redirectPage),"redirection should happen");
        Commons.switchToDefaultContext();

        // navigate back to the test page
        editorPage.open();
        editorPage.enterEditMode();
        Commons.switchContext("ContentFrame");
        image.resizeImageElementWidth(300);

        assertTrue(image.isAreaCoordinatesCorrectlySet(new String[]{"0","0","58","38"}), "Area coordinates should be correctly set");
    }

    public void testCheckMapAreaNotAvailable(CQClient client) throws ClientException {
        // persist a test image map with a single map area
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("imageMap", "[rect(0,0,226,230)\""+redirectPage+"\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]");
        data.put("fileReference", testImagePath);
        Commons.editNodeProperties(client, compPath, data, 200);

        // refresh the component
        editorPage.refresh();

        // verify the map area is not available
        Commons.switchContext("ContentFrame");
        assertFalse(image.isAreaElementPresent(), "Area element should not be present");
    }

    public void testLazyLoadingEnabled() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImageWithLazyLoadingEnabled(), "Image with native lazy loading enabled should be present");
    }

    public void testPageImageWithEmptyAltTextFromPageImage(boolean aem65) throws InterruptedException, ClientException {
        setPageImage(aem65);
        editorPage.open();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, ""),"image should be rendered with an empty alt text");
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithAltTextFromPageImage(boolean aem65) throws InterruptedException, ClientException {
        setPageImage(aem65);
        setPageImageAlt(pageImageAlt);
        editorPage.open();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, pageImageAlt),"image should be rendered with alt text: " + pageImageAlt);
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithAltTextFromImage(boolean aem65) throws TimeoutException, InterruptedException, ClientException {
        setPageImage(aem65);
        setPageImageAlt(pageImageAlt);
        editorPage.open();
        ImageEditDialog editDialog = image.getEditDialog();
        Commons.openEditDialog(editorPage, compPath);
        editDialog.checkAltValueFromPageImage();
        editDialog.setAltText(altText);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, altText),"image should be rendered with alt text: " + altText);
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithDecorative(boolean aem65) throws TimeoutException, InterruptedException, ClientException {
        setPageImage(aem65);
        setPageImageAlt(pageImageAlt);
        editorPage.open();
        ImageEditDialog editDialog = image.getEditDialog();
        Commons.openEditDialog(editorPage, compPath);
        editDialog.checkDecorative();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, ""),"image should be rendered with an empty alt text");
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithDragAndDropImage(boolean aem65) throws TimeoutException, InterruptedException, ClientException {
        setPageImage(aem65);
        editorPage.open();
        Commons.openSidePanel();
        Commons.selectInAutocomplete(image.getAssetPath(), testAssetsPath);
        Commons.openEditDialog(editorPage, compPath);
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, originalDamDescription),"image should be rendered with alt text: " + originalDamDescription);
        assertTrue(image.isImagePresentWithFileName(imageFileName),"image should be rendered with file name: " + imageFileName);
    }

    public void testPageImageWithLinkedPage(boolean aem65) throws TimeoutException, InterruptedException, ClientException {
        setPageImage(aem65, redirectPage, climbingAsset, true);
        setPageImage(aem65, testPage, logoNodeName, true);
        editorPage.open();
        ImageEditDialog editDialog = image.getEditDialog();
        Commons.openEditDialog(editorPage, compPath);
        editDialog.openMetadataTab();
        editDialog.setLinkURL(redirectPage);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAltText(testPage, climbingAssetAltText),"image should be rendered with an empty alt text");
        assertTrue(image.isImagePresentWithFileName(climbingAssetFormatted),"image should be rendered with file name: " + climbingAssetFormatted);
        image.imageClick();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(Commons.getCurrentUrl().endsWith(redirectPage+".html"),"Current page should be link URL set after redirection");
    }

    public void testSetLinkWithTarget() throws TimeoutException, InterruptedException {
        Commons.openSidePanel();
        dragImage();
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.openMetadataTab();
        image.getEditDialog().setLinkURL(redirectPage);
        image.getEditDialog().clickLinkTarget();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        String link = (contextPath != null)? contextPath + redirectPage + ".html": redirectPage + ".html";
        String target = "_blank";
        assertTrue(image.checkLinkPresentWithTarget(link, target),"Title with link " + link + " and target "+ target + " should be present");
    }

    // ----------------------------------------------------------
    // private stuff
    // ----------------------------------------------------------

    private void dragImage() throws TimeoutException, InterruptedException {
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.setAssetFilter(testAssetsPath);
        Commons.openEditDialog(editorPage, compPath);
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
    }

    private void dragImageWithoutDescription() throws TimeoutException, InterruptedException {
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.setAssetFilter(testAssetsPath);
        Commons.openEditDialog(editorPage, compPath);
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImageWithoutDescriptionPath);
    }

    /**
     * Sets the featured image of the page.
     */
    private void setPageImage(boolean aem65) throws ClientException, InterruptedException {
        setPageImage(aem65, testPage, logoNodeName, false);
    }

    /**
     * Sets the featured image for a page.
     */
    private void setPageImage(boolean aem65, String page, String asset, boolean altFromDam) throws ClientException, InterruptedException {
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
        clickableClick($(".granite-pickerdialog-submit"));
        if (altFromDam) {
            // inherit alt text from DAM
            String altValueFromDAMSelector = ".cq-siteadmin-admin-properties coral-checkbox[name='./cq:featuredimage/altValueFromDAM']";
            CoralCheckbox altValueFromDAMCheckbox = new CoralCheckbox(altValueFromDAMSelector);
            altValueFromDAMCheckbox.click();
        }
        pageProperties.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    /**
     * Sets the alt text of the featured image of the page.
     */
    private void setPageImageAlt(String text) throws InterruptedException {
        propertiesPage.open();
        // open the Images tab
        $("coral-tab[data-foundation-tracking-event*='images']").click();
        SelenideElement altField = $("[name='./cq:featuredimage/alt']");
        altField.clear();
        altField.sendKeys(text);
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

}
