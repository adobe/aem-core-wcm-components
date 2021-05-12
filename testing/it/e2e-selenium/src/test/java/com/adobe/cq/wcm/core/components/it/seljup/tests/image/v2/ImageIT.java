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

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.image.v2.Image;
import com.adobe.cq.wcm.core.components.it.seljup.tests.image.ImageTests;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

@Tag("group2")
public class ImageIT extends AuthorBaseUITest {

    private ImageTests imageTests;
    private static final String clientlibs = "core.wcm.components.image.v2";

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        imageTests = new ImageTests();
        imageTests.setup(adminClient, label, Commons.rtImage_v2, rootPage, defaultPageTemplate, clientlibs, new Image());
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
    public void addImage() throws TimeoutException, InterruptedException {
        imageTests.addImage();
    }

    /**
     * Test: set Alt Text and Title
     */
    @Test
    @DisplayName("Test: set Alt Text and Title")
    public void addAltTextAndTitle() throws TimeoutException, InterruptedException {
        imageTests.addAltTextAndTitle();
    }

    /**
     * Test: Disable caption popup
     */
    @Test
    @DisplayName("Test: Disable caption popup")
    public void disableCaptionAsPopup() throws TimeoutException, InterruptedException {
        imageTests.disableCaptionAsPopup();
    }

    /**
     * Test: set image as decorative
     */
    @Test
    @DisplayName("Test: set image as decorative")
    public void setImageAsDecorative() throws TimeoutException, InterruptedException {
        imageTests.setImageAsDecorativeV2();
    }

}
