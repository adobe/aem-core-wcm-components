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
package com.adobe.cq.wcm.core.components.commons.editor.dialog;

import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.commons.editor.dialog.inherited.PageImageThumbnail;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class PageImageThumbnailTest {

    private static final String TEST_BASE = "/commons/editor/dialog/pageimagethumbnail";
    private static final String CONTENT_ROOT = "/content";
    private static final String RESOURCE = CONTENT_ROOT + "/page/jcr:content/root/responsivegrid/image";
    private static final String RESOURCE1 = CONTENT_ROOT + "/page/jcr:content/root/responsivegrid/image1";
    private static final String TEASER1 = CONTENT_ROOT + "/page/jcr:content/root/responsivegrid/teaser_empty";
    private static final String TEASER2 = CONTENT_ROOT + "/page/jcr:content/root/responsivegrid/teaser_with_link";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() throws Exception {
        context.load().json(TEST_BASE + "/test-content.json", CONTENT_ROOT);
    }

    @Test
    void testPageImageThumbnailWithSuffix() {
        context.currentResource(RESOURCE);
        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(RESOURCE);
        requestPathInfo.setResourcePath(RESOURCE);
        PageImageThumbnail pageImageThumbnail = request.adaptTo(PageImageThumbnail.class);
        if (pageImageThumbnail != null) {
            assertEquals("featured image alt", pageImageThumbnail.getAlt(), "getAlt()");
            assertEquals("/content/page/_jcr_content/_cq_featuredimage.coreimg.png", pageImageThumbnail.getSrc(), "getSrc()");
            assertEquals("/content/page/jcr:content/root/responsivegrid/image", pageImageThumbnail.getComponentPath(), "getComponentPath()");
            assertEquals("/content/page/jcr:content/root/responsivegrid/image", pageImageThumbnail.getConfigPath(), "getConfigPath()");
            assertEquals("/content/page", pageImageThumbnail.getCurrentPagePath(), "getCurrentPagePath()");
        } else {
            fail("can't create page image thumnbail model");
        }
    }

    @Test
    void testPageImageThumbnailWithParam() {
        context.currentResource(RESOURCE);
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("item", RESOURCE));
        PageImageThumbnail pageImageThumbnail = request.adaptTo(PageImageThumbnail.class);
        if (pageImageThumbnail != null) {
            assertEquals("featured image alt", pageImageThumbnail.getAlt(), "getAlt()");
            assertEquals("/content/page/_jcr_content/_cq_featuredimage.coreimg.png", pageImageThumbnail.getSrc(), "getSrc()");
        }
    }

    @Test
    void testPageImageThumbnailWithLinkURL() {
        context.currentResource(RESOURCE);
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("item", RESOURCE, "pageLink", "/content/page1"));
        PageImageThumbnail pageImageThumbnail = request.adaptTo(PageImageThumbnail.class);
        if (pageImageThumbnail != null) {
            assertEquals("featured image alt for page 1", pageImageThumbnail.getAlt(), "getAlt()");
            assertEquals("/content/page1/_jcr_content/_cq_featuredimage.coreimg.png", pageImageThumbnail.getSrc(), "getSrc()");
        }
    }

    @Test
    void testPageImageThumbnailWithoutParam() {
        context.currentResource(RESOURCE);
        MockSlingHttpServletRequest request = context.request();
        PageImageThumbnail pageImageThumbnail = request.adaptTo(PageImageThumbnail.class);
        if (pageImageThumbnail != null) {
            assertNull(pageImageThumbnail.getAlt(), "getAlt()");
            assertNull(pageImageThumbnail.getSrc(), "getSrc()");
        }
    }

    @Test
    void testPageImageThumbnailWithNonExistingResource() {
        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(RESOURCE1);
        PageImageThumbnail pageImageThumbnail = request.adaptTo(PageImageThumbnail.class);
        if (pageImageThumbnail != null) {
            assertNull(pageImageThumbnail.getAlt(), "getAlt()");
            assertNull(pageImageThumbnail.getSrc(), "getSrc()");
        }
    }

    @Test
    void testWithTeaserAndNoLink() {
        context.currentResource(TEASER1);
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("item", TEASER1));
        PageImageThumbnail pageImageThumbnail = request.adaptTo(PageImageThumbnail.class);
        if (pageImageThumbnail != null) {
            assertEquals("featured image alt", pageImageThumbnail.getAlt(), "getAlt()");
            assertEquals("/content/page/_jcr_content/_cq_featuredimage.coreimg.png", pageImageThumbnail.getSrc(), "getSrc()");
        }
    }

}
