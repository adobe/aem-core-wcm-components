/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.page.v1.page;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;

public class ExperienceInfo extends WCMUsePojo {

    private String id;
    private String description;
    private String experienceTitle;
    private String analyzeUrl;
    private String lastModifiedDate;


    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        Resource resource = getResource();
        Resource parentResource = resource.getParent();
        id = parentResource.getName();
        analyzeUrl = parentResource.getPath();
        lastModifiedDate = properties.get(NameConstants.PN_PAGE_LAST_MOD, String.class);
        experienceTitle = extractPropertyWithJcrContentFallback(properties, resource, JcrConstants.JCR_TITLE);
        if (StringUtils.isEmpty(experienceTitle)) {
            experienceTitle = parentResource.getName();
        }
        description = extractPropertyWithJcrContentFallback(properties, resource, JcrConstants.JCR_DESCRIPTION);
    }

    private String extractPropertyWithJcrContentFallback(ValueMap properties, Resource resource, String propertyName) {
        String value = properties.get(propertyName, String.class);
        if (StringUtils.isEmpty(value)) {
            Resource jcrContent = resource.getChild(JcrConstants.JCR_CONTENT);
            if (jcrContent != null) {
                ValueMap jcrContentProperties = jcrContent.adaptTo(ValueMap.class);
                if (jcrContentProperties != null) {
                    value = jcrContentProperties.get(propertyName, String.class);
                }
            }
        }
        return value;
    }

    /**
     * Retrieves the experience's ID.
     *
     * @return the ID as a {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the experience's description.
     *
     * @return the description as a {@link String}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the experience's title
     *
     * @return the title as a {@link String}
     */
    public String getExperienceTitle() {
        return experienceTitle;
    }

    /**
     * Retrieves the URL to analyse.
     *
     * @return the URL as a {@link String}
     */
    public String getAnalyzeUrl() {
        return analyzeUrl;
    }

    /**
     * Retrieves the last modified date of the page.
     *
     * @return the last modified date of the page as a {@link String}
     */
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }
}
