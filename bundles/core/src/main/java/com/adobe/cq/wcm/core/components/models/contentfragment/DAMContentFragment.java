/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.models.contentfragment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines the API for a DAM content fragment used by the {@link ContentFragment content fragment component}
 * and the {@link ContentFragmentList content fragment list component}. The model provides information about the
 * referenced content fragment and access to representations of its elements.
 *
 * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
 */
@ConsumerType
public interface DAMContentFragment extends ComponentExporter {

    /**
     * Name of the property (in JSON export) that provides the path to the model of the content fragment
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String JSON_PN_MODEL = "model";

    /**
     * Name of the property (in JSON export) that provides an object containing the elements of the
     * content fragment; the keys of the object properties refer to the element names
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String JSON_PN_ELEMENTS = "elements";

    /**
     * Name of the property (in JSON export) that provides an array containing the names of the
     * elements of the content fragment in the correct order
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String JSON_PN_ELEMENTS_ORDER = "elementsOrder";

    /**
     * Represents a content element of a content fragment.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @ConsumerType
    interface DAMContentElement extends ComponentExporter {

        /**
         * Returns the technical name of the element.
         *
         * @return the technical name of the element
         * @see com.adobe.cq.dam.cfm.ContentElement#getName()
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @NotNull
        @JsonIgnore
        default String getName() {
            return "";
        }

        /**
         * Returns the title of the element.
         *
         * @return the title of the element
         * @see com.adobe.cq.dam.cfm.ContentElement#getTitle()
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @Nullable
        default String getTitle() {
            return null;
        }

        /**
         * Returns the string representation of data type of {@link com.adobe.cq.dam.cfm.FragmentData} of the element.
         * For the possible values see {@link com.adobe.cq.dam.cfm.BasicDataType}. Note that this doesn't
         * contain information about the multivalued characteristic of element. Eg. even if the actual value is of type
         * String [], the data type returned would be String.
         *
         * @return the data type string
         * @see com.adobe.cq.dam.cfm.FragmentData#getDataType()
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @NotNull
        default String getDataType() {
            return "";
        }

        /**
         * Returns the value of the element. The returned object's type would correspond to the types as specified in
         * {@link com.adobe.cq.dam.cfm.BasicDataType} or an array of those types.
         *
         * @return the value of the element
         * @see com.adobe.cq.dam.cfm.FragmentData#getValue()
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @Nullable
        default Object getValue() {
            return null;
        }

        /**
         * Returns the value of the element. The returned object's type would correspond to the types as specified in
         * {@link com.adobe.cq.dam.cfm.BasicDataType} or an array of those types.
         *
         * @param <T> type of the element
         * @param var1 element object
         * @return the value of the element
         * @see com.adobe.cq.dam.cfm.FragmentData#getValue()
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @Nullable
        default <T> T getValue(Class<T> var1) {
            return null;
        }

        /**
         * Returns the type of the resource for which the export is performed.
         *
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @NotNull
        @Override
        default String getExportedType() {
            return "";
        }

        /**
         * Returns {@code true} if this is a multiline text element, i.e. a textual element containing multiple lines
         * (paragraphs).
         *
         * @return {@code true} if the element is a multiline text element, {@code false} otherwise
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @JsonIgnore
        default boolean isMultiLine() {
            return false;
        }

        /**
         * Returns {@code true} if this is a multi-valued element value.
         *
         * @return {@code true} if the element is multi-valued, {@code false} otherwise
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @JsonProperty("multiValue")
        default boolean isMultiValue() {
            return false;
        }

        /**
         * Returns the value of a multiline text element converted to HTML. It uses
         * {@link com.adobe.cq.dam.cfm.converter.ContentTypeConverter#convertToHTML(String, String)} to convert the
         * value to html. Returns {@code null} for non-multiline-text elements.
         *
         * @return the value of the element converted to HTML or {@code null} for non-multiline-text elements
         * @see #isMultiLine()
         * @see com.adobe.cq.dam.cfm.converter.ContentTypeConverter#convertToHTML(String, String)
         * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
         */
        @Nullable
        @JsonIgnore
        default String getHtml() {
            return null;
        }
    }

    /**
     * Returns the title of the content fragment.
     *
     * @return the title of the content fragment
     * @see com.adobe.cq.dam.cfm.ContentFragment#getTitle()
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @Nullable
    default String getTitle() {
        return null;
    }

    /**
     * Returns the technical name of the content fragment.
     *
     * @return the technical name of the content fragment
     * @see com.adobe.cq.dam.cfm.ContentFragment#getName()
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.3.0
     */
    @NotNull
    @JsonIgnore
    default String getName() {
        return "";
    }

    /**
     * Returns the description of the content fragment.
     *
     * @return the description of the content fragment
     * @see com.adobe.cq.dam.cfm.ContentFragment#getDescription()
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @Nullable
    default String getDescription() {
        return null;
    }

    /**
     * Returns the type of the content fragment. The type is a string that uniquely identifies the model or template of
     * the content fragment (e.g. "my-project/models/my-model" for a structured or
     * "/content/dam/my-cf/jcr:content/model" for a text-only content fragment).
     *
     * @return the type of the content fragment
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @Nullable
    @JsonProperty(JSON_PN_MODEL)
    default String getType() {
        return null;
    }

    /**
     * Returns a list of content fragment elements. The list contains the elements whose names are specified in the
     * property {@link ContentFragment#PN_ELEMENT_NAMES} (in that order, and skipping non-existing
     * elements). If {@link ContentFragment#PN_ELEMENT_NAMES} is not set, then all elements are returned,
     * in the order in which they occur in the content fragment.
     *
     * @return a selection or all of the content fragment's elements
     * @see com.adobe.cq.dam.cfm.ContentFragment#getElements()
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @Nullable
    @JsonIgnore
    default List<DAMContentElement> getElements() {
        return null;
    }

    /**
     * Returns a map of elements that are used for creating the JSON export. The keys of the map determine the
     * name of the element. The value holds the Sling Model that is used for the export. The map only contains
     * elements that are configured through the property {@link ContentFragment#PN_ELEMENT_NAMES}
     * or all elements if the property is not set.
     *
     * @return a map containing the elements that are subject to be exported
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @JsonProperty(JSON_PN_ELEMENTS)
    default Map<String, DAMContentElement> getExportedElements() {
        return Collections.emptyMap();
    }

    /**
     * Returns the names of the elements in the correct order. The names correspond to the keys of the map
     * returned by {@link DAMContentFragment#getExportedElements}.
     *
     * @return Array that determines the order of the elements
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @JsonProperty(JSON_PN_ELEMENTS_ORDER)
    default String[] getExportedElementsOrder() {
        return new String[]{};
    }

    /**
     * Returns a list of resources representing the collections that are associated to this content fragment.
     *
     * @return a list of collection resources
     * @see DAMContentFragment#getAssociatedContent()
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @Nullable
    @JsonIgnore
    default List<Resource> getAssociatedContent() {
        return null;
    }

    /**
     * Returns the type of the resource for which the export is performed.
     *
     * @return the type of the resource
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        return "";
    }

    /**
     * Returns a JSON format string containing information about this fragment.
     *
     * @return JSON string
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @JsonIgnore
    default String getEditorJSON() {
        return "";
    }
}
