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
package com.adobe.cq.wcm.core.components.internal.services.table;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultResourceProcessorTest {

    private static final String[] HEADER_NAMES = {"email", "name", "gender", "status"};
    private static final String[] PROP_VALUES = {"test@test.com", "test", "male", "active"};
    @InjectMocks
    private DefaultResourceProcessor defaultResourceProcessor;

    @Mock
    Resource resource;

    @Mock
    ValueMap props;

    @Mock
    Iterable<Resource> iterable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Happy path when the data is available under child nodes.")
    void processData() {
        Iterator<Resource> children = getChildrenIterator();
        when(resource.hasChildren()).thenReturn(true);
        when(resource.getChildren()).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(children);
        when(props.get("email", StringUtils.EMPTY)).thenReturn("test@test.com");
        when(props.get("name", StringUtils.EMPTY)).thenReturn("test");
        when(props.get("gender", StringUtils.EMPTY)).thenReturn("male");
        when(props.get("status", StringUtils.EMPTY)).thenReturn("active");

        assertEquals(expectedOutput(), defaultResourceProcessor.processData(resource, HEADER_NAMES));
    }

    @Test
    @DisplayName("When data is not available under child nodes")
    void processDataWithNoChildNodes() {
        Iterator<Resource> children = getEmptyIterator();
        when(resource.hasChildren()).thenReturn(false);
        when(resource.getChildren()).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(children);
        List<List<String>> rows = new ArrayList<>();
        assertEquals(rows, defaultResourceProcessor.processData(resource, HEADER_NAMES));
    }

    @Test
    void canProcess() {
        assertTrue(defaultResourceProcessor.canProcess(StringUtils.EMPTY));
    }

    private Iterator<Resource> getEmptyIterator() {
        final List<Resource> children = new ArrayList<>();
        return children.iterator();
    }

    private Iterator<Resource> getChildrenIterator() {
        Resource child = mock(Resource.class);
        when(child.adaptTo(ValueMap.class)).thenReturn(props);
        final List<Resource> children = new ArrayList<>();
        children.add(child);
        return children.iterator();
    }

    private List<List<String>> expectedOutput() {
        List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList(PROP_VALUES));
        return tableData;
    }
}
