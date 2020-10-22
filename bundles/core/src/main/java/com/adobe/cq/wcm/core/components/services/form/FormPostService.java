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

/**
 * A service that posting form data to a remote service.
 *
 * @since com.adobe.cq.wcm.core.components.services.form 1.0.0
 */
public interface FormPostService {

    /**
     * Send form data to a remote servcie.
     *
     * @param request the {@link SlingHttpServletRequest}
     * @param response the {@link SlingHttpServletResponse}
     * @return true if the remote request was successful, otherwise false
     *
     * @since com.adobe.cq.wcm.core.components.services.form 1.0.0
     */
    boolean sendFormData(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException;
}
