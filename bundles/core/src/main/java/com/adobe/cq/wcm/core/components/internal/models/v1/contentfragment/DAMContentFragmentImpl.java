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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.*;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DAMContentFragmentImpl implements DAMContentFragment {

    private static final Logger LOG = LoggerFactory.getLogger(DAMContentFragmentImpl.class);

    /**
     * The name of the master variation.
     */
    private static final String MASTER_VARIATION = "master";

    private final com.adobe.cq.dam.cfm.ContentFragment contentFragment;
    private final String variationName;
    private final String[] elementNames;
    private final String resourceType;

    private String type;
    private List<DAMContentElement> elements;
    private Map<String, DAMContentElement> exportedElements;

    /**
     * Creates a new instance of a content fragment. Requires the content fragment's resource and a content type
     * converter, and optional variation name and element names to be filtered.
     *
     * @param contentFragmentResource resource of the content fragment
     * @param contentTypeConverter    content type converter
     * @param variationName           name of an optional variation
     * @param elementNames            names of optional elements to be filtered
     */
    public DAMContentFragmentImpl(@NotNull Resource contentFragmentResource,
                                  @NotNull ContentTypeConverter contentTypeConverter,
                                  String variationName, String[] elementNames) {

        this.variationName = variationName;
        this.resourceType = contentFragmentResource.getResourceType();
        if (elementNames == null) {
            this.elementNames = null;
        } else {
            this.elementNames = new String[elementNames.length];
            System.arraycopy(elementNames, 0, this.elementNames, 0, elementNames.length);
        }

        this.contentFragment = contentFragmentResource.adaptTo(com.adobe.cq.dam.cfm.ContentFragment.class);
        if (contentFragment == null) {
            LOG.error("Content Fragment can not be initialized because '{}' is not a content fragment.",
                    contentFragmentResource.getPath());
        } else {
            // Type cannot be determined lazily due to leaking resource resolver (query builder)
            this.type = ContentFragmentUtils.getType(contentFragment);

            final Iterator<ContentElement> contentElementIterator =
                    ContentFragmentUtils.filterElements(contentFragment, elementNames);

            // Wrap elements and get their configured variation (if any)
            this.exportedElements = new LinkedHashMap<>();
            while (contentElementIterator.hasNext()) {
                final ContentElement contentElement = contentElementIterator.next();
                ContentVariation variation = null;
                if (StringUtils.isNotEmpty(variationName) && !MASTER_VARIATION.equals(variationName)) {
                    variation = contentElement.getVariation(variationName);
                    if (variation == null) {
                        LOG.warn("Non-existing variation '{}' of element '{}'", variationName, contentElement.getName());
                    }
                }
                this.exportedElements.put(contentElement.getName(),
                        new DAMContentElementImpl(contentTypeConverter, contentElement, variation));
            }

            this.elements = new ArrayList<>(exportedElements.values());
        }
    }

    @Nullable
    @Override
    public String getTitle() {
        return contentFragment.getTitle();
    }

    @Nullable
    @Override
    public String getDescription() {
        return contentFragment.getDescription();
    }

    /**
     * Returns the type of a {@link ContentFragment content fragment}. The type is a string that uniquely identifies the
     * model or template of the content fragment (CF) (e.g. <code>my-project/models/my-model</code> for a structured CF
     * or <code>/content/dam/my-cf/jcr:content/model</code> for a text-only CF).
     *
     * @return the type of the content fragment
     */
    @Nullable
    @Override
    public String getType() {
        return type;
    }

    @NotNull
    @Override
    public String getName() {
        return contentFragment.getName();
    }

    @Nullable
    @Override
    public List<DAMContentElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @NotNull
    @Override
    public Map<String, DAMContentElement> getExportedElements() {
        return exportedElements;
    }

    @NotNull
    @Override
    public String[] getExportedElementsOrder() {
        return ContentFragmentUtils.getItemsOrder(exportedElements);
    }

    @Nullable
    @Override
    public List<Resource> getAssociatedContent() {
        return IteratorUtils.toList(contentFragment.getAssociatedContent());
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resourceType;
    }

    @NotNull
    @Override
    public String getEditorJSON() {
        return ContentFragmentUtils.getEditorJSON(contentFragment, variationName, elementNames);
    }

    /**
     * Represents a content element of a content fragment.
     */
    public static class DAMContentElementImpl implements DAMContentElement {

        private static final Logger LOG = LoggerFactory.getLogger(DAMContentElementImpl.class);

        private static final String TEXT_HTML = "text/html";

        private final ContentTypeConverter converter;
        private final ContentElement element;
        private final ContentVariation variation;

        private String htmlValue;

        /**
         * @param converter the converter to use to convert the value of text elements to HTML
         * @param element   the original element
         * @param variation the configured variation of the element, or {@code null}
         */
        public DAMContentElementImpl(@NotNull ContentTypeConverter converter,
                                     @NotNull ContentElement element,
                                     @Nullable ContentVariation variation) {
            this.converter = converter;
            this.element = element;
            this.variation = variation;
        }


        @NotNull
        @Override
        public String getName() {
            return element.getName();
        }

        @Nullable
        @Override
        public String getTitle() {
            return element.getTitle();
        }

        private FragmentData getData() {
            if (variation != null) {
                return variation.getValue();
            }
            return element.getValue();
        }

        @NotNull
        @Override
        public String getDataType() {
            return getData().getDataType().getTypeString();
        }

        @Nullable
        @Override
        public Object getValue() {
            return getData().getValue();
        }

        @Nullable
        @Override
        public <T> T getValue(Class<T> var1) {
            return getData().getValue(var1);
        }

        @NotNull
        @Override
        public String getExportedType() {
            final FragmentData value = getData();
            // Mime type for text-based data types
            String type = value.getContentType();

            // Data type for non text-based data types
            if (type == null) {
                type = value.getDataType().getTypeString();
            }

            return type;
        }

        private String getContentType() {
            return getData().getContentType();
        }

        @Override
        public boolean isMultiLine() {
            String contentType = getContentType();
            // a text element is defined as a single-valued element with a certain content type (e.g. "text/plain",
            // "text/html", "text/x-markdown", potentially others)
            return contentType != null && contentType.startsWith("text/") && !isMultiValue();
        }

        @Override
        public boolean isMultiValue() {
            return getData().getDataType().isMultiValue();
        }

        @Nullable
        @Override
        public String getHtml() {
            // restrict this method to text elements
            if (!isMultiLine()) {
                return null;
            }

            String contentType = getContentType();
            String[] values = getData().getValue(String[].class);
            String value = null;
            if (values != null) {
                value = StringUtils.join(values, ", ");
            }

            if (TEXT_HTML.equals(contentType)) {
                // return HTML as is
                return value;
            } else {
                if (htmlValue == null) {
                    try {
                        // convert element value to HTML
                        htmlValue = converter.convertToHTML(value, contentType);
                    } catch (ContentFragmentException e) {
                        LOG.warn("Could not convert value to HTML", e);
                        return null;
                    }
                }
                return htmlValue;
            }
        }
    }
}
