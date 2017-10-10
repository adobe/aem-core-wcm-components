/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.DataType;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines the Sling Model for the {@code /apps/core/wcm/sandbox/components/contentfragment} component. The model
 * provides information about the referenced content fragment and access to representations of its elements.
 *
 * @since com.adobe.cq.wcm.core.components.sandbox.models 2.6.0
 */
@ConsumerType
public interface ContentFragment extends ContainerExporter {

    /**
     * Name of the mandatory resource property that stores the path to a content fragment.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
     */
    String PN_PATH = "fragmentPath";

    /**
     * Name of the optional resource property that stores the names of the elements to be used.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
     */
    String PN_ELEMENT_NAMES = "elementNames";

    /**
     * Represents a content element of a content fragment.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
     */
    @ConsumerType
    interface Element extends ComponentExporter {

        /**
         * Returns the technical name of the element.
         *
         * @return the technical name of the element
         * @see ContentElement#getName()
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
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
         * @see ContentElement#getTitle()
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
         */
        @Nullable
        default String getTitle() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns {@code true} if the element is multi-valued, {@code false} otherwise.
         *
         * @return {@code true} if the element is multi-valued, {@code false} otherwise
         * @see DataType#isMultiValue()
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
         */
        default boolean isMultiValued() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the content type of the element.
         *
         * @return the content type
         * @see ContentElement#getContentType()
         * @see FragmentData#getContentType()
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
         */
        @Nullable
        default String getContentType() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the value of the element.
         *
         * @return the value of the element
         * @see FragmentData#getValue()
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
         */
        @Nullable
        default Object getValue() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the value of the element as a string to be displayed. If the element is multi-valued, the returned
         * string is a comma-separated concatenation of all its values.
         *
         * @return the value as a string
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
         */
        @Nullable
        @JsonIgnore
        default String getDisplayValue() {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        default String getExportedType() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the values of the multi-valued element as an array of strings to be displayed. If the element is not
         * multi-valued, the returned array will contain a single entry.
         *
         * @return the values as strings
         * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
         */
        @Nullable
        default String[] getDisplayValues() {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Returns the title of the content fragment.
     *
     * @return the title of the content fragment
     * @see com.adobe.cq.dam.cfm.ContentFragment#getTitle()
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
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
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
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
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
     */
    @Nonnull
    @JsonProperty("model")
    default String getType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a list of content fragment elements. The list contains the elements whose names are specified in the
     * property {@link ContentFragment#PN_ELEMENT_NAMES} (in that order, and skipping non-existing elements). If
     * {@link ContentFragment#PN_ELEMENT_NAMES} is not set, then all elements are returned, in the order in which they
     * occur in the content fragment.
     *
     * @return a selection or all of the content fragment's elements
     * @see com.adobe.cq.dam.cfm.ContentFragment#getElements()
     * @since com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models 0.0.1
     */
    @Nonnull
    @JsonIgnore
    default java.util.List<Element> getElements() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    default Map<String, ComponentExporter> getExportedItems() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    default String[] getExportedItemsOrder() {
        throw new UnsupportedOperationException();
    }

}
