/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.internal.contentfragment;

import java.util.Hashtable;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.adobe.cq.dam.cfm.vcf.VcfUrlProvider;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class VcfUrlProviderBridgeTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private static final class TestVcfUrlProvider implements VcfUrlProvider {
        @Override
        public String getVcfApiBase() {
            return "api-base";
        }

        @Override
        public String getVcfAuthorUrlFormat() {
            return "author-%s";
        }

        @Override
        public String getVcfPublishUrlFormat() {
            return "publish-%s-%s-%s";
        }

        @Override
        public String getVcfTemplatesApiBase() {
            return "templates-base";
        }
    }

    @BeforeEach
    void setUp() {
        context.registerService(VcfUrlProvider.class, new TestVcfUrlProvider(), new Hashtable<>());
    }

    @AfterEach
    void tearDown() {
        VcfUrlProviderBridge.clearResolveBundleContextOverride();
    }

    @Test
    void resolveBundleContext_prefersThreadLocalOverride() {
        BundleContext bc = context.bundleContext();
        VcfUrlProviderBridge.pushResolveBundleContextOverride(bc);
        assertEquals(bc, VcfUrlProviderBridge.resolveBundleContext(null));
    }

    @Test
    void pushResolveBundleContextOverride_nullClearsThreadLocal() {
        BundleContext bc = context.bundleContext();
        VcfUrlProviderBridge.pushResolveBundleContextOverride(bc);
        VcfUrlProviderBridge.pushResolveBundleContextOverride(null);
        assertNull(VcfUrlProviderBridge.resolveBundleContext(null));
    }

    @Test
    void resolveBundleContext_usesRequestAttribute() {
        BundleContext bc = context.bundleContext();
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(), bc);
        request.setAttribute(VcfUrlProviderBridge.MOCK_BUNDLE_CONTEXT_REQUEST_ATTRIBUTE, bc);
        assertEquals(bc, VcfUrlProviderBridge.resolveBundleContext(request));
    }

    @Test
    void bundleContextForCore_runsWithoutThrowing() {
        // May be null outside an OSGi framework; still exercises the branch.
        VcfUrlProviderBridge.bundleContextForCore();
    }

    @Test
    void isServicePresent_nullContext() {
        assertFalse(VcfUrlProviderBridge.isServicePresent(null));
    }

    @Test
    void isServicePresent_withRegisteredProvider() {
        assertTrue(VcfUrlProviderBridge.isServicePresent(context.bundleContext()));
    }

    @Test
    void isServicePresent_catchesIllegalArgumentFromGetServiceReferences() throws Exception {
        BundleContext bc = mock(BundleContext.class);
        when(bc.getServiceReferences(eq(VcfUrlProvider.class.getName()), isNull()))
            .thenThrow(new IllegalArgumentException("test"));
        assertFalse(VcfUrlProviderBridge.isServicePresent(bc));
    }

    @Test
    void getVcfFormats_delegateToRegisteredProvider() {
        BundleContext bc = context.bundleContext();
        assertEquals("templates-base", VcfUrlProviderBridge.getVcfTemplatesApiBase(bc));
        assertEquals("publish-%s-%s-%s", VcfUrlProviderBridge.getVcfPublishUrlFormat(bc));
        assertEquals("author-%s", VcfUrlProviderBridge.getVcfAuthorUrlFormat(bc));
    }

    @Test
    void getVcfFormats_nullContext() {
        assertNull(VcfUrlProviderBridge.getVcfTemplatesApiBase(null));
        assertNull(VcfUrlProviderBridge.getVcfPublishUrlFormat(null));
        assertNull(VcfUrlProviderBridge.getVcfAuthorUrlFormat(null));
    }

    @Test
    void invokeString_skipsWhenGetServiceReturnsNull() throws Exception {
        BundleContext bc = mock(BundleContext.class);
        ServiceReference<?> ref = mock(ServiceReference.class);
        when(bc.getServiceReferences(eq(VcfUrlProvider.class.getName()), isNull()))
            .thenReturn(new ServiceReference<?>[] { ref });
        doReturn(null).when(bc).getService(ref);
        assertNull(VcfUrlProviderBridge.getVcfPublishUrlFormat(bc));
    }

    @Test
    void invokeString_skipsServiceMissingMethod() throws Exception {
        BundleContext bc = mock(BundleContext.class);
        ServiceReference<?> ref = mock(ServiceReference.class);
        when(bc.getServiceReferences(eq(VcfUrlProvider.class.getName()), isNull()))
            .thenReturn(new ServiceReference<?>[] { ref });
        doReturn(new Object()).when(bc).getService(ref);
        assertNull(VcfUrlProviderBridge.getVcfPublishUrlFormat(bc));
        verify(bc).ungetService(ref);
    }

    @Test
    void invokeString_skipsNonStringReturnThenUsesNext() throws Exception {
        BundleContext bc = mock(BundleContext.class);
        ServiceReference<?> ref1 = mock(ServiceReference.class);
        ServiceReference<?> ref2 = mock(ServiceReference.class);
        Object returnsInteger = new Object() {
            public Integer getVcfPublishUrlFormat() {
                return 42;
            }
        };
        when(bc.getServiceReferences(eq(VcfUrlProvider.class.getName()), isNull()))
            .thenReturn(new ServiceReference<?>[] { ref1, ref2 });
        doReturn(returnsInteger).when(bc).getService(ref1);
        doReturn(new TestVcfUrlProvider()).when(bc).getService(ref2);
        assertEquals("publish-%s-%s-%s", VcfUrlProviderBridge.getVcfPublishUrlFormat(bc));
        verify(bc).ungetService(ref1);
        verify(bc).ungetService(ref2);
    }

    @Test
    void clearResolveBundleContextOverride_removesThreadLocal() {
        BundleContext mockBc = mock(BundleContext.class);
        VcfUrlProviderBridge.pushResolveBundleContextOverride(mockBc);
        assertSame(mockBc, VcfUrlProviderBridge.resolveBundleContext(null));
        VcfUrlProviderBridge.clearResolveBundleContextOverride();
        assertNotSame(mockBc, VcfUrlProviderBridge.resolveBundleContext(null));
    }

    @Test
    void mockBundleContextAttribute_constantIsStable() {
        assertNotNull(VcfUrlProviderBridge.MOCK_BUNDLE_CONTEXT_REQUEST_ATTRIBUTE);
        assertTrue(VcfUrlProviderBridge.MOCK_BUNDLE_CONTEXT_REQUEST_ATTRIBUTE.contains("mockBundleContext"));
    }
}
