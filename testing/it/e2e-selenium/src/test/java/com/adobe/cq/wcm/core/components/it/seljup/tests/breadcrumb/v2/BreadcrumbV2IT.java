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

package com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.v2;

import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v2.BreadcrumbItemsV2;
import com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.BreadcrumbTests;
import com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.v1.BreadcrumbV1IT;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BreadcrumbV2IT extends BreadcrumbV1IT {

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        breadcrumbTests = new BreadcrumbTests();
        breadcrumbTests.setup(adminClient, Commons.rtBreadcrumb_v2, rootPage, defaultPageTemplate, "/core/wcm/components/breadcrumb/v2/breadcrumb/clientlibs/site.css", new BreadcrumbItemsV2());
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        breadcrumbTests.cleanup(adminClient);
    }

    /**
     * Test: structure data (schema.org)
     */
    @Test
    @DisplayName("Test: structure data (schema.org)")
    public void testStructureData() {
        breadcrumbTests.testStructureData();
    }


}
