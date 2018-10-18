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

import com.adobe.cq.wcm.core.components.models.Download;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.designer.Style;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Calendar;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Download.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = DownloadImpl.RESOURCE_TYPE)
public class DownloadImpl  implements Download {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadImpl.class);

    public final static String RESOURCE_TYPE = "mnrdlm/brand/components/content/download";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    protected Style currentStyle;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String fileReference;

    private String downloadUrl;

    @ValueMapValue
    private boolean titleFromAsset;

    @ValueMapValue
    private boolean descriptionFromAsset;

    @ValueMapValue(name = "jcr:title")
    private String title;

    @ValueMapValue(name = "jcr:description")
    private String description;

    @ValueMapValue
    private String ctaText;

    private String imagePath;

    private String titleType;

    private long lastModified = 0;

    @PostConstruct
    protected void initModel()
    {
        if(currentStyle != null)
        {
            if(StringUtils.isBlank(ctaText))
            {
                ctaText = currentStyle.get("ctaText", String.class);
            }
            titleType = currentStyle.get("titleType", String.class);
        }
        if(StringUtils.isNotBlank(fileReference))
        {
            Resource downloadResource = resourceResolver.getResource(fileReference);
            if(downloadResource != null)
            {
                Asset downloadAsset = downloadResource.adaptTo(Asset.class);
                if(downloadAsset != null)
                {
                    Calendar resourceLastModified = properties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
                    if(resourceLastModified != null)
                    {
                        lastModified = resourceLastModified.getTimeInMillis();
                    }
                    long assetLastModified = downloadAsset.getLastModified();
                    if(assetLastModified > lastModified)
                    {
                        lastModified = assetLastModified;
                    }

                    downloadUrl = downloadAsset.getPath();

                    StringBuilder imagePathBuilder = new StringBuilder();

                    String resourcePath = resourceResolver.map(request, resource.getPath());

                    imagePathBuilder.append(resourcePath).append(".coreimg.jpeg");
                    if(lastModified > 0)
                    {
                        imagePathBuilder.append("/").append(lastModified).append(".jpeg");
                    }

                    imagePath = imagePathBuilder.toString();

                    if(titleFromAsset)
                    {
                        String assetTitle = downloadAsset.getMetadataValue("dc:title");
                        if(StringUtils.isNotBlank(assetTitle))
                        {
                            title = assetTitle;
                        }
                    }
                    if(descriptionFromAsset)
                    {
                        String assetDescription = downloadAsset.getMetadataValue("dc:description");
                        if(StringUtils.isNotBlank(assetDescription))
                        {
                            description = assetDescription;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCtaText() {
        return ctaText;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String getTitleType() {
        return titleType;
    }
}
