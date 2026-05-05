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
package com.adobe.cq.wcm.core.components.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.FragmentData;

/**
 * Builds structured payloads for composite content fragment fields using the CFM extensions API.
 *
 * <p>Export shape:</p>
 * <ul>
 *     <li><strong>Single-valued composite</strong> ({@link ContentElement} multi-value flag {@code false}):
 *     one JSON object with optional {@value #JSON_PN_MODEL_PATH} plus field entries (no {@code items} wrapper).</li>
 *     <li><strong>Multi-valued composite</strong> ({@code true}): JSON array of such objects.</li>
 * </ul>
 *
 * <p>The Adobe {@code cq-dam-cfm-extensions} bundle is resolved at runtime on AEM; access is reflection-based so this
 * module builds cleanly against the AEM uber-jar alone.</p>
 */
public final class ContentFragmentCompositeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentCompositeUtils.class);

    private static final String EXT_FRAGMENT_PKG = "com.adobe.cq.dam.cfm.extensions.fragment.";
    private static final String COMPOSITE_ELEMENT_CLASS = EXT_FRAGMENT_PKG + "CompositeElement";
    private static final String COMPOSITE_VARIATION_CLASS = EXT_FRAGMENT_PKG + "CompositeVariation";

    private static final ConcurrentHashMap<ClassLoader, Optional<Class<?>>> COMPOSITE_ELEMENT_TYPES =
            new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassLoader, Optional<Class<?>>> COMPOSITE_VARIATION_TYPES =
            new ConcurrentHashMap<>();

    /**
     * Variation name that refers to master content.
     */
    public static final String MASTER_VARIATION = "master";

    /**
     * JSON property name for the resolved composite model path on each exported item.
     */
    public static final String JSON_PN_MODEL_PATH = "_modelPath";

    /**
     * @deprecated Legacy wrapper key; composite export no longer nests instances under {@code items}. Kept for binary
     * compatibility of constants only.
     */
    @Deprecated
    public static final String JSON_PN_ITEMS = "items";

    private ContentFragmentCompositeUtils() {
    }

    /**
     * @param element content fragment field element
     * @return {@code true} if the element is a composite field and the CFM extensions API is present
     */
    public static boolean isCompositeElement(@Nullable ContentElement element) {
        if (element == null) {
            return false;
        }
        Class<?> compositeType = resolveCompositeElementType(element.getClass().getClassLoader());
        return compositeType != null && compositeType.isInstance(element);
    }

    /**
     * Builds a structure suitable for Jackson JSON export.
     *
     * @param element             top-level or nested content element
     * @param configuredVariation variation name from the content fragment component (may be blank or master)
     * @return for single-valued composites, a {@link Map}; for multi-valued composites, a {@link List} of maps;
     *         {@code null} when not composite or extensions unavailable
     */
    @Nullable
    public static Object buildCompositeExport(@NotNull ContentElement element,
                                              @Nullable String configuredVariation) {

        if (!isCompositeElement(element)) {
            return null;
        }

        boolean multi = isCompositeMultiValue(element);
        Object composite = element;

        if (useMasterCompositeExport(configuredVariation)) {
            return buildMasterCompositePayload(composite, multi);
        }

        Object compositeVariation = invokeWithStringArg(composite, "getCompositeVariation", configuredVariation);
        if (compositeVariation == null) {
            LOG.debug("No composite variation '{}' for element '{}', falling back to master composite export.",
                    configuredVariation, element.getName());
            return buildMasterCompositePayload(composite, multi);
        }

        return buildVariationCompositePayload(compositeVariation, configuredVariation, multi);
    }

    private static boolean isCompositeMultiValue(@NotNull ContentElement element) {
        FragmentData data = element.getValue();
        return data != null && data.getDataType().isMultiValue();
    }

    /**
     * Uses the field template when available (variation subtree).
     */
    private static boolean isFieldMultiValued(@Nullable Object fieldTemplate) {
        if (fieldTemplate == null) {
            return false;
        }
        try {
            Object dataType = fieldTemplate.getClass().getMethod("getDataType").invoke(fieldTemplate);
            if (dataType != null) {
                Object mv = dataType.getClass().getMethod("isMultiValue").invoke(dataType);
                return Boolean.TRUE.equals(mv);
            }
        } catch (ReflectiveOperationException e) {
            LOG.debug("Could not read multi-value flag from field template {}", fieldTemplate.getClass().getName(), e);
        }
        return false;
    }

    private static boolean useMasterCompositeExport(@Nullable String configuredVariation) {
        return StringUtils.isBlank(configuredVariation) || MASTER_VARIATION.equals(configuredVariation);
    }

    @Nullable
    private static Class<?> loadClass(@Nullable ClassLoader classLoader, String className) {
        if (classLoader == null) {
            return null;
        }
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            LOG.debug("Class not found (CFM extensions may be absent): {}", className);
            return null;
        }
    }

    @Nullable
    private static Class<?> resolveCompositeElementType(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            return null;
        }
        return COMPOSITE_ELEMENT_TYPES.computeIfAbsent(classLoader,
                cl -> Optional.ofNullable(loadClass(cl, COMPOSITE_ELEMENT_CLASS))).orElse(null);
    }

    @Nullable
    private static Class<?> resolveCompositeVariationType(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            return null;
        }
        return COMPOSITE_VARIATION_TYPES.computeIfAbsent(classLoader,
                cl -> Optional.ofNullable(loadClass(cl, COMPOSITE_VARIATION_CLASS))).orElse(null);
    }

    private static Object invokeNoArg(@Nullable Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (ReflectiveOperationException e) {
            LOG.debug("Reflective invoke failed: {} on {}", methodName, target.getClass().getName(), e);
            return null;
        }
    }

    private static Object invokeWithStringArg(@Nullable Object target, String methodName, String arg) {
        if (target == null) {
            return null;
        }
        try {
            return target.getClass().getMethod(methodName, String.class).invoke(target, arg);
        } catch (ReflectiveOperationException e) {
            LOG.debug("Reflective invoke failed: {} on {}", methodName, target.getClass().getName(), e);
            return null;
        }
    }

    private static Iterator<?> invokeIterator(@Nullable Object target, String methodName) {
        Object raw = invokeNoArg(target, methodName);
        if (raw instanceof Iterator) {
            return (Iterator<?>) raw;
        }
        return Collections.emptyIterator();
    }

    private static boolean isCompositeVariationInstance(Object structuredVariation) {
        Class<?> cvType = resolveCompositeVariationType(structuredVariation.getClass().getClassLoader());
        return cvType != null && cvType.isInstance(structuredVariation);
    }

    @NotNull
    private static Object normalizeContainerRows(@NotNull List<Map<String, Object>> rows, boolean multiValue) {
        if (multiValue) {
            return rows;
        }
        if (rows.isEmpty()) {
            return new LinkedHashMap<String, Object>();
        }
        return rows.get(0);
    }

    @NotNull
    private static Object buildMasterCompositePayload(@NotNull Object compositeElement, boolean multiValue) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Iterator<?> containers = invokeIterator(compositeElement, "getCompositeContainers");
        while (containers.hasNext()) {
            Object container = containers.next();
            if (container != null) {
                rows.add(buildMasterItem(container));
            }
        }
        return normalizeContainerRows(rows, multiValue);
    }

    @NotNull
    private static Map<String, Object> buildMasterItem(@NotNull Object container) {
        Map<String, Object> item = new LinkedHashMap<>();
        Object model = invokeNoArg(container, "getModel");
        if (model != null) {
            Object path = invokeNoArg(model, "getPath");
            if (path instanceof String && StringUtils.isNotBlank((String) path)) {
                item.put(JSON_PN_MODEL_PATH, path);
            }
        }
        Iterator<?> fields = invokeIterator(container, "getElements");
        while (fields.hasNext()) {
            Object fieldObj = fields.next();
            if (fieldObj instanceof ContentElement) {
                putMasterField(item, (ContentElement) fieldObj);
            }
        }
        return item;
    }

    private static void putMasterField(@NotNull Map<String, Object> item, @NotNull ContentElement field) {
        Object nameObj = invokeNoArg(field, "getName");
        String name = nameObj instanceof String ? (String) nameObj : field.getName();
        if (isCompositeElement(field)) {
            boolean nestedMulti = isCompositeMultiValue(field);
            Object nested = buildMasterCompositePayload(field, nestedMulti);
            item.put(name, nested);
        } else {
            Object dataObj = invokeNoArg(field, "getValue");
            Object value = null;
            if (dataObj instanceof FragmentData) {
                value = ((FragmentData) dataObj).getValue();
            }
            item.put(name, value);
        }
    }

    @NotNull
    private static Object buildVariationCompositePayload(@NotNull Object compositeVariation,
                                                         @NotNull String variationName,
                                                         boolean multiValue) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Iterator<?> containers = invokeIterator(compositeVariation, "getCompositeContainers");
        while (containers.hasNext()) {
            Object container = containers.next();
            if (container != null) {
                rows.add(buildVariationItem(container, variationName));
            }
        }
        return normalizeContainerRows(rows, multiValue);
    }

    @NotNull
    private static Map<String, Object> buildVariationItem(@NotNull Object container,
                                                          @NotNull String variationName) {
        Map<String, Object> item = new LinkedHashMap<>();
        Object model = invokeNoArg(container, "getModel");
        if (model != null) {
            Object path = invokeNoArg(model, "getPath");
            if (path instanceof String && StringUtils.isNotBlank((String) path)) {
                item.put(JSON_PN_MODEL_PATH, path);
            }
        }
        Iterator<?> fields = invokeIterator(container, "getVariationFields");
        while (fields.hasNext()) {
            Object structuredVariation = fields.next();
            if (structuredVariation == null) {
                continue;
            }
            Object fieldObj = invokeNoArg(structuredVariation, "getField");
            String fieldName = null;
            if (fieldObj != null) {
                Object fn = invokeNoArg(fieldObj, "getName");
                if (fn instanceof String) {
                    fieldName = (String) fn;
                }
            }
            if (fieldName != null) {
                item.put(fieldName, exportStructuredVariation(structuredVariation, variationName));
            }
        }
        return item;
    }

    @Nullable
    private static Object exportStructuredVariation(@NotNull Object structuredVariation,
                                                    @NotNull String variationName) {
        if (isCompositeVariationInstance(structuredVariation)) {
            boolean nestedMulti = isFieldMultiValued(invokeNoArg(structuredVariation, "getField"));
            return buildVariationCompositePayload(structuredVariation, variationName, nestedMulti);
        }
        Object dataObj = invokeNoArg(structuredVariation, "getValue");
        if (dataObj instanceof FragmentData) {
            return ((FragmentData) dataObj).getValue();
        }
        return null;
    }
}
