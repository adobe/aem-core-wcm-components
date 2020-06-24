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

package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import java.io.StringReader;

import javax.json.Json;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Title;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class to verify the data layer JSON of a component, when the data layer is enabled or disabled.
 * The tests are based on the Title component but it could be written with any other component
 * supporting the data layer feature.
 */
@ExtendWith(AemContextExtension.class)
class ComponentDataImplTest {

    private static final String TEST_BASE = "/title";
    private static final String TEST_PAGE = "/content/title";
    private static final String SUFFIX_NO_DATA_LAYER = "-nodatalayer";
    private static final String TITLE_RESOURCE_JCR_TITLE = TEST_PAGE + "/jcr:content/par/title-jcr-title";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", TEST_PAGE);
    }

    @Test
    void getDataLayerJsonWhenDataLayerEnabled() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE, true);
        assertNotNull(title.getData());
        String expected = "{\"title-7968bee19c\":{\"@type\":\"core/wcm/components/title/v1/title\",\"dc:title\":\"Hello World\",\"repo:modifyDate\":\"2016-01-13T15:14:51Z\"}}";
        assertEquals(Json.createReader(new StringReader(expected)).read(),
                Json.createReader(new StringReader(title.getData().getJson())).read());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE));
    }

    @Test
    void getDataLayerJsonWhenDataLayerDisabled() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE, false);
        assertNull(title.getData());
    }

    private Title getTitleUnderTest(String resourcePath, boolean dataLayerEnabled, Object ... properties) {
        Utils.enableDataLayer(context, dataLayerEnabled);
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Title.class);
    }


}
