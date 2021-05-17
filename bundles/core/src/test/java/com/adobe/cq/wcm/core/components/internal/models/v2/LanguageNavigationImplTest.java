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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class LanguageNavigationImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.LanguageNavigationImplTest {

    private static final String TEST_BASE = "/languagenavigation/v2";

    @BeforeEach
    @Override
    protected void setUp() {
        testBase = TEST_BASE;
        resourceType = LanguageNavigationImpl.RESOURCE_TYPE;
        internalSetup();
    }

    @Override
    protected void verifyLanguageNavigationItem(Object[] expectedPage, LanguageNavigationItem item) {
        super.verifyLanguageNavigationItem(expectedPage, item);
        assertValidLink(item.getLink(), (String) expectedPage[6], context.request());
    }

}
