package com.adobe.cq.wcm.core.components.internal.services.table;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
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
    private ResourceResolver resourceResolver;

    @Mock
    Resource resource;

    @Mock
    ValueMap props;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void processData() throws IOException {

        Iterator<Resource> children = getChildrenIterator();
        when(resourceResolver.listChildren(resource)).thenReturn(children);
        when(props.get("email", StringUtils.EMPTY)).thenReturn("test@test.com");
        when(props.get("name", StringUtils.EMPTY)).thenReturn("test");
        when(props.get("gender", StringUtils.EMPTY)).thenReturn("male");
        when(props.get("status", StringUtils.EMPTY)).thenReturn("active");

        assertEquals(expectedOutput(), defaultResourceProcessor.processData(resource, HEADER_NAMES));
    }

    @Test
    void canProcess() {
        assertTrue(defaultResourceProcessor.canProcess(StringUtils.EMPTY));
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
