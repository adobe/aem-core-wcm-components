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

import org.mockito.Mockito;
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
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.trace;

import com.adobe.cq.wcm.core.components.internal.services.amp.AmpUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import com.adobe.cq.commerce.common.ValueMapDecorator;
import org.apache.sling.api.resource.ValueMap;
import java.util.HashMap;
import java.util.Map;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicy;

public class AmpUtilTest {
    private static final String AMP_MODE_PROP = "ampMode";

    private TestLogger testLogger;

    private PageManager pageManagerMock;
    private Page pageMock;
    private SlingHttpServletRequest slingHttpServletRequestMock;
    private ResourceResolver resourceResolverMock;
    private Resource resourceMock;
    private ContentPolicyManager contentPolicyManagerMock;
    private ContentPolicy contentPolicyMock;

    private ValueMap mapSample;

    @BeforeEach
    void setUp() {
        this.testLogger = TestLoggerFactory.getTestLogger(AmpUtil.class);

        this.pageManagerMock = Mockito.mock(PageManager.class);
        this.pageMock = Mockito.mock(Page.class);
        this.slingHttpServletRequestMock = Mockito.mock(SlingHttpServletRequest.class);
        this.resourceResolverMock = Mockito.mock(ResourceResolver.class);
        this.resourceMock = Mockito.mock(Resource.class);
        this.contentPolicyManagerMock = Mockito.mock(ContentPolicyManager.class);
        this.contentPolicyMock = Mockito.mock(ContentPolicy.class);
    }

    @AfterEach
    void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void getAmpMode_pageManagerNull() {
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(null);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);

        String output = AmpUtil.getAmpMode(this.slingHttpServletRequestMock);
        assertThat(this.testLogger.getLoggingEvents(), hasItem(debug("Can't resolve page manager. Falling back to content policy AMP mode.")));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("Policy manager is null. Unable to read policy property.")));
        assertEquals("", output);
    }

    @Test
    public void getAmpMode_pageNull() {
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.slingHttpServletRequestMock.getResource()))
          .thenReturn(null);

        String output = AmpUtil.getAmpMode(this.slingHttpServletRequestMock);
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("Policy manager is null. Unable to read policy property.")));
        assertEquals("", output);
    }

    @Test
    public void getAmpMode_pageExists() {
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "ampOnly");
        }};
        this.mapSample = new ValueMapDecorator(map);

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

        assertEquals("ampOnly", AmpUtil.getAmpMode(this.slingHttpServletRequestMock));
    }

    @Test
    public void getPolicyProperty_contentPolicyNull() {
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(this.contentPolicyManagerMock);
        when(this.pageManagerMock.getContainingPage(this.slingHttpServletRequestMock.getResource()))
          .thenReturn(null);
        when(this.contentPolicyManagerMock.getPolicy(this.resourceMock))
          .thenReturn(null);

        String output = AmpUtil.getAmpMode(this.slingHttpServletRequestMock);
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("Content policy is null. Unable to read policy property.")));
        assertEquals("", output);
    }

    @Test
    public void getPolicyProperty_contentPolicyExists() {
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "inheritPageTemplate");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(this.contentPolicyManagerMock);
        when(this.pageManagerMock.getContainingPage(this.slingHttpServletRequestMock.getResource()))
          .thenReturn(null);
        when(this.contentPolicyManagerMock.getPolicy(this.resourceMock))
          .thenReturn(this.contentPolicyMock);
        when(this.contentPolicyMock.getProperties())
          .thenReturn(this.mapSample);

        assertEquals("inheritPageTemplate", AmpUtil.getAmpMode(this.slingHttpServletRequestMock));
    }
}
