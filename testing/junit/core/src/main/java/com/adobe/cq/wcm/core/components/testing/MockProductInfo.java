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
package com.adobe.cq.wcm.core.components.testing;

import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Version;

import com.adobe.granite.license.ProductInfo;

public class MockProductInfo implements ProductInfo {

    private Version version;

    public MockProductInfo(Version version) {
        this.version = version;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public String getShortName() {
        return null;
    }

    @Override
    public String getShortVersion() {
        return version.toString();
    }

    @Override
    public String getYear() {
        return null;
    }

    @Override
    public String getVendor() {
        return null;
    }

    @Override
    public String getVendorUrl() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public ValueMap getProperties() {
        return null;
    }
}
