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
package com.adobe.cq.wcm.core.components.internal.services;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;
import java.io.IOException;
import org.apache.sling.api.resource.LoginException;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import com.adobe.cq.wcm.core.components.internal.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;

import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class ClientLibraryAggregatorServiceImplTest {
    private TestLogger testLogger;

    private ClientLibraryAggregatorServiceImpl cl;
    private String categoryCsv;
    private String type;
    private Set<String> resourceTypes;
    private String primaryPath;
    private String fallbackPath;

    private InputStream inputStreamSample;

    private ClientLibrary clientLibraryMock;
    private Collection<ClientLibrary> clientLibraryCollection;
    private HtmlLibrary htmlLibraryMock;
    private HtmlLibraryManager htmlLibraryManagerMock;

    private ResourceResolverFactory resourceResolverFactoryMock;
    private ResourceResolver resourceResolverMock;

    @BeforeEach
    void setUp() {
        this.cl = new ClientLibraryAggregatorServiceImpl();

        this.testLogger = TestLoggerFactory.getTestLogger(ClientLibraryAggregatorServiceImpl.class);

        this.htmlLibraryManagerMock = Mockito.mock(HtmlLibraryManager.class);
        this.clientLibraryMock = Mockito.mock(ClientLibrary.class);
        this.htmlLibraryMock = Mockito.mock(HtmlLibrary.class);

        this.clientLibraryCollection = new LinkedList<ClientLibrary>();
        clientLibraryCollection.add(clientLibraryMock);

        this.resourceResolverFactoryMock = Mockito.mock(ResourceResolverFactory.class);
        this.resourceResolverMock = Mockito.mock(ResourceResolver.class);
    }

    @AfterEach
    void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void getClientLibOutput_categoryCsvEmpty() {
        this.categoryCsv = "";
        this.type = "js";

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type));
    }

    @Test
    public void getClientLibOutput_typeEmpty() {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "";

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("No client libraries of type '{}'.", this.type)));
    }

    @Test
    public void getClientLibOutput_typeWrong() {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "txt";

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("No client libraries of type '{}'.", this.type)));
    }

    @Test
    public void getClientLibOutput_libraryIsNull() {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "js";

        Mockito
          .when(this.htmlLibraryManagerMock.getLibraries(this.cl.getClientLibArrayCategories(this.categoryCsv), this.cl.getClientLibType(this.type), false, true))
          .thenReturn(this.clientLibraryCollection);
        Mockito
          .when(this.htmlLibraryManagerMock.getLibrary(this.cl.getClientLibType(this.type), this.clientLibraryMock.getPath()))
          .thenReturn(null);

        this.cl.htmlLibraryManager = this.htmlLibraryManagerMock;

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type));
    }

    @Test
    public void getClientLibOutput_libraryIncorrect() throws IOException {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "js";

        this.inputStreamSample = Mockito.mock(InputStream.class);

        Mockito
          .when(this.htmlLibraryManagerMock.getLibraries(this.cl.getClientLibArrayCategories(this.categoryCsv), this.cl.getClientLibType(this.type), false, true))
          .thenReturn(this.clientLibraryCollection);
        Mockito
          .when(this.htmlLibraryManagerMock.getLibrary(this.cl.getClientLibType(this.type), this.clientLibraryMock.getPath()))
          .thenReturn(this.htmlLibraryMock);
        Mockito
          .when(this.htmlLibraryMock.getInputStream(false))
          .thenReturn(this.inputStreamSample);

        this.cl.htmlLibraryManager = this.htmlLibraryManagerMock;

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("Error getting input stream from clientlib with path '{}'.", clientLibraryMock.getPath())));
    }

    @Test
    public void getClientLibOutput_libraryCorrect() throws IOException {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "js";

        String inputSample = "Some test data for my input stream";
        this.inputStreamSample = IOUtils.toInputStream(inputSample, StandardCharsets.UTF_8);

        Mockito
          .when(this.htmlLibraryManagerMock.getLibraries(this.cl.getClientLibArrayCategories(this.categoryCsv), this.cl.getClientLibType(this.type), false, true))
          .thenReturn(this.clientLibraryCollection);
        Mockito
          .when(this.htmlLibraryManagerMock.getLibrary(this.cl.getClientLibType(this.type), this.clientLibraryMock.getPath()))
          .thenReturn(this.htmlLibraryMock);
        Mockito
          .when(this.htmlLibraryMock.getInputStream(false))
          .thenReturn(this.inputStreamSample);

        this.cl.htmlLibraryManager = this.htmlLibraryManagerMock;

        assertEquals(inputSample, this.cl.getClientLibOutput(this.categoryCsv, this.type));
    }

    @Test
    public void getClientLibOutputExtended_primaryAndFallbackPathsIncorrect() throws IOException {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "";
        this.resourceTypes = new HashSet<String>();
        this.primaryPath = "";
        this.fallbackPath = "";

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type, this.resourceTypes, this.primaryPath, this.fallbackPath));
        assertThat(this.testLogger.getLoggingEvents(), is(asList(debug("Resource type clientlib aggregator must have a path value."))));
    }

    @Test
    public void getClientLibOutputExtended_primaryPathCorrectTypeEmpty() throws LoginException {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "";
        this.resourceTypes = new HashSet<String>();
        this.primaryPath = "path/to/resources";
        this.fallbackPath = "";

        this.resourceTypes.add("/dummyType");

        Mockito
          .when(this.resourceResolverFactoryMock.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE)))
          .thenReturn(this.resourceResolverMock);

        this.cl.resolverFactory = this.resourceResolverFactoryMock;

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type, this.resourceTypes, this.primaryPath, this.fallbackPath));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("No client libraries of type '{}'.", this.type)));
    }

    @Test
    public void getClientLibOutputExtended_fallbackPathCorrectTypeEmpty() throws LoginException {
        this.categoryCsv = "cmp-examples.base,cmp-examples.site";
        this.type = "";
        this.resourceTypes = new HashSet<String>();
        this.primaryPath = "";
        this.fallbackPath = "path/to/resources";

        this.resourceTypes.add("/dummyType");

        Mockito
          .when(this.resourceResolverFactoryMock.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE)))
          .thenReturn(this.resourceResolverMock);

        this.cl.resolverFactory = this.resourceResolverFactoryMock;

        assertEquals("", this.cl.getClientLibOutput(this.categoryCsv, this.type, this.resourceTypes, this.primaryPath, this.fallbackPath));
        assertThat(this.testLogger.getLoggingEvents(), hasItem(error("No client libraries of type '{}'.", this.type)));
    }
}
