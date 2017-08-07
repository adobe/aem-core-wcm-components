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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Page.class, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = Constants.EXPORTER_NAME, extensions = Constants.EXPORTER_EXTENSION)
public class PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v1/page";

    @ScriptVariable
    protected com.day.cq.wcm.api.Page currentPage;

    @ScriptVariable
    protected ValueMap pageProperties;

    @ScriptVariable
    @JsonIgnore
    protected Design currentDesign;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    protected Style currentStyle;

    @ScriptVariable
    @JsonIgnore
    protected ResourceResolver resolver;

    protected String[] keywords = new String[0];
    protected String designPath;
    protected String staticDesignPath;
    protected String title;
    protected String[] clientLibCategories = new String[0];
    protected Calendar lastModifiedDate;
    protected String templateName;

    protected static final String DEFAULT_TEMPLATE_EDITOR_CLIENT_LIB = "wcm.foundation.components.parsys.allowedcomponents";
    protected static final String PN_CLIENTLIBS = "clientlibs";

    @JsonIgnore
    protected Map<String, String> favicons = new HashMap<>();

    @PostConstruct
    protected void initModel() {
        title = currentPage.getTitle();
        if (StringUtils.isBlank(title)) {
            title = currentPage.getName();
        }
        Tag[] tags = currentPage.getTags();
        keywords = new String[tags.length];
        int index = 0;
        for (Tag tag : tags) {
            keywords[index++] = tag.getTitle(currentPage.getLanguage(false));
        }
        if (currentDesign != null) {
            String designPath = currentDesign.getPath();
            if (!Designer.DEFAULT_DESIGN_PATH.equals(designPath)) {
                this.designPath = designPath;
                if (resolver.getResource(designPath + "/static.css") != null) {
                    staticDesignPath = designPath + "/static.css";
                }
                loadFavicons(designPath);
            }
        }
        populateClientLibCategories();
        templateName = extractTemplateName();
    }

    protected String extractTemplateName() {
        String templateName = null;
        String templatePath = pageProperties.get(NameConstants.PN_TEMPLATE, String.class);
        if (StringUtils.isNotEmpty(templatePath)) {
            int i = templatePath.lastIndexOf("/");
            if (i > 0) {
                templateName = templatePath.substring(i + 1);
            }
        }
        return templateName;
    }


    @Override
    public String getLanguage() {
        return currentPage == null ? Locale.getDefault().toLanguageTag() : currentPage.getLanguage(false).toLanguageTag();
    }

    @Override
    public Calendar getLastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = pageProperties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
        }
        return lastModifiedDate;
    }

    @Override
    public String[] getKeywords() {
        return Arrays.copyOf(keywords, keywords.length);
    }

    @Override
    public String getDesignPath() {
        return designPath;
    }

    @Override
    public String getStaticDesignPath() {
        return staticDesignPath;
    }

    @Override
    public Map<String, String> getFavicons() {
        return favicons;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public String[] getClientLibCategories() {
        return Arrays.copyOf(clientLibCategories, clientLibCategories.length);
    }

    protected void loadFavicons(String designPath) {
        favicons.put(PN_FAVICON_ICO, getFaviconPath(designPath, FN_FAVICON_ICO));
        favicons.put(PN_FAVICON_PNG, getFaviconPath(designPath, FN_FAVICON_PNG));
        favicons.put(PN_TOUCH_ICON_120, getFaviconPath(designPath, FN_TOUCH_ICON_120));
        favicons.put(PN_TOUCH_ICON_152, getFaviconPath(designPath, FN_TOUCH_ICON_152));
        favicons.put(PN_TOUCH_ICON_60, getFaviconPath(designPath, FN_TOUCH_ICON_60));
        favicons.put(PN_TOUCH_ICON_76, getFaviconPath(designPath, FN_TOUCH_ICON_76));
    }

    protected String getFaviconPath(String designPath, String faviconName) {
        String path = designPath + "/" + faviconName;
        if (resolver.getResource(path) == null) {
            return null;
        }
        return path;
    }

    protected void populateClientLibCategories() {
        List<String> categories = new ArrayList<>();
        Template template = currentPage.getTemplate();
        if (template != null && template.hasStructureSupport()) {
            Resource templateResource = template.adaptTo(Resource.class);
            if (templateResource != null) {
                addDefaultTemplateEditorClientLib(templateResource, categories);
                addPolicyClientLibs(categories);
            }
        }
        clientLibCategories = categories.toArray(new String[categories.size()]);
    }

    protected void addDefaultTemplateEditorClientLib(Resource templateResource, List<String> categories) {
        if (currentPage.getPath().startsWith(templateResource.getPath())) {
            categories.add(DEFAULT_TEMPLATE_EDITOR_CLIENT_LIB);
        }
    }

    protected void addPolicyClientLibs(List<String> categories) {
        if (currentStyle != null) {
            Collections.addAll(categories, currentStyle.get(PN_CLIENTLIBS, ArrayUtils.EMPTY_STRING_ARRAY));
        }
    }
}
