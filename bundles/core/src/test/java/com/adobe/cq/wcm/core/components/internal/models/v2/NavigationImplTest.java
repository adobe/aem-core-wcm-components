/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.day.cq.wcm.api.WCMException;
import io.wcm.testing.mock.aem.junit.AemContext;

public class NavigationImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.NavigationImplTest {

    protected static String TEST_BASE = "/navigation/v2";

    @ClassRule
    public static final AemContext AEM_CONTEXT_1 = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    //points to the navigation component with rootLevel = 0.
    private static final String NAV_COMPONENT_1 = TEST_ROOT + "/jcr:content/root/navigation-component-1";
    //points to the navigation component with rootLevel = 1.
    private static final String NAV_COMPONENT_2 = TEST_ROOT + "/jcr:content/root/navigation-component-2";
    //points to the navigation component with rootLevel = 2.
    private static final String NAV_COMPONENT_3 = TEST_ROOT + "/jcr:content/root/navigation-component-3";

    @BeforeClass
    public static void initSetup() throws WCMException {
        setup(AEM_CONTEXT_1);
    }

    @Test
    public void testRootLevelZero() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_1, AEM_CONTEXT_1);
        Object[][] expectedPages = {
            {"/content/navigation", 0, true, "/content/navigation.html"},
            {"/content/navigation/navigation-1", 1, false, "/navigation-1-vanity"},
            {"/content/navigation/navigation-1/navigation-1-1", 2, false, "/content/navigation/navigation-1/navigation-1-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 3, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 3, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 4, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 4, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
            {"/content/navigation/navigation-2", 1, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation1"));
    }

    @Test
    public void testRootLevelOne() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_2, AEM_CONTEXT_1);
        Object[][] expectedPages = {
            {"/content/navigation/navigation-1", 0, false, "/navigation-1-vanity"},
            {"/content/navigation/navigation-1/navigation-1-1", 1, false, "/content/navigation/navigation-1/navigation-1-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 2, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 2, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 3, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 3, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
            {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation2"));
    }

    @Test
    public void testRootLevelTwo() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_3, AEM_CONTEXT_1);
        Object[][] expectedPages = {
            {"/content/navigation/navigation-1/navigation-1-1", 0, false, "/content/navigation/navigation-1/navigation-1-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 1, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 1, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 2, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
            {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 2, false,
                "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation3"));
    }
}
