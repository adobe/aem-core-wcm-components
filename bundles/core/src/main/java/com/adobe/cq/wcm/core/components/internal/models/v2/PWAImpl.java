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

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.cq.wcm.core.components.models.PWA;
import com.day.cq.commons.jcr.JcrConstants;

@Model(adaptables = Resource.class,
    adapters = {PWA.class})
public class PWAImpl implements PWA {

    static final String MANIFEST_NAME = "manifest.webmanifest";
    static final int SITES_PROJECT_LEVEL = 3;

    private boolean isPWAEnabled = false;
    private String projectName = "";
    private String manifestPath = "";
    private String serviceWorkerPath = "";
    private String themecolor = "";
    private String iconPath = "";

    @Self
    private Resource resource;

    @PostConstruct
    protected void initModel() {
        String projectPath = this.getSitesProjectPath(resource.getPath());
        Resource project = resource.getResourceResolver().getResource(projectPath + JcrConstants.JCR_CONTENT);

        if (project != null) {
            ValueMap valueMap = project.getValueMap();
            Boolean isPWAEnabled = valueMap.get("enablePWA", Boolean.class);
            this.isPWAEnabled = (isPWAEnabled != null) ? isPWAEnabled : false;
            this.themecolor = colorToHex(valueMap.get("themecolor", ""));
            this.iconPath = valueMap.get("pwaicon", "");
        }

        String[] levels = projectPath.split("/");
        this.projectName = levels[levels.length - 1];
        this.manifestPath = projectPath + MANIFEST_NAME;
        this.serviceWorkerPath = "/" + this.projectName + "sw.js";
    }

    @Override
    public boolean isPWAEnabled() {
        return this.isPWAEnabled;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public String getThemecolor() {
        return this.themecolor;
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

    @Nonnull
    private String getSitesProjectPath(String path) {
        String[] levels = path.split("/");

        if (levels.length < SITES_PROJECT_LEVEL) {
            return "";
        }

        if (levels.length == SITES_PROJECT_LEVEL) {
            return path;
        }

        int i = 0;
        StringBuilder projectPath = new StringBuilder();
        while (i < SITES_PROJECT_LEVEL) {
            projectPath.append(levels[i]).append('/');
            i++;
        }

        return projectPath.toString();
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
