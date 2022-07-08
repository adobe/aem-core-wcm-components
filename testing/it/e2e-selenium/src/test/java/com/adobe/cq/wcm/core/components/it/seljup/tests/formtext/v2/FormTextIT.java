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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formtext.v2;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext.v2.FormText;
import com.adobe.cq.wcm.core.components.it.seljup.tests.formtext.FormTextTests;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V2;

@Tag("group1")
public class FormTextIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.formtext.v1.FormTextIT {
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        formTextTests = new FormTextTests();
        formTextTests.setup(authorClient, RT_FORMTEXT_V2, rootPage, defaultPageTemplate, new FormText());
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        formTextTests.cleanup(authorClient);
    }

}
