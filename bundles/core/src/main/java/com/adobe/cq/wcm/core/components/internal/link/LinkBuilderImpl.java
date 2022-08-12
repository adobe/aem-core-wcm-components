/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkBuilder;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_ACCESSIBILITY_LABEL;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TITLE_ATTRIBUTE;
import static com.adobe.cq.wcm.core.components.internal.Utils.resolveRedirects;
import static com.adobe.cq.wcm.core.components.internal.link.LinkManagerImpl.VALID_LINK_TARGETS;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.ATTR_ARIA_LABEL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.ATTR_TARGET;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.ATTR_TITLE;

public class LinkBuilderImpl implements LinkBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkBuilderImpl.class);
    private final static List<Pattern> PATTERNS = Collections.singletonList(Pattern.compile("(<%[=@].*?%>)"));
    public static final String HTML_EXTENSION = ".html";

    SlingHttpServletRequest request;
    List<PathProcessor> pathProcessors;
    boolean shadowingDisabled;

    Resource linkConfiguration;
    String linkUrlPropertyName = PN_LINK_URL;
    Page targetPage;
    Asset targetAsset;
    String linkUrl;
    Map<String, String> linkAttributes = new HashMap<>();
    Object reference;

    public LinkBuilderImpl(Resource resource, SlingHttpServletRequest req, List<PathProcessor> pathProcs, boolean shadowingDisabled) {
        linkConfiguration = resource;
        request = req;
        pathProcessors = pathProcs;
        this.shadowingDisabled = shadowingDisabled;
    }

    public LinkBuilderImpl(Page page, SlingHttpServletRequest req, List<PathProcessor> pathProcs, boolean shadowingDisabled) {
        targetPage = page;
        request = req;
        pathProcessors = pathProcs;
        this.shadowingDisabled = shadowingDisabled;
    }

    public LinkBuilderImpl(Asset asset, SlingHttpServletRequest req, List<PathProcessor> pathProcs) {
        targetAsset = asset;
        request = req;
        pathProcessors = pathProcs;
    }

    public LinkBuilderImpl(String url, SlingHttpServletRequest req, List<PathProcessor> pathProcs, boolean shadowingDisabled) {
        linkUrl = url;
        request = req;
        pathProcessors = pathProcs;
        this.shadowingDisabled = shadowingDisabled;
    }

    @Override
    public @NotNull LinkBuilder withLinkUrlPropertyName(@NotNull String name) {
        linkUrlPropertyName = name;
        return this;
    }

    @Override
    public @NotNull LinkBuilder withLinkTarget(@NotNull String target) {
        String resolvedLinkTarget = validateAndResolveLinkTarget(target);
        linkAttributes.put(PN_LINK_TARGET, resolvedLinkTarget);
        return this;
    }

    @Override
    public @NotNull LinkBuilder withLinkAttribute(@NotNull String name, @Nullable String value) {
        String validatedLinkAttributeValue = validateLinkAttributeValue(value);
        linkAttributes.put(name, validatedLinkAttributeValue);
        return this;
    }

    @Override
    public @NotNull Link build() {
        if (linkConfiguration != null) {
            ValueMap props = linkConfiguration.getValueMap();
            linkUrl = props.get(linkUrlPropertyName, String.class);
            String linkTarget = props.get(PN_LINK_TARGET, String.class);
            if (StringUtils.isNotEmpty(linkTarget)) {
                withLinkTarget(linkTarget);
            }
            String linkAccessibilityLabel = props.get(PN_LINK_ACCESSIBILITY_LABEL, String.class);
            if (StringUtils.isNotEmpty(linkAccessibilityLabel)) {
                withLinkAttribute(PN_LINK_ACCESSIBILITY_LABEL, linkAccessibilityLabel);
            }
            String linkTitleAttribute = props.get(PN_LINK_TITLE_ATTRIBUTE, String.class);
            if (StringUtils.isNotEmpty(linkTitleAttribute)) {
                withLinkAttribute(PN_LINK_TITLE_ATTRIBUTE, linkTitleAttribute);
            }
        } else if (targetPage != null) {
            linkUrl = targetPage.getPath();
        } else if (targetAsset != null) {
            linkUrl = targetAsset.getPath();
        }

        Asset asset = getAsset(linkUrl);
        Page page = getPage(linkUrl).orElse(null);
        if (asset != null) {
            this.reference = asset;
        } else if (page != null) {
            Pair<Page, String> pair = resolvePage(page);
            this.reference = pair.getLeft();
            linkUrl = StringUtils.isNotEmpty(pair.getRight()) ? pair.getRight() : linkUrl;
        }
        String resolvedLinkURL = validateAndResolveLinkURL(linkUrl);
        Map<String, String> htmlAttributes = mapLinkAttributesToHtml();
        return buildLink(resolvedLinkURL, request, htmlAttributes);
    }

    private @NotNull Link buildLink(String path, SlingHttpServletRequest request, Map<String, String> htmlAttributes) {
        if (StringUtils.isNotEmpty(path)) {
            try {
                // The link contain character sequences that are not well formatted and cannot be decoded, for example
                // Adobe Campaign expressions like: /content/path/to/page.html?recipient=<%= recipient.id %>
                Map<String, String> placeholders = new LinkedHashMap<>();
                String maskedPath = mask(path, placeholders);
                maskedPath = URLDecoder.decode(maskedPath, StandardCharsets.UTF_8.name());
                path = unmask(maskedPath, placeholders);
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn(e.getMessage());
            }
            String decodedPath = path;
            return pathProcessors.stream()
                    .filter(pathProcessor -> pathProcessor.accepts(decodedPath, request))
                    .findFirst()
                    .map(pathProcessor -> new LinkImpl(
                            pathProcessor.sanitize(decodedPath, request),
                            pathProcessor.map(decodedPath, request),
                            pathProcessor.externalize(decodedPath, request),
                            this.reference,
                            pathProcessor.processHtmlAttributes(decodedPath, htmlAttributes)))
                    .orElse(new LinkImpl(path, path, path, this.reference, htmlAttributes));
        } else {
            return new LinkImpl(path, path, path, this.reference, htmlAttributes);
        }
    }

    @NotNull
    private Map<String, String> mapLinkAttributesToHtml() {
        return new HashMap<String, String>() {{
            put(ATTR_TARGET, linkAttributes.get(PN_LINK_TARGET));
            put(ATTR_ARIA_LABEL, linkAttributes.get(PN_LINK_ACCESSIBILITY_LABEL));
            put(ATTR_TITLE, linkAttributes.get(PN_LINK_TITLE_ATTRIBUTE));
        }};
    }

    /**
     * Validates and resolves the link target.
     * @param linkTarget Link target
     *
     * @return The validated link target or {@code null} if not valid
     */
    @Nullable
    private String validateAndResolveLinkTarget(@Nullable final String linkTarget) {
        return Optional.ofNullable(linkTarget)
                .filter(VALID_LINK_TARGETS::contains)
                .orElse(null);
    }

    /**
     * Validates the link accessibility label.
     * @param value Link accessibility label
     *
     * @return The validated link accessibility label or {@code null} if not valid
     */
    @Nullable
    private String validateLinkAttributeValue(@Nullable final String value) {
        return Optional.ofNullable(value)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .orElse(null);
    }

    /**
     * Validates and resolves a link URL.
     *
     * @param linkURL Link URL
     * @return The validated link URL or {@code null} if not valid
     */
    @Nullable
    private String validateAndResolveLinkURL(@Nullable final String linkURL) {
        return Optional.ofNullable(linkURL)
                .filter(StringUtils::isNotEmpty)
                .map(this::getLinkURL)
                .orElse(null);
    }

    /**
     * If the provided {@code path} identifies a {@link Page}, this method will generate the correct URL for the page. Otherwise the
     * original {@code String} is returned.
     * @param path the page path
     *
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a {@link Page}
     */
    @NotNull
    private String getLinkURL(@NotNull String path) {
        return getPage(path)
                .map(this::getPageLinkURL)
                .orElse(path);
    }

    /**
     * Given a {@link Page}, this method returns the correct URL with the extension
     * @param page the page
     *
     * @return the URL of the provided (@code page}
     */
    @NotNull
    private String getPageLinkURL(@NotNull Page page) {
        return page.getPath() + HTML_EXTENSION;
    }

    /**
     * Returns the DAM asset referenced by the path.
     *
     * @param path The reference path
     * @return the DAM asset referenced by the path.
     */
    @Nullable
    private Asset getAsset(@NotNull String path) {
        return Optional.ofNullable(request.getResourceResolver().getResource(path))
                .map(assetRes -> assetRes.adaptTo(Asset.class))
                .orElse(null);
    }

    /**
     * Given a path, tries to resolve to the corresponding page.
     *
     * @param path The path
     * @return The {@link Page} corresponding to the path
     */
    @NotNull
    private Optional<Page> getPage(@Nullable String path) {
        return Optional.ofNullable(path)
                .flatMap(p -> Optional.ofNullable(this.request.getResourceResolver().adaptTo(PageManager.class))
                        .map(pm -> pm.getPage(p)));
    }

    /**
     * Attempts to resolve a Link URL and page for the given page. Redirect chains are followed, if
     * shadowing is not disabled.
     *
     * @param page Page
     * @return A pair of {@link String} and {@link Page} the page resolves to.
     */
    @NotNull
    private Pair<Page, String> resolvePage(@NotNull final Page page) {
        Pair<Page, String> pair = !shadowingDisabled ? resolveRedirects(page) : new ImmutablePair<>(page, null);
        if (pair.getLeft() == null && StringUtils.isNotEmpty(pair.getRight())) {
            return new ImmutablePair<>(page, pair.getRight());
        }
        Page resolved = Optional.ofNullable(pair.getLeft()).orElse(page);
        return new ImmutablePair<>(resolved, getPageLinkURL(resolved));
    }

    /**
     * Masks a given {@link String} by replacing all occurrences of {@link LinkBuilderImpl#PATTERNS} with a placeholder.
     * The generated placeholders are put into the given {@link Map} and can be used to unmask a {@link String} later on.
     *
     * @param original     the original {@link String}
     * @param placeholders a {@link Map} the generated placeholders will be put in
     * @return the masked {@link String}
     * @see LinkBuilderImpl#unmask(String, Map)
     */
    private static String mask(String original, Map<String, String> placeholders) {
        String masked = original;
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(masked);
            while (matcher.find()) {
                String expression = matcher.group(1);
                String placeholder = newPlaceholder(masked);
                masked = masked.replaceFirst(Pattern.quote(expression), placeholder);
                placeholders.put(placeholder, expression);
            }
        }
        return masked;
    }

    /**
     * Unmasks the given {@link String} by replacing the given placeholders with their original value.
     *
     * @param masked       the masked {@link String}
     * @param placeholders the {@link Map} of placeholders to replace
     * @return the unmasked {@link String}
     */
    private static String unmask(String masked, Map<String, String> placeholders) {
        String unmasked = masked;
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            unmasked = unmasked.replaceFirst(placeholder.getKey(), placeholder.getValue());
        }
        return unmasked;
    }

    /**
     * Generate a new random placeholder that is not conflicting with any character sequence in the the given {@link String}.
     *
     * @param str the given {@link String}
     * @return the placeholder name
     */
    private static String newPlaceholder(String str) {
        SecureRandom random = new SecureRandom();
        StringBuilder placeholderBuilder = new StringBuilder(5);

        do {
            placeholderBuilder.setLength(0);
            placeholderBuilder
                .append("_")
                .append(new BigInteger(16, random).toString(16))
                .append("_");
        } while (str.contains(placeholderBuilder));

        return placeholderBuilder.toString();
    }

}
