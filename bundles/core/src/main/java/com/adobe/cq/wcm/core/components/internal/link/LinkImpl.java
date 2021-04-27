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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

/**
 * Wraps link information to be used in models.
 */
@JsonInclude(Include.NON_NULL)
public final class LinkImpl<T> implements Link<T> {

    public static final String ATTR_HREF = "href";
    public static final String ATTR_TARGET = "target";
    public static final String ATTR_ARIA_LABEL = "aria-label";
    public static final String ATTR_TITLE = "title";

    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<String>() {{
        add(ATTR_TARGET);
        add(ATTR_ARIA_LABEL);
        add(ATTR_TITLE);
    }};

    private final String url;
    private final T reference;
    private final LinkProcessor linkProcessor;
    private final Map<String, String> htmlAttributes;
    private String processedUrl;
    private boolean processed = false;

    public LinkImpl(String url, T reference, LinkProcessor linkProcessor, Map<String, String> htmlAttributes) {
        this.url = url;
        this.reference = reference;
        this.linkProcessor = linkProcessor;
        this.htmlAttributes = buildHtmlAttributes(url, htmlAttributes);
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
    @JsonIgnore
    public @Nullable String getURL() {
        return url;
    }

    /**
     * Getter for the processed URL.
     *
     * @return Processed link URL, can be {@code null} if link is not valid or no processors are defined
     */
    @Override
    @JsonProperty("url")
    public @Nullable String getProcessedURL() {
        if (!processed) {
            if (linkProcessor != null) {
                processedUrl = linkProcessor.process(url);
            } else {
                processedUrl = url;
            }
            processed = true;
        }
        return processedUrl;
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
     * Getter for link reference, if existing.
     *
     * @return Link referenced WCM/DAM entity or {@code null} if link does not point to one
     */
    @Override
    @JsonIgnore  // exclude referenced object in jSON
    public @Nullable T getReference() {
        return reference;
    }

    /**
     * Builds link HTML attributes.
     *
     * @param linkURL Link URL
     * @param htmlAttributes HTML attributes to add
     *
     * @return {@link Map} of link attributes
     */
    private static Map<String, String> buildHtmlAttributes(String linkURL, Map<String, String> htmlAttributes) {
        Map<String,String> attributes = new LinkedHashMap<>();
        if (linkURL != null) {
            attributes.put(ATTR_HREF, linkURL);
        }
        if (htmlAttributes != null) {
            Map<String, String> filteredAttributes = htmlAttributes.entrySet().stream()
                    .filter(e -> ALLOWED_ATTRIBUTES.contains(e.getKey()) && e.getValue() != null)
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            attributes.putAll(filteredAttributes);
        }
        return ImmutableMap.copyOf(attributes);
    }

}
