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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
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
import com.adobe.cq.wcm.style.ComponentStyleInfo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

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
    
    
    @Test
    public void testStyleSystemClasses()
    {
    	final String WE_RETAIL_TITLE = TEST_PAGE_EN+"/jcr:content/root/title_core";
    	
    	ComponentStyleInfo componentStyleInfoMock = mock(ComponentStyleInfo.class);
    	Resource resource = spy(context.resourceResolver().getResource(WE_RETAIL_TITLE));
    	Mockito.doReturn(componentStyleInfoMock).when(resource).adaptTo(ComponentStyleInfo.class);
    	
        MockSlingHttpServletRequest request = context.request();
        request.setResource(resource);
    	
    	Mockito.doReturn("class1 class2").when(componentStyleInfoMock).getAppliedCssClasses();
    	Component component = request.adaptTo(Component.class);
    	String styleClasses = component.getAppliedCssClasses();
    	assertNotNull(styleClasses);
    	assertEquals("class1 class2",styleClasses);
    	
    	Mockito.doReturn(" ").when(componentStyleInfoMock).getAppliedCssClasses();
    	component = request.adaptTo(Component.class);
    	styleClasses = component.getAppliedCssClasses();
    	assertNull(styleClasses);
    	
    	Mockito.doReturn(null).when(componentStyleInfoMock).getAppliedCssClasses();
    	component = request.adaptTo(Component.class);
    	styleClasses = component.getAppliedCssClasses();
    	assertNull(styleClasses);
    }
    
    private Component getComponentUnderTest(String resourcePath, Object ... properties) {
        Resource resource = spy(context.resourceResolver().getResource(resourcePath));
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        
        MockSlingHttpServletRequest request = context.request();
        request.setResource(resource);
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
