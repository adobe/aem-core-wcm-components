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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.WCMException;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;

@ExtendWith(AemContextExtension.class)
public class NavigationImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.NavigationImplTest {

    private static final String TEST_BASE = "/navigation/v2";

    @BeforeEach
    protected void setUp() throws WCMException {
        testBase = TEST_BASE;
        resourceType = NavigationImpl.RESOURCE_TYPE;
        internalSetup();
    }

    @Override
    protected void verifyNavigationItem(Object[] expectedPage, NavigationItem item) {
        super.verifyNavigationItem(expectedPage, item);
        assertValidLink(item.getLink(), (String) expectedPage[3], context.request());
    }

}
