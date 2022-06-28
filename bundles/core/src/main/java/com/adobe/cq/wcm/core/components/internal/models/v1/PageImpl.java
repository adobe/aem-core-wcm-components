/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Page.class,
        ContainerExporter.class }, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends AbstractComponentImpl implements Page {

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
    @Nullable
    protected Style currentStyle;

    @ScriptVariable
    @JsonIgnore
    protected ResourceResolver resolver;

    @Inject
    private ModelFactory modelFactory;

    @Inject
    private SlingModelFilter slingModelFilter;

    @Self
    protected LinkManager linkManager;

    protected String[] keywords = new String[0];
    protected String designPath;
    protected String staticDesignPath;
    protected String title;
    protected String description;
    protected String brandSlug;

    protected String[] clientLibCategories = new String[0];
    protected Calendar lastModifiedDate;
    protected String templateName;

    protected static final String DEFAULT_TEMPLATE_EDITOR_CLIENTLIB = "wcm.foundation.components.parsys.allowedcomponents";
    protected static final String PN_CLIENTLIBS = "clientlibs";

    protected static final String PN_BRANDSLUG = "brandSlug";

    private Map<String, ComponentExporter> childModels = null;
    private String resourceType;
    private Set<String> resourceTypes;

    @JsonIgnore
    protected Map<String, String> favicons = new HashMap<>();

    @PostConstruct
    protected void initModel() {
        title = currentPage.getTitle();
        description = currentPage.getDescription();
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
        populateClientlibCategories();
        templateName = extractTemplateName();
        brandSlug = Utils.getInheritedValue(currentPage, PN_BRANDSLUG);
    }

    protected String extractTemplateName() {
        String templateName = null;
        String templatePath = pageProperties.get(NameConstants.PN_TEMPLATE, String.class);
        if (StringUtils.isNotEmpty(templatePath)) {
            int i = templatePath.lastIndexOf('/');
            if (i > 0) {
                templateName = templatePath.substring(i + 1);
            }
        }
        return templateName;
    }

    @Override
    public String getLanguage() {
        return currentPage == null ? Locale.getDefault().toLanguageTag()
                : currentPage.getLanguage(false).toLanguageTag();
    }

    @Override
    public Calendar getLastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = pageProperties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
        }
        return lastModifiedDate;
    }

    @Override
    @JsonIgnore
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
    @JsonIgnore
    @Deprecated
    public Map<String, String> getFavicons() {
        return favicons;
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
    public String getBrandSlug() {
		return brandSlug;
	}

	@Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    @JsonIgnore
    public String[] getClientLibCategories() {
        return Arrays.copyOf(clientLibCategories, clientLibCategories.length);
    }

    @Override
    @JsonIgnore
    public Set<String> getComponentsResourceTypes() {
        if (resourceTypes == null) {
            resourceTypes = Utils.getPageResourceTypes(currentPage, request, modelFactory);
        }
        return resourceTypes;
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        if (childModels == null) {
            childModels = getChildModels(request, ComponentExporter.class);
        }

        return childModels;
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        Map<String, ? extends ComponentExporter> models = getExportedItems();

        if (models.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);

    }

    @NotNull
    @Override
    public String getExportedType() {
        if (StringUtils.isEmpty(resourceType)) {
            resourceType = pageProperties.get(ResourceResolver.PROPERTY_RESOURCE_TYPE, String.class);
            if (StringUtils.isEmpty(resourceType)) {
                resourceType = currentPage.getContentResource().getResourceType();
            }
        }
        return resourceType;
    }

    /**
     * Returns a map (resource name => Sling Model class) of the given resource
     * children's Sling Models that can be adapted to {@link T}.
     *
     * @param slingRequest the current request
     * @param modelClass   the Sling Model class to be adapted to
     * @return a map (resource name => Sling Model class) of the given resource
     *         children's Sling Models that can be adapted to {@link T}
     */
    @NotNull
    private <T> Map<String, T> getChildModels(@NotNull SlingHttpServletRequest slingRequest,
            @NotNull Class<T> modelClass) {
        Map<String, T> itemWrappers = new LinkedHashMap<>();

        for (final Resource child : slingModelFilter.filterChildResources(request.getResource().getChildren())) {
            itemWrappers.put(child.getName(), modelFactory.getModelFromWrappedRequest(slingRequest, child, modelClass));
        }

        return itemWrappers;
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

    protected void populateClientlibCategories() {
        List<String> categories = new ArrayList<>();
        Template template = currentPage.getTemplate();
        if (template != null && template.hasStructureSupport()) {
            Resource templateResource = template.adaptTo(Resource.class);
            if (templateResource != null) {
                addDefaultTemplateEditorClientLib(templateResource, categories);
                addPolicyClientLibs(categories);
            }
        }
        clientLibCategories = categories.toArray(new String[0]);
    }

    protected void addDefaultTemplateEditorClientLib(Resource templateResource, List<String> categories) {
        if (currentPage.getPath().startsWith(templateResource.getPath())) {
            categories.add(DEFAULT_TEMPLATE_EDITOR_CLIENTLIB);
        }
    }

    protected void addPolicyClientLibs(List<String> categories) {
        if (currentStyle != null) {
            Collections.addAll(categories, currentStyle.get(PN_CLIENTLIBS, ArrayUtils.EMPTY_STRING_ARRAY));
        }
    }

    @Override
    @NotNull
    protected final PageData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asPage()
            .withTitle(this::getTitle)
            .withTags(() -> Arrays.copyOf(this.keywords, this.keywords.length))
            .withDescription(() -> this.pageProperties.get(NameConstants.PN_DESCRIPTION, String.class))
            .withTemplatePath(() -> this.currentPage.getTemplate().getPath())
            .withUrl(() -> linkManager.get(currentPage).build().getURL())
            .withLanguage(this::getLanguage)
            .build();
    }

}
