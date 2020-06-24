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
package com.adobe.cq.wcm.core.extensions.amp.models.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.extensions.amp.AmpTestContext;
import com.adobe.cq.wcm.core.extensions.amp.internal.ClientLibraryAggregatorServiceImpl;
import com.adobe.cq.wcm.core.extensions.amp.models.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(AemContextExtension.class)
class ClientLibraryImplTest {

    private static final String TEST_BASE = "/clientlib";
    private static final String TEST_ROOT_PAGE = "/content";
    private static final String CLIENT_LIB_CSS = TEST_ROOT_PAGE + "/clientlib-css";
    private static final String CLIENT_LIB_JS = TEST_ROOT_PAGE + "/clientlib-js";
    private static final String TEST_APPS_ROOT = "/apps/core-components-examples/clientlibs";

    private final AemContext context = AmpTestContext.newAemContext();


    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + AmpTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);
        context.load().json(TEST_BASE + AmpTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);

    }

    @Test
    void testEmptyGetInlineCss() throws Exception {
        context.registerInjectActivateService(new MockHtmlLibraryManager(mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class)));
        context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());

        ClientLibrary clientLibrary = getClientLibraryUnderTestFromResource(CLIENT_LIB_CSS);

        assertEquals("", clientLibrary.getInline());
    }

    @Test
    void testEmptyGetInlineJs() throws Exception {
        context.registerInjectActivateService(new MockHtmlLibraryManager(mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class)));
        context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());
        ClientLibrary clientLibrary = getClientLibraryUnderTestFromResource(CLIENT_LIB_JS);

        assertEquals("", clientLibrary.getInline());
    }

    @Test
    void testGetInlineCss() throws Exception {
        HtmlLibraryManager htmlLibraryManager = context.registerInjectActivateService(mock(MockHtmlLibraryManager.class));
        context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());
        HtmlLibrary library = mock(HtmlLibrary.class);

        ClientLibrary clientLibrary = getClientLibraryUnderTestFromResource(CLIENT_LIB_CSS);
        Resource clientLibResource = context.currentResource(TEST_APPS_ROOT + "/clientlib-base-amp/styles/index.css");

        when(htmlLibraryManager.getLibraries(any(), any(), anyBoolean(), anyBoolean())).thenReturn(Arrays.asList(mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class)));
        when(htmlLibraryManager.getLibrary(any(), any())).thenReturn(library);
        when(library.getInputStream(anyBoolean())).thenReturn(clientLibResource.adaptTo(InputStream.class));

        String outputString = "html { \n box-sizing: border-box;\n font-size: 14px; \n}";
        assertEquals(outputString, clientLibrary.getInline());
    }

    @Test
    void testGetInlineJs() throws Exception {
        HtmlLibraryManager htmlLibraryManager = context.registerInjectActivateService(mock(MockHtmlLibraryManager.class));
        context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());
        HtmlLibrary library = mock(HtmlLibrary.class);

        ClientLibrary clientLibrary = getClientLibraryUnderTestFromResource(CLIENT_LIB_JS);
        Resource clientLibResource = context.currentResource(TEST_APPS_ROOT + "/clientlib-base-amp/scripts/index.js");

        when(htmlLibraryManager.getLibraries(any(), any(), anyBoolean(), anyBoolean())).thenReturn(Arrays.asList(mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class)));
        when(htmlLibraryManager.getLibrary(any(), any())).thenReturn(library);
        when(library.getInputStream(anyBoolean())).thenReturn(clientLibResource.adaptTo(InputStream.class));

        String outputString = "console.log{'cmp-examples.base.amp clientlib js'}";
        assertEquals(outputString, clientLibrary.getInline());
    }

    @Test
    void testEmptyGetInlineLimited() {
        context.registerInjectActivateService(new MockHtmlLibraryManager(mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class)));
        context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());

        ClientLibrary clientLibrary = getClientLibraryUnderTestFromRequest(CLIENT_LIB_CSS);
        assertEquals("", clientLibrary.getInlineLimited());
    }

    private ClientLibrary getClientLibraryUnderTestFromResource(String path) {
        Resource resource = context.currentResource(path + "/jcr:content");
        if (resource != null) {
            return resource.adaptTo(ClientLibrary.class);
        }
        return null;
    }

    private ClientLibrary getClientLibraryUnderTestFromRequest(String path) {
        Resource resource = context.currentResource(path);
        if (resource != null) {
            context.request().setResource(resource);
            return context.request().adaptTo(ClientLibrary.class);
        }
        return null;
    }

}
