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
package com.adobe.cq.wcm.core.components.internal.models.v1.embeddable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.EmbeddableData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.models.embeddable.YouTube;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.commons.policy.ContentPolicyStyle;
import com.google.common.collect.ImmutableMap;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {YouTube.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class YouTubeImpl extends AbstractComponentImpl implements YouTube {

    /**
     * URL pattern is defined in https://developers.google.com/youtube/player_parameters
     */
    private static final String BASE_EMBED_URL = "https://www.youtube.com/embed/%s";
    private static final String PARAMETER_ORIGIN = "origin";
    private static final String PARAMETER_MUTE = "mute"; // not documented but still works
    private static final String PARAMETER_LOOP = "loop";
    private static final String PARAMETER_AUTOPLAY = "autoplay";
    private static final String PARAMETER_REL = "rel";
    private static final String PARAMETER_PLAYS_INLINE = "playsinline";
    private static final String PARAMETER_PLAYLIST = "playlist";
    private static final String PARAMETER_LANGUAGE = "hl";

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeImpl.class);

    @ValueMapValue(name = PN_VIDEO_ID)
    @Nullable
    private String videoId;

    @ValueMapValue(name = PN_WIDTH)
    @Nullable
    private String iFrameWidth;

    @ValueMapValue(name = PN_HEIGHT)
    @Nullable
    private String iFrameHeight;

    @ValueMapValue(name = PN_ASPECT_RATIO)
    @Nullable
    private String iFrameAspectRatio;

    @ValueMapValue(name = PN_LAYOUT)
    @Nullable
    private String layout;

    @ValueMapValue(name = PN_MUTE)
    @Nullable
    private Boolean isMute;

    @ValueMapValue(name = PN_AUTOPLAY)
    @Nullable
    private Boolean isAutoPlay;

    @ValueMapValue(name = PN_LOOP)
    @Nullable
    private Boolean isLoop;

    @ValueMapValue(name = PN_REL)
    @Nullable
    private Boolean isRel;

    @ValueMapValue(name = PN_PLAYS_INLINE)
    @Nullable
    private Boolean isPlaysInline;

    @ScriptVariable(injectionStrategy = InjectionStrategy.REQUIRED)
    private Page currentPage;

    @ScriptVariable(injectionStrategy = InjectionStrategy.REQUIRED)
    private ComponentContext componentContext;

    @Self
    private SlingHttpServletRequest request;

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(videoId);
    }

    @Override
    public @Nullable String getIFrameWidth() {
        return iFrameWidth;
    }

    @Override
    public @Nullable String getIFrameHeight() {
        return iFrameHeight;
    }

    @Override
    public @Nullable String getIFrameAspectRatio() {
        return iFrameAspectRatio;
    }

    @Override
    public @Nullable String getLayout() {
        return layout;
    }

    private static @Nullable Resource getWrappedResource(Resource resource) {
        if (resource instanceof ResourceWrapper) {
            return ResourceWrapper.class.cast(resource).getResource();
        } else {
            return null;
        }
    }

    /**
     * Relying on the injected currentStyle does not work, as this uses the wrong resource type "youtube" instead of "embed".
     * This helper method rebuilds the logic from "com.day.cq.wcm.scripting.impl.WcmBindingsValesProvider" but uses the wrapped resource.
     *
     * @return the style for the  wrapped resource
     */
    Style getStyleForWrappedResource(Resource resource) {
        ContentPolicyManager policyManager = resource.getResourceResolver().adaptTo(ContentPolicyManager.class);
        if (policyManager != null) {
            Resource wrappedResource = getWrappedResource(resource);
            if (wrappedResource != null) {
                ContentPolicy currentPolicy = policyManager.getPolicy(wrappedResource, request);
                if (currentPolicy != null) {
                    return new ContentPolicyStyle(currentPolicy, componentContext.getCell());
                } else {
                    LOGGER.debug("No policy found for wrapped resource {}", wrappedResource);
                }
            } else {
                LOGGER.debug("The current resource is not a wrapped resource: {}", resource);
            }
        } else {
            LOGGER.debug("Could not get ContentPolicyManager from resource resolver. Probably service not running!");
        }
        return null;
    }

    @Override
    public @Nullable String getIFrameSrc() throws URISyntaxException {
        Optional<URI> uri = getIFrameSrc(getStyleForWrappedResource(request.getResource()));
        if (!uri.isPresent()) {
            return null;
        } else {
            return uri.get().toString();
        }
    }

    @Override
    @NotNull
    protected EmbeddableData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asEmbeddable()
                .withEmbeddableDetails(() -> ImmutableMap.of(PN_VIDEO_ID, videoId))
                .build();
    }

    Optional<URI> getIFrameSrc(Style currentStyle) throws URISyntaxException {
        if (isEmpty()) {
            return Optional.empty();
        }
        URIBuilder uriBuilder = new URIBuilder(String.format(BASE_EMBED_URL, videoId));
        StringBuffer requestURL = request.getRequestURL();
        String currentProtocolHostandPort = requestURL.substring(0, requestURL.length() - request.getPathInfo().length());
        uriBuilder.addParameter(PARAMETER_ORIGIN, currentProtocolHostandPort);
        uriBuilder.addParameter(PARAMETER_LANGUAGE, currentPage.getLanguage().toString());
        // optional parameters
        if (currentStyle != null) {
            if (Boolean.TRUE.equals(currentStyle.get(PN_DESIGN_MUTE_ENABLED, false))) {
                addYouTubeBooleanUriParameter(uriBuilder, PARAMETER_MUTE, isMute, currentStyle, PN_DESIGN_MUTE_DEFAULT_VALUE);
            }
            if (Boolean.TRUE.equals(currentStyle.get(PN_DESIGN_AUTOPLAY_ENABLED, false))) {
                // going via WCMMode to determine if one is on publish does not work as this resource is included explicitly with wcmmode=disabled
                // therefore use edit context of parent component
                if (componentContext.getParent().getEditContext() == null) {
                    addYouTubeBooleanUriParameter(uriBuilder, PARAMETER_AUTOPLAY, isAutoPlay, currentStyle,
                            PN_DESIGN_AUTOPLAY_DEFAULT_VALUE);
                } else {
                    LOGGER.debug("Autoplay disabled because WCMMode is not disabled");
                }
            }
            if (Boolean.TRUE.equals(currentStyle.get(PN_DESIGN_LOOP_ENABLED, false))) {
                if (addYouTubeBooleanUriParameter(uriBuilder, PARAMETER_LOOP, isLoop, currentStyle, PN_DESIGN_LOOP_DEFAULT_VALUE)) {
                    // from https://developers.google.com/youtube/player_parameters#loop
                    // This parameter has limited support in IFrame embeds. To loop a single video, set the loop parameter value to 1 and set the playlist parameter value to the same video ID already specified in the Player API URL
                    uriBuilder.addParameter(PARAMETER_PLAYLIST, videoId);
                }
            }
            if (Boolean.TRUE.equals(currentStyle.get(PN_DESIGN_RELATED_VIDEOS_ENABLED, false))) {
                addYouTubeBooleanUriParameter(uriBuilder, PARAMETER_REL, isRel, currentStyle, PN_DESIGN_RELATED_VIDEOS_DEFAULT_VALUE);
            }
            if (Boolean.TRUE.equals(currentStyle.get(PN_DESIGN_PLAYS_INLINE_ENABLED, false))) {
                addYouTubeBooleanUriParameter(uriBuilder, PARAMETER_PLAYS_INLINE, isPlaysInline, currentStyle,
                        PN_DESIGN_PLAYS_INLINE_DEFAULT_VALUE);
            }
        } else {
            LOGGER.debug("No style available, optional YouTube parameters are not used!");
        }
        return Optional.of(uriBuilder.build());
    }

    boolean getBooleanValueOrDefaultFromStyle(Boolean value, Style style, String stylePropertyName) {
        if (value == null) {
            return style.get(stylePropertyName, false);
        } else {
            return value.booleanValue();
        }
    }

    boolean addYouTubeBooleanUriParameter(URIBuilder uriBuilder, String parameterName, Boolean value, Style style,
                                          String stylePropertyName) {
        boolean effectiveValue = getBooleanValueOrDefaultFromStyle(value, style, stylePropertyName);
        uriBuilder.addParameter(parameterName, effectiveValue ? "1" : "0");
        return effectiveValue;
    }
}
