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

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.LinkBuilder;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.collect.ImmutableSet;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = LinkManager.class)
public class LinkManagerImpl implements LinkManager {

    /**
     * List of allowed/supported values for link target.
     * <code>_self</code> is used in the edit dialog but not listed as allowed here as we do not
     * want to render a target attribute at all when <code>_self</code> is selected.
     */
    public static final Set<String> VALID_LINK_TARGETS = ImmutableSet.of("_blank", "_parent", "_top");

    /**
     * Name of the resource property that for redirecting pages will indicate if original page or redirect target page should be returned.
     * Default is `false`. If `true` - original page is returned. If `false` or not configured - redirect target page.
     */
    public static final String PN_DISABLE_SHADOWING = "disableShadowing";

    /**
     * Flag indicating if shadowing is disabled.
     */
    public static final boolean PROP_DISABLE_SHADOWING_DEFAULT = false;

    /**
     * The current {@link SlingHttpServletRequest}.
     */
    @Self
    private SlingHttpServletRequest request;

    /**
     * The current resource properties
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private ValueMap properties;

    /**
     * The current resource style/policies
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Style currentStyle;

    /**
     * Registered path processors.
     */
    @OSGiService
    private List<PathProcessor> pathProcessors;

    /**
     * Variable that defines how to handle pages that redirect. Given pages PageA and PageB where PageA redirects to PageB,
     * when shadowing is disabled, the link will point to the original page (PageA).
     */
    private Boolean shadowingDisabled;

    @PostConstruct
    private void initModel() {
        shadowingDisabled = PROP_DISABLE_SHADOWING_DEFAULT;
        if (currentStyle != null) {
            shadowingDisabled = currentStyle.get(PN_DISABLE_SHADOWING, shadowingDisabled);
        }
        if (properties != null) {
            shadowingDisabled = properties.get(PN_DISABLE_SHADOWING, shadowingDisabled);
        }
    }

    @Override
    public @NotNull LinkBuilder get(@NotNull Resource resource) {
        return new LinkBuilderImpl(resource, request, pathProcessors, shadowingDisabled);
    }

    @Override
    public @NotNull LinkBuilder get(@NotNull Page page) {
        return new LinkBuilderImpl(page, request, pathProcessors, shadowingDisabled);
    }

    @Override
    public @NotNull LinkBuilder get(@NotNull Asset asset) {
        return new LinkBuilderImpl(asset, request, pathProcessors);
    }

    @Override
    public @NotNull LinkBuilder get(@NotNull String url) {
        return new LinkBuilderImpl(url, request, pathProcessors, shadowingDisabled);
    }

}
