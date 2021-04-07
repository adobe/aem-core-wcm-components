/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formbutton.v2;

import com.adobe.cq.wcm.core.components.it.seljup.components.FormButton.v2.FormButtonV2;
import com.adobe.cq.wcm.core.components.it.seljup.tests.formbutton.v1.FormButtonV1IT;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.PageEditorPage;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;

public class FormButtonV2IT extends FormButtonV1IT {

    final String COMPONENT_RESOURCE_TYPE = "core/wcm/components/form/button/v2/button";

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBefore() throws ClientException {
        testPage = authorClient.createPage("testPage", "Test Page", rootPage, defaultPageTemplate, 200, 201).getSlingPath();
        proxyCompoenetPath = Commons.creatProxyComponent(adminClient, COMPONENT_RESOURCE_TYPE, "Proxy Form Button", "formbutton");
        addPathtoComponentPolicy(responsiveGridPath, proxyCompoenetPath);
        formButton = new FormButtonV2();
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }
}
