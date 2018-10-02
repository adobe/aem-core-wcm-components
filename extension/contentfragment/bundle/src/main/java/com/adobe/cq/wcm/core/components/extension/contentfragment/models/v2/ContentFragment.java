/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.ContentFragmentListComponentModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.api.resource.Resource;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Defines the API for a content fragment used by the {@link ContentFragmentComponentModel content fragment component}
 * and the {@link ContentFragmentListComponentModel content fragment list component}. The model provides information
 * about the referenced content fragment and access to representations of its elements.
 *
 * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
 */
@ConsumerType
public interface ContentFragment extends ComponentExporter {

    /**
     * Name of the property (in JSON export) that provides the path to the model of the content fragment
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    String JSON_PN_MODEL = "model";

    /**
     * Name of the property (in JSON export) that provides an object containing the elements of the
     * content fragment; the keys of the object properties refer to the element names
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    String JSON_PN_ELEMENTS = "elements";

    /**
     * Name of the property (in JSON export) that provides an array containing the names of the
     * elements of the content fragment in the correct order
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    String JSON_PN_ELEMENTS_ORDER = "elementsOrder";

    /**
     * Returns the title of the content fragment.
     *
     * @return the title of the content fragment
     * @see com.adobe.cq.dam.cfm.ContentFragment#getTitle()
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nullable
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the description of the content fragment.
     *
     * @return the description of the content fragment
     * @see com.adobe.cq.dam.cfm.ContentFragment#getDescription()
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nullable
    default String getDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the type of the content fragment. The type is a string that uniquely identifies the model or template of
     * the content fragment (e.g. "my-project/models/my-model" for a structured or
     * "/content/dam/my-cf/jcr:content/model" for a text-only content fragment).
     *
     * @return the type of the content fragment
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nullable
    @JsonProperty(JSON_PN_MODEL)
    default String getType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a list of all content fragment elements.
     *
     * @return a selection or all of the content fragment's elements
     * @see com.adobe.cq.dam.cfm.ContentFragment#getElements()
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nullable
    @JsonIgnore
    default List<ContentElement> getElements() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a map of elements that are used for creating the JSON export
     *
     * @return a map containing the elements that are subject to be exported
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nonnull
    @JsonProperty(JSON_PN_ELEMENTS)
    default Map<String, ContentElement> getExportedElements() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the names of the elements in the correct order. The names correspond to the keys of the map
     * returned by {@link ContentFragment#getExportedElements}.
     *
     * @return Array that determines the order of the elements
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nonnull
    @JsonProperty(JSON_PN_ELEMENTS_ORDER)
    default String[] getExportedElementsOrder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a list of resources representing the collections that are associated to this content fragment.
     *
     * @return a list of collection resources
     * @see ContentFragment#getAssociatedContent()
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nullable
    @JsonIgnore
    default List<Resource> getAssociatedContent() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the type of the resource for which the export is performed.
     *
     * @return the type of the resource
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a JSON format string containing information about this fragment.
     *
     * @return JSON string
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nonnull
    @JsonIgnore
    default String getEditorJSON() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the path to the content fragment.
     *
     * @return the path of the content fragment
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @Nonnull
    default String getPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Represents a content element of a content fragment.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
     */
    @ConsumerType
    interface ContentElement extends ComponentExporter {

        /**
         * Returns the technical name of the element.
         *
         * @return the technical name of the element
         * @see com.adobe.cq.dam.cfm.ContentElement#getName()
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nonnull
        @JsonIgnore
        default String getName() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the title of the element.
         *
         * @return the title of the element
         * @see com.adobe.cq.dam.cfm.ContentElement#getTitle()
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nullable
        default String getTitle() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the string representation of data type of {@link com.adobe.cq.dam.cfm.FragmentData} of the element.
         * For the possible values see {@link com.adobe.cq.dam.cfm.BasicDataType}. Note that this doesn't
         * contain information about the multivalued characteristic of element. Eg. even if the actual value is of type
         * String [], the data type returned would be String.
         *
         * @return the data type string
         * @see com.adobe.cq.dam.cfm.FragmentData#getDataType()
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nonnull
        default String getDataType() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the value of the element. The returned object's type would correspond to the types as specified in
         * {@link com.adobe.cq.dam.cfm.BasicDataType} or an array of those types.
         *
         * @return the value of the element
         * @see com.adobe.cq.dam.cfm.FragmentData#getValue()
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nullable
        default Object getValue() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the type of the resource for which the export is performed.
         *
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nonnull
        @Override
        default String getExportedType() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns {@code true} if this is a multiline text element, i.e. a textual element containing multiple lines
         * (paragraphs).
         *
         * @return {@code true} if the element is a multiline text element, {@code false} otherwise
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @JsonIgnore
        default boolean isMultiLine() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the value of a multiline text element converted to HTML. It uses
         * {@link com.adobe.cq.dam.cfm.converter.ContentTypeConverter#convertToHTML(String, String)} to convert the
         * value to html. Returns {@code null} for non-multiline-text elements.
         *
         * @return the value of the element converted to HTML or {@code null} for non-multiline-text elements
         * @see #isMultiLine()
         * @see com.adobe.cq.dam.cfm.converter.ContentTypeConverter#convertToHTML(String, String)
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nullable
        @JsonIgnore
        default String getHtml() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the paragraphs of a multiline text element.
         *
         * @return an array containing HTML paragraphs or {@code null} for non-multiline-text elements
         * @see #isMultiLine()
         * @see #getHtml()
         * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2 1.1.0
         */
        @Nullable
        default String[] getParagraphs() {
            throw new UnsupportedOperationException();
        }
    }
}
