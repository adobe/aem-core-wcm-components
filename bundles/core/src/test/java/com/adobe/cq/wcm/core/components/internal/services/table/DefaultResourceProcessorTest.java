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
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DefaultResourceProcessorTest {

    private static final String[] headerNames={"email","name","gender","status"};
    @InjectMocks
    private DefaultResourceProcessor defaultResourceProcessor;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    Resource resource;

    @Mock
    Iterator<Resource> children;

    @Mock
    Resource child;

    @Mock
    ValueMap props;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void processData() throws IOException {

        when(resourceResolver.listChildren(resource)).thenReturn(children);
        when(children.hasNext()).thenReturn(true);
        when(children.next()).thenReturn(child);
        when(child.adaptTo(ValueMap.class)).thenReturn(props);
        when(props.get("email", StringUtils.EMPTY)).thenReturn("test@test.com");
        when(props.get("name", StringUtils.EMPTY)).thenReturn("test");
        when(props.get("gender", StringUtils.EMPTY)).thenReturn("male");
        when(props.get("status", StringUtils.EMPTY)).thenReturn("active");

        assertEquals("",defaultResourceProcessor.processData(resource,headerNames));
    }

    @Test
    void canProcess() {
        assertEquals(true, defaultResourceProcessor.canProcess(""));
    }
}
