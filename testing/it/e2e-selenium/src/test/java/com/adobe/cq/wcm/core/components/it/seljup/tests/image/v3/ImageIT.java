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

package com.adobe.cq.wcm.core.components.it.seljup.tests.image.v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.tests.image.ImageTests;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.image.v2.Image;
import com.google.common.collect.ImmutableMap;

@Tag("group2")
public class ImageIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.image.v2.ImageIT {

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        clientlibs = Commons.CLIENTLIBS_IMAGE_V3;
        imageTests = new ImageTests();
        imageTests.setup(adminClient, contextPath, label, Commons.RT_IMAGE_V3, rootPage, defaultPageTemplate, clientlibs, new Image());
    }

    /**
     * Test: Check image map areas are not rendered
     *
     * @throws ClientException
     */
    @Test
    @DisplayName("Test: Check image map areas are not rendered")
    public void testCheckMapAreaNavigationAndResponsiveResize() throws ClientException {
        imageTests.testCheckMapAreaNotAvailable(adminClient);
    }

    /**
     * Test: Lazy loading enabled by default
     */
    @Test
    @DisplayName("Test: Lazy loading enabled by default")
    public void testLazyLoadingEnabled() throws TimeoutException, InterruptedException {
        imageTests.testLazyLoadingEnabled();
    }

    @Test
    @DisplayName("Test: Lazy loading enabled by default")
    public void testLazyLoadingDisabled() throws TimeoutException, InterruptedException {
        imageTests.testLazyLoadingDisabled();
    }

    @Test
    @DisplayName("Test: Set image sizes attribute")
    public void testSizesAttribute() throws TimeoutException, InterruptedException, ClientException {
        createComponentPolicy(Commons.RT_IMAGE_V3.substring(Commons.RT_IMAGE_V3.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("sizes", "(min-width: 36em) 33.3vw"));
            add(new BasicNameValuePair("sizes", "100vw"));
        }});
        imageTests.testSetSizes();
    }

    /**
     * Test: set Alt Text and Title
     */
    @Test
    @DisplayName("Test: set Alt Text and Title")
    public void testAddAltTextAndTitle() throws TimeoutException, InterruptedException {
        imageTests.testAddAltTextAndTitleV3();
    }

    /**
     * Test: set image as decorative
     */
    @Test
    @DisplayName("Test: set image as decorative")
    public void testSetImageAsDecorative() throws TimeoutException, InterruptedException {
        imageTests.testSetImageAsDecorativeV3();
    }

    /**
     * Test: set page featured image with empty alt text
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: set page featured image with empty alt text")
    public void testPageImageWithEmptyAltTextFromPageImage() throws InterruptedException, ClientException {
        imageTests.testPageImageWithEmptyAltTextFromPageImage(false);
    }

    /**
     * Test: set page featured image with empty alt text
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): set page featured image with empty alt text")
    public void testPageImageWithEmptyAltTextFromPageImage65() throws InterruptedException, ClientException {
        imageTests.testPageImageWithEmptyAltTextFromPageImage(true);
    }

    /**
     * Test: set page featured image with alt text from the featured image
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: set page featured image with alt text from the featured image")
    public void testPageImageWithAltTextFromPageImage() throws InterruptedException, ClientException {
        imageTests.testPageImageWithAltTextFromPageImage(false);
    }

    /**
     * Test: set page featured image with alt text from the featured image
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): set page featured image with alt text from the featured image")
    public void testPageImageWithAltTextFromPageImage65() throws InterruptedException, ClientException {
        imageTests.testPageImageWithAltTextFromPageImage(true);
    }

    /**
     * Test: set page featured image with alt text from the image
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: set page featured image with alt text from the image")
    public void testPageImageWithAltTextFromImage() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithAltTextFromImage(false);
    }

    /**
     * Test: set page featured image with alt text from the image
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): set page featured image with alt text from the image")
    public void testPageImageWithAltTextFromImage65() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithAltTextFromImage(true);
    }

    /**
     * Test: set page featured image with decorative image
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: set page featured image with decorative image")
    public void testPageImageWithDecorative() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithDecorative(false);
    }

    /**
     * Test: set page featured image with decorative image
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): set page featured image with decorative image")
    public void testPageImageWithDecorative65() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithDecorative(true);
    }

    /**
     * Test: set page featured image with dragged and dropped image
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: set page featured image with dragged and dropped image")
    public void testPageImageWithDragAndDropImage() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithDragAndDropImage(false);
    }

    /**
     * Test: set page featured image with dragged and dropped image
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): set page featured image with dragged and dropped image")
    public void testPageImageWithDragAndDropImage65() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithDragAndDropImage(true);
    }

    /**
     * Test: set page featured image with linked page
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: set page featured image with linked page")
    public void testPageImageWithLinkedPage() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithLinkedPage(false);
    }

    /**
     * Test: set page featured image with linked page
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test (6.5): set page featured image with linked page")
    public void testPageImageWithLinkedPage65() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithLinkedPage(true);
    }

    /**
     * Test: set link with target on image
     */
    @Test
    @DisplayName("Test: set link with target on image")
    public void testSetLinkWithTarget() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testSetLinkWithTarget();
    }

    /**
     * Test: set asset from DAM without description
     */
    @Test
    @Override
    @DisplayName("Test: set asset from DAM without description")
    public void testSetAssetWithoutDescription() throws TimeoutException, InterruptedException {
        imageTests.testSetAssetWithoutDescriptionV3();
    }

    @Test
    @Ignore
    @DisplayName("Test : NextGen DM image smart crop dialog.")
    public void testSmartCropDialogOnNGDMImageV3() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testNGDMSmartCropDialogImageV3();
    }

    @Test
    @Ignore
    @DisplayName("Test : NextGen DM image smart crop - select aspect ratio from list.")
    public void testSmartCropDialogOnNGDMImageV3_aspectRatioSelection() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testNGDMSmartCropDialogImageV3_aspectRatioSelection();
    }

    @Test
    @Ignore
    @DisplayName("Test : NextGen DM image smart crop - select custom aspect ratio.")
    public void testSmartCropDialogOnNGDMImageV3_customAspectRatio() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testNGDMSmartCropDialogImageV3_customAspectRatio();
    }

    @Test
    @Ignore
    @DisplayName("Test : NextGen DM image smart crop - flip aspect ratio.")
    public void testSmartCropDialogOnNGDMImageV3_flipAspectRatio() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testNGDMSmartCropDialogImageV3_aspectRatioFlip();
    }
}
