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

package com.adobe.cq.wcm.core.components.it.seljup.tests.page.v3;

import com.adobe.cq.wcm.core.components.it.seljup.components.page.v1.Page;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("group4")
public class PageIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.page.v2.PageIT {

    public void setupResources() {
        this.pageRT =  "core/wcm/components/page/v3/page";
        this.segmentPath = "/conf/we-retail/settings/wcm/segments";
    }

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        // create the test page
        testPage = Commons.createPage(adminClient, Commons.template, rootPage, "testPage", pageTitle, pageRT, "Test Page", 200);
        page = new Page();
    }

}
