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

import org.apache.commons.lang3.StringUtils;
import com.day.text.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ExperienceFragment.class, ComponentExporter.class },
    resourceType = {ExperienceFragmentImpl.RESOURCE_TYPE_V1 })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ExperienceFragmentImpl implements ExperienceFragment {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceFragmentImpl.class);

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/experiencefragment/v1/experiencefragment";

    public static final String PATH_DELIMITER = "/";
    public static final char PATH_DELIMITER_CHAR = '/';

    private static final String CONTENT_ROOT = "/content";
    private static final String EXPERIENCE_FRAGMENTS_ROOT = "/content/experience-fragments";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    @ValueMapValue(name = ExperienceFragment.PN_FRAGMENT_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentPath;

    private int localizationDepth;
    private String localizationRoot;
    private PageManager pageManager;

    @PostConstruct
    private void initModel() {
        localizationRoot = properties.get(ExperienceFragment.PN_LOCALIZATION_ROOT, currentStyle.get(ExperienceFragment.PN_LOCALIZATION_ROOT, String.class));
        localizationDepth = properties.get(ExperienceFragment.PN_LOCALIZATION_DEPTH, currentStyle.get(ExperienceFragment.PN_LOCALIZATION_DEPTH, 1));
        pageManager = currentPage.getPageManager();
    }

    @Override
    public String getFragmentPath() {
        return fragmentPath;
    }

    @Override
    public String getLocalizedFragmentPath() {
        String localizedFragmentPath = null;
        Page xfPage = pageManager.getPage(fragmentPath);
        if (xfPage != null) {
            Page xfLocalizedPage = getXfLocalizedPage(xfPage, currentPage);
            if (xfLocalizedPage != null) {
                localizedFragmentPath = xfLocalizedPage.getContentResource().getPath();
            } else {
                localizedFragmentPath = xfPage.getContentResource().getPath();
            }
        } else {
            LOGGER.debug("Experience Fragment variation not found at path:{}", fragmentPath);
        }
        return localizedFragmentPath;
    }

    @NotNull
    @Override public String getExportedType() {
        return request.getResource().getResourceType();
    }

    /**
     * Returns the localized page of the experience fragment page, based on the content page and the localization properties.
     *
     * @param xfPage the experience fragment page
     * @param sitePage the content page
     * @return
     */
    private Page getXfLocalizedPage(@NotNull Page xfPage, @NotNull Page sitePage) {
        Page siteRoot = pageManager.getPage(localizationRoot);
        if (siteRoot == null) {
            return null;
        }
        String xfLocalizationRoot = StringUtils.replace(localizationRoot, CONTENT_ROOT, EXPERIENCE_FRAGMENTS_ROOT, 1);
        Page xfRoot = pageManager.getPage(xfLocalizationRoot);
        if (xfRoot == null) {
            return null;
        }

        String xfPageLocalizationString = getLocalizationString(xfPage, xfRoot, localizationDepth); // e.g. us/en
        String sitePageLocalizationString = getLocalizationString(sitePage, siteRoot, localizationDepth); // e.g. us/es
        if (StringUtils.isEmpty(xfPageLocalizationString)) {
            return null;
        }
        if (StringUtils.isEmpty(sitePageLocalizationString)) {
            return null;
        }

        String xfLocalizationSrcPath = StringUtils.joinWith(PATH_DELIMITER, xfLocalizationRoot, xfPageLocalizationString); // e.g. /content/experience-fragments/my-site/us/en
        String xfLocalizationDestPath = StringUtils.joinWith(PATH_DELIMITER, xfLocalizationRoot, sitePageLocalizationString); // e.g. /content/experience-fragments/my-site/us/es

        String xfLocalizedPagePath = StringUtils.replace(xfPage.getPath(), xfLocalizationSrcPath, xfLocalizationDestPath, 1);
        return pageManager.getPage(xfLocalizedPagePath);
    }

    /**
     * Returns the localization string of the given page based on the localization root and depth.
     *
     * Use case                                  | Page path                         | localizationRoot  | localizationDepth | localizationString (output)
     * ----------------------------------------  |-----------------------------------|-------------------|-------------------|----------------------------
     * 1. No localization                        | /content/my-site/my-page          | empty string      | 0                 | empty string
     * 2. Language localization                  | /content/my-site/en/my-page       | /content/my-site  | 1                 | en
     * 3. Country-language localization          | /content/my-site/us/en/my-page    | /content/my-site  | 2                 | us/en
     *
     * @param page the page that contains the localization string
     * @param root the localization root page
     * @param depth the localization depth
     * @return the localization string
     */
    private String getLocalizationString(@NotNull Page page, @NotNull Page root, int depth) {
        if (depth < 1) {
            return null;
        }
        String localizationRootPath = root.getPath();
        String pagePath = page.getPath();
        if (!Text.isDescendant(localizationRootPath, pagePath)) {
            return null;
        }
        String[] pageTokens = Text.explode(pagePath, PATH_DELIMITER_CHAR);
        String[] rootTokens = Text.explode(localizationRootPath, PATH_DELIMITER_CHAR);
        if (pageTokens.length < rootTokens.length + depth) {
            return null;
        }
        String[] localizationTokens = new String[depth];
        System.arraycopy(pageTokens, rootTokens.length, localizationTokens, 0, depth);
        return Text.implode(localizationTokens, PATH_DELIMITER);
    }

}
