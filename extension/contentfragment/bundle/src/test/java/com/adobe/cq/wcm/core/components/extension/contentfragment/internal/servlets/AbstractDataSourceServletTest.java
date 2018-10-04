/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.servlets;

import com.adobe.granite.ui.components.ExpressionResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
        assertThat(resource.getValueMap().get("text"), is(title));
        assertThat(resource.getValueMap().get("value"), is(value));
        assertThat(resource.getResourceType(), is(Resource.RESOURCE_TYPE_NON_EXISTING));
    }
}
