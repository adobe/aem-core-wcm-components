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

@Model(adaptables = Resource.class,
    adapters = {PWA.class})
public class PWAImpl implements PWA {

    static final String PROP_PWA_PWAENABLED = "pwaEnabled";
    static final String PROP_PWA_STARTURL = "startURL";
    static final String PROP_PWA_THEMECOLOR = "themeColor";
    static final String PROP_PWA_ICON = "pwaIcon";
    static final String MANIFEST_NAME = "manifest.webmanifest";
    static final String CONTENT_PATH = "/content/";

    private boolean isPWAEnabled = false;
    private String manifestPath = "";
    private String serviceWorkerPath = "";
    private String themeColor = "";
    private String iconPath = "";

    @Self
    private Resource resource;

    @PostConstruct
    protected void initModel() {
        ValueMap valueMap = resource.getValueMap();
        Boolean isPWAEnabled = valueMap.get(PROP_PWA_PWAENABLED, Boolean.class);
        this.isPWAEnabled = (isPWAEnabled != null) ? isPWAEnabled : false;
        if (!this.isPWAEnabled) {
            return;
        }

        this.themeColor = colorToHex(valueMap.get(PROP_PWA_THEMECOLOR, ""));
        this.iconPath = valueMap.get(PROP_PWA_ICON, "");

        String startURL = valueMap.get(PROP_PWA_STARTURL, "");
        this.manifestPath = replaceSuffix(startURL, MANIFEST_NAME);

        Resource page = resource.getParent();
        if (page != null) {
            String mappingName = page.getPath().replace("/", ".").substring(CONTENT_PATH.length());
            this.serviceWorkerPath = "/" + mappingName + "sw.js";
        }
    }

    @Override
    public boolean isPWAEnabled() {
        return this.isPWAEnabled;
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

    private String replaceSuffix(@Nonnull String url, @Nonnull String newSuffix) {
        int index = url.lastIndexOf(".");
        if (index == -1) {
            return url + (url.endsWith("/") ? newSuffix : "/" + newSuffix);
        }

        return url.replace(url.substring(index, url.length()), "/" + newSuffix);
    }
}
