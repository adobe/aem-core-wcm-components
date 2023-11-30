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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
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

    /**
     * The default logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentListImpl.class);

    /**
     * Resource type for V1 component.
     */
    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/contentfragmentlist/v1/contentfragmentlist";

    /**
     * Resource type for V2 component.
     */
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/contentfragmentlist/v2/contentfragmentlist";

    /**
     * Root path of the DAM.
     */
    public static final String DEFAULT_DAM_PARENT_PATH = "/content/dam";

    /**
     * Default maximum number of items to return (-1 means all).
     */
    public static final int DEFAULT_MAX_ITEMS = -1;

    /**
     * Default tag match requirement.
     */
    private static final String TAGS_MATCH_ANY_VALUE = "any";

    /**
     * The request.
     */
    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;

    /**
     * Content type converter service.
     */
    @Inject
    private ContentTypeConverter contentTypeConverter;

    /**
     * List of content fragment items.
     */
    private final List<DAMContentFragment> items = new ArrayList<>();

    @PostConstruct
    private void initModel() {
        ResourceResolver resourceResolver = this.request.getResourceResolver();
        ValueMap properties = this.request.getResource().getValueMap();

        String modelPath = properties.get(ContentFragmentList.PN_MODEL_PATH, String.class);
        if (StringUtils.isBlank(modelPath)) {
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
        queryParameterMap.put("path", Optional.ofNullable(properties.get(ContentFragmentList.PN_PARENT_PATH, String.class))
            .filter(StringUtils::isNotEmpty)
            .orElse(DEFAULT_DAM_PARENT_PATH));
        queryParameterMap.put("type", NT_DAM_ASSET);
        queryParameterMap.put("p.limit", Integer.toString(properties.get(ContentFragmentList.PN_MAX_ITEMS, DEFAULT_MAX_ITEMS)));
        queryParameterMap.put("1_property", JcrConstants.JCR_CONTENT + "/data/cq:model");
        queryParameterMap.put("1_property.value", modelPath);

        queryParameterMap.put("orderby", "@" + Optional.ofNullable(properties.get(ContentFragmentList.PN_ORDER_BY, String.class))
            .filter(StringUtils::isNotBlank)
            .orElse(JcrConstants.JCR_CREATED));
        queryParameterMap.put("orderby.sort", Optional.ofNullable(properties.get(ContentFragmentList.PN_SORT_ORDER, String.class))
            .filter(StringUtils::isNotBlank)
            .orElse(Predicate.SORT_ASCENDING));


        List<String> allTags = Optional.ofNullable(properties.get(ContentFragmentList.PN_TAG_NAMES, String[].class))
            .filter(array -> array.length > 0)
            .map(Arrays::asList)
            .orElseGet(Collections::emptyList);

        if (!allTags.isEmpty()) {
            // Check for the taggable mixin
            queryParameterMap.put("2_property", JcrConstants.JCR_CONTENT + "/metadata/" + JcrConstants.JCR_MIXINTYPES);
            queryParameterMap.put("2_property.value", TagConstants.NT_TAGGABLE);
            queryParameterMap.put("tagid.property", JcrConstants.JCR_CONTENT + "/metadata/cq:tags");
            queryParameterMap.put("tagid.and", Boolean.toString(!properties.get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE)));
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

            String[] elementNames = properties.get(ContentFragmentList.PN_ELEMENT_NAMES, String[].class);

            // Iterate over the hits if you need special information
            Iterator<Resource> resourceIterator = searchResult.getResources();
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();
                if (leakingResourceResolver == null) {
                    // Get a reference to QB's leaking resource resolver
                    leakingResourceResolver = resource.getResourceResolver();
                }


                // re-resolve the resource so that no references to the leaking resource resolver are retained
                Resource currentSessionResource = resourceResolver.getResource(resource.getPath());
                if (currentSessionResource != null) {
                    DAMContentFragment contentFragmentModel = new DAMContentFragmentImpl(
                        resource, contentTypeConverter, null, elementNames);
                    items.add(contentFragmentModel);
                }
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
