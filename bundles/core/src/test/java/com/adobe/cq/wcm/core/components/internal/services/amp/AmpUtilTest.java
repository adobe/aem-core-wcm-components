/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(AemContextExtension.class)
public class AmpUtilTest {
    private static final String TEST_BASE = "/amp-util";
    private static final String TEST_ROOT_PAGE = "/content";

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    @Test
    public void isAmpModeWithDefaults() {
        Assert.assertEquals("", AmpUtil.getAmpMode(context.request()));
    }

    @Test
    public void isAmpMode() {

        Resource resource = context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);

        SlingHttpServletRequest mockSlingHttpServletRequest = mock(SlingHttpServletRequest.class);
        ResourceResolver mockResourceResolver = mock(ResourceResolver.class);
        PageManager mockPageManager = mock(PageManager.class);
        Page page = mock(Page.class);

        when(mockSlingHttpServletRequest.getResourceResolver()).thenReturn(mockResourceResolver);
        when(mockSlingHttpServletRequest.getResourceResolver().adaptTo(PageManager.class)).thenReturn(mockPageManager);
        when(mockSlingHttpServletRequest.getResource()).thenReturn(resource);
        when(mockPageManager.getContainingPage(mockSlingHttpServletRequest.getResource())).thenReturn(page);
        when(page.getProperties()).thenReturn(resource.getChild("amp-only/jcr:content").getValueMap());

        Assert.assertEquals("ampOnly", AmpUtil.getAmpMode(mockSlingHttpServletRequest));
    }



}
