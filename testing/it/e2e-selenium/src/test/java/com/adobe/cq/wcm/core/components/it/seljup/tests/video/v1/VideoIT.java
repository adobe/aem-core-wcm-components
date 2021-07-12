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

package com.adobe.cq.wcm.core.components.it.seljup.tests.video.v1;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.junit.annotations.Author;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pageobject.granite.LoginPage;
import com.adobe.cq.testing.selenium.pagewidgets.sidepanel.SidePanel;
import com.adobe.cq.testing.selenium.utils.DisableTour;
import com.adobe.cq.testing.selenium.utils.TestContentBuilder;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.video.VideoEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.video.v1.Video;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.adobe.cq.testing.selenium.Constants.GROUPID_CONTENT_AUTHORS;
import static com.adobe.cq.testing.selenium.pagewidgets.Helpers.setAffinityCookie;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class VideoIT extends AuthorBaseUITest {

    private static String componentName             = "video";
    private static String testAssetsPath            = "/content/dam/core-components-examples";
    private static String testVideoPath             = testAssetsPath + "/library/videoSample/mp4";
    private static String assetFilterSelect         = "coral-select.assetfilter";
    private static String assetFilterVideosOption   = "coral-selectlist-item[value='Videos']";

    private String proxyComponentPath;
    private SidePanel sidePanel;
    private WebDriver webDriver;
    private VideoEditDialog editDialog;

    protected String testPage;
    protected EditorPage editorPage;
    protected Video video;
    protected String cmpPath;
    protected String videoRT;

    private void setupResources() {
        videoRT = Commons.rtVideo_v1;
    }

    protected void setup() throws ClientException {
        webDriver = WebDriverRunner.getWebDriver();
        testPage = authorClient.createPage("video-v1", "Video v1", rootPage, defaultPageTemplate, HttpStatus.SC_OK).getSlingPath();
        proxyComponentPath = Commons.creatProxyComponent(adminClient, videoRT, "Proxy Video", componentName);
        addPathtoComponentPolicy(responsiveGridPath, proxyComponentPath);
        cmpPath = Commons.addComponent(adminClient, proxyComponentPath,testPage + Commons.relParentCompPath, componentName, null);
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        video = new Video();
        editDialog = video.getEditDialog();
    }

    /**
     * Opens the side panel if it is closed.
     * @return
     */
    private void openSidePanel() {
        sidePanel = new SidePanel();
        if(sidePanel.isHidden()) {
            sidePanel.show();
        }
    }

    /**
     * Filter and show only video assets in the side panel.
     * @return
     */
    private void selectVideoAssets() {
        new WebDriverWait(webDriver, CoreComponentConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(assetFilterSelect)));
        sidePanel.element().find(assetFilterSelect).click();

        new WebDriverWait(webDriver, CoreComponentConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(assetFilterVideosOption)));
        sidePanel.element().find(assetFilterVideosOption).click();
    }

    /**
     * Adds minimum configuration needed for the component to be displayed.
     * @param closeDialog
     * @return
     */
    private void addMinimumConfig(Boolean closeDialog) {
        try {
            if (!Commons.iseditDialogVisible()) {
                Commons.openEditDialog(editorPage, cmpPath);
            }

            editDialog.openVideoTab();
            editDialog.uploadVideoFromSidePanel(testVideoPath);

            if (closeDialog) {
                Commons.saveConfigureDialog();
            }
        } catch (Exception e) {
            System.out.println("Could not initialize component.");
        }
    }

    /**
     * Adds minimum configuration needed for the component to be displayed.
     * @return
     */
    private void addMinimumConfig() {
        addMinimumConfig(false);
    }

    /**
     * Activates preview mode and switch the context to the content iframe.
     * @return
     */
    private void enterPreviewMode() {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
    }

    @BeforeEach
    public void setupBefore() throws ClientException {
        setupResources();
        setup();
        openSidePanel();
        selectVideoAssets();
    }

    /**
     * Overrides AuthorBaseUITest.getUserGroupMembership
     * Allows login as administrator in order to cleanup the temporary sites.
     * @return
     */
    public List<String> getUserGroupMembership() {
        return Arrays.asList("administrators", "administrators");
    }

    @AfterEach
    public void cleanup() throws ClientException, InterruptedException {
        Commons.deleteProxyComponent(adminClient, proxyComponentPath);
        authorClient.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        authorClient.deletePath(proxyComponentPath.replace("/components/video", ""), HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Test: Add video component")
    public void testAddVideo() throws InterruptedException, TimeoutException {
        addMinimumConfig(true);
        enterPreviewMode();

        assertTrue(video.element().isDisplayed(), "video is set");
    }

    @Test
    @DisplayName("Test: Video component is not added")
    public void testVideoIsNotAdded() throws InterruptedException, TimeoutException {
        enterPreviewMode();

        assertFalse(video.element().isDisplayed(), "video is not visible");
    }

    @Test
    @DisplayName("Test: Check video boxes")
    public void testCheckBoxes() throws InterruptedException, TimeoutException {
        addMinimumConfig();

        editDialog.openPropertiesTab();
        editDialog.clickLoopEnabled();

        Commons.saveConfigureDialog();

        enterPreviewMode();

        assertTrue(video.element().find("video").getAttribute("loop").equals("true"), "loop is set");
    }
}
