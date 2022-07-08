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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.osgi.MapUtil;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.google.common.collect.Sets;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class PageImplTest {

    protected static final String CONTEXT_PATH = "/core";
    protected static final String CONTENT_ROOT = "/content/page";
    protected static final String PAGE = CONTENT_ROOT + "/templated-page";
    protected static final String DESIGN_PATH_KEY = "designPath";
    protected static final String DESIGN_PATH = "/etc/designs/mysite";
    protected static final String CSS_CLASS_NAMES_KEY = "cssClassNames";

    private static final String DESIGN_CACHE_KEY = "io.wcm.testing.mock.aem.context.MockAemSlingBindings_design_/content/page/templated" +
            "-page";
    private static final String TEST_BASE = "/page";
    private static final String FN_FAVICON_ICO = "favicon.ico";
    private static final String FN_FAVICON_PNG = "favicon_32.png";
    private static final String FN_TOUCH_ICON_60 = "touch-icon_60.png";
    private static final String FN_TOUCH_ICON_76 = "touch-icon_76.png";
    private static final String FN_TOUCH_ICON_120 = "touch-icon_120.png";
    private static final String FN_TOUCH_ICON_152 = "touch-icon_152.png";

    private static final String PN_FAVICON_ICO = "faviconIco";
    private static final String PN_FAVICON_PNG = "faviconPng";
    private static final String PN_TOUCH_ICON_60 = "touchIcon60";
    private static final String PN_TOUCH_ICON_76 = "touchIcon76";
    private static final String PN_TOUCH_ICON_120 = "touchIcon120";
    private static final String PN_TOUCH_ICON_152 = "touchIcon152";

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    protected String testBase;

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    protected void internalSetup() {
        this.context.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        this.context.load().json(testBase + "/test-conf.json", "/conf/coretest/settings");
        this.context.load().json(testBase + "/default-tags.json", "/content/cq:tags/default");
        this.context.load().binaryFile(TEST_BASE + "/" + FN_FAVICON_ICO, DESIGN_PATH + "/" + FN_FAVICON_ICO);
        this.context.load().binaryFile(TEST_BASE + "/" + FN_FAVICON_PNG, DESIGN_PATH + "/" + FN_FAVICON_PNG);
        this.context.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_60, DESIGN_PATH + "/" + FN_TOUCH_ICON_60);
        this.context.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_76, DESIGN_PATH + "/" + FN_TOUCH_ICON_76);
        this.context.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_120, DESIGN_PATH + "/" + FN_TOUCH_ICON_120);
        this.context.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_152, DESIGN_PATH + "/" + FN_TOUCH_ICON_152);
    }

    @Test
    protected void testPage() throws ParseException {
        context.load().binaryFile(TEST_BASE + "/static.css", DESIGN_PATH + "/static.css");

        Page page = getPageUnderTest(PAGE,
                PageImpl.PN_CLIENTLIBS, new String[]{"coretest.product-page"},
                DESIGN_PATH_KEY, DESIGN_PATH);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        calendar.setTime(sdf.parse("2016-01-20T10:33:36.000+0100"));
        assertEquals(page.getLastModifiedDate().getTime(), calendar.getTime());
        assertEquals("en-GB", page.getLanguage());
        assertEquals("Templated Page", page.getTitle());
        assertEquals("Description", page.getDescription());
        assertEquals("Brand Slug", page.getBrandSlug());
        assertEquals(DESIGN_PATH, page.getDesignPath());
        assertEquals(DESIGN_PATH + "/static.css", page.getStaticDesignPath());
        String[] keywordsArray = page.getKeywords();
        assertEquals(3, keywordsArray.length);
        Set<String> keywords = new HashSet<>(keywordsArray.length);
        keywords.addAll(Arrays.asList(keywordsArray));
        assertTrue(keywords.contains("one") && keywords.contains("two") && keywords.contains("three"));
        assertEquals("coretest.product-page", page.getClientLibCategories()[0]);
        assertEquals("product-page", page.getTemplateName());
        Utils.testJSONExport(page, Utils.getTestExporterJSONPath(testBase, PAGE));
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testFavicons() {
        Page page = getPageUnderTest(PAGE, DESIGN_PATH_KEY, DESIGN_PATH);
        Map<String, String> favicons = page.getFavicons();
        assertEquals(DESIGN_PATH + "/" + FN_FAVICON_ICO, favicons.get(PN_FAVICON_ICO));
        assertEquals(DESIGN_PATH + "/" + FN_FAVICON_PNG, favicons.get(PN_FAVICON_PNG));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_60, favicons.get(PN_TOUCH_ICON_60));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_76, favicons.get(PN_TOUCH_ICON_76));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_120, favicons.get(PN_TOUCH_ICON_120));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_152, favicons.get(PN_TOUCH_ICON_152));
    }

    @Test
    protected void testDefaultDesign() {
        Page page = getPageUnderTest(PAGE);
        assertNull(page.getDesignPath());
        assertNull(page.getStaticDesignPath());
    }

    protected Page getPageUnderTest(String pagePath, Object... properties) {
        Utils.enableDataLayer(context, true);
        Map<String, Object> propertyMap = MapUtil.toMap(properties);
        Resource resource = context.currentResource(pagePath + "/" + JcrConstants.JCR_CONTENT);
        MockSlingHttpServletRequest request = context.request();
        context.request().setContextPath("/core");

        if (resource != null && !propertyMap.isEmpty()) {
            if (propertyMap.containsKey(DESIGN_PATH_KEY)) {
                Designer designer = context.resourceResolver().adaptTo(Designer.class);
                if (designer != null) {
                    Design design = designer.getDesign(pagePath);
                    Design spyDesign = Mockito.spy(design);
                    Mockito.doReturn(propertyMap.get(DESIGN_PATH_KEY)).when(spyDesign).getPath();
                    request.setAttribute(DESIGN_CACHE_KEY, spyDesign);
                }
            }

            if (propertyMap.containsKey(CSS_CLASS_NAMES_KEY)) {
                ComponentContext spyComponentContext =
                        Mockito.spy((ComponentContext)request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME));
                Mockito.doReturn(Sets.newLinkedHashSet(Arrays.asList((String[])propertyMap.get(CSS_CLASS_NAMES_KEY))))
                        .when(spyComponentContext).getCssClassNames();
                request.setAttribute(ComponentContext.CONTEXT_ATTR_NAME, spyComponentContext);
            }
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        return request.adaptTo(Page.class);
    }
}
