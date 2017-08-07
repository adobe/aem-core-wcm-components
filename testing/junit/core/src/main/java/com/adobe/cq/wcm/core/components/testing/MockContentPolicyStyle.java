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
package com.adobe.cq.wcm.core.components.testing;

import org.apache.sling.api.resource.Resource;

import com.day.cq.commons.ValueMapWrapper;
import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;

public class MockContentPolicyStyle extends ValueMapWrapper implements Style {

    public MockContentPolicyStyle(ContentPolicy contentPolicy) {
        super(contentPolicy.getProperties());
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
    public Cell getCell() {
        return null;
    }

    @Override
    public Resource getDefiningResource(String s) {
        return null;
    }

    @Override
    public String getDefiningPath(String s) {
        return null;
    }

    @Override
    public Style getSubStyle(String s) {
        return null;
    }
}
