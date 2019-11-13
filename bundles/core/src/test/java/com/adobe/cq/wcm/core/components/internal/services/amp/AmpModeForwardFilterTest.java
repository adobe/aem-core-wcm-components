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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import java.util.HashMap;
import java.util.Map;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Page;
import javax.servlet.RequestDispatcher;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import com.adobe.cq.commerce.common.ValueMapDecorator;

public class AmpModeForwardFilterTest {
    private static final String AMP_MODE_PROP = "ampMode";

    private TestLogger testLogger;

    private SlingHttpServletRequest slingHttpServletRequestMock;
    @Mock
    private FilterChain filterChainMock;
    @Mock(extraInterfaces = SlingHttpServletRequest.class)
    private ServletRequest servletRequestMock;
    @Mock
    private ServletResponse servletResponseMock;
    @Mock
    private RequestPathInfo requestPathInfoMock;
    @Mock
    private RequestDispatcher requestDispatcherMock;
    @Mock
    private PageManager pageManagerMock;
    @Mock
    private Page pageMock;
    @Mock
    private ResourceResolver resourceResolverMock;
    @Mock
    private Resource resourceMock;
    @InjectMocks
    private AmpModeForwardFilter amff;

    private ValueMap mapSample;

    @BeforeEach
    void setUp() {
        this.testLogger = TestLoggerFactory.getTestLogger(AmpModeForwardFilter.class);

        initMocks(this);
        this.slingHttpServletRequestMock = (SlingHttpServletRequest) this.servletRequestMock;
    }

    @AfterEach
    void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void doFilter_noAmpForwardFalse() throws IOException, ServletException {
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "noAmp");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectorString())
          .thenReturn("amp");

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);


        when(this.slingHttpServletRequestMock.getRequestDispatcher(any(Resource.class), any(RequestDispatcherOptions.class)))
          .thenReturn(null);

        this.amff.doFilter(this.servletRequestMock, this.servletResponseMock, this.filterChainMock);

        assertThat(this.testLogger.getLoggingEvents(), hasItem(debug("Request dispatcher is null. AMP mode forwarding aborted.")));
        verify(this.filterChainMock, times(1)).doFilter(this.servletRequestMock, this.servletResponseMock);
    }

    @Test
    public void doFilter_ampOnlyForwardFalse() throws IOException, ServletException {
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "ampOnly");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectorString())
          .thenReturn("");

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);


        when(this.slingHttpServletRequestMock.getRequestDispatcher(any(Resource.class), any(RequestDispatcherOptions.class)))
          .thenReturn(null);

        this.amff.doFilter(this.servletRequestMock, this.servletResponseMock, this.filterChainMock);

        assertThat(this.testLogger.getLoggingEvents(), hasItem(debug("Request dispatcher is null. AMP mode forwarding aborted.")));
        verify(this.filterChainMock, times(1)).doFilter(this.servletRequestMock, this.servletResponseMock);
    }

    @Test
    public void doFilter_dotPlusAmpForwardFalse() throws IOException, ServletException {
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "ampOnly");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectorString())
          .thenReturn(".amp");

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);


        when(this.slingHttpServletRequestMock.getRequestDispatcher(any(Resource.class), any(RequestDispatcherOptions.class)))
          .thenReturn(this.requestDispatcherMock);

        this.amff.doFilter(this.servletRequestMock, this.servletResponseMock, this.filterChainMock);

        verify(this.requestDispatcherMock, times(1)).forward(this.slingHttpServletRequestMock, this.servletResponseMock);
    }
}
