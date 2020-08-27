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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.cq.wcm.core.components.models.PWA;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {PWA.class})
public class PWAImpl implements PWA {

    /**
     * The current request.
     */
    @Self
    protected SlingHttpServletRequest request;

    @Override
    public boolean isPWAEnabled() {
        Resource resource = request.getResource();
        String projectPath = this.getSitesProject(resource.getPath());
        Resource project = resource.getResourceResolver().getResource(projectPath + "/jcr:content");

        if (project == null) {
            return false;
        }

        ValueMap valueMap = project.getValueMap();
        Boolean isEnabled = valueMap.get("enablePWA", Boolean.class);
        return isEnabled;
    }

    @Override
    public String getProjectName() {
        Resource resource = request.getResource();
        String projectPath = this.getSitesProject(resource.getPath());
        String[] levels = projectPath.split("/");
        if (levels.length > 0) {
            return levels[levels.length - 1];
        }
        return "";
    }

    @Nonnull
    private String getSitesProject(String path) {
        String[] levels = path.split("/");

        if (levels.length < 3) {
            return "";
        };

        if (levels.length == 3) {
            return path;
        };

        int i = 0;
        StringBuilder projectPath = new StringBuilder();
        while (i < 3) {
            projectPath.append(levels[i]).append('/');
            i++;
        }

        return projectPath.toString();
    }
}
