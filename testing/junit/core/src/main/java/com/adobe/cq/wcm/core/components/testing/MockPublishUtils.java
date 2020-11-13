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
package com.adobe.cq.wcm.core.components.testing;

import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;

public class MockPublishUtils implements PublishUtils {
    @Override
    public String getPublishNodeURL(Resource resource) throws RepositoryException {
        return null;
    }

    @Override
    public String externalizeImageDeliveryAsset(Resource resource, String s) throws RepositoryException {
        return null;
    }

    @Override
    public String[] externalizeImageDeliveryAsset(Resource resource) throws RepositoryException {
        return new String[] {
            "https://s7d9.scene7.com",
            "dmtestcompany/Adobe_Systems_logo_and_wordmark_DM"
        };
    }

    @Override
    public String externalizeImageDeliveryUrl(Resource resource, String s) throws RepositoryException {
        return null;
    }

    @Override
    public String getISProperty(String s, String s1) {
        return null;
    }
}
