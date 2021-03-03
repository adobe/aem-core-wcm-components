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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(AemContextExtension.class)
public class ComponentImplTest {

    private static final String TEST_BASE = "/experiencefragment";
    private static final String TEST_PAGE = "/content/mysite/page";
    private static final String TEST_PAGE_EN = "/content/mysite/en/page";
    private static final String XF_COMPONENT_1 = TEST_PAGE + "/jcr:content/root/xf-component-1";
    private static final String XF_COMPONENT_10 = TEST_PAGE_EN + "/jcr:content/root/xf-component-10";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
    	context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONF_JSON, "/conf/we-retail/settings");
    }

    @Test
    public void testComponentId() {
        Component component = getComponentUnderTest(XF_COMPONENT_1);
        Assertions.assertEquals("experiencefragment-4d2d8fd496", component.getId(), "ID mismatch");
    }

    @Test
    public void testReferencedComponentId() {
        Component component = getReferencedComponentUnderTest(XF_COMPONENT_1, TEST_PAGE_EN, XF_COMPONENT_10);
        Assertions.assertEquals("experiencefragment-789e0d5de3", component.getId(), "ID mismatch");
    }
    
    
    private List<String> getStyleSystemClassesToTest(String pagePath, String resourcePath)
    {
    	context.currentPage(pagePath);
		ResourceResolver resourceResolver = spy(context.resourceResolver());
		
		Component component = getStyledComponentUnderTest(resourceResolver, resourcePath);
		return component.getAppliedStyleClasses();
    }
    
    @Test
    public void testStyleSystemClasses()
    {
    	final String WE_RETAIL_TITLE = TEST_PAGE_EN+"/jcr:content/root/title_core";
    	List<String> styleClasses = getStyleSystemClassesToTest(TEST_PAGE_EN,WE_RETAIL_TITLE);
    	assertNotNull(styleClasses);
		assertEquals(2,styleClasses.size());	
    }
    
    @Test
    public void testStyleSystemDefaultClasses()
    {
    	final String WE_RETAIL_TITLE = TEST_PAGE_EN+"/jcr:content/root/title_core_1";
    	List<String> styleClasses = getStyleSystemClassesToTest(TEST_PAGE_EN,WE_RETAIL_TITLE);
    	assertNotNull(styleClasses);
		assertEquals(1,styleClasses.size());	
		assertEquals("we-retail-title-default",styleClasses.get(0));
    }
    
    @Test
    public void testStyleSystemNoClasses()
    {
    	final String WE_RETAIL_TITLE = TEST_PAGE_EN+"/jcr:content/root/title_core_2";
    	List<String> styleClasses = getStyleSystemClassesToTest(TEST_PAGE_EN,WE_RETAIL_TITLE);
    	assertNull(styleClasses);
    }
    
    private Component getStyledComponentUnderTest(ResourceResolver resourceResolver, String resourcePath) {
    	
    	Resource currentResourceSpy = spy(resourceResolver.getResource(resourcePath));
		
		Mockito.when(currentResourceSpy.getResourceResolver()).thenReturn(resourceResolver);
		ContentPolicyManager contentPolicyManager = mock(ContentPolicyManager.class);
		Mockito.when(resourceResolver.adaptTo(ContentPolicyManager.class)).thenReturn(contentPolicyManager);
		String titlePolicyPath = "/conf/we-retail/settings/wcm/policies/weretail/components/content/title/policy_205022283770700";
		ContentPolicy contentPolicy = spy(resourceResolver.getResource(titlePolicyPath).adaptTo(ContentPolicy.class));
		ValueMap valueMap = spy(contentPolicy.getProperties());
		final String WE_RETAIL_TITLE = TEST_PAGE_EN+"/jcr:content/root/title_core_2";
		if(StringUtils.equals(resourcePath, WE_RETAIL_TITLE))
			Mockito.when(valueMap.get(ComponentUtils.CQ_STYLECLASSES_DEFAULT)).thenReturn(null);
		Mockito.when(contentPolicy.getProperties()).thenReturn(valueMap);
		Mockito.when(contentPolicyManager.getPolicy(currentResourceSpy)).thenReturn(contentPolicy);
		
        context.currentResource(currentResourceSpy);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Component.class);
    }


    private Component getComponentUnderTest(String resourcePath, Object ... properties) {
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Component.class);
    }

    private Component getReferencedComponentUnderTest(String resourcePath, String currentPagePath, String referencerPath, Object ... properties) {
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        Resource referencer = context.resourceResolver().getResource(referencerPath);
        SlingBindings slingBindings = new SlingBindings();
        ComponentContext componentContext = mock(ComponentContext.class);
        ComponentContext parentContext = mock(ComponentContext.class);
        when(parentContext.getResource()).thenReturn(referencer);
        when(componentContext.getParent()).thenReturn(parentContext);
        Page currentPage = context.pageManager().getPage(currentPagePath);
        slingBindings.put(WCMBindings.COMPONENT_CONTEXT, componentContext);
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        MockSlingHttpServletRequest request = context.request();
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Component.class);
    }       
}
