/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.ImmutableMap;

/**
 * Wraps link information to be used in models.
 */
@JsonInclude(Include.NON_NULL)
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

    /**
     * Getter exposing if link is valid.
     *
     * @return {@code true} if link is valid, {@code false} if link is not valid
     */
    @Override
    public boolean isValid() {
        return url != null;
    }

    /**
     * Getter for link URL.
     *
     * @return Link URL, can be {@code null} if link is not valid
     */
    @Override
    public @Nullable String getURL() {
        return url;
    }

    /**
     * Getter for link HTML attributes.
     *
     * @return {@link Map} of HTML attributes, may include the URL as {@code href}
     */
    @Override
    @JsonIgnore  // exclude HTML-specific attributes in JSON
    public @NotNull Map<String, String> getHtmlAttributes() {
        return htmlAttributes;
    }

    /**
     * Getter for link target page, if existing.
     *
     * @return Link target page or {@code null} if link does not point to a page
     */
    @Override
    @JsonIgnore  // exclude HTML-specific target attribute in JSON
    public @Nullable Page getTargetPage() {
        return targetPage;
    }

    /**
     * Builds link HTML attributes.
     *
     * @param linkURL Link URL
     * @param linkTarget Link target
     *
     * @return {@link Map} of link attributes
     */
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
