/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.sandbox.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class NavigationItemImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.NavigationItemImpl implements NavigationItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationItemImpl.class);

    public static final String PN_REDIRECT_TARGET = "redirectTarget";

    protected SlingHttpServletRequest request;
    protected List<NavigationItem> children = Collections.emptyList();
    protected int level;

    private String url;

    public NavigationItemImpl(Page page, boolean active, SlingHttpServletRequest request, int level, List<NavigationItem> children) {
        super(page, active);
        this.request = request;
        this.level = level;
        this.children = children;
        Page redirectTarget = getRedirectTarget(page);
        if (redirectTarget != null && !redirectTarget.equals(page)) {
            this.page = redirectTarget;
        }
    }

    @Override
    public List<NavigationItem> getChildren() {
        return children;
    }

    @Override
    public String getURL() {
        if (url == null) {
            url = Utils.getURL(request, page);
        }
        return url;
    }

    @Override
    public int getLevel() {
        return level;
    }

    private Page getRedirectTarget(@Nonnull Page page) {
        Page result = page;
        String redirectTarget = null;
        PageManager pageManager = page.getPageManager();
        Set<String> redirectCandidates = new LinkedHashSet<>();
        redirectCandidates.add(page.getPath());
        while (result != null && StringUtils.isNotEmpty((redirectTarget = result.getProperties().get(PN_REDIRECT_TARGET, String.class)))) {
            result = pageManager.getPage(redirectTarget);
            if (result != null) {
                if (!redirectCandidates.add(result.getPath())) {
                    LOGGER.warn("Detected redirect loop for the following pages: {}.", redirectCandidates.toString());
                    break;
                }
            }
        }
        return result;
    }
}
