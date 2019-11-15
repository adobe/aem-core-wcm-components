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

import static org.junit.Assert.assertEquals;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.services.ClientLibraryAggregatorServiceImpl;
import com.adobe.cq.wcm.core.components.models.ClientLibrary;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(AemContextExtension.class)
public class ClientLibraryImplTest {

    private static final String TEST_BASE = "/clientlib";
    private static final String TEST_ROOT_PAGE = "/content";
    private static final String CLIENT_LIB = TEST_ROOT_PAGE + "/clientlib";

    protected final AemContext context = CoreComponentTestContext.newAemContext();


    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);

        com.adobe.granite.ui.clientlibs.ClientLibrary clientLibrary = Mockito.mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class);
        context.registerInjectActivateService(new MockHtmlLibraryManager(clientLibrary));
        context.registerInjectActivateService(new ClientLibraryAggregatorServiceImpl());
        context.currentPage(CLIENT_LIB);

    }

    @Test
    void testGetInline() {
        Resource resource = context.currentResource(CLIENT_LIB + "/" + JcrConstants.JCR_CONTENT);
        ClientLibrary clientLibrary = resource.adaptTo(ClientLibraryImpl.class);
        assertEquals("", clientLibrary.getInline());
    }

    @Test
    void testGetInlineLimited() {
        Resource resource = context.currentResource(CLIENT_LIB + "/" + JcrConstants.JCR_CONTENT);
        ClientLibrary clientLibrary = context.request().adaptTo(ClientLibraryImpl.class);
        // TODO - currentPage is getting null when adapted from resource, inject the currentPage variable.
        assertEquals("", clientLibrary.getInlineLimited());
    }

}
