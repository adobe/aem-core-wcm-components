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

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.adobe.cq.wcm.core.components.services.link.LinkHandler;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_ACCESSIBILITY_LABEL;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TITLE_ATTRIBUTE;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.ATTR_ARIA_LABEL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.ATTR_TARGET;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.ATTR_TITLE;

/**
 * Simple implementation for resolving and validating links from model's resources.
 * This is a Sling model that can be injected into other models using the <code>@Self</code> annotation.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {LinkHandler.class})
public final class LinkHandlerImpl implements LinkHandler {

    /**
     * Default logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkHandlerImpl.class);

    /**
     * List of allowed/supported values for link target.
     * <code>_self</code> is used in the edit dialog but not listed as allowed here as we do not
     * want to render a target attribute at all when <code>_self</code> is selected.
     */
    private static final Set<String> VALID_LINK_TARGETS = ImmutableSet.of("_blank", "_parent", "_top");

    /**
     * The current {@link SlingHttpServletRequest}.
     */
    private final SlingHttpServletRequest request;

    /**
     * The current resource style/policies
     */
    private final Style style;

    /**
     * Registered path processors.
     */
    private final List<PathProcessor> pathProcessors;

    /**
     * Variable that defines how to handle pages that redirect. Given pages PageA and PageB where PageA redirects to PageB,
     * when shadowing is disabled, the link will point to the original page (PageA).
     */
    private Boolean shadowingDisabled;

    /**
     * Construct a LinkHandler.
     *
     * @param slingRequest      The current request.
     * @param pathProcessorList The list of path processors.
     * @param currentStyle      The current style.
     */
    @Inject
    public LinkHandlerImpl(@Named("sling-object") @NotNull final SlingHttpServletRequest slingRequest,
                           @Named("osgi-services") @NotNull final List<PathProcessor> pathProcessorList,
                           @ScriptVariable(name = "currentStyle") @org.apache.sling.models.annotations.Optional @Nullable final Style currentStyle) {
        this.request = slingRequest;
        this.pathProcessors = pathProcessorList;
        this.style = currentStyle;
    }

    @NotNull
    @Override
    public Optional<Link<@Nullable Page>> getLink(@NotNull final Resource resource) {
        return getLink(resource, PN_LINK_URL);
    }

    @NotNull
    @Override
    public Optional<Link<@Nullable Page>> getLink(@NotNull final Resource resource,
                                                  @NotNull final String linkURLPropertyName) {
        ValueMap props = resource.getValueMap();
        return Optional.ofNullable(props.get(linkURLPropertyName, String.class))
            .flatMap(linkURL -> getLink(
                linkURL,
                props.get(PN_LINK_TARGET, String.class),
                props.get(PN_LINK_ACCESSIBILITY_LABEL, String.class),
                props.get(PN_LINK_TITLE_ATTRIBUTE, String.class)
            ));
    }

    @NotNull
    @Override
    public Optional<Link<@Nullable Page>> getLink(@Nullable final Page page) {
        return Optional.ofNullable(page)
            .map(this::resolvePage)
            .flatMap(pair -> buildLink(pair.getRight(), pair.getLeft(), null));
    }

    @NotNull
    @Override
    public Optional<Link<@Nullable Page>> getLink(@Nullable final String linkURL, @Nullable final String target) {
        return this.getLink(linkURL, target, null, null);
    }

    @NotNull
    @Override
    public Optional<Link<@Nullable Page>> getLink(@Nullable final String linkURL,
                                                  @Nullable final String target,
                                                  @Nullable final String linkAccessibilityLabel,
                                                  @Nullable final String linkTitleAttribute) {
        Pair<Page, String> pair = Optional.ofNullable(linkURL)
            .flatMap(this::getPage)
            .map(this::resolvePage)
            .map(p -> ImmutablePair.of(p.getLeft(), validateAndResolveLinkURL(p.getRight())))
            .orElseGet(() -> ImmutablePair.of(null, validateAndResolveLinkURL(linkURL)));

        return buildLink(pair.getRight(), pair.getLeft(),
            new HashMap<String, String>() {{
                put(ATTR_TARGET, validateAndResolveLinkTarget(target));
                validateLinkAccessibilityLabel(linkAccessibilityLabel).ifPresent((v) -> put(ATTR_ARIA_LABEL, v));
                validateLinkTitleAttribute(linkTitleAttribute).ifPresent((v) -> put(ATTR_TITLE, v));
            }});
    }

    @NotNull
    private Optional<Link<@Nullable Page>> buildLink(@Nullable final String path,
                                                     @Nullable final Page page,
                                                     @Nullable final Map<String, String> htmlAttributes) {
        return Optional.ofNullable(path)
            .filter(StringUtils::isNotEmpty)
            .map(p -> this.pathProcessors.stream()
                .filter(pathProcessor -> pathProcessor.accepts(p, this.request))
                .findFirst()
                .map(pathProcessor -> (Link<Page>) new LinkImpl<>(
                    pathProcessor.sanitize(p, this.request),
                    pathProcessor.map(p, this.request),
                    pathProcessor.externalize(p, this.request),
                    page,
                    pathProcessor.processHtmlAttributes(p, htmlAttributes)
                )))
            .orElseGet(() -> Optional.of(new LinkImpl<>(path, path, path, page, htmlAttributes)));
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
     * Validates and resolves the link target.
     *
     * @param linkTarget Link target
     * @return The validated link target or {@code null} if not valid
     */
    @Nullable
    private static String validateAndResolveLinkTarget(@Nullable final String linkTarget) {
        return Optional.ofNullable(linkTarget)
            .filter(VALID_LINK_TARGETS::contains)
            .orElse(null);
    }

    /**
     * Validates the link accessibility label.
     *
     * @param linkAccessibilityLabel Link accessibility label
     * @return The validated link accessibility label or empty if not valid
     */
    @NotNull
    private static Optional<String> validateLinkAccessibilityLabel(@Nullable final String linkAccessibilityLabel) {
        return Optional.ofNullable(linkAccessibilityLabel)
            .filter(StringUtils::isNotBlank)
            .map(String::trim);
    }

    /**
     * Validates the link title attribute.
     *
     * @param linkTitleAttribute Link title attribute
     * @return The validated link title attribute or empty if not valid
     */
    @NotNull
    private static Optional<String> validateLinkTitleAttribute(@Nullable final String linkTitleAttribute) {
        return Optional.ofNullable(linkTitleAttribute)
            .filter(StringUtils::isNotBlank)
            .map(String::trim);
    }

    /**
     * If the provided {@code path} identifies a {@link Page}, this method will generate the correct URL for the page. Otherwise the
     * original {@code String} is returned.
     *
     * @param path the page path
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a {@link Page}
     */
    @NotNull
    private String getLinkURL(@NotNull final String path) {
        return getPage(path)
            .map(LinkHandlerImpl::getPageLinkURL)
            .orElse(path);
    }

    /**
     * Given a {@link Page}, this method returns the correct URL with the extension
     *
     * @param page the page
     * @return the URL of the provided (@code page}
     */
    @NotNull
    private static String getPageLinkURL(@NotNull final Page page) {
        return page.getPath() + HTML_EXTENSION;
    }

    /**
     * Given a path, tries to resolve to the corresponding page.
     *
     * @param path The path
     * @return The {@link Page} corresponding to the path
     */
    @NotNull
    private Optional<Page> getPage(@Nullable final String path) {
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
    private Pair<@Nullable Page, @NotNull String> resolvePage(@NotNull final Page page) {
        Pair<Page, String> pair = !isShadowingDisabled() ? resolveRedirects(page) : new ImmutablePair<>(page, null);
        if (pair.getLeft() == null && StringUtils.isNotEmpty(pair.getRight())) {
            return new ImmutablePair<>(page, pair.getRight());
        }
        Page resolved = Optional.ofNullable(pair.getLeft()).orElse(page);
        return new ImmutablePair<>(resolved, getPageLinkURL(resolved));
    }

    @NotNull
    @Override
    public Pair<@Nullable Page, @Nullable String> resolveRedirects(@Nullable final Page page) {
        Page result = page;
        String redirectTarget = null;
        if (page != null) {
            Set<String> redirectCandidates = new LinkedHashSet<>();
            redirectCandidates.add(page.getPath());
            while (result != null && StringUtils
                .isNotEmpty((redirectTarget = result.getProperties().get(PageImpl.PN_REDIRECT_TARGET, String.class)))) {
                result = page.getPageManager().getPage(redirectTarget);
                if (result != null) {
                    if (!redirectCandidates.add(result.getPath())) {
                        LOGGER.warn("Detected redirect loop for the following pages: {}.", redirectCandidates);
                        break;
                    }
                }
            }
        }
        return new ImmutablePair<>(result, redirectTarget);
    }

    /**
     * Checks if redirect page shadowing is disabled
     *
     * @return {@code true} if page shadowing is disabled, {@code false} otherwise
     */
    private boolean isShadowingDisabled() {
        if (this.shadowingDisabled == null) {
            this.shadowingDisabled = Optional.ofNullable(this.request.getResource().getValueMap().get(PN_DISABLE_SHADOWING, Boolean.class))
                .orElseGet(() ->
                    Optional.ofNullable(this.style)
                        .map(cs -> cs.get(PN_DISABLE_SHADOWING, Boolean.class))
                        .orElse(PROP_DISABLE_SHADOWING_DEFAULT)
                );
        }
        return this.shadowingDisabled;
    }
}
