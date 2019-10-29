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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common value constants and methods used across AMP services.
 */
class AmpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AmpUtil.class);

    private static final String AMP_MODE_PROP = "ampMode";

    static final String AMP_ONLY = "ampOnly";

    static final String AMP_SELECTOR = "amp";

    static final String DOT = ".";

    static final String NO_AMP = "noAmp";

    /**
     * Retrieves the AMP mode value of the requested resource.
     * @param slingRequest Request used to resolve the resource and AMP mode value from.
     * @return The AMP mode value.
     */
    static String getAmpMode(@NotNull SlingHttpServletRequest slingRequest) {

        PageManager pageManager = slingRequest.getResourceResolver().adaptTo(PageManager.class);

        if (pageManager == null) {
            LOG.debug("Can't resolve page manager. Falling back to content policy AMP mode.");
            return getPolicyProperty(AMP_MODE_PROP, "", slingRequest);
        }

        Page page = pageManager.getContainingPage(slingRequest.getResource());

        if (page != null) {

            String ampMode = page.getProperties().get(AMP_MODE_PROP, "");

            if (!ampMode.isEmpty()) {
                return ampMode;
            }
        }

        return getPolicyProperty(AMP_MODE_PROP, "", slingRequest);
    }

    /**
     * Retrieves the value of the given property from the request resource's content policy.
     * @param property The name of the property to read.
     * @param defaultValue The type hint and default value returned.
     * @param slingRequest The request used to get the resource and its content policy.
     * @param <T> The type of the property value expected.
     * @return The value of the property of the resource's content policy. Returns null if fails to read the content
     * policy.
     */
    private static <T> T getPolicyProperty(String property, T defaultValue,
                                           @NotNull SlingHttpServletRequest slingRequest) {

        ContentPolicyManager policyManager = slingRequest.getResourceResolver().adaptTo(ContentPolicyManager.class);
        if (policyManager == null) {
            LOG.trace("Policy manager is null. Unable to read policy property.");
            return defaultValue;
        }

        ContentPolicy contentPolicy = policyManager.getPolicy(slingRequest.getResource());
        if (contentPolicy == null) {
            LOG.trace("Content policy is null. Unable to read policy property.");
            return defaultValue;
        }

        return contentPolicy.getProperties().get(property, defaultValue);
    }
}
