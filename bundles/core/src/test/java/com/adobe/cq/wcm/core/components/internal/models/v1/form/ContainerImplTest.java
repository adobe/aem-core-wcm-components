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
package com.adobe.cq.wcm.core.components.internal.models.v1.form;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlethelpers.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.form.Container;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.foundation.forms.FormStructureHelper;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContainerImplTest {

    private static final String TEST_BASE = "/form/container";
    private static final String CONTEXT_PATH = "/core";
    private static final String CONTAINING_PAGE = "/content/coretest/demo-page";
    private static final String FORM1_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container";
    private static final String FORM2_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container_350773202";
    private static final String FORM3_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container-v2";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @Mock
    private FormStructureHelper formStructureHelper;

    @Mock
    private MockRequestDispatcherFactory requestDispatcherFactory;

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTAINING_PAGE);
        context.registerService(FormStructureHelperFactory.class, resource -> formStructureHelper);
        context.registerService(SlingModelFilter.class, new SlingModelFilter() {

            private final Set<String> IGNORED_NODE_NAMES = new HashSet<String>() {{
                add(NameConstants.NN_RESPONSIVE_CONFIG);
                add(MSMNameConstants.NT_LIVE_SYNC_CONFIG);
                add("cq:annotations");
            }};

            @Override
            public Map<String, Object> filterProperties(Map<String, Object> map) {
                return map;
            }

            @Override
            public Iterable<Resource> filterChildResources(Iterable<Resource> childResources) {
                return StreamSupport
                        .stream(childResources.spliterator(), false)
                        .filter(r -> !IGNORED_NODE_NAMES.contains(r.getName()))
                        .collect(Collectors.toList());
            }
        });
        FormsHelperStubber.createStub();
    }

    @Test
    public void testFormWithCustomAttributesAndFields() {
        Container container = getContainerUnderTest(FORM1_PATH);
        assertEquals("my-id", container.getId());
        assertEquals("my-id", container.getName());
        assertEquals("application/x-www-form-urlencoded", container.getEnctype());
        assertEquals("GET", container.getMethod());
        assertEquals(CONTEXT_PATH + CONTAINING_PAGE + ".html", container.getAction());
        assertEquals("wcm/foundation/components/responsivegrid/new", container.getResourceTypeForDropArea());
        assertEquals(CONTEXT_PATH + "/content/coretest/home", container.getRedirect());
        assertEquals(0, container.getErrorMessages().length);
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, FORM1_PATH));
    }

    @Test
    public void testFormWithoutCustomAttributesAndField() {
        Container container = getContainerUnderTest(FORM2_PATH);
        assertEquals("multipart/form-data", container.getEnctype());
        assertEquals("POST", container.getMethod());
        assertEquals(CONTEXT_PATH + CONTAINING_PAGE + ".html", container.getAction());
        assertEquals("wcm/foundation/components/responsivegrid/new", container.getResourceTypeForDropArea());
        assertNull(container.getRedirect());
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, FORM2_PATH));
    }

    @Test
    public void testV2JSONExport() {
        Container container = getContainerUnderTest(FORM3_PATH);
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, FORM3_PATH));
    }

    private Container getContainerUnderTest(String resourcePath) {
        Resource resource = context.currentResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        MockSlingHttpServletRequest request = context.request();
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(Container.class);
    }
}
