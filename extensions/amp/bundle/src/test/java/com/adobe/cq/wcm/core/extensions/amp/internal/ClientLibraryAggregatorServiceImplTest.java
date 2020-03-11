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
package com.adobe.cq.wcm.core.extensions.amp.internal;


import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.extensions.amp.AmpTestContext;
import com.adobe.cq.wcm.core.extensions.amp.services.ClientLibraryAggregatorService;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ClientLibraryAggregatorServiceImplTest {


    private AemContext context = AmpTestContext.newAemContext();
    private final String testRegex = "(?![A-Za-z]{2}:).*";

    private ClientLibraryAggregatorService serviceUnderTest;

    @BeforeEach
    void setUp() {
        ClientLibrary clientLibrary = Mockito.mock(ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        serviceUnderTest =
                context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl(),
                        ImmutableMap.of("resource.type.regex", testRegex));
    }

    @Test
    void testClientLibOutput() {
        String cssLibrary = serviceUnderTest.getClientLibOutput("cmp-examples.base.amp","css");
        assertEquals("", cssLibrary);

        String jsLibrary = serviceUnderTest.getClientLibOutput("cmp-examples.base.amp","js");
        assertEquals("", jsLibrary);

        String xmlLibrary = serviceUnderTest.getClientLibOutput("cmp-examples.base.amp","xml");
        assertEquals("", xmlLibrary);

        String emptyLibrary = serviceUnderTest.getClientLibOutput("","css");
        assertEquals("", emptyLibrary);
    }

    @Test
    void testClientLibOutputWithResourceTypes() {
        Set<String> resourceTypes = new HashSet<>();
        resourceTypes.add("core/wcm/components/text/v2/text");
        resourceTypes.add("core/wcm/components/teaser/v1/teaser");

        String clientLibOutput_1 = serviceUnderTest.getClientLibOutput("cmp-examples.base.amp","css", resourceTypes, "clientlibs/amp", "clientlibs/site");
        assertEquals("", clientLibOutput_1);


        String clientLibOutput_2 = serviceUnderTest.getClientLibOutput("cmp-examples.base.amp","css", resourceTypes, "", "");
        assertEquals("", clientLibOutput_2);

        String clientLibOutput_3 = serviceUnderTest.getClientLibOutput("cmp-examples.base.amp,cmp-examples.base.amp","css", resourceTypes, "", "");
        assertEquals("", clientLibOutput_3);
    }

    @Test
    void testValidResourceTypeRegex() {
        String resourceTypeRegex = serviceUnderTest.getResourceTypeRegex();
        assertEquals(testRegex, resourceTypeRegex);
    }

    @Test
    void testClientlibResourceResolver() throws LoginException {
        assertNotNull(serviceUnderTest.getClientlibResourceResolver());
    }

    @Test
    void testClientLibType() throws LoginException {
        ClientLibraryAggregatorServiceImpl clientLibraryAggregatorService = new ClientLibraryAggregatorServiceImpl();

        LibraryType cssLibraryType = clientLibraryAggregatorService.getClientLibType("css");
        assertEquals("text/css", cssLibraryType.contentType);
        assertEquals(".css", cssLibraryType.extension);

        LibraryType jsLibraryType = clientLibraryAggregatorService.getClientLibType("js");
        assertEquals("application/javascript", jsLibraryType.contentType);
        assertEquals(".js", jsLibraryType.extension);

        LibraryType xmlLibraryType = clientLibraryAggregatorService.getClientLibType("xml");
        assertNull(xmlLibraryType);

    }

    @Test
    void testClientLibArrayCategoriesWithMultiple() {
        ClientLibraryAggregatorServiceImpl clientLibraryAggregatorService = new ClientLibraryAggregatorServiceImpl();

        String[] categories = new String[] {"clientlib-a", "clientlib-b"};
        String categoriesString = "clientlib-a,clientlib-b";

        assertEquals(ArrayUtils.toString(categories), ArrayUtils.toString(clientLibraryAggregatorService.getClientLibArrayCategories(categoriesString)));
    }
}
