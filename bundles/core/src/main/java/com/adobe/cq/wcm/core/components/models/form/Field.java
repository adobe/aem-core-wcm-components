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
package com.adobe.cq.wcm.core.components.models.form;

import javax.annotation.Nonnull;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * A base interface to be extended by all the different types of form fields.
 *
 * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
 */
@ConsumerType
public interface Field extends ComponentExporter {

    /**
     * Returns an unique identifier for this field.
     *
     * @return an unique identifier for the field
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value of the HTML <code>name</code> attribute.
     *
     * @return the value of the HTML <code>name</code> attribute
     * <p>
     * Note: <code>{'name':'value'}</code> is sent as a request parameter when POST-ing the form
     * </p>
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value of the HTML <code>value</code> attribute.
     *
     * @return the value of the HTML <code>value</code> attribute
     * <p>
     * Note: <code>{'name':'value'}</code> is sent as a request parameter when POST-ing the form
     * </p>
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getValue() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the title of the field (text displayed on the field).
     *
     * @return the title of the field (text displayed on the field)
     * <p>
     * Implementations can return <code>null</code> if the title is not required.
     * </p>
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the message for the help block.
     *
     * @return the message for the help block
     * <p>
     * Implementations can return <code>null</code> if the help message is not required.
     * </p>
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getHelpMessage() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models.form 14.2.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
