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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import static uk.org.lidalia.slf4jtest.LoggingEvent.trace;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;

import com.day.crx.JcrConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.internal.services.amp.AmpTransformerFactory;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.LoginException;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Page;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.io.InputStream;
import com.adobe.cq.commerce.common.ValueMapDecorator;

public class AmpTransformerTest {
    private static final String AMP_MODE_PROP = "ampMode";

    private TestLogger testLogger;

    @Mock
    private AmpTransformerFactory.Cfg cfgMock;
    @Mock
    private ProcessingContext processingContextMock;
    @Mock
    private ProcessingComponentConfiguration processingComponentConfigurationMock;
    @Mock
    private SlingHttpServletRequest slingHttpServletRequestMock;
    @Mock
    private RequestPathInfo requestPathInfoMock;
    @Mock
    private ResourceResolverFactory resourceResolverFactoryMock;
    @Mock
    private ResourceResolver resourceResolverMock;
    @Mock
    private Resource resourceMock;
    @Mock
    private Resource coreResourceMock;
    @Mock
    private Resource supertypeResourceMock;
    @Mock
    private ContentPolicyManager contentPolicyManagerMock;
    @Mock
    private ContentPolicy contentPolicyMock;
    @Mock
    private PageManager pageManagerMock;
    @Mock
    private Page pageMock;
    @Mock
    private ContentHandler contentHandlerMock;
    @Mock
    private Locator locatorMock;
    @Mock
    private Attributes attributesMock;
    @InjectMocks
    private AmpTransformer at;

    private ValueMap mapSample;
    private String resourceTypeRegexSample;

    @BeforeEach
    void setUp() {
        this.testLogger = TestLoggerFactory.getTestLogger(AmpTransformer.class);

        initMocks(this);
    }

