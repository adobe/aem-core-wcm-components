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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.services.ClientLibraryAggregatorService;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import java.util.Set;
import java.util.HashSet;
import org.apache.sling.api.resource.LoginException;

public class ClientLibraryImplTest {
    private TestLogger testLogger;

    @Mock
    private ClientLibraryAggregatorService clientLibraryAggregatorServiceMock;
    @Mock
    private Page pageMock;
    @Mock
    private ResourceResolver resourceResolverMock;
    @Mock
    private Resource resourceMock;
    @InjectMocks
    private ClientLibraryImpl cl;

    private String categories = "category";
    private String type = "js";
    private String primaryPath = "/primary/path";
    private String fallbackPath = "/fallback/path";

    String resourceTypeRegexSample;

    @BeforeEach
    void setUp() {
        this.testLogger = TestLoggerFactory.getTestLogger(ClientLibraryImpl.class);

        initMocks(this);
    }

    @AfterEach
    void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void getInline() {
        this.cl.categories = this.categories;
        this.cl.type = this.type;

        when(this.clientLibraryAggregatorServiceMock.getClientLibOutput(this.categories, this.type))
          .thenReturn("output inline");

        assertEquals("output inline", this.cl.getInline());
    }

    @Test
    public void getInlineLimited_correct() throws LoginException {
        this.cl.categories = this.categories;
        this.cl.type = this.type;
        this.cl.primaryPath = this.primaryPath;
        this.cl.fallbackPath = this.fallbackPath;

        Set<String> resourceTypes = Utils.getResourceTypes(this.resourceMock, this.resourceTypeRegexSample, new HashSet<>());

        when(this.pageMock.getContentResource())
          .thenReturn(this.resourceMock);
        when(this.clientLibraryAggregatorServiceMock.getResourceTypeRegex())
          .thenReturn(this.resourceTypeRegexSample);
        when(this.clientLibraryAggregatorServiceMock.getClientlibResourceResolver())
        .thenReturn(this.resourceResolverMock);
        when(this.clientLibraryAggregatorServiceMock.getClientLibOutput(this.categories, this.type, resourceTypes, this.primaryPath, this.fallbackPath))
          .thenReturn("output inline");

        assertEquals("output inline", this.cl.getInlineLimited());
    }
}
