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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.models.v3.ImageImpl.DEFAULT_NGDM_ASSET_WIDTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class NextgenDMImageURIBuilderTest {

    private static final String TEST_ASSET_ID = "urn:aaid:aem:79bd2087-c200-495d-854a-fd1b95d5cecd";
    private static final String TEST_SEO_NAME = "Cat.jpg";
    private static final String FILE_REFERENCE = String.format("/%s/%s", TEST_ASSET_ID, TEST_SEO_NAME);
    private static final String IMAGE_DELIVERY_BASE_PATH = "/adobe/dynamicmedia/deliver/{asset-id}/{seo-name}.{format}";
    private static final String REPOSITORY_ID = "delivery-pxxxx-exxxx-cmstg.adobeaemcloud.com";

    private static final String BASE_URI_TEMPLATE = "https://" + REPOSITORY_ID + IMAGE_DELIVERY_BASE_PATH;

    private NextGenDynamicMediaConfig config;

    private String baseUri;

    private NextGenDMImageURIBuilder uriBuilder;

    @BeforeEach
    public void setUp() {
        config = mock(NextGenDynamicMediaConfig.class);
        when(config.getImageDeliveryBasePath()).thenReturn(IMAGE_DELIVERY_BASE_PATH);
        when(config.getRepositoryId()).thenReturn(REPOSITORY_ID);
        baseUri = BASE_URI_TEMPLATE.replace("{asset-id}", TEST_ASSET_ID).replace("{seo-name}.{format}", TEST_SEO_NAME);
        baseUri = baseUri + "?width=" + DEFAULT_NGDM_ASSET_WIDTH + "&preferwebp=true";
        uriBuilder = new NextGenDMImageURIBuilder(config, FILE_REFERENCE);
    }

    @Test
    public void testUriWithDefaultValues() {
        String uri = uriBuilder.build();
        assertEquals(baseUri, uri);
    }

    @Test
    public void testUriWithCustomWidth() {
        uriBuilder.withWidth(200);
        String uri = uriBuilder.build();
        assertTrue(uri.contains("width=200"));
    }

    @Test
    public void testUriWithCustomHeight() {
        uriBuilder.withHeight(200);
        String uri = uriBuilder.build();
        assertTrue(uri.contains("height=200"));
    }

    @Test
    public void testUriWithCustomPreferWebp() {
        uriBuilder.withPreferWebp(false);
        String uri = uriBuilder.build();
        assertFalse(uri.contains("preferwebp"));
    }

    @Test
    public void testUriWithSmartCrop() {
        uriBuilder.withSmartCrop("2:3");
        String uri = uriBuilder.build();
        assertTrue(uri.contains("crop=2:3,smart"));
    }


    @Test
    public void testUriWithInvalidSmartCrop() {
        uriBuilder.withSmartCrop("x:y");
        String uri = uriBuilder.build();
        assertEquals(baseUri, uri);
    }

    @Test
    public void testNoFileReference() {
        uriBuilder = new NextGenDMImageURIBuilder(config, "");
        assertTrue(uriBuilder.build() == null);
    }
}