    @AfterEach
    void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void init_pageManagerNull() {
        String[] selectors = new String[] {"amp"};

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(null);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);

        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("Failed to resolve page manager while initializing AMP transformer.")));
    }

    @Test
    public void init_pageNull() {
        String[] selectors = new String[] {"amp"};

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.slingHttpServletRequestMock.getResource()))
          .thenReturn(null);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);

        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("Failed to resolve page while initializing AMP transformer")));
    }

    @Test
    public void contentHandler() throws SAXException {
        char[] ch = new char[26];
        int index = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            ch[index++] = c;
        }

        this.at.setContentHandler(this.contentHandlerMock);

        this.at.startDocument();
        verify(this.contentHandlerMock, times(1)).startDocument();
        this.at.startElement("uri", "localName", "qName", this.attributesMock);
        verify(this.contentHandlerMock, times(1)).startElement("uri", "localName", "qName", this.attributesMock);
        this.at.startPrefixMapping("prefix", "uri");
        verify(this.contentHandlerMock, times(1)).startPrefixMapping("prefix", "uri");
        this.at.characters(ch, 0, 5);
        verify(this.contentHandlerMock, times(1)).characters(ch, 0, 5);
        this.at.ignorableWhitespace(ch, 3, 15);
        verify(this.contentHandlerMock, times(1)).ignorableWhitespace(ch, 3, 15);
        this.at.processingInstruction("target", "data");
        verify(this.contentHandlerMock, times(1)).processingInstruction("target", "data");
        this.at.setDocumentLocator(locatorMock);
        verify(this.contentHandlerMock, times(1)).setDocumentLocator(locatorMock);
        this.at.skippedEntity("name");
        verify(this.contentHandlerMock, times(1)).skippedEntity("name");
        this.at.endPrefixMapping("pre");
        verify(this.contentHandlerMock, times(1)).endPrefixMapping("pre");
        this.at.endDocument();
        verify(this.contentHandlerMock, times(1)).endDocument();
    }

    @Test
    public void endElement_ampOnlyHeadlibEmpty() throws SAXException {
        String[] selectors = new String[] {"amp"};
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "ampOnly");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        when(this.pageMock.getPath())
          .thenReturn("/path/to/testing/page");

        when(this.cfgMock.getHeadlibName())
          .thenReturn("");

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);
        this.at.setContentHandler(this.contentHandlerMock);
        this.at.endElement("uri", "head", "qName");

        verify(this.contentHandlerMock, times(1)).endElement("uri", "head", "qName");
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("Headlib name not defined. Failed to aggregate AMP component js.")));
    }

    @Test
    public void endElement_pairedAmpHeadlibNameEmpty() throws SAXException {
        String[] selectors = new String[] {"amp"};
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "pairedAmp");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);
        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        when(this.pageMock.getPath())
          .thenReturn("/path/to/testing/page");

        when(this.cfgMock.getHeadlibName())
          .thenReturn("");

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);
        this.at.setContentHandler(this.contentHandlerMock);
        this.at.endElement("uri", "head", "qName");

        verify(this.contentHandlerMock, times(1)).endElement("uri", "head", "qName");
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("Headlib name not defined. Failed to aggregate AMP component js.")));
    }

    @Test
    public void endElement_pairedAmpHeadlibResourceAlwaysNull() throws SAXException, LoginException {
        String[] selectors = new String[] {"amp"};
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "pairedAmp");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        when(this.pageMock.getPath())
          .thenReturn("/path/to/testing/page");

        when(this.cfgMock.getHeadlibName())
          .thenReturn("customheadlibs.amp.html");
        when(this.cfgMock.getHeadlibResourceTypeRegex())
          .thenReturn(this.resourceTypeRegexSample);
        when(this.resourceMock.getResourceType())
          .thenReturn("/fakeType");
        when(this.resourceResolverFactoryMock.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE)))
          .thenReturn(this.resourceResolverMock);
        when(this.resourceResolverMock.getResource("/fakeType/customheadlibs.amp.html/" + JcrConstants.JCR_CONTENT))
          .thenReturn(null);
        when(this.resourceResolverMock.getResource("/fakeType"))
          .thenReturn(null);

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);
        this.at.setContentHandler(this.contentHandlerMock);
        this.at.endElement("uri", "head", "qName");

        verify(this.contentHandlerMock, times(1)).endElement("uri", "head", "qName");
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("No custom headlib for resource type {}.", "/fakeType")));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(debug("Can't access resource from resource type {}.", "/fakeType")));
    }

    @Test
    public void endElement_pairedAmpHeadlibResourceSupertypeNull() throws SAXException, LoginException {
        String[] selectors = new String[] {"amp"};
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "pairedAmp");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        when(this.pageMock.getPath())
          .thenReturn("/path/to/testing/page");

        when(this.cfgMock.getHeadlibName())
          .thenReturn("customheadlibs.amp.html");
        when(this.cfgMock.getHeadlibResourceTypeRegex())
          .thenReturn(this.resourceTypeRegexSample);
        when(this.resourceMock.getResourceType())
          .thenReturn("/fakeType");
        when(this.resourceResolverFactoryMock.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE)))
          .thenReturn(this.resourceResolverMock);
        when(this.resourceResolverMock.getResource("/fakeType/customheadlibs.amp.html/" + JcrConstants.JCR_CONTENT))
          .thenReturn(null);
        when(this.resourceResolverMock.getResource("/fakeType"))
          .thenReturn(this.coreResourceMock);
        when(this.coreResourceMock.getResourceSuperType())
          .thenReturn(null);

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);
        this.at.setContentHandler(this.contentHandlerMock);
        this.at.endElement("uri", "head", "qName");

        verify(this.contentHandlerMock, times(1)).endElement("uri", "head", "qName");
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("No custom headlib for resource type {}.", "/fakeType")));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("No resource superType from resource type {}.", "/fakeType")));
    }

    @Test
    public void endElement_pairedAmpHeadlibResourceSupertypeExistsInputStreamNull() throws SAXException, LoginException {
        String[] selectors = new String[] {"amp"};
        Map<String, Object> map = new HashMap<String, Object>(){{
            put(AMP_MODE_PROP, "pairedAmp");
        }};
        this.mapSample = new ValueMapDecorator(map);

        when(this.processingContextMock.getRequest())
          .thenReturn(this.slingHttpServletRequestMock);

        when(this.slingHttpServletRequestMock.getResourceResolver())
          .thenReturn(this.resourceResolverMock);
        when(this.slingHttpServletRequestMock.getResource())
          .thenReturn(this.resourceMock);
        when(this.resourceResolverMock.adaptTo(PageManager.class))
          .thenReturn(this.pageManagerMock);
        when(this.resourceResolverMock.adaptTo(ContentPolicyManager.class))
          .thenReturn(null);
        when(this.pageManagerMock.getContainingPage(this.resourceMock))
          .thenReturn(this.pageMock);
        when(this.pageMock.getProperties())
          .thenReturn(this.mapSample);
        when(this.slingHttpServletRequestMock.getRequestPathInfo())
          .thenReturn(this.requestPathInfoMock);
        when(this.requestPathInfoMock.getSelectors())
          .thenReturn(selectors);

        when(this.pageMock.getPath())
          .thenReturn("/path/to/testing/page");

        when(this.cfgMock.getHeadlibName())
          .thenReturn("customheadlibs.amp.html");
        when(this.cfgMock.getHeadlibResourceTypeRegex())
          .thenReturn(this.resourceTypeRegexSample);
        when(this.resourceMock.getResourceType())
          .thenReturn("/fakeType");
        when(this.resourceResolverFactoryMock.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE)))
          .thenReturn(this.resourceResolverMock);
        when(this.resourceResolverMock.getResource("/fakeType/customheadlibs.amp.html/" + JcrConstants.JCR_CONTENT))
          .thenReturn(null);
        when(this.resourceResolverMock.getResource("/fakeType"))
          .thenReturn(this.coreResourceMock);
        when(this.coreResourceMock.getResourceSuperType())
          .thenReturn("/path/to/superType");
        when(this.resourceResolverMock.getResource("/path/to/superType/customheadlibs.amp.html/" + JcrConstants.JCR_CONTENT))
          .thenReturn(this.supertypeResourceMock);
        when(this.supertypeResourceMock.adaptTo(InputStream.class))
          .thenReturn(null);

        this.at.init(this.processingContextMock, this.processingComponentConfigurationMock);
        this.at.setContentHandler(this.contentHandlerMock);
        this.at.endElement("uri", "head", "qName");

        verify(this.contentHandlerMock, times(1)).endElement("uri", "head", "qName");
        assertThat(this.testLogger.getLoggingEvents(), hasItem(trace("No custom headlib for resource type {}.", "/fakeType")));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(debug("Failed to read input stream from {}.", "/fakeType/customheadlibs.amp.html/" + JcrConstants.JCR_CONTENT)));
    }
}
