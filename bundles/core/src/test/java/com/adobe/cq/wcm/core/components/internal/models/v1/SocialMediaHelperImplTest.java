/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.SocialMediaHelper;
import com.adobe.cq.wcm.core.components.testing.MockCommerceFactory;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.adobe.cq.wcm.core.components.testing.MockXFFactory;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class SocialMediaHelperImplTest {

    private static final String CLASS_RESOURCE = "org.apache.sling.api.resource.Resource";
    private static final String CLASS_PRODUCT = "com.adobe.cq.commerce.api.Product";
    private static final String CLASS_COMMERCE_SERVICE = "com.adobe.cq.commerce.api.CommerceService";
    private static final String CLASS_XF_SOCIAL_VARIATION = "com.adobe.cq.xf.social.ExperienceFragmentSocialVariation";
    private static final String CLASS_PAGE = "com.day.cq.wcm.api.Page";

    private static final String TESTS_CONTENT_ROOT = "/content/sharing-tests";
    private static final String TEST_BASE = "/sharing";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/context";
    private static final String BASIC_SHARING_PAGE_1 = TESTS_CONTENT_ROOT + "/" + "basic-sharing-page-1";
    private static final String BASIC_SHARING_PAGE_2 = TESTS_CONTENT_ROOT + "/" + "basic-sharing-page-2";
    private static final String BASIC_SHARING_PAGE_3 = TESTS_CONTENT_ROOT + "/" + "basic-sharing-page-3";
    private static final String PRODUCT_SHARING_PAGE = TESTS_CONTENT_ROOT + "/" + "product-page";
    private static final String XF_SHARING_PAGE = TESTS_CONTENT_ROOT + "/" + "xf-sharing-page";
    private static final String XF_PRODUCT_SHARING_PAGE = TESTS_CONTENT_ROOT + "/" + "xf-product-page";
    private static final String EXTENSION = "html";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.registerService(AdapterFactory.class, new AdapterFactory() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public <AdapterType> AdapterType getAdapter(@NotNull Object o, @NotNull Class<AdapterType> clazz) {
                        Object result = null;
                        switch (clazz.getName()) {
                            case CLASS_PRODUCT:
                                if (o instanceof Resource) {
                                    result = MockCommerceFactory.getProduct((Resource) o);
                                }
                                break;
                            case CLASS_COMMERCE_SERVICE:
                                if (o instanceof Resource) {
                                    result = MockCommerceFactory.getCommerceService((Resource) o);
                                }
                                break;
                            case CLASS_XF_SOCIAL_VARIATION:
                                if (o instanceof Page) {
                                    result = MockXFFactory.getExperienceFragmentSocialVariation((Page) o);
                                }
                        }

                        return (AdapterType) result;
                    }
                },
            new HashMap<String, Object>(){{
                put(AdapterFactory.ADAPTABLE_CLASSES, new String[] {
                        CLASS_RESOURCE,
                        CLASS_PAGE
                });
                put(AdapterFactory.ADAPTER_CLASSES, new String[] {
                        CLASS_PRODUCT,
                        CLASS_COMMERCE_SERVICE,
                        CLASS_XF_SOCIAL_VARIATION
                });
            }}
        );
    }

    @Test
    void testWebsiteProvider() {
        SocialMediaHelper socialMediaHelper = getSocialMediaHelperUnderTest(BASIC_SHARING_PAGE_1);
        assertTrue(socialMediaHelper.isSocialMediaEnabled());
        assertTrue(socialMediaHelper.isFacebookEnabled());
        assertTrue(socialMediaHelper.isPinterestEnabled());
        assertTrue(socialMediaHelper.hasFacebookSharing());
        assertTrue(socialMediaHelper.hasPinterestSharing());
        Map<String, String> metadata = socialMediaHelper.getMetadata();
        assertEquals("About Us", metadata.get(SocialMediaHelperImpl.OG_TITLE));
        assertEquals("About Us Page", metadata.get(SocialMediaHelperImpl.OG_DESCRIPTION));
        assertEquals(MockExternalizerFactory.ROOT + BASIC_SHARING_PAGE_1 + "." + EXTENSION, metadata.get(SocialMediaHelperImpl
                .OG_URL));
        assertEquals("website", metadata.get(SocialMediaHelperImpl.OG_TYPE));
        assertEquals("About Us", metadata.get(SocialMediaHelperImpl.OG_SITE_NAME));
        assertEquals(MockExternalizerFactory.ROOT + BASIC_SHARING_PAGE_1 + ".thumb.800.480.png?ck=1495097346",
                metadata.get(SocialMediaHelperImpl.OG_IMAGE));

        socialMediaHelper = getSocialMediaHelperUnderTest(BASIC_SHARING_PAGE_2);
        metadata = socialMediaHelper.getMetadata();
        assertEquals(MockExternalizerFactory.ROOT + BASIC_SHARING_PAGE_2 + ".thumb.800.480.png?ck=1495097341",
                metadata.get(SocialMediaHelperImpl.OG_IMAGE));

        socialMediaHelper = getSocialMediaHelperUnderTest(BASIC_SHARING_PAGE_3);
        metadata = socialMediaHelper.getMetadata();
        assertEquals(MockExternalizerFactory.ROOT + BASIC_SHARING_PAGE_3 + ".thumb.800.480.png?ck=1495097341",
                metadata.get(SocialMediaHelperImpl.OG_IMAGE));
    }

    @Test
    void testCommerceProvider() {
        SocialMediaHelper socialMediaHelper = getSocialMediaHelperUnderTest(PRODUCT_SHARING_PAGE);
        assertTrue(socialMediaHelper.isSocialMediaEnabled());
        assertTrue(socialMediaHelper.isFacebookEnabled());
        assertTrue(socialMediaHelper.isPinterestEnabled());
        assertTrue(socialMediaHelper.hasFacebookSharing());
        assertTrue(socialMediaHelper.hasPinterestSharing());
        Map<String, String> metadata = socialMediaHelper.getMetadata();
        assertEquals("Eton Short-Sleeve Shirt", metadata.get(SocialMediaHelperImpl.OG_TITLE));
        assertEquals("Express yourself around town or at play with our rugby-inspired Eton shirt.",
                metadata.get(SocialMediaHelperImpl.OG_DESCRIPTION));
        assertEquals(MockExternalizerFactory.ROOT + PRODUCT_SHARING_PAGE +"." + EXTENSION, metadata.get(SocialMediaHelperImpl
                .OG_URL));
        assertEquals("product", metadata.get(SocialMediaHelperImpl.OG_TYPE));
        assertEquals("Eton Short-Sleeve Shirt", metadata.get(SocialMediaHelperImpl.OG_SITE_NAME));
        assertEquals(MockExternalizerFactory.ROOT + "/content/dam/we-retail/en/products/apparel/shirts/Eton.jpg", metadata.get
                (SocialMediaHelperImpl.OG_IMAGE));
        assertEquals(MockCommerceFactory.UNIVERSAL_PRICE.toBigInteger().toString(),
                metadata.get(SocialMediaHelperImpl.OG_PRODUCT_PRICE_AMOUNT));
        assertEquals("USD", metadata.get(SocialMediaHelperImpl.OG_PRODUCT_PRICE_CURRENCY));
    }

    @Test
    void testXFWebsiteProvider() {
        SocialMediaHelper socialMediaHelper = getSocialMediaHelperUnderTest(XF_SHARING_PAGE);
        assertTrue(socialMediaHelper.isSocialMediaEnabled());
        assertTrue(socialMediaHelper.isFacebookEnabled());
        assertTrue(socialMediaHelper.isPinterestEnabled());
        assertTrue(socialMediaHelper.hasFacebookSharing());
        assertTrue(socialMediaHelper.hasPinterestSharing());
        Map<String, String> metadata = socialMediaHelper.getMetadata();
        assertEquals("About Us", metadata.get(SocialMediaHelperImpl.OG_TITLE));
        assertEquals("<p>About Us XF description</p>", metadata.get(SocialMediaHelperImpl.OG_DESCRIPTION));
        assertEquals(MockExternalizerFactory.ROOT + XF_SHARING_PAGE + "." + EXTENSION, metadata.get(SocialMediaHelperImpl
                .OG_URL));
        assertEquals("website", metadata.get(SocialMediaHelperImpl.OG_TYPE));
        assertEquals("About Us", metadata.get(SocialMediaHelperImpl.OG_SITE_NAME));
        assertEquals(MockExternalizerFactory.ROOT + "/content/dam/we-retail/en/activities/hiking-camping/trekker-khumbu-valley.jpg",
                metadata.get(SocialMediaHelperImpl.OG_IMAGE));
    }

    @Test
    void testXFProductProvider() {
        SocialMediaHelper socialMediaHelper = getSocialMediaHelperUnderTest(XF_PRODUCT_SHARING_PAGE);
        assertTrue(socialMediaHelper.isSocialMediaEnabled());
        assertTrue(socialMediaHelper.isFacebookEnabled());
        assertTrue(socialMediaHelper.isPinterestEnabled());
        assertTrue(socialMediaHelper.hasFacebookSharing());
        assertTrue(socialMediaHelper.hasPinterestSharing());
        Map<String, String> metadata = socialMediaHelper.getMetadata();
        assertEquals("Eton Short-Sleeve Shirt", metadata.get(SocialMediaHelperImpl.OG_TITLE));
        assertEquals("<p>Blue Polo T-Shirt</p>", metadata.get(SocialMediaHelperImpl.OG_DESCRIPTION));
        assertEquals(MockExternalizerFactory.ROOT + XF_PRODUCT_SHARING_PAGE + "." + EXTENSION, metadata.get(SocialMediaHelperImpl
                .OG_URL));
        assertEquals("product", metadata.get(SocialMediaHelperImpl.OG_TYPE));
        assertEquals("Eton Short-Sleeve Shirt", metadata.get(SocialMediaHelperImpl.OG_SITE_NAME));
        assertEquals(MockExternalizerFactory.ROOT + "/content/dam/we-retail/en/products/apparel/shirts/Eton.jpg",
                metadata.get(SocialMediaHelperImpl.OG_IMAGE));
    }

    private SocialMediaHelper getSocialMediaHelperUnderTest(String pagePath) {
        Resource currentResource = context.resourceResolver().getResource(pagePath);
        Page currentPage = currentResource.adaptTo(Page.class);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(), context.bundleContext());
        MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
        request.setContextPath(CONTEXT_PATH);
        request.setResource(currentResource);
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setExtension(EXTENSION);
        requestPathInfo.setResourcePath(currentResource.getPath());
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        slingBindings.put(SlingBindings.RESOLVER, context.resourceResolver());
        slingBindings.put(SlingBindings.RESPONSE, response);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(SocialMediaHelper.class);
    }

}
