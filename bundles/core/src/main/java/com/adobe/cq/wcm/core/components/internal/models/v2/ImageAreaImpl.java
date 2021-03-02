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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ImageAreaImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageAreaImpl {

    public ImageAreaImpl(String shape, String coordinates, String relativeCoordinates, @NotNull Link link, String alt) {
        super(shape, coordinates, relativeCoordinates, link, alt);
    }

    @Override
    public @NotNull Link getLink() {
        return link;
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getHref() {
        return super.getHref();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getTarget() {
        return super.getTarget();
    }

}
