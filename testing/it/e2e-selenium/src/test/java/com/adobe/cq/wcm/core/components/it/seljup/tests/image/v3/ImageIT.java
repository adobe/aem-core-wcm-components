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

import com.adobe.cq.wcm.core.components.it.seljup.components.image.v2.Image;
import com.adobe.cq.wcm.core.components.it.seljup.tests.image.ImageTests;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

@Tag("group2")
public class ImageIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.image.v2.ImageIT {

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        clientlibs = "core.wcm.components.image.v3";
        imageTests = new ImageTests();
        imageTests.setup(adminClient, label, Commons.rtImage_v3, rootPage, defaultPageTemplate, clientlibs, new Image());
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
    @Test
    @DisplayName("Test: set page featured image with empty alt text")
    public void testPageImageWithEmptyAltTextFromPageImage() throws InterruptedException, ClientException {
        imageTests.testPageImageWithEmptyAltTextFromPageImage();
    }

    /**
     * Test: set page featured image with alt text from the featured image
     */
    @Test
    @DisplayName("Test: set page featured image with alt text from the featured image")
    public void testPageImageWithAltTextFromPageImage() throws InterruptedException, ClientException {
        imageTests.testPageImageWithAltTextFromPageImage();
    }

    /**
     * Test: set page featured image with alt text from the image
     */
    @Test
    @DisplayName("Test: set page featured image with alt text from the image")
    public void testPageImageWithAltTextFromImage() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithAltTextFromImage();
    }

    /**
     * Test: set page featured image with decorative image
     */
    @Test
    @DisplayName("Test: set page featured image with decorative image")
    public void testPageImageWithDecorative() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithDecorative();
    }

    /**
     * Test: set page featured image with dragged and dropped image
     */
    @Test
    @DisplayName("Test: set page featured image with dragged and dropped image")
    public void testPageImageWithDragAndDropImage() throws TimeoutException, InterruptedException, ClientException {
        imageTests.testPageImageWithDragAndDropImage();
    }

}
