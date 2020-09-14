/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.form;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.scripting.api.resource.ScriptingResourceResolverProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class FormStructureHelperImplTest {

    private static final String TEST_BASE = "/form/form-structure-helper";
    private static final String CONTENT_ROOT = "/content";
    private static final String APPS_ROOT = "/apps";
    private static final String CONTAINING_PAGE = "/content/we-retail/demo-page";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @Mock
    ScriptingResourceResolverProvider scriptingResourceResolverProvider;

    @InjectMocks
    private FormStructureHelperImpl formStructureHelper;


    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);
        context.registerService(ScriptingResourceResolverProvider.class, scriptingResourceResolverProvider);
    }

    @Test
    public void testCanManage() {
        Resource resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/start");
        assertFalse(formStructureHelper.canManage(resource));

        resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text");
        assertTrue(formStructureHelper.canManage(resource));

        resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container");
        assertTrue(formStructureHelper.canManage(resource));
    }

    @Test
    public void testGetFormResource() {
        Resource resource = null;
        Resource formResource = formStructureHelper.getFormResource(resource);
        assertNull(formResource);

        resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/title");
        formResource = formStructureHelper.getFormResource(resource);
        assertNull(formResource);

        resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container");
        formResource = formStructureHelper.getFormResource(resource);
        assertEquals(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container", formResource.getPath());

        resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text");
        formResource = formStructureHelper.getFormResource(resource);
        assertEquals(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container", formResource.getPath());
    }


    @Test
    public void testGetFormElements() {
        Resource resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid");
        Iterator<Resource> formFields = formStructureHelper.getFormElements(resource).iterator();
        assertFalse(formFields.hasNext());
        when(scriptingResourceResolverProvider.getRequestScopedResourceResolver()).thenReturn(context.resourceResolver());
        Set<String> allowedFields = new HashSet<>();
        allowedFields.add("text");
        allowedFields.add("hidden");
        allowedFields.add("button_button");
        allowedFields.add("text_inside_non_form_node");
        allowedFields.add("button_button_inherited");

        Set<String> returnedFormFields = new HashSet<>();

        resource = context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container");
        formFields = formStructureHelper.getFormElements(resource).iterator();
        //test only the allowed fields should be returned
        while (formFields.hasNext()) {
            Resource field = formFields.next();
            assertTrue(allowedFields.contains(field.getName()));
            returnedFormFields.add(field.getName());
        }

        //test all the fields preset in allowedFields should be returned
        for (String field : allowedFields) {
            assertTrue(returnedFormFields.contains(field));
        }
    }

    @Test
    public void testUpdateFormStructure() {
        Resource resource = Objects.requireNonNull(context.resourceResolver().getResource(CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container"));
        formStructureHelper.updateFormStructure(resource);

        ValueMap properties = resource.getValueMap();
        assertEquals("foundation/components/form/actions/store", properties.get("actionType", String.class));
        String action = properties.get("action", String.class);
        assertNotNull(action);
        assertTrue(action.length() > 0);
    }

}
