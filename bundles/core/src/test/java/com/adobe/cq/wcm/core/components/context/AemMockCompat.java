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
package com.adobe.cq.wcm.core.components.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.Language;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;

/**
 * Backports behaviour of newer AEM Mocks (5.x, Java 11+) on top of the Java 8 compatible AEM Mocks 4.x, so that the
 * unit tests can be run with Java 8 as well.
 */
final class AemMockCompat {

    private AemMockCompat() {
        // only static methods
    }

    /**
     * Wraps an {@link Externalizer} so that the paths are mapped with the resource resolver before being externalized.
     */
    static Externalizer mappingExternalizer(@NotNull Externalizer delegate) {
        return proxy(Externalizer.class, delegate, (method, args) -> {
            Class<?>[] types = method.getParameterTypes();
            int last = types.length - 1;
            if (last > 0 && types[last] == String.class && args[last] != null) {
                ResourceResolver resolver = null;
                if (args[0] instanceof ResourceResolver) {
                    resolver = (ResourceResolver) args[0];
                } else if (args[0] instanceof SlingHttpServletRequest) {
                    resolver = ((SlingHttpServletRequest) args[0]).getResourceResolver();
                }
                if (resolver != null) {
                    args[last] = resolver.map((String) args[last]);
                }
            }
            return method.invoke(delegate, args);
        });
    }

    /**
     * Wraps a {@link LanguageManager} to add support for {@link LanguageManager#getLanguageRoot(Resource, boolean)}.
     */
    static LanguageManager contentAwareLanguageManager(@NotNull LanguageManager delegate) {
        return proxy(LanguageManager.class, delegate, (method, args) -> {
            if ("getLanguageRoot".equals(method.getName()) && args != null && args.length == 2) {
                Resource resource = getLanguageRootResource((Resource) args[0], (Boolean) args[1]);
                return resource != null ? resource.adaptTo(Page.class) : null;
            }
            return method.invoke(delegate, args);
        });
    }

    @Nullable
    private static Resource getLanguageRootResource(@NotNull Resource resource, boolean respectContent) {
        String path = resource.getPath();
        if (respectContent) {
            int idx = path.indexOf("/jcr:content");
            if (idx > 0) {
                path = path.substring(0, idx);
            }
            Resource current = resource.getResourceResolver().getResource(path);
            while (current != null && !StringUtils.equals(current.getPath(), "/")) {
                ValueMap props = current.getValueMap();
                if (props.get("jcr:content/cq:isLanguageRoot", Boolean.FALSE)) {
                    String iso = props.get("jcr:content/jcr:language", "");
                    Language language = iso.isEmpty() ? null : LanguageUtil.getLanguage(iso);
                    if (language != null) {
                        return current;
                    }
                }
                current = current.getParent();
            }
        }
        String rootPath = LanguageUtil.getLanguageRoot(path);
        return rootPath != null ? resource.getResourceResolver().getResource(rootPath) : null;
    }

    @FunctionalInterface
    private interface Invoker {
        Object invoke(Method method, Object[] args) throws Exception;
    }

    private static <T> T proxy(Class<T> type, T delegate, Invoker invoker) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(delegate, args);
            }
            try {
                return invoker.invoke(method, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        };
        return type.cast(Proxy.newProxyInstance(AemMockCompat.class.getClassLoader(), new Class<?>[] {type}, handler));
    }
}
