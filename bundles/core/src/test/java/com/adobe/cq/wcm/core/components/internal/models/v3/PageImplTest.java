/*******************************************************************************
 * Copyright 2019 Adobe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.internal.models.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImplTest {

    private static final String TEST_BASE = "/page/v3";

    @BeforeEach
    @Override
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testRedirectTarget() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertEquals("Templated Page", redirectTarget.getPage().getTitle());
        assertEquals("/core/content/page/templated-page.html", redirectTarget.getURL());
        assertValidLink(redirectTarget.getLink(), "/core/content/page/templated-page.html");
    }

}
