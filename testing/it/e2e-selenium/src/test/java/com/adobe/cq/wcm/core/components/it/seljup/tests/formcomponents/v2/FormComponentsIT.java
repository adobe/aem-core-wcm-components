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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formcomponents.v2;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.*;

@Tag("group1")
public class FormComponentsIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.formcomponents.v1.FormComponentsIT {

    public void setComponentResources() {
        formContainerRT =RT_FORMCONTAINER_V2;
        formTextRT = RT_FORMTEXT_V2;
        formHiddenRT = RT_FORMHIDDEN_V2;
        formOptionsRT = RT_FORMOPTIONS_V2;
        formButtonRT = RT_FORMBUTTON_V2;
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

}
