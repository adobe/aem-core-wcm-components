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


import static org.junit.Assert.assertEquals;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.ClientLibraryAggregatorService;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(AemContextExtension.class)
public class ClientLibraryAggregatorServiceImplTest {


    private AemContext context = CoreComponentTestContext.newAemContext();
    private ClientLibraryAggregatorService clientLibraryAggregatorService;
    private final String testRegex = "(?![A-Za-z]{2}:).*";

    @Test
    public void testClientLibOutput() {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        clientLibraryAggregatorService = context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());
        String text = clientLibraryAggregatorService.getClientLibOutput("cmp-examples.base.amp","css");
        assertEquals("", text);
    }

    @Test
    public void testClientLibOutputWithResourceTypes() {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        clientLibraryAggregatorService = context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());

        Set<String> resourceTypes = new HashSet<>();
        resourceTypes.add("core/wcm/components/text/v2/text");
        resourceTypes.add("core/wcm/components/teaser/v1/teaser");

        String text = clientLibraryAggregatorService.getClientLibOutput("cmp-examples.base.amp","css", resourceTypes, "clientlibs/amp", "clientlibs/site");
        assertEquals("", text);
    }

    @Test
    public void testResourceTypeRegex() {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        Map<String, Object> configs = getClientLibraryAggregatorConfig();
        clientLibraryAggregatorService = context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl(), configs);
        String resourceTypeRegex = clientLibraryAggregatorService.getResourceTypeRegex();
        assertEquals(testRegex, resourceTypeRegex);
    }

    @Test
    public void testClientlibResourceResolver() throws LoginException {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        clientLibraryAggregatorService = context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());
        ResourceResolver mockResourceResolver = clientLibraryAggregatorService.getClientlibResourceResolver();
        assertEquals(MockResourceResolver.class, mockResourceResolver.getClass());
    }

    @Test
    public void testClientLibType() throws LoginException {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        ClientLibraryAggregatorServiceImpl clientLibraryAggregatorService = new ClientLibraryAggregatorServiceImpl();

        LibraryType cssLibraryType = clientLibraryAggregatorService.getClientLibType("css");
        assertEquals("text/css", cssLibraryType.contentType);
        assertEquals(".css", cssLibraryType.extension);

        LibraryType jsLibraryType = clientLibraryAggregatorService.getClientLibType("js");
        assertEquals("application/javascript", jsLibraryType.contentType);
        assertEquals(".js", jsLibraryType.extension);

    }

    @Test
    public void testClientLibArrayCategoriesWithMultiple() throws LoginException {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        ClientLibraryAggregatorServiceImpl clientLibraryAggregatorService = new ClientLibraryAggregatorServiceImpl();

        String[] categories = new String[] {"clientlib-a", "clientlib-b"};
        String categoriesString = "clientlib-a,clientlib-b";

        assertEquals(ArrayUtils.toString(categories), ArrayUtils.toString(clientLibraryAggregatorService.getClientLibArrayCategories(categoriesString)));
    }

    @Test
    public void testClientLibArrayCategories() throws LoginException {
        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        ClientLibraryAggregatorServiceImpl clientLibraryAggregatorService = new ClientLibraryAggregatorServiceImpl();

        String[] categories = new String[] {"clientlib-a"};
        String categoriesString = "clientlib-a";

        assertEquals(categoriesString, categories[0]);
    }

    private Map<String, Object> getClientLibraryAggregatorConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("resource.type.regex", testRegex);

        return config;
    }


}
