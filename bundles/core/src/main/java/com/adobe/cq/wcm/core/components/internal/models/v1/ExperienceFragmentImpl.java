/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
import javax.inject.Inject;
import javax.jcr.RangeIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.xf.ExperienceFragmentsConstants;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveCopy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.text.Text;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ExperienceFragment.class, ComponentExporter.class },
    resourceType = {ExperienceFragmentImpl.RESOURCE_TYPE_V1 })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ExperienceFragmentImpl implements ExperienceFragment {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceFragmentImpl.class);

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/experiencefragment/v1/experiencefragment";

    private static final String PATH_DELIMITER = "/";
    private static final char PATH_DELIMITER_CHAR = '/';
    private static final String CONTENT_ROOT = "/content";
    private static final String EXPERIENCE_FRAGMENTS_ROOT = "/content/experience-fragments";
    private static final String JCR_CONTENT_ROOT = "/jcr:content";

    @Self
    private SlingHttpServletRequest request;

    @Inject
    protected Resource resource;

    @SlingObject
    private ResourceResolver resolver;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue(name = ExperienceFragment.PN_FRAGMENT_VARIATION_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String fragmentVariationPath;

    @OSGiService
    private LanguageManager languageManager;

    @OSGiService
    private LiveRelationshipManager relationshipManager;

    private String localizedFragmentVariationPath;
    private String name;

    @PostConstruct
    protected void initModel() {
        if (inTemplate()) {
            String currentPageRootPath = getLocalizationRoot(currentPage.getPath());
            // we should use getLocalizationRoot instead of getXfLocalizationRoot once the XF UI supports creating Live and Language Copies
            String xfRootPath = getXfLocalizationRoot(fragmentVariationPath, currentPageRootPath);
            if (!StringUtils.isEmpty(currentPageRootPath) && !StringUtils.isEmpty(xfRootPath)) {
                String xfRelativePath = StringUtils.substring(fragmentVariationPath, xfRootPath.length());
                String localizedXfRootPath = StringUtils.replace(currentPageRootPath, CONTENT_ROOT, EXPERIENCE_FRAGMENTS_ROOT, 1);
                localizedFragmentVariationPath = StringUtils.join(localizedXfRootPath, xfRelativePath, JCR_CONTENT_ROOT);
            }
        }

        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page xfVariationPage = pageManager.getPage(fragmentVariationPath);
            if (xfVariationPage != null) {
                Page xfPage = xfVariationPage.getParent();
                if (xfPage != null) {
                    name = xfPage.getName();
                }
            }
        }

        String xfContentPath = StringUtils.join(fragmentVariationPath, JCR_CONTENT_ROOT);
        if (!resourceExists(localizedFragmentVariationPath) && resourceExists(xfContentPath)) {
            localizedFragmentVariationPath = xfContentPath;
        }
        if (!isExperienceFragmentVariation(localizedFragmentVariationPath)) {
            localizedFragmentVariationPath = null;
        }
    }

    @Override
    public String getLocalizedFragmentVariationPath() {
        return localizedFragmentVariationPath;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return name;
    }

    @NotNull
    @Override public String getExportedType() {
        return request.getResource().getResourceType();
    }

    /**
     * Returns the localization root of the resource defined at the given path.
     *
     * Use case                                  | Path                                 | Root
     * ------------------------------------------|--------------------------------------|------------------
     * 1. No localization                        | /content/mysite/mypage               | null
     * 2. Language localization                  | /content/mysite/en/mypage            | /content/mysite/en
     * 3. Country-language localization          | /content/mysite/us/en/mypage         | /content/mysite/us/en
     * 4. Country-language localization (variant)| /content/us/mysite/en/mypage         | /content/us/mysite/en
     * 5. Blueprint                              | /content/mysite/blueprint/mypage     | /content/mysite/blueprint
     * 6. Live Copy                              | /content/mysite/livecopy/mypage      | /content/mysite/livecopy
     *
     * @param path the resource path
     * @return the localization root of the resource at the given path if it exists, {@code null} otherwise
     */
    private String getLocalizationRoot(String path) {
        String root = null;
        if (!StringUtils.isEmpty(path)) {
            Resource resource = resolver.getResource(path);
            root = getLanguageRoot(resource);
            if (StringUtils.isEmpty(root)) {
                root = getBlueprintPath(resource);
            }
            if (StringUtils.isEmpty(root)) {
                root = getLiveCopyPath(resource);
            }
        }
        return root;
    }

    /**
     * Returns the language root of the resource.
     *
     * @param resource the resource
     * @return the language root of the resource if it exists, {@code null} otherwise
     */
    private String getLanguageRoot(Resource resource) {
        Page rootPage = languageManager.getLanguageRoot(resource);
        if (rootPage != null) {
            return rootPage.getPath();
        }
        return null;
    }

    /**
     * Returns the path of the blueprint of the resource.
     *
     * @param resource the resource
     * @return the path of the blueprint of the resource if it exists, {@code null} otherwise
     */
    private String getBlueprintPath(Resource resource) {
        try {
            if (relationshipManager.isSource(resource)) {
                // the resource is a blueprint
                RangeIterator liveCopiesIterator = relationshipManager.getLiveRelationships(resource, null, null);
                if (liveCopiesIterator != null) {
                    LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                    LiveCopy liveCopy = relationship.getLiveCopy();
                    if (liveCopy != null) {
                        return liveCopy.getBlueprintPath();
                    }
                }
            }
        } catch (WCMException e) {
            LOGGER.error("Unable to get the blueprint: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Returns the path of the live copy of the resource.
     *
     * @param resource the resource
     * @return the path of the live copy of the resource if it exists, {@code null} otherwise
     */
    private String getLiveCopyPath(Resource resource) {
        try {
            if (relationshipManager.hasLiveRelationship(resource)) {
                // the resource is a live copy
                LiveRelationship liveRelationship = relationshipManager.getLiveRelationship(resource, false);
                if (liveRelationship != null) {
                    LiveCopy liveCopy = liveRelationship.getLiveCopy();
                    if (liveCopy != null) {
                        return liveCopy.getPath();
                    }
                }
            }
        } catch (WCMException e) {
            LOGGER.error("Unable to get the live copy: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Returns the localization root of the experience fragment path based on the localization root of the current page.
     *
     * As of today (08/aug/2019) the XF UI does not support creating Live and Language Copies, which prevents getRoot
     * to be used with XF.
     * This method works around this issue by deducting the XF root from the XF path and the root of the current page.
     *
     * @param xfPath the experience fragment path
     * @param currentPageRoot the localization root of the current page
     * @return the localization root of the experience fragment path if it exists, {@code null} otherwise
     */
    private String getXfLocalizationRoot(String xfPath, String currentPageRoot) {
        String xfRoot = null;
        if (!StringUtils.isEmpty(xfPath) && !StringUtils.isEmpty(currentPageRoot)
            && resolver.getResource(xfPath) != null && resolver.getResource(currentPageRoot) != null) {
            String[] xfPathTokens = Text.explode(xfPath, PATH_DELIMITER_CHAR);
            String[] referenceRootTokens = Text.explode(currentPageRoot, PATH_DELIMITER_CHAR);
            int xfRootDepth = referenceRootTokens.length + 1;
            if (xfPathTokens.length >= xfRootDepth) {
                String[] xfRootTokens = new String[xfRootDepth];
                System.arraycopy(xfPathTokens, 0, xfRootTokens, 0, xfRootDepth);
                xfRoot = StringUtils.join(PATH_DELIMITER, Text.implode(xfRootTokens, PATH_DELIMITER));
            }
        }
        return xfRoot;
    }

    /**
     * Checks if the resource exists at the given path.
     *
     * @param path the resource path
     * @return {@code true} if the resource exists, {@code false} otherwise
     */
    private boolean resourceExists(String path) {
        return (StringUtils.isNotEmpty(path) && resolver.getResource(path) != null);
    }

    /**
     * Checks if the resource is defined in the template.
     *
     * @return {@code true} if the resource is defined in the template, {@code false} otherwise
     */
    private boolean inTemplate () {
        Template template = currentPage.getTemplate();
        return template != null && StringUtils.startsWith(resource.getPath(), template.getPath());
    }

    /**
     * Checks if the resource at the given path is an Experience Fragment variation.
     *
     * @return {@code true} if the resource is an XF variation, {@code false} otherwise
     */
    private boolean isExperienceFragmentVariation(String path) {
        if (StringUtils.isNotEmpty(path)) {
            Resource resource = resolver.getResource(path);
            if (resource != null) {
                ValueMap properties = resource.getValueMap();
                String xfVariantType = properties.get(ExperienceFragmentsConstants.PN_XF_VARIANT_TYPE, String.class);
                return xfVariantType != null;
            }
        }
        return false;
    }

}
