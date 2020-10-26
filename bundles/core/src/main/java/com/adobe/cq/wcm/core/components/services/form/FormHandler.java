/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.services.form;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONObject;

/**
 * A service that posts form data as JSON to a remote service.
 *
 * @since com.adobe.cq.wcm.core.components.services.form 1.0.0
 */
public interface FormHandler {

    /**
     * Forward form data to a remote service.
     *
     * @param formData    the form data JSON object
     * @param endPointUrl the URL of the remote service
     * @return true if the remote request was successful, otherwise false
     * @since com.adobe.cq.wcm.core.components.services.form 1.0.0
     */
    boolean forwardFormData(JSONObject formData, String endPointUrl);
}
