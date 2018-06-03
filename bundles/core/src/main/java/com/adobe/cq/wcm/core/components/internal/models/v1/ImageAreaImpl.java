/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

import com.adobe.cq.wcm.core.components.models.ImageArea;

public class ImageAreaImpl implements ImageArea {

    private String shape;
    private String coordinates;
    private String relativeCoordinates;
    private String href;
    private String target;
    private String alt;

    public ImageAreaImpl(String shape, String coordinates, String relativeCoordinates, String href, String target, String alt) {
        this.shape = shape;
        this.coordinates = coordinates;
        this.relativeCoordinates = relativeCoordinates;
        this.href = href;
        this.target = target;
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
    public String getHref() {
        return href;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getAlt() {
        return alt;
    }
}
