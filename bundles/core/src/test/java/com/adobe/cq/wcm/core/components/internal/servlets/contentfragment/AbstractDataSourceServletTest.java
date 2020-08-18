/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.adobe.granite.ui.components.ExpressionResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractDataSourceServletTest {

    @Test
    public void createResourceReturnsSyntheticResource() {
        // GIVEN
        ResourceResolver resourceResolver = Mockito.mock(ResourceResolver.class);
        String title = "foobar";
        String value = "qux";

        AbstractDataSourceServlet dataSource = new AbstractDataSourceServlet() {
            @Nonnull
            @Override
            protected ExpressionResolver getExpressionResolver() {
                return Mockito.mock(ExpressionResolver.class);
            }
        };

        // WHEN
        Resource resource = dataSource.createResource(resourceResolver, title, value);

        // THEN
        assertEquals(title, resource.getValueMap().get("text"));
        assertEquals(value, resource.getValueMap().get("value"));
        assertEquals(Resource.RESOURCE_TYPE_NON_EXISTING, resource.getResourceType());
    }
}
