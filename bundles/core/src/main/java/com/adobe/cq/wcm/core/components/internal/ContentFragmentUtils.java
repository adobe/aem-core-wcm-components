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
package com.adobe.cq.wcm.core.components.internal;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.export.json.ComponentExporter;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.text.Text;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

/**
 * Utilities to ease the work with {@link ContentFragment content fragments}.
 */
public class ContentFragmentUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentUtils.class);

    /**
     * The default grid type.
     */
    protected static final String DEFAULT_GRID_TYPE = "dam/cfm/components/grid";

    /**
     * Name of the property of an optional {@link ContentPolicy content policy} holding the name of the grid type.
     */
    protected static final String PN_CFM_GRID_TYPE = "cfm-grid-type";

    /**
     * Name of request attribute that holds information about the calling resource path (used for JSON export when
     * including other components via XFs).
     */
    public final static String ATTR_RESOURCE_CALLER_PATH = "resourceCallerPath";

    /* Hide the constructor of utility classes */
    private ContentFragmentUtils() {
    }

    /**
     * Returns the type of a {@link ContentFragment content fragment}. The type is a string that uniquely identifies the
     * model or template of the content fragment (CF) (e.g. <code>my-project/models/my-model</code> for a structured CF
     * or <code>/content/dam/my-cf/jcr:content/model</code> for a text-only CF).
     *
     * @param contentFragment the content fragment
     * @return the type of the content fragment
     */
    public static String getType(ContentFragment contentFragment) {
        String type = "";
        if (contentFragment == null) {
            return type;
        }

        Resource fragmentResource = contentFragment.adaptTo(Resource.class);
        FragmentTemplate fragmentTemplate = contentFragment.getTemplate();
        if (fragmentTemplate == null) {
            return type;
        }
        Resource templateResource = fragmentTemplate.adaptTo(Resource.class);
        if (fragmentResource == null || templateResource == null) {
            LOG.warn("Unable to return type: fragment or template resource is null");
            type = contentFragment.getName();
        } else {
            // use the parent if the template resource is the jcr:content child
            Resource parent = templateResource.getParent();
            if (JCR_CONTENT.equals(templateResource.getName()) && parent != null) {
                templateResource = parent;
            }
            // get data node to check if this is a text-only or structured content fragment
            Resource data = fragmentResource.getChild(JCR_CONTENT + "/data");
            if (data == null || data.getValueMap().get("cq:model") == null) {
                // this is a text-only content fragment, for which we use the model path as the type
                type = templateResource.getPath();
            } else {
                // this is a structured content fragment, assemble type string (e.g. "my-project/models/my-model" or
                // "my-project/nested/models/my-model")
                StringBuilder prefix = new StringBuilder();
                String[] segments = Text.explode(templateResource.getPath(), '/', false);
                // get the configuration names (e.g. for "my-project/" or "my-project/nested/")
                for (int i = 1; i < segments.length - 5; i++) {
                    prefix.append(segments[i]);
                    prefix.append("/");
                }
                type = prefix.toString() + "models/" + templateResource.getName();
            }
        }

        return type;
    }


    /**
     * Filters {@link ContentElement content elements} by their name.
     *
     * <p><b>Note:</b> The natural order of the elements in the content fragment will not be maintained. Instead
     * elements will be order as provided in the filter.</p>
     *
     * @param contentFragment the content fragment
     * @param elementNames    the names of the elements to filter
     * @return all content elements found matching the filter
     */
    public static Iterator<ContentElement> filterElements(final ContentFragment contentFragment,
                                                          final String[] elementNames) {

        if (ArrayUtils.isNotEmpty(elementNames)) {
            List<ContentElement> elements = new LinkedList<>();
            for (String name : elementNames) {
                if (!contentFragment.hasElement(name)) {
                    // skip non-existing element
                    LOG.warn("Skipping non-existing element '{}'", name);
                    continue;
                }
                elements.add(contentFragment.getElement(name));
            }
            return elements.iterator();
        }
        return contentFragment.getElements();
    }

    /**
     * Returns a JSON representation of given content fragment taking optional element names and a variation name into
     * account.
     *
     * @param contentFragment The content fragment
     * @param variationName   An optional variation name
     * @param elementNames    Optional element names
     * @return A JSON representation of the content fragment
     */
    public static String getEditorJSON(final ContentFragment contentFragment,
                                       final String variationName,
                                       final String[] elementNames) {

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("title", contentFragment.getTitle());

        Resource contentFragmentResource = contentFragment.adaptTo(Resource.class);
        if (contentFragmentResource != null) {
            jsonObjectBuilder.add("path", contentFragmentResource.getPath());
        }

        if (variationName != null) {
            jsonObjectBuilder.add("variation", variationName);
        }

        if (elementNames != null) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String elementName : elementNames) {
                arrayBuilder.add(elementName);
            }
            jsonObjectBuilder.add("elements", arrayBuilder);
        }

        Iterator<Resource> associatedContentIterator = contentFragment.getAssociatedContent();
        if (associatedContentIterator.hasNext()) {
            JsonArrayBuilder associatedContentArray = Json.createArrayBuilder();
            while (associatedContentIterator.hasNext()) {
                Resource resource = associatedContentIterator.next();
                ValueMap valueMap = resource.adaptTo(ValueMap.class);
                JsonObjectBuilder contentObject = Json.createObjectBuilder();
                if (valueMap != null && valueMap.containsKey(JCR_TITLE)) {
                    contentObject.add("title", valueMap.get(JCR_TITLE, String.class));
                }
                contentObject.add("path", resource.getPath());
                associatedContentArray.add(contentObject);
            }
            jsonObjectBuilder.add("associatedContent", associatedContentArray);
        }

        return jsonObjectBuilder.build().toString();
    }

    /**
     * Returns an optional grid type configured via {@link ContentPolicy content policy} property
     * ({@value #PN_CFM_GRID_TYPE}) or {@value #DEFAULT_GRID_TYPE} as default.
     *
     * @param fragmentResource the content fragment resource resource to be checked
     * @return the configured grid type of a default
     */
    public static String getGridResourceType(Resource fragmentResource) {
        String gridResourceType = DEFAULT_GRID_TYPE;
        if (fragmentResource == null) {
            return gridResourceType;
        }
        ResourceResolver resourceResolver = fragmentResource.getResourceResolver();
        ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
        ContentPolicy fragmentContentPolicy = contentPolicyManager != null ? contentPolicyManager.getPolicy(fragmentResource) : null;
        if (fragmentContentPolicy != null) {
            ValueMap contentPolicyProperties = fragmentContentPolicy.getProperties();
            gridResourceType = contentPolicyProperties.get(PN_CFM_GRID_TYPE, DEFAULT_GRID_TYPE);
        }
        return gridResourceType;
    }

    /**
     * Returns an array with all item keys in order of given map.
     *
     * @param itemsMap a map of items
     * @return an array with all keys in order of given map
     */
    public static String[] getItemsOrder(Map<String, ?> itemsMap) {
        if (itemsMap == null || itemsMap.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return itemsMap.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Gets a map of all resources with the resource name as the key and the corresponding {@link ComponentExporter}
     * model as the value.
     *
     * The returned map is ordered as provided by the resourceIterator.
     * Any resource that cannot be adapted to a {@link ComponentExporter} model is omitted from the returned map.
     *
     * @param resourceIterator Iterator of resources for which to get the component exporters.
     * @param modelFactory Model factory service.
     * @param slingHttpServletRequest Current request.
     * @param callerResource The page or template resource that references the experience fragment or content fragment.
     * @return Ordered map of resource names to {@link ComponentExporter} models.
     */
    @NotNull
    public static LinkedHashMap<String, ComponentExporter> getComponentExporters(
        @NotNull final Iterator<Resource> resourceIterator,
        @NotNull final ModelFactory modelFactory,
        @NotNull final SlingHttpServletRequest slingHttpServletRequest,
        @NotNull final Resource callerResource) {
        final LinkedHashMap<String, ComponentExporter> componentExporterMap = new LinkedHashMap<>();

        SlingHttpServletRequest wrappedSlingHttpServletRequest = new SlingHttpServletRequestWrapper(slingHttpServletRequest) {

            @Override
            public Object getAttribute(String name) {
                if (ATTR_RESOURCE_CALLER_PATH.equals(name)) {
                    String resourceCallerPath = (String)super.getAttribute(ATTR_RESOURCE_CALLER_PATH);
                    // If the attribute is already defined then we're in a nested situation.
                    // The code for computing the components id uses the root-most resource path
                    // (because of how componentContext is handled in HTML rendering, so we return
                    // that.
                    return (resourceCallerPath != null) ? resourceCallerPath : callerResource.getPath();
                }
                return super.getAttribute(name);
            }
        };

        while (resourceIterator.hasNext()) {
            Resource resource = resourceIterator.next();

            if (!(resource instanceof TemplatedResource)) {
                resource = Optional.ofNullable((Resource) resource.adaptTo(TemplatedResource.class))
                                .orElse(resource);
            }

            ComponentExporter exporter = modelFactory.getModelFromWrappedRequest(wrappedSlingHttpServletRequest, resource, ComponentExporter.class);

            if (exporter != null) {
                String name = resource.getName();
                if (componentExporterMap.put(name, exporter) != null) {
                    throw new IllegalStateException(String.format("Duplicate key '%s'", name));
                }
            }
        }

        return componentExporterMap;
    }
}
