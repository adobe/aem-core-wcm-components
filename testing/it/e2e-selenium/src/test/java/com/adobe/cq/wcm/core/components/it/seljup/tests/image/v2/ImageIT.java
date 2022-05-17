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

package com.adobe.cq.wcm.core.components.it.seljup.tests.image.v2;

import java.util.concurrent.TimeoutException;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.tests.image.ImageTests;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.image.v2.Image;

@Tag("group2")
public class ImageIT extends AuthorBaseUITest {

    protected ImageTests imageTests;
    protected String clientlibs = Commons.CLIENTLIBS_IMAGE_V2;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        imageTests = new ImageTests();
        imageTests.setup(adminClient, contextPath, label, Commons.RT_IMAGE_V2, rootPage, defaultPageTemplate, clientlibs, new Image());
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        imageTests.cleanup(adminClient);
    }

    /**
     * Test: add image
     */
    @Test
    @DisplayName("Test: add image")
    public void testAddImage() throws TimeoutException, InterruptedException {
        imageTests.testAddImage();
    }

    /**
     * Test: set Alt Text and Title
     */
    @Test
    @DisplayName("Test: set Alt Text and Title")
    public void testAddAltTextAndTitle() throws TimeoutException, InterruptedException {
        imageTests.testAddAltTextAndTitle();
    }

    /**
     * Test: set link on image
     */
    @Test
    @DisplayName("Test: set link on image")
    public void testSetLink() throws InterruptedException, TimeoutException {
        imageTests.testSetLinkV2();
    }

    /**
     * Test: Disable caption popup
     */
    @Test
    @DisplayName("Test: Disable caption popup")
    public void testDisableCaptionAsPopup() throws TimeoutException, InterruptedException {
        imageTests.testDisableCaptionAsPopup();
    }

    /**
     * Test: set image as decorative
     */
    @Test
    @DisplayName("Test: set image as decorative")
    public void testSetImageAsDecorative() throws TimeoutException, InterruptedException {
        imageTests.testSetImageAsDecorativeV2();
    }

    /**
     * Test: Check image map areas are rendered, navigate correctly and are responsively adjusted on window resize
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check image map areas are rendered, navigate correctly and are responsively adjusted on window resize")
    public void testCheckMapAreaNavigationAndResponsiveResize() throws ClientException, TimeoutException, InterruptedException {
        imageTests.testCheckMapAreaNavigationAndResponsiveResize(authorClient);
    }

    /**
     * Test: set asset from DAM without description
     */
    @Test
    @DisplayName("Test: set asset from DAM without description")
    public void testSetAssetWithoutDescription() throws TimeoutException, InterruptedException {
        imageTests.testSetAssetWithoutDescription();
    }

}
