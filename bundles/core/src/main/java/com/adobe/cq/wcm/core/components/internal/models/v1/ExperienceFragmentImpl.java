/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
    ExperienceFragment.class, ComponentExporter.class }, resourceType = {
        ExperienceFragmentImpl.RESOURCE_TYPE_V1 })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ExperienceFragmentImpl implements ExperienceFragment {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ExperienceFragmentImpl.class);

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/experiencefragment/v1/experiencefragment";

    public static final String PATH_DELIMITER = "/";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private LanguageManager languageManager;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    private String fragmentPath;

    @PostConstruct
    private void initModel() {
        fragmentPath = properties.get(PN_FRAGMENT_PATH, String.class);
    }

    @Override
    public String getExperienceFragmentVariationPath() {
        String experFragmentVariationPath = null;
        PageManager pageManager = currentPage.getPageManager();
        Page xfpage = pageManager.getPage(fragmentPath);
        if (xfpage != null) {
            Page localizedXfPage = getLocalizedXfPage(currentPage, xfpage);
            if (localizedXfPage != null) {
                experFragmentVariationPath = localizedXfPage.getContentResource().getPath();
            } else {
                experFragmentVariationPath = xfpage.getContentResource().getPath();
            }
        } else {
            LOGGER.debug("Experience Fragment variation not found at path:{}",
                fragmentPath);
        }
        return experFragmentVariationPath;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    private Page getLocalizedXfPage(Page currentPage, Page xfPage) {
        Page localizedXfPage = null;

        String xfPageLanguageRootPath = LanguageUtil.getLanguageRoot(
            xfPage.getPath());
        Page currentPageLanguageRoot = languageManager.getLanguageRoot(
            currentPage.getContentResource());

        if (xfPageLanguageRootPath != null && currentPageLanguageRoot != null) {

            String xfPageLanguageRootName = xfPageLanguageRootPath.substring(
                xfPageLanguageRootPath.lastIndexOf('/') + 1);
            String currentPageLanguageRootName = currentPageLanguageRoot.getName();

            PageManager pageManager = currentPage.getPageManager();

            if (currentPageLanguageRootName.equals(xfPageLanguageRootName)) {
                localizedXfPage = xfPage;
            } else {
                String expFragmentVariationPath = xfPage.getPath().substring(
                    xfPageLanguageRootPath.length());
                // Replace language root in the variation path
                String languageVariationPath = xfPageLanguageRootPath.replaceAll(
                    PATH_DELIMITER + xfPageLanguageRootName + "$",
                    PATH_DELIMITER + currentPageLanguageRootName);
                Page localizedXfVariationPage = pageManager.getPage(
                    languageVariationPath + expFragmentVariationPath);
                if (localizedXfVariationPage != null) {
                    localizedXfPage = localizedXfVariationPage;
                }
            }
        }
        return localizedXfPage;
    }
}
