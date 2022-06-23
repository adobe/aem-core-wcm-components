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

package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableMap;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class AllowedColorSwatchesDataSourceServletTest {

    private static final String COLOR_VALUE_1 = "#FF0000";
    private static final String COLOR_VALUE_2 = "#00FF00";
    private static final String COLOR_VALUE_3 = "#0000FF";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @Test
    public void testDataSource() throws Exception {
        AllowedColorSwatchesDataSourceServlet dataSourceServlet = new AllowedColorSwatchesDataSourceServlet();
        String[] expected = new String[] {COLOR_VALUE_1, COLOR_VALUE_2, COLOR_VALUE_3};
        context.build().resource("/content", ImmutableMap.of("sling:resourceType", "/restype")).commit();
        context.contentPolicyMapping("/restype", ImmutableMap.of(AllowedColorSwatchesDataSourceServlet.PN_ALLOWED_COLOR_SWATCHES, expected));
        context.request().setAttribute(Value.CONTENTPATH_ATTRIBUTE, "/content");

        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        Assertions.assertNotNull(dataSource);
        Iterable<Resource> iterable = dataSource::iterator;
        List<Resource> dataSourceList = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        assertEquals(3, dataSourceList.size());
        for (Resource resource: dataSourceList) {
            ValueMap props = resource.getValueMap();
            String allowedColorSwatch = props.get(AllowedColorSwatchesDataSourceServlet.PN_COLOR_VALUE, String.class);
            Assertions.assertTrue(Arrays.asList(expected).contains(allowedColorSwatch), "Allowed color swatches values are not as expected");
        }
    }

    @Test
    public void testDataSourceWithSuffix() throws Exception {
        AllowedColorSwatchesDataSourceServlet dataSourceServlet = new AllowedColorSwatchesDataSourceServlet();
        String[] expected = new String[] {COLOR_VALUE_1, COLOR_VALUE_2, COLOR_VALUE_3};
        context.build().resource("/content", ImmutableMap.of("sling:resourceType", "/restype")).commit();
        context.contentPolicyMapping("/restype", ImmutableMap.of(AllowedColorSwatchesDataSourceServlet.PN_ALLOWED_COLOR_SWATCHES, expected));
        context.requestPathInfo().setSuffix("/content");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        Assertions.assertNotNull(dataSource);
        Iterable<Resource> iterable = dataSource::iterator;
        List<Resource> dataSourceList = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        assertEquals(3, dataSourceList.size());
        for (Resource resource: dataSourceList) {
            ValueMap props = resource.getValueMap();
            String allowedColorSwatch = props.get(AllowedColorSwatchesDataSourceServlet.PN_COLOR_VALUE, String.class);
            Assertions.assertTrue(Arrays.asList(expected).contains(allowedColorSwatch), "Allowed color swatches values are not as expected");
        };
    }
}
