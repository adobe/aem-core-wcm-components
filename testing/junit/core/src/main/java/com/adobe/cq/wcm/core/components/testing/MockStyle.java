/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.testing;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.resourceresolver.MockValueMap;

import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;

/**
 * Mock implementation of {@link Style}
 */
public class MockStyle extends MockValueMap implements Style {

    public MockStyle(Resource resource) {
        super(resource);
    }

    public MockStyle(Resource resource, ValueMap valueMap) {
        super(resource, valueMap);
    }

    @Override
    public Cell getCell() {
        return null;
    }

    @Override
    public Design getDesign() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public Resource getDefiningResource(String name) {
        return null;
    }

    @Override
    public String getDefiningPath(String name) {
        return null;
    }

    @Override
    public Style getSubStyle(String relPath) {
        return null;
    }
}
