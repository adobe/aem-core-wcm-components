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
package com.adobe.cq.wcm.core.extensions.amp.internal;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * Provides common value constants and methods used across AMP services.
 */
public class AmpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AmpUtil.class);

    private static final String AMP_MODE_PROP = "ampMode";

    public static final String AMP_SELECTOR = "amp";

    public static final String DOT = ".";

    /**
     * Retrieves the AMP mode value of the requested resource.
     * @param slingRequest Request used to resolve the resource and AMP mode value from.
     * @return The AMP mode value.
     */
    public static AMP_MODE getAmpMode(@NotNull SlingHttpServletRequest slingRequest) {
        PageManager pageManager = slingRequest.getResourceResolver().adaptTo(PageManager.class);
        if (pageManager != null) {
            Page page = pageManager.getContainingPage(slingRequest.getResource());
            if (page != null) {
                String ampMode = page.getProperties().get(AMP_MODE_PROP, "");
                if (!ampMode.isEmpty()) {
                    return AMP_MODE.fromString(ampMode);
                }
            }
        }

        return AMP_MODE.fromString(getPolicyProperty(AMP_MODE_PROP, "", slingRequest));
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
        if (policyManager != null) {
            ContentPolicy contentPolicy = policyManager.getPolicy(slingRequest.getResource());
            if (contentPolicy != null) {
                return contentPolicy.getProperties().get(property, defaultValue);
            }
        }
        return defaultValue;

    }

    public enum AMP_MODE {
        AMP_ONLY("ampOnly"),
        NO_AMP("noAmp"),
        PAIRED_AMP("pairedAmp");

        private String text;

        AMP_MODE(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static AMP_MODE fromString(String ampMode) {
            for (AMP_MODE mode: AMP_MODE.values()) {
                if (mode.text.equalsIgnoreCase(ampMode)) {
                    return mode;
                }
            }
            return NO_AMP;
        }
    }
}
