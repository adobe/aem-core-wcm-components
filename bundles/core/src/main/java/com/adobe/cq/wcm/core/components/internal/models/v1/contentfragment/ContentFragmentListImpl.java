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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;

import static com.day.cq.dam.api.DamConstants.NT_DAM_ASSET;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {
                ContentFragmentList.class,
                ComponentExporter.class
        },
        resourceType = {ContentFragmentListImpl.RESOURCE_TYPE_V1,ContentFragmentListImpl.RESOURCE_TYPE_V2}
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentFragmentListImpl extends AbstractComponentImpl implements ContentFragmentList {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentListImpl.class);

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/contentfragmentlist/v1/contentfragmentlist";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/contentfragmentlist/v2/contentfragmentlist";

    public static final String DEFAULT_DAM_PARENT_PATH = "/content/dam";

    public static final int DEFAULT_MAX_ITEMS = -1;

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    private ContentTypeConverter contentTypeConverter;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue(name = ContentFragmentList.PN_MODEL_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String modelPath;

    @ValueMapValue(name = ContentFragmentList.PN_ELEMENT_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String[] elementNames;

    @ValueMapValue(name = ContentFragmentList.PN_TAG_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String[] tagNames;

    @ValueMapValue(name = ContentFragmentList.PN_PARENT_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String parentPath;

    @ValueMapValue(name = ContentFragmentList.PN_MAX_ITEMS, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = DEFAULT_MAX_ITEMS)
    private int maxItems;

    @ValueMapValue(name = ContentFragmentList.PN_ORDER_BY, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = JcrConstants.JCR_CREATED)
    private String orderBy;

    @ValueMapValue(name = ContentFragmentList.PN_SORT_ORDER, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = Predicate.SORT_ASCENDING)
    private String sortOrder;

    private final List<DAMContentFragment> items = new ArrayList<>();

    @PostConstruct
    private void initModel() {
        // Default path limits search to DAM
        if (StringUtils.isEmpty(parentPath)) {
            parentPath = DEFAULT_DAM_PARENT_PATH;
        }

        if (StringUtils.isEmpty(modelPath)) {
            LOG.warn("Please provide a model path");
            return;
        }

        Session session = resourceResolver.adaptTo(Session.class);
        if (session == null) {
            LOG.warn("Session was null therefore no query was executed");
            return;
        }

        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        if (queryBuilder == null) {
            LOG.warn("Query builder was null therefore no query was executed");
            return;
        }

        Map<String, String> queryParameterMap = new HashMap<>();
        queryParameterMap.put("path", parentPath);
        queryParameterMap.put("type", NT_DAM_ASSET);
        queryParameterMap.put("p.limit", Integer.toString(maxItems));
        queryParameterMap.put("1_property", JcrConstants.JCR_CONTENT + "/data/cq:model");
        queryParameterMap.put("1_property.value", modelPath);

        if (StringUtils.isNotEmpty(orderBy)) {
            queryParameterMap.put("orderby", "@" + orderBy);
            if (StringUtils.isNotEmpty(sortOrder)) {
                queryParameterMap.put("orderby.sort", sortOrder);
            }
        }

        ArrayList<String> allTags = new ArrayList<>();
        if (tagNames != null && tagNames.length > 0) {
            allTags.addAll(Arrays.asList(tagNames));
        }

        if (!allTags.isEmpty()) {
            // Check for the taggable mixin
            queryParameterMap.put("2_property", JcrConstants.JCR_CONTENT + "/metadata/" + JcrConstants.JCR_MIXINTYPES);
            queryParameterMap.put("2_property.value", TagConstants.NT_TAGGABLE);
            // Check for the actual tags (by default, tag are or'ed)
            queryParameterMap.put("tagid.property", JcrConstants.JCR_CONTENT + "/metadata/cq:tags");
            for (int i = 0; i < allTags.size(); i++) {
                queryParameterMap.put(String.format("tagid.%d_value", i + 1), allTags.get(i));
            }
        }

        PredicateGroup predicateGroup = PredicateGroup.create(queryParameterMap);
        Query query = queryBuilder.createQuery(predicateGroup, session);

        SearchResult searchResult = query.getResult();

        LOG.debug("Query statement: '{}'", searchResult.getQueryStatement());

        // Query builder has a leaking resource resolver, so the following work around is required.
        ResourceResolver leakingResourceResolver = null;
        try {
            // Iterate over the hits if you need special information
            Iterator<Resource> resourceIterator = searchResult.getResources();
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();
                if (leakingResourceResolver == null) {
                    // Get a reference to QB's leaking resource resolver
                    leakingResourceResolver = resource.getResourceResolver();
                }

                DAMContentFragment contentFragmentModel = new DAMContentFragmentImpl(
                        resource, contentTypeConverter, null, elementNames);

                items.add(contentFragmentModel);
            }
        } finally {
            if (leakingResourceResolver != null) {
                // Always close the leaking query builder resource resolver
                leakingResourceResolver.close();
            }
        }
    }

    @NotNull
    @Override
    public Collection<DAMContentFragment> getListItems() {
        return Collections.unmodifiableCollection(items);
    }

    @NotNull
    @Override
    public String getExportedType() {
        return slingHttpServletRequest.getResource().getResourceType();
    }
}
