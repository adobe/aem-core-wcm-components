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

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
class BreadcrumbImplTest {

    private static final String TEST_BASE = "/breadcrumb";
    private static final String CURRENT_PAGE = "/content/breadcrumb/women/shirts/devi-sleeveless-shirt";
    private static final String BREADCRUMB_1 = CURRENT_PAGE + "/jcr:content/header/breadcrumb";
    private static final String BREADCRUMB_2 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-show-hidden";
    private static final String BREADCRUMB_3 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-hide-current";
    private static final String BREADCRUMB_4 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-start-level";
    private static final String BREADCRUMB_5 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-style-based";
    private static final String BREADCRUMB_6 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-v2";


    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content/breadcrumb/women");
    }

    @Test
    void testBreadcrumbItems() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_1);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_1));
    }

    @Test
    void testGetShowHidden() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_2);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women", "Shirts", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_2));
    }

    @Test
    void testGetHideCurrent() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_3);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_3));
    }

    @Test
    void testStartLevel() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_4);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Shirts", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_4));
    }

    @Test
    void testStyleBasedBreadcrumb() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_5,
                BreadcrumbImpl.PN_START_LEVEL, 3,
                BreadcrumbImpl.PN_HIDE_CURRENT, false,
                BreadcrumbImpl.PN_SHOW_HIDDEN, false
        );
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_5));
    }

    @Test
    void testV2JSONExporter() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_6);
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_6));
    }

    private void checkBreadcrumbConsistency(Breadcrumb breadcrumb, String[] expectedPages) {
        assertEquals("Expected that the returned breadcrumb will contain " + expectedPages.length + " items", breadcrumb.getItems().size(),
                expectedPages.length);
        int index = 0;
        for (NavigationItem item : breadcrumb.getItems()) {
            assertEquals(expectedPages[index++], item.getTitle());
        }
    }

    private Breadcrumb getBreadcrumbUnderTest(String resourcePath, Object... properties) {
        Utils.enableDataLayer(context, true);
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        context.request().setContextPath("");
        return context.request().adaptTo(Breadcrumb.class);
    }

}
