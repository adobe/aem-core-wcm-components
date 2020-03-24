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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.cq.wcm.core.components.models.CloudConfig;
import com.day.cq.wcm.api.Page;

import static com.day.cq.commons.jcr.JcrConstants.*;
import static org.apache.jackrabbit.JcrConstants.NT_FOLDER;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_FOLDER;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_ORDERED_FOLDER;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {CloudConfig.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CloudConfigImpl implements CloudConfig {

    private static final String CLOUDCONFIG_BUCKET_NAME = "cloudconfigs";
    private static final String CONF_CONTAINER_BUCKET_NAME = "settings";
    private static final String CONF_ROOT = "/conf";
    private static final String CLOUDCONFIG_BUCKET_PATH = CONF_CONTAINER_BUCKET_NAME + "/" + CLOUDCONFIG_BUCKET_NAME;
    private static final String ACTIONS_CREATECONFIG_ACTIVATOR = "cq-confadmin-actions-createconfig-activator";

    @RequestAttribute
    private Resource useResource;

    @SlingObject
    private Resource resource;

    private String title;
    private Calendar lastModifiedDate;

    @PostConstruct
    private void initModel() {
        if (useResource == null) {
            useResource = resource;
        }
    }

    @Override
    public String getTitle() {
        if (StringUtils.isEmpty(title)) {
            title = useResource.getValueMap().get(JCR_CONTENT + "/" + JCR_TITLE, useResource.getValueMap().get(JCR_TITLE,
                    useResource.getName()));
        }
        return title;
    }

    @Override
    public Calendar getLastModifiedDate() {
        if (lastModifiedDate == null) {
            Page page = useResource.adaptTo(Page.class);
            if (page != null) {
                lastModifiedDate = page.getLastModified();
            } else {
                lastModifiedDate = useResource.getValueMap().get(JCR_LASTMODIFIED, Calendar.class);
            }
        }
        return lastModifiedDate;
    }

    @Override
    public boolean hasChildren() {
        if (useResource.hasChildren()) {
            for (Resource child: useResource.getChildren()) {
                if((child.isResourceType(NT_SLING_FOLDER)
                        && !CONF_CONTAINER_BUCKET_NAME.equals(child.getName())) ||
                        child.getChild(CloudConfigImpl.CLOUDCONFIG_BUCKET_NAME) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFolder() {
        return isResourceType(useResource, NT_FOLDER, NT_SLING_FOLDER, NT_SLING_ORDERED_FOLDER);
    }

    @Override
    public Set<String> getActionsRels() {
        Set<String> actions = new LinkedHashSet<>();

        boolean isRoot = CONF_ROOT.equals(resource.getPath());
        boolean hasCapability = resource.getChild(CLOUDCONFIG_BUCKET_PATH) != null;
        if (useResource != null &&
                !isRoot && hasCapability) {
            actions.add(ACTIONS_CREATECONFIG_ACTIVATOR);
        }

        return actions;
    }

    private boolean isResourceType(Resource resource, String... resourceTypes) {
        if (resource != null && resourceTypes != null) {
            for (String resourceType : resourceTypes) {
                Resource child = resource.getChild(JCR_CONTENT);
                if (child != null) {
                    resource = child;
                }
                if (resource.isResourceType(resourceType)) {
                    return true;
                }
            }
        }
        return false;
    }

}
