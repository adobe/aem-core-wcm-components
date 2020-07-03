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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ComponentFiles;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class ComponentFilesImplTest {

    private static final String APPS_ROOT = "/apps/core/wcm/components";
    private static final String BASE = "/clientlibs";
    private static final String CONTENT_ROOT = "/content";
    private static final String ROOT_PAGE = "/content/clientlibs";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);
    }

    @Test
    void testGetPathWithOneResourceType() {
        Map<String, Object> attributes = new HashMap<String, Object>() {{
            put(ComponentFiles.OPTION_RESOURCE_TYPES, new LinkedHashSet<String>() {{
                add("core/wcm/components/accordion/v1/accordion");
            }});
            put(ComponentFiles.OPTION_FILTER_REGEX, "customheadlibs\\.amp\\.html");
        }};
        ComponentFiles componentFiles = getComponentFilesUnderTest(ROOT_PAGE, attributes);
        List<String> expected = new LinkedList<String>() {{
            add("/apps/core/wcm/components/accordion/v1/accordion/customheadlibs.amp.html");
        }};
        assertEquals(expected, componentFiles.getPaths());
    }

    @Test
    void testGetPathWithMultipleResourceType() {
        Map<String, Object> attributes = new HashMap<String, Object>() {{
            put(ComponentFiles.OPTION_RESOURCE_TYPES, new LinkedHashSet() {{
                add("/apps/core/wcm/components/accordion/v1/accordion");
                add("/apps/core/wcm/components/carousel/v2/carousel");
                add("/apps/core/wcm/components/teaser/v1/teaser");
            }});
            put(ComponentFiles.OPTION_FILTER_REGEX, "customheadlibs\\.amp\\.html");
        }};
        ComponentFiles componentFiles = getComponentFilesUnderTest(ROOT_PAGE, attributes);
        List<String> expected = new LinkedList<String>() {{
            add("/apps/core/wcm/components/accordion/v1/accordion/customheadlibs.amp.html");
            add("/apps/core/wcm/components/carousel/v1/carousel/customheadlibs.amp.html");
        }};
        assertEquals(expected, componentFiles.getPaths());
    }

    @Test
    void testGetPathWithMultipleResourceTypesAndNoInheritance() {
        Map<String, Object> attributes = new HashMap<String, Object>() {{
            put(ComponentFiles.OPTION_RESOURCE_TYPES, new LinkedHashSet<String>() {{
                add("/apps/core/wcm/components/accordion/v1/accordion");
                add("/apps/core/wcm/components/carousel/v2/carousel");
                add("/apps/core/wcm/components/teaser/v1/teaser");
            }});
            put(ComponentFiles.OPTION_FILTER_REGEX, "customheadlibs\\.amp\\.html");
            put(ComponentFiles.OPTION_INHERITED, false);
        }};
        ComponentFiles componentFiles = getComponentFilesUnderTest(ROOT_PAGE, attributes);
        List<String> expected = new LinkedList<String>() {{
            add("/apps/core/wcm/components/accordion/v1/accordion/customheadlibs.amp.html");
        }};
        assertEquals(expected, componentFiles.getPaths());
    }

    private ComponentFiles getComponentFilesUnderTest(String path, Map<String,Object> attributes) {
        Resource resource = context.currentResource(path);
        if (resource != null) {
            if (attributes != null) {
                SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
                for (Map.Entry<String,Object> entry : attributes.entrySet()) {
                    slingBindings.put(entry.getKey(), entry.getValue());
                }
                context.request().setAttribute(SlingBindings.class.getName(), slingBindings);
            }
            context.request().setResource(resource);
            return context.request().adaptTo(ComponentFiles.class);
        }
        return null;
    }
}
