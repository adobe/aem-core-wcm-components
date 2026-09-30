/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.pw.form;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;

import com.adobe.cq.wcm.core.components.it.pw.PlaywrightAuthorBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

public abstract class FormGroupPwBase extends PlaywrightAuthorBaseTest {

    protected String testPage;

    protected String createFormPage() throws Exception {
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        return testPage;
    }

    protected String addStandaloneComponent(String resourceType, String name) throws Exception {
        createFormPage();
        String path = Commons.addComponentWithRetry(authorClient, resourceType,
            testPage + Commons.relParentCompPath, name);
        openEditor(testPage);
        return path;
    }

    protected String addContainer(String containerType, String name) throws Exception {
        createFormPage();
        String path = Commons.addComponentWithRetry(authorClient, containerType,
            testPage + Commons.relParentCompPath, name);
        openEditor(testPage);
        return path;
    }

    @AfterEach
    protected void deleteFormPage() throws Exception {
        if (testPage != null) {
            authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS,
                RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
            testPage = null;
        }
    }
}
