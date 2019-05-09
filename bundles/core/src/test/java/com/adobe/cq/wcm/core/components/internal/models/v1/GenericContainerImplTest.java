/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.GenericContainer;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;
import com.day.cq.wcm.api.designer.Style;
import com.adobe.cq.wcm.core.components.Utils;

import io.wcm.testing.mock.aem.junit.AemContext;

public class GenericContainerImplTest {

    private static final String TEST_BASE = "/generic-container";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/container";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String CONTAINER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-2";
    private static final String CONTAINER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-3";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @Rule
    public final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @Before
    public void init() {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        AEM_CONTEXT.registerService(SlingModelFilter.class, new MockSlingModelFilter());
    }

    @Test
    public void testContainerProperties() {        
        GenericContainer container = getContainerUnderTest(CONTAINER_1);
        assertNotNull("The container background color is not null", container.getBackgroundColor());
        assertNotNull("The container background image src is not null", container.getBackgroundImageSrc());
        assertNotNull("The container background style is not null", container.getBackgroundStyleString());
        assertNotNull("The container background exported items is not null", container.getExportedItems());
        assertNotNull("The container background exported item order is not null", container.getExportedItemsOrder());
        assertEquals("The container backgroun exported type match","core/wcm/components/container/v1/container", container.getExportedType());
        assertEquals("The container background color is what was expected",new String("#000000"),container.getBackgroundColor());
        assertEquals("The container background image is what was expected",new String("/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg"),container.getBackgroundImageSrc());
        assertEquals("The container background style is what was expected",new String("background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000"),container.getBackgroundStyleString());
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container1"));
    }
    
    @Test
    public void testContainerNullProperties() {        
        GenericContainer container = getContainerUnderTest(CONTAINER_2);
        assertNull("The container background color is null", container.getBackgroundColor());
        assertNull("The container background image src is null", container.getBackgroundImageSrc());
    }
    
    @Test
    public void testContainerPolicyAndDialogProperties() {        
        GenericContainer container = getContainerUnderTest(CONTAINER_3);
        assertNotNull("The container background color from dialog is not null", container.getBackgroundColor());
        assertNotNull("The container background image src from dialog not is null", container.getBackgroundImageSrc());
        assertNotNull("The container background style is null because policy disabled from policy", container.getBackgroundStyleString());
    }

    private GenericContainer getContainerUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }        
        AEM_CONTEXT.currentResource(resource);
        AEM_CONTEXT.request().setContextPath(CONTEXT_PATH);
        return AEM_CONTEXT.request().adaptTo(GenericContainer.class);
    }
}
