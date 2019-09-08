/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.internal.link.Link;
import com.adobe.cq.wcm.core.components.models.ImageArea;

public class ImageAreaImpl implements ImageArea {

    private String shape;
    private String coordinates;
    private String relativeCoordinates;
    private Link link;
    private String alt;

    public ImageAreaImpl(String shape, String coordinates, String relativeCoordinates, @NotNull Link link, String alt) {
        this.shape = shape;
        this.coordinates = coordinates;
        this.relativeCoordinates = relativeCoordinates;
        this.link = link;
        this.alt = alt;
    }

    @Override
    public String getShape() {
        return shape;
    }

    @Override
    public String getCoordinates() {
        return coordinates;
    }

    @Override
    public String getRelativeCoordinates() {
        return relativeCoordinates;
    }

    @Override
    public @Nullable String getLinkURL() {
        return link.getLinkURL();
    }

    @Override
    public boolean isLinkValid() {
        return link.isLinkValid();
    }

    @Override
    public @Nullable Map<String, String> getLinkHtmlAttributes() {
        return link.getLinkHtmlAttributes();
    }

    @Override
    public String getHref() {
        // fallback for old method for keeping backward compatibility
        return StringUtils.defaultString(getLinkURL());
    }

    @Override
    public String getTarget() {
        // fallback for old method for keeping backward compatibility
        String target = null;
        Map<String, String> attrs = getLinkHtmlAttributes();
        if (attrs != null) {
            target = attrs.get("target");
        }
        return StringUtils.defaultString(target);
    }

    @Override
    public String getAlt() {
        return alt;
    }
}
