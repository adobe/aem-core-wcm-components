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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formcontainer.v2;

import com.adobe.cq.wcm.core.components.it.seljup.tests.formcontainer.v1.FormContainerV1IT;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;

public class FormContainerV2IT extends FormContainerV1IT {

    public void setComponentResources() {
        formContainerRT = "core/wcm/components/form/container/v2/container";
        formTextRT = "core/wcm/components/form/text/v2/text";
        formButtonRT = "core/wcm/components/form/button/v2/button";
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }
}
