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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;

/**
 * Wraps link information to be used in models.
 */
public final class LinkImpl implements Link {

    private final String url;
    private final Map<String, String> htmlAttributes;
    private final Page targetPage;

    /**
     * @param url Link URL
     */
    public LinkImpl(String url) {
        this(url, null, null);
    }

    /**
     * @param url Link URL
     * @param target Target
     */
    public LinkImpl(String url, String target) {
        this(url, target, null);
    }

    /**
     * @param url Link URL
     * @param target Target
     * @param targetPage Target page
     */
    LinkImpl(String url, String target, Page targetPage) {
        this.url = url;
        this.htmlAttributes = buildHtmlAttributes(url, target);
        this.targetPage = targetPage;
    }

    @Override
    public boolean isValid() {
        return url != null;
    }

    @Override
    public @Nullable String getURL() {
        return url;
    }

    @Override
    public @NotNull Map<String, String> getHtmlAttributes() {
        return htmlAttributes;
    }

    @Override
    public @Nullable Page getTargetPage() {
        return targetPage;
    }

    private static Map<String, String> buildHtmlAttributes(String linkURL, String linkTarget) {
        Map<String,String> attributes = new HashMap<>();
        if (linkURL != null) {
            attributes.put("href", linkURL);
        }
        if (linkTarget != null) {
            attributes.put("target", linkTarget);
        }
        return ImmutableMap.copyOf(attributes);
    }

}
