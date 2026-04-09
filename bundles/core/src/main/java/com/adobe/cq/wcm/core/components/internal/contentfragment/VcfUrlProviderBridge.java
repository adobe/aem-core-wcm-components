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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Invokes the DAM CFM {@code VcfUrlProvider} OSGi service without a compile-time dependency on
 * {@code cq-dam-cfm-api}. The service is registered by {@code VcfUrlProviderImpl} in {@code cq-dam-cfm-impl}.
 */
public final class VcfUrlProviderBridge {

    static final String VCF_URL_PROVIDER_CLASS = "com.adobe.cq.dam.cfm.vcf.VcfUrlProvider";

    /**
     * Unit tests set this on the {@link SlingHttpServletRequest} so {@link #resolveBundleContext} uses the Sling OSGi
     * mock {@link BundleContext} (see {@code AbstractContentFragmentTest#getModelInstanceUnderTest}).
     */
    public static final String MOCK_BUNDLE_CONTEXT_REQUEST_ATTRIBUTE =
            VcfUrlProviderBridge.class.getName() + ".mockBundleContext";

    private static final ThreadLocal<BundleContext> RESOLVE_BUNDLE_CONTEXT_OVERRIDE = new ThreadLocal<>();

    private VcfUrlProviderBridge() {
    }

    /**
     * Scoped to a single {@code adaptTo} call in unit tests (same thread as {@code @PostConstruct}).
     */
    public static void pushResolveBundleContextOverride(@Nullable BundleContext ctx) {
        if (ctx == null) {
            RESOLVE_BUNDLE_CONTEXT_OVERRIDE.remove();
        } else {
            RESOLVE_BUNDLE_CONTEXT_OVERRIDE.set(ctx);
        }
    }

    public static void clearResolveBundleContextOverride() {
        RESOLVE_BUNDLE_CONTEXT_OVERRIDE.remove();
    }

    @Nullable
    public static BundleContext bundleContextForCore() {
        Bundle b = FrameworkUtil.getBundle(VcfUrlProviderBridge.class);
        return b != null ? b.getBundleContext() : null;
    }

    @Nullable
    public static BundleContext resolveBundleContext(@Nullable SlingHttpServletRequest request) {
        BundleContext override = RESOLVE_BUNDLE_CONTEXT_OVERRIDE.get();
        if (override != null) {
            return override;
        }
        if (request != null) {
            Object attr = request.getAttribute(MOCK_BUNDLE_CONTEXT_REQUEST_ATTRIBUTE);
            if (attr instanceof BundleContext) {
                return (BundleContext) attr;
            }
        }
        return bundleContextForCore();
    }

    public static boolean isServicePresent(@Nullable BundleContext ctx) {
        if (ctx == null) {
            return false;
        }
        try {
            ServiceReference<?>[] refs = ctx.getServiceReferences(VCF_URL_PROVIDER_CLASS, null);
            return refs != null && refs.length > 0;
        } catch (IllegalArgumentException | InvalidSyntaxException e) {
            return false;
        }
    }

    @Nullable
    public static String getVcfTemplatesApiBase(@Nullable BundleContext ctx) {
        return invokeString(ctx, "getVcfTemplatesApiBase");
    }

    @Nullable
    public static String getVcfPublishUrlFormat(@Nullable BundleContext ctx) {
        return invokeString(ctx, "getVcfPublishUrlFormat");
    }

    @Nullable
    public static String getVcfAuthorUrlFormat(@Nullable BundleContext ctx) {
        return invokeString(ctx, "getVcfAuthorUrlFormat");
    }

    @Nullable
    private static String invokeString(@Nullable BundleContext ctx, String methodName) {
        if (ctx == null) {
            return null;
        }
        ServiceReference<?>[] refs;
        try {
            refs = ctx.getServiceReferences(VCF_URL_PROVIDER_CLASS, null);
        } catch (IllegalArgumentException | InvalidSyntaxException e) {
            return null;
        }
        if (refs == null || refs.length == 0) {
            return null;
        }
        // Try every matching service: order is undefined; the uber-jar may register a provider that returns null
        // while a higher-ranked test stub has the URLs.
        for (ServiceReference<?> ref : refs) {
            Object svc = ctx.getService(ref);
            if (svc == null) {
                continue;
            }
            try {
                Method m = svc.getClass().getMethod(methodName);
                // Test doubles may live in non-exported types (e.g. nested classes on the test classpath); allow invoke.
                m.setAccessible(true);
                Object r = m.invoke(svc);
                if (r instanceof String) {
                    return (String) r;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                continue;
            } finally {
                ctx.ungetService(ref);
            }
        }
        return null;
    }
}
