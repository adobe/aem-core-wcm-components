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

package com.adobe.cq.wcm.core.components.internal.models.v3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class BreadcrumbImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.BreadcrumbImplTest {

    private static final String TEST_BASE = "/breadcrumb/v3";

    @BeforeEach
    @Override
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    @Test
    protected void testBreadcrumbItems() {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_1);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(testBase, BREADCRUMB_1));
    }

    @Test
    @Override
    protected void testV2JSONExporter() {
        // ignore for v3
    }

}
