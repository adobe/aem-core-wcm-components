/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.internal.models.v2;

import javax.annotation.CheckForNull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.Model;

import com.adobe.cq.wcm.core.components.config.PWACaConfig;
import com.adobe.cq.wcm.core.components.models.PWA;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {PWA.class}, resourceType = PageImpl.RESOURCE_TYPE)
public class PWAImpl implements PWA {

    private static final String MANIFEST_NAME = "manifest.webmanifest";

    @Inject
    private Resource resource;

    private boolean isEnabled = false;
    private String projectName = "";
    private String manifestPath = "";
    private String serviceWorkerPath = "";
    private String themeColor = "";
    private String iconPath = "";

    @PostConstruct
    protected void initModel() {
        Page pwaSiteRootPage = getPWASiteRootPage();
        if (pwaSiteRootPage != null) {
            Resource contentResource = pwaSiteRootPage.getContentResource();
            if (contentResource != null) {
                ValueMap valueMap = contentResource.adaptTo(ValueMap.class);
                if (valueMap != null) {
                    isEnabled = valueMap.get(PN_ENABLE_PWA, isEnabled);
                    if (isEnabled) {
                        themeColor = colorToHex(valueMap.get(PN_THEME_COLOR, StringUtils.EMPTY));
                        iconPath = valueMap.get(PN_PWA_ICON, StringUtils.EMPTY);
                        projectName = pwaSiteRootPage.getName();
                        manifestPath = pwaSiteRootPage.getPath() + "/" + MANIFEST_NAME;
                        serviceWorkerPath = "/" + projectName + "sw.js";
                    }
                }
            }
        }
    }

    @CheckForNull
    private Page getPWASiteRootPage() {
        Page pwaSiteRoot = null;
        ResourceResolver resourceResolver = resource.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page page = pageManager.getContainingPage(resource);
            ConfigurationBuilder configurationBuilder = resource.adaptTo(ConfigurationBuilder.class);
            if (configurationBuilder != null && page != null) {
                PWACaConfig caConfig = configurationBuilder.as(PWACaConfig.class);
                pwaSiteRoot = page.getAbsoluteParent(caConfig.projectSiteRootLevel());
            }
        }
        return pwaSiteRoot;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public String getThemeColor() {
        return this.themeColor;
    }

    @Override
    public String getIconPath() {
        return this.iconPath;
    }

    @Override
    public String getManifestPath() {
        return this.manifestPath;
    }

    @Override
    public String getServiceWorkerPath() {
        return this.serviceWorkerPath;
    }

    private String colorToHex(String color) {
        if (color == null || color.length() == 0) {
            return "";
        }

        if (color.startsWith("#")) {
            return color;
        }

        if (!color.startsWith("rgb")) {
            return "";
        }

        try {
            String[] parts = color.split(",");
            String r = Integer.toHexString(Integer.parseInt(parts[0].substring(parts[0].indexOf("(") + 1)));
            String g = Integer.toHexString(Integer.parseInt(parts[1]));
            String b;

            if (color.startsWith("rgba")) {
                b = Integer.toHexString(Integer.parseInt(parts[2]));
            }
            else {
                b = Integer.toHexString(Integer.parseInt(parts[2].substring(0, parts[2].indexOf(")"))));
            }

            return "#" + (r.length() == 2 ? r : "0" + r) + (g.length() == 2 ? g : "0" + g) + (b.length() == 2 ? b : "0" + b);
        }
        catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return "";
        }
    }

}
