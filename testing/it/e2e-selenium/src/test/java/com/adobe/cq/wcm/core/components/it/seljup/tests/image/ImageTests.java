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

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.wcm.core.components.it.seljup.components.image.BaseImage;
import com.adobe.cq.wcm.core.components.it.seljup.components.image.ImageEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest.adminClient;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageTests {

    private static String testAssetsPath         = "/content/dam/core-components";
    private static String testImagePath          = testAssetsPath + "/core-comp-test-image.jpg";
    private static String altText                = "Return to Arkham";
    private static String captionText            = "The Last Guardian";
    private static String originalDamTitle       = "Beach house";
    private static String originalDamDescription = "House on a beach with blue sky";
    private static String logoFileName           = "adobe-systems-logo-and-wordmark.png";
    private static String imageFileName          = "core-comp-test-image.jpeg";
    private static String pageImageAlt           = "page image alt";


    private String testPage;
    private String proxyPath;
    private String compPath;
    private String policyPath;
    private PageEditorPage editorPage;
    private BaseImage image;
    private String redirectPage;
    private PropertiesPage propertiesPage;

    public String getProxyPath() {
        return proxyPath;
    }

    public void setup(CQClient client, String label, String imageRT, String rootPage,
                      String defaultPageTemplate, String clientlibs, BaseImage image) throws ClientException {
        // 1.
        testPage = client.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        redirectPage = client.createPage("redirectPage", "Redirect Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        propertiesPage = new PropertiesPage(testPage);

        // 2.
        String policySuffix = "/structure/page/new_policy";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:title", "New Policy");
        data.put("sling:resourceType", "wcm/core/components/policy/policy");
        data.put("clientlibs", clientlibs);
        String policyPath1 = "/conf/"+ label + "/settings/wcm/policies/core-component/components";
        policyPath = Commons.createPolicy(client, policySuffix, data , policyPath1);

        // 3.
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content";
        data.clear();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(client,"",data, policyAssignmentPath);

        // 4.
        proxyPath = Commons.createProxyComponent(client, imageRT, Commons.proxyPath, null, null);

        // 6.
        compPath = Commons.addComponent(client, proxyPath,testPage + Commons.relParentCompPath, "image", null);

        // 7.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        this.image = image;

    }



    public void cleanup(CQClient client) throws ClientException, InterruptedException {
        client.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        Commons.deleteProxyComponent(client, proxyPath);
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
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
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
        assertTrue(image.isImagePresentWithAtlTextAndTitle(testPage, originalDamDescription, originalDamTitle), "Image should be present with alt text " + originalDamDescription
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
        assertTrue(image.isImagePresentWithAtlTextAndTitle(testPage, altText, captionText), "Image should be present with alt text " + altText
                + " and title " + captionText);
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
        assertTrue(image.isImagePresentWithAtlTextAndTitle(testPage, altText, captionText), "Image should be present with alt text " + altText
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
        assertTrue(image.isImagePresentWithAtlText(testPage, originalDamDescription), "Image should be present with alt text " + originalDamDescription);
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
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
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
    public void testPageImageWithEmptyAltTextFromPageImage() throws InterruptedException, ClientException {
        setPageImage();
        editorPage.open();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAtlText(testPage, ""),"image should be rendered with an empty alt text");
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithAltTextFromPageImage() throws InterruptedException, ClientException {
        setPageImage();
        setPageImageAlt(pageImageAlt);
        editorPage.open();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAtlText(testPage, pageImageAlt),"image should be rendered with alt text: " + pageImageAlt);
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithAltTextFromImage() throws TimeoutException, InterruptedException, ClientException {
        setPageImage();
        setPageImageAlt(pageImageAlt);
        editorPage.open();
        ImageEditDialog editDialog = image.getEditDialog();
        Commons.openEditDialog(editorPage, compPath);
        editDialog.checkAltValueFromPageImage();
        editDialog.setAltText(altText);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAtlText(testPage, altText),"image should be rendered with alt text: " + altText);
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithDecorative() throws TimeoutException, InterruptedException, ClientException {
        setPageImage();
        setPageImageAlt(pageImageAlt);
        editorPage.open();
        ImageEditDialog editDialog = image.getEditDialog();
        Commons.openEditDialog(editorPage, compPath);
        editDialog.checkDecorative();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAtlText(testPage, ""),"image should be rendered with an empty alt text");
        assertTrue(image.isImagePresentWithFileName(logoFileName),"image should be rendered with file name: " + logoFileName);
    }

    public void testPageImageWithDragAndDropImage() throws TimeoutException, InterruptedException, ClientException {
        setPageImage();
        editorPage.open();
        Commons.openSidePanel();
        Commons.selectInAutocomplete(image.getAssetPath(), testAssetsPath);
        Commons.openEditDialog(editorPage, compPath);
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.checkImageFromPageImage();
        editDialog.uploadImageFromSidePanel(testImagePath);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(image.isImagePresentWithAtlText(testPage, originalDamDescription),"image should be rendered with alt text: " + originalDamDescription);
        assertTrue(image.isImagePresentWithFileName(imageFileName),"image should be rendered with file name: " + imageFileName);
    }

    // ----------------------------------------------------------
    // private stuff
    // ----------------------------------------------------------

    private void dragImage() throws TimeoutException, InterruptedException {
        ImageEditDialog editDialog = image.getEditDialog();
        editDialog.setAssetFilter(testAssetsPath);
        Commons.openEditDialog(editorPage, compPath);
        editDialog.uploadImageFromSidePanel(testImagePath);
    }

    /**
     * Sets the featured image of the page.
     */
    private void setPageImage() throws ClientException, InterruptedException {
        // set page resource type to page v3
        adminClient.setPageProperty(testPage, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        propertiesPage.open();
        $("coral-tab[data-foundation-tracking-event*='images']").click();
        $(".cq-FileUpload-picker").click();
        $("*[data-foundation-collection-item-id='/content/dam/core-components']").click();
        $("*[data-foundation-collection-item-id='/content/dam/core-components/Adobe_Systems_logo_and_wordmark.png'] coral-checkbox").click();
        $(".granite-pickerdialog-submit").click();
        propertiesPage.saveAndClose();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
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
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
    }

}
