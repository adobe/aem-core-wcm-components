/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A base interface to be extended by all the different types of form fields.
 * It contains commons attributes to be present in  all the form fields.
 */
@ConsumerType
public interface Field {

    /**
     * @return unique identifier for the field
     */
    String getId();

    /**
     * @return value of the HTML <code>name</code> attribute.
     * <p>
     * Note: <code>{'name':'value'}</code> is sent as a request parameter when POST-ing the form
     * </p>
     */
    String getName();

    /**
     * @return value of the HTML <code>value</code> attribute.
     * <p>
     * Note: <code>{'name':'value'}</code> is sent as a request parameter when POST-ing the form
     * </p>
     */
    String getValue();

    /**
     * @return the title of the field (text displayed on the field).
     * <p>
     * Implementations can return null if title is not required.
     * </p>
     */
    String getTitle();

    /**
     * @return the message for the help block.
     * <p>
     * Implementations can return null if help message is not required.
     * </p>
     */
    String getHelpMessage();

}
