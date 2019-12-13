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

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
public class AmpTransformerTest {
    private static final String TEST_BASE = "/amp-transformer";
    private static final String TEST_ROOT_PAGE = "/content";
    private static final String AMP_PAGE_PROPERTY = TEST_ROOT_PAGE + "/amp-only";
    private static final String AMP_SELECTOR = TEST_ROOT_PAGE + "/amp-selector";
    private static final String PAIRED_AMP = TEST_ROOT_PAGE + "/paired-amp";
    private static final String AMP_SELECTOR_WITH_AMP_MODE = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode";
    private static final String AMP_SELECTOR_WITH_AMP_MODE_NO_COMPONENT = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode-no-component";
    private static final String AMP_SELECTOR_WITH_AMP_MODE_NO_SUPERTYPE = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode-no-supertype";
    private static final String AMP_SELECTOR_WITH_AMP_MODE_INVALID_HEADLIB = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode-invalid-headlib";
    private static final String INVALID_PAGE_RESOURCE = TEST_ROOT_PAGE + "/invalid-page-resource";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    protected final AemContext context = CoreComponentTestContext.newAemContext();


    private AmpTransformer ampTransformer;
    private ResourceResolverFactory resolverFactory;
    private ContentHandler contentHandler;

    @BeforeEach
    void setUp() throws LoginException {

        resolverFactory = mock(MockResourceResolverFactory.class);
        contentHandler = mock(ContentHandler.class);

        Map<String, Object> configs = AmpTransformerFactoryTest.getAmpTransformerFactoryConfig();
        AmpTransformerFactory ampTransformerFactory = context.registerInjectActivateService(new AmpTransformerFactory(), configs);

        AmpTransformerFactory.Cfg ampCfg = ampTransformerFactory.getCfg();

        ampTransformer = new AmpTransformer(ampCfg, resolverFactory);

        ampTransformer.setContentHandler(contentHandler);

        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);

    }

    @Test
    void initTransformer() throws SAXException {
        context.currentResource(AMP_PAGE_PROPERTY);
        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);

        ampTransformer.init(processingContext, null);
    }

    @Test
    void initTransformerWithEmptyPage() throws SAXException {
        context.currentResource(INVALID_PAGE_RESOURCE);
        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);

        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        verify(contentHandler, atLeastOnce()).endElement(isNull(), eq("head"), isNull());
        verify(contentHandler, never()).characters(isNull(), eq(0), anyInt());

    }

    @Test
    void testNotAHeadElement() throws SAXException {
        context.currentPage(AMP_PAGE_PROPERTY);

        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);

        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "body", null);

        verify(contentHandler, atLeastOnce()).endElement(isNull(), eq("body"), isNull());
        verify(contentHandler, never()).characters(isNull(), eq(0), anyInt());

    }


    @Test
    void testHeadLinkAmpOnlyMode() throws SAXException {
        context.currentPage(AMP_PAGE_PROPERTY);


        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);

        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n<link rel=\"canonical\" href=\"/content/amp-only.html\">";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void testHeadLinkPairedAmpMode() throws SAXException {
        context.currentPage(PAIRED_AMP);

        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);

        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n<link rel=\"amphtml\" href=\"/content/paired-amp.amp.html\">";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void testHeadJsContent() throws SAXException, LoginException {
        context.currentPage(AMP_SELECTOR_WITH_AMP_MODE);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with amp selector
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");


        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);


        ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        ResourceResolver serviceResouceResolver = resourceResolver.clone(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE));
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResouceResolver);


        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n"
            + "<link rel=\"canonical\" href=\"/content/amp-selector-with-amp-mode.html\">\n"
            + "<script>console.log('This is amp page script')</script>\n"
            + "<script>console.log('This is amp text component script')</script>\n";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void testHeadJsContentNoComponent() throws SAXException, LoginException {
        context.currentPage(AMP_SELECTOR_WITH_AMP_MODE_NO_COMPONENT);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with amp selector
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");


        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);


        ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        ResourceResolver serviceResouceResolver = resourceResolver.clone(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE));
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResouceResolver);


        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n"
            + "<link rel=\"canonical\" href=\"/content/amp-selector-with-amp-mode-no-component.html\">\n"
            + "<script>console.log('This is amp page script')</script>\n";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void testHeadJsContentNoSupertype() throws SAXException, LoginException {
        context.currentPage(AMP_SELECTOR_WITH_AMP_MODE_NO_SUPERTYPE);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with amp selector
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");


        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);


        ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        ResourceResolver serviceResouceResolver = resourceResolver.clone(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE));
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResouceResolver);


        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n"
            + "<link rel=\"canonical\" href=\"/content/amp-selector-with-amp-mode-no-supertype.html\">\n"
            + "<script>console.log('This is amp page script')</script>\n";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void testHeadJsContentInvalidHeadlib() throws SAXException, LoginException {
        context.currentPage(AMP_SELECTOR_WITH_AMP_MODE_INVALID_HEADLIB);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with amp selector
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");


        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);


        ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        ResourceResolver serviceResouceResolver = resourceResolver.clone(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE));
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResouceResolver);


        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n"
            + "<link rel=\"canonical\" href=\"/content/amp-selector-with-amp-mode-invalid-headlib.html\">\n"
            + "<script>console.log('This is amp page script')</script>\n";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void testHeadJsContentWithNoConfig() throws SAXException, LoginException {

        Map<String, Object> configs = new HashMap<>();
        configs.put("getHeadlibName", StringUtils.EMPTY);
        configs.put("getHeadlibResourceTypeRegex", StringUtils.EMPTY);

        AmpTransformerFactory ampTransformerFactory = context.registerInjectActivateService(new AmpTransformerFactory(), configs);

        AmpTransformerFactory.Cfg ampCfg = ampTransformerFactory.getCfg();

        ampTransformer = new AmpTransformer(ampCfg, resolverFactory);

        setContentHandler();

        context.currentPage(AMP_SELECTOR_WITH_AMP_MODE);
        context.requestPathInfo().setResourcePath(AMP_SELECTOR);
        //with amp selector
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");


        MockSlingHttpServletRequest slingHttpServletRequest = context.request();
        ProcessingContext processingContext = mock(ProcessingContext.class);
        when(processingContext.getRequest()).thenReturn(slingHttpServletRequest);


        ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        ResourceResolver serviceResouceResolver =resourceResolver.clone(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE));
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResouceResolver);


        ampTransformer.init(processingContext, null);
        ampTransformer.endElement(null, "head", null);

        ArgumentCaptor<char[]> charCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);

        String output ="\n"
            + "<link rel=\"canonical\" href=\"/content/amp-selector-with-amp-mode.html\">\n";
        verify(contentHandler, atLeastOnce()).characters(charCaptor.capture(), eq(0), lengthCaptor.capture());
        Assert.assertEquals(output, new String(charCaptor.getValue()));
        Assert.assertEquals(Integer.valueOf(output.length()), lengthCaptor.getValue());

    }

    @Test
    void setContentHandler() {
        ampTransformer.setContentHandler(contentHandler);
    }

    @Test
    void dispose() {
        ampTransformer.dispose();
    }

    @Test
    void setDocumentLocator() {
        ampTransformer.setDocumentLocator(null);
        verify(contentHandler).setDocumentLocator(isNull());
    }

    @Test
    void startDocument() throws SAXException {
        ampTransformer.startDocument();
        verify(contentHandler).startDocument();
    }

    @Test
    void endDocument() throws SAXException {
        ampTransformer.endDocument();
        verify(contentHandler).endDocument();
    }

    @Test
    void startPrefixMapping() throws SAXException {
        ampTransformer.startPrefixMapping(null, null);
        verify(contentHandler).startPrefixMapping(null, null);
    }

    @Test
    void endPrefixMapping() throws SAXException {
        ampTransformer.endPrefixMapping(null);
        verify(contentHandler).endPrefixMapping(null);
    }

    @Test
    void startElement() throws SAXException {
        ampTransformer.startElement(null, null, null, null);
        verify(contentHandler).startElement(null, null, null, null);
    }

    @Test
    void characters() throws SAXException {
        ampTransformer.characters(null, 0, 0);
        verify(contentHandler).characters(null, 0, 0);
    }

    @Test
    void ignorableWhitespace() throws SAXException {
        ampTransformer.ignorableWhitespace(null, 0, 0);
        verify(contentHandler).ignorableWhitespace(null, 0, 0);
    }

    @Test
    void processingInstruction() throws SAXException {
        ampTransformer.processingInstruction(null, null);
        verify(contentHandler).processingInstruction(null, null);
    }

    @Test
    void skippedEntity() throws SAXException {
        ampTransformer.skippedEntity(null);
        verify(contentHandler).skippedEntity(null);
    }
}
