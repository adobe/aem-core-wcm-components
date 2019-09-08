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
package com.adobe.cq.wcm.core.components.internal.link;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.mixin.LinkMixin;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;

/**
 * Wraps link information to be used in models.
 */
public final class Link implements LinkMixin {

    private final String linkURL;
    private final Map<String, String> linkHtmlAttributes;
    private final Page targetPage;

    Link(String linkURL, String linkTarget, Page targetPage) {
        this.linkURL = linkURL;
        this.linkHtmlAttributes = buildAttributes(linkURL, linkTarget);
        this.targetPage = targetPage;
    }

    @Override
    public boolean isLinkValid() {
        return linkURL != null;
    }

    @Override
    public @Nullable String getLinkURL() {
        return linkURL;
    }

    @Override
    public @Nullable Map<String, String> getLinkHtmlAttributes() {
        return linkHtmlAttributes;
    }

    /**
     * @return Target page if the link URL pointed to an internal page.
     */
    public Page getTargetPage() {
        return targetPage;
    }

    private static Map<String, String> buildAttributes(String linkURL, String linkTarget) {
        if (linkURL == null) {
            return null;
        }
        Map<String,String> attributes = new HashMap<>();
        attributes.put("href", linkURL);
        if (linkTarget != null) {
            attributes.put("target", linkTarget);
        }
        return ImmutableMap.copyOf(attributes);
    }

}
