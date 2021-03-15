/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.models.form;

import java.util.Collections;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;

/**
 * Defines the form {@code Container} Sling Model used for the {@code /apps/core/wcm/components/form/container} component.
 *
 * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
 */
@ConsumerType
public interface Container extends ContainerExporter {

    /**
     * Returns the form's submit method (the value of the form's HTML <code>method</code> attribute).
     *
     * @return form submit method (method attribute of form)
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getMethod() {
        return null;
    }

    /**
     * Returns the form's submit method (the value of the form's HTML <code>action</code> attribute).
     *
     * @return form submit action (used in action attribute of form)
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getAction() {
        return null;
    }

    /**
     * Returns the form's id (the value of the form's HTML <code>id</code> attribute).
     *
     * @return form id (used in id attribute of form)
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getId() {
        return null;
    }

    /**
     * Returns the form's name (the value of the form's HTML <code>name</code> attribute).
     *
     * @return form name (used in name attribute of form)
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getName() {
        return null;
    }

    /**
     * Returns the form's encoding type (the value of the form's HTML <code>enctype</code> attribute).
     *
     * @return form data enctype (used in enctype attribute of form)
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getEnctype() {
        return null;
    }

    /**
     * Returns the resource type for the "new" section in the core form container where other input components will
     * be dropped.
     *
     * @return resource type for the "new" section in core form container where other input components will
     * be dropped
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getResourceTypeForDropArea() {
        return null;
    }

    /**
     * This method returns the redirect url property of this form. If the current sling request has a non-blank context path, the context
     * path is prepended to the redirect url if the redirect is an absolute path starting with '/'. This method also appends ".html" to the
     * redirect path.
     *
     * @return The form redirect url (used in the :redirect hidden input field of the form)
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getRedirect() {
        return null;
    }

    /**
     * This method returns a general error messages which should be displayed inside of the form if the submit action fails.
     *
     * @return The general error message
     * @since com.adobe.cq.wcm.core.components.models.form 14.3.0
     */
    @Nullable
    default String[] getErrorMessages() {
        return null;
    }

    /**
     * @see ContainerExporter#getExportedItemsOrder()
     * @since com.adobe.cq.wcm.core.components.models.form 14.2.0
     */
    @NotNull
    @Override
    default String[] getExportedItemsOrder() {
        return new String[]{};
    }

    /**
     * @see ContainerExporter#getExportedItems()
     * @since com.adobe.cq.wcm.core.components.models.form 14.2.0
     */
    @NotNull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        return Collections.emptyMap();
    }

    /**
     * @see ContainerExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models.form 14.2.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        return "";
    }
}
