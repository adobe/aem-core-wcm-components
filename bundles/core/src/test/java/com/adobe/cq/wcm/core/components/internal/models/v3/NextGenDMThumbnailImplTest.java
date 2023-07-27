/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.models.v3;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.commons.editor.nextgendm.NextGenDMThumbnail;
import com.adobe.cq.wcm.core.components.testing.MockNextGenDynamicMediaConfig;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class NextGenDMThumbnailImplTest {
    protected final AemContext context = CoreComponentTestContext.newAemContext();

    private static final String NGDM_IMAGE_PATH = "/content/ngdm_test_page/jcr:content/root/ngdm_test_page_image1";

    protected static final String TEST_CONTENT_ROOT = "/content";

    protected static final String TEST_APPS_ROOT = "/apps";
    private static final String TEST_ASSET_ID = "urn:aaid:aem:e82c3c87-1453-48f5-844b-1822fb610911";
    private static final String TEST_SEO_NAME = "cutfruits.png";
    private static final String IMAGE_DELIVERY_BASE_PATH = "/adobe/dynamicmedia/deliver/{asset-id}/{seo-name}.{format}";
    private static final String REPOSITORY_ID = "delivery-pxxxx-exxxx-cmstg.adobeaemcloud.com";
    private static final String BASE_URI_TEMPLATE = "https://" + REPOSITORY_ID + IMAGE_DELIVERY_BASE_PATH;

    private static final String altText = "Smart crop preview";

    private static final String BASE_URI = BASE_URI_TEMPLATE.replace("{asset-id}", TEST_ASSET_ID).replace("{seo-name}.{format}", TEST_SEO_NAME);


    @Mock
    private Resource resource;
    @Mock
    private ValueMap valueMap;

    private NextGenDMThumbnail nextGenDMThumbnail;


    @BeforeEach
    public void setUp() {
        context.load().json("/image/v3" + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_CONTENT_ROOT);
        context.load().json("/image/v3" + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);

        MockNextGenDynamicMediaConfig config = new MockNextGenDynamicMediaConfig();
        config.setEnabled(true);
        config.setRepositoryId(REPOSITORY_ID);
        context.contentPolicyMapping(NextGenDMThumbnailImpl.RESOURCE_TYPE);
        context.requestPathInfo().setSuffix(NGDM_IMAGE_PATH);
        context.registerInjectActivateService(config);

        nextGenDMThumbnail = getNextGenImageThumbnail("/apps/ngdm-smartcropthumbnail");
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testThumbnailSrc() {
        assertTrue(nextGenDMThumbnail.getSrc().contains(BASE_URI));
        assertTrue(nextGenDMThumbnail.getSrc().contains("width=260"));
        assertTrue(nextGenDMThumbnail.getSrc().contains("height=260"));
        assertTrue(nextGenDMThumbnail.getSrc().contains("crop=2:3,smart"));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testThumbnailAlt() {
        assertTrue(nextGenDMThumbnail.getAlt().equals(altText));
    }

    protected NextGenDMThumbnail getNextGenImageThumbnail(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(NextGenDMThumbnail.class);
    }
}
