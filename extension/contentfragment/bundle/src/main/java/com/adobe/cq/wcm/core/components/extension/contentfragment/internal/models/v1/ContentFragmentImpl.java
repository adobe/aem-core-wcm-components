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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.ContentFragment;
import com.day.text.Text;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import static com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v1.ContentFragmentImpl.RESOURCE_TYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static org.apache.sling.models.annotations.injectorspecific.InjectionStrategy.OPTIONAL;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ContentFragment.class, ComponentExporter.class}, resourceType = RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentFragmentImpl implements ContentFragment {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentImpl.class);

    /**
     * The resource type of the component associated with this Sling model.
     */
    public static final String RESOURCE_TYPE = "core/wcm/extension/components/contentfragment/v1/contentfragment";

    private static final String DEFAULT_GRID_TYPE = "dam/cfm/components/grid";

    /**
     * The name of the master variation.
     */
    private static final String MASTER_VARIATION = "master";

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private FragmentRenderService renderService;

    @Inject
    private ContentTypeConverter converter;

    @Inject
    private ModelFactory modelFactory;

    @ScriptVariable
    private ResourceResolver resolver;

    @ScriptVariable
    private Resource resource;

    @ValueMapValue(name = ContentFragment.PN_PATH, injectionStrategy = OPTIONAL)
    private String path;

    @ValueMapValue(name = ContentFragment.PN_ELEMENT_NAMES, injectionStrategy = OPTIONAL)
    private String[] elementNames;

    @ValueMapValue(name = ContentFragment.PN_VARIATION_NAME, injectionStrategy = OPTIONAL)
    private String variationName;

    private com.adobe.cq.dam.cfm.ContentFragment fragment;
    private String type;
    private List<Element> elements;
    private Map<String, ContentFragment.Element> exportedElements;
    private List<Resource> associatedContentList;
    private boolean isInitialized;

    @PostConstruct
    private void initialize() {
        if (!StringUtils.isEmpty(path)) {
            // get fragment resource
            Resource fragmentResource = resolver.getResource(path);
            if (fragmentResource != null) {
                // convert it to a content fragment
                fragment = fragmentResource.adaptTo(com.adobe.cq.dam.cfm.ContentFragment.class);
                if (fragment == null) {
                    LOG.error("Content Fragment can not be initialized because '{}' is not a content fragment.", path);
                }
            } else {
                LOG.error("Content Fragment can not be initialized because the '{}' does not exist.", path);
            }
        } else {
            LOG.warn("Please provide a path for the content fragment component.");
        }
    }

    private void initializeElements() {
        // get either all elements...
        Iterator<ContentElement> iterator = fragment.getElements();
        // ...or configured elements
        if (ArrayUtils.isNotEmpty(elementNames)) {
            List<ContentElement> elements = new LinkedList<>();
            for (String name : elementNames) {
                if (!fragment.hasElement(name)) {
                    // skip non-existing element
                    LOG.warn("Skipping non-existing element " + name);
                    continue;
                }
                elements.add(fragment.getElement(name));
            }
            iterator = elements.iterator();
        }

        // wrap elements and get their configured variation (if any)
        exportedElements = new LinkedHashMap<>();
        while (iterator.hasNext()) {
            ContentElement element = iterator.next();
            ContentVariation variation = null;
            if (StringUtils.isNotEmpty(variationName) && !MASTER_VARIATION.equals(variationName)) {
                variation = element.getVariation(variationName);
                if (variation == null) {
                    LOG.warn("Non-existing variation " + variationName + " of element " + element.getName());
                }
            }
            exportedElements.put(
                    element.getName(),
                    new ElementImpl(renderService, converter, resource, element, variation));
        }

        elements = new ArrayList<>(exportedElements.values());
        isInitialized = true;
    }

    @Nullable
    @Override
    public String getTitle() {
        if (fragment != null) {
            return fragment.getTitle();
        }
        return null;
    }

    @Nullable
    @Override
    public String getDescription() {
        if (fragment != null) {
            return fragment.getDescription();
        }
        return null;
    }

    @Nullable
    @Override
    public String getType() {
        if (type == null && fragment != null) {
            Resource fragmentResource = fragment.adaptTo(Resource.class);
            FragmentTemplate template = fragment.getTemplate();
            Resource templateResource = template.adaptTo(Resource.class);
            if (fragmentResource == null || templateResource == null) {
                LOG.warn("Unable to return type: fragment or template resource is null");
                type = fragment.getName();
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
        }
        return type;
    }

    @Nonnull
    @Override
    public String getGridResourceType() {
        String gridResourceType = DEFAULT_GRID_TYPE;
        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        ContentPolicy fragmentPolicy = policyManager != null ? policyManager.getPolicy(resource) : null;
        if (fragmentPolicy != null) {
            ValueMap props = fragmentPolicy.getProperties();
            gridResourceType = props.get("cfm-grid-type", DEFAULT_GRID_TYPE);
        }
        return gridResourceType;
    }

    @Nonnull
    @Override
    public String getEditorJSON() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("title", fragment.getTitle());
        jsonObjectBuilder.add("path", path);
        if (variationName != null) {
            jsonObjectBuilder.add("variation", variationName);
        }
        if (elementNames != null) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String ele : elementNames) {
                arrayBuilder.add(ele);
            }
            jsonObjectBuilder.add("elements", arrayBuilder);
        }
        Iterator<Resource> associatedContentIter = fragment.getAssociatedContent();
        if (associatedContentIter.hasNext()) {
            JsonArrayBuilder associatedContentArray = Json.createArrayBuilder();
            while (associatedContentIter.hasNext()) {
                Resource resource = associatedContentIter.next();
                ValueMap vm = resource.adaptTo(ValueMap.class);
                JsonObjectBuilder contentObject = Json.createObjectBuilder();
                if (vm!= null && vm.containsKey(JCR_TITLE)) {
                    contentObject.add("title", vm.get(JCR_TITLE, String.class));
                }
                contentObject.add("path", resource.getPath());
                associatedContentArray.add(contentObject);
            }
            jsonObjectBuilder.add("associatedContent", associatedContentArray);
        }
        return jsonObjectBuilder.build().toString();
    }

    @Nonnull
    @Override
    public Map<String, Element> getExportedElements() {
        if (!isInitialized && fragment != null) {
            initializeElements();
        }
        return (exportedElements != null ? exportedElements : Collections.emptyMap());
    }

    @Nonnull
    @Override
    public String[] getExportedElementsOrder() {
        Map<String, ContentFragment.Element> elements = getExportedElements();

        if (elements.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return elements.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Nullable
    @Override
    public List<Element> getElements() {
        if (!isInitialized && fragment != null)  {
            initializeElements();
        }
        return elements;
    }

    @Nullable
    @Override
    public List<Resource> getAssociatedContent() {
        if (fragment != null && associatedContentList == null) {
            associatedContentList = IteratorUtils.toList(fragment.getAssociatedContent());
        }
        return associatedContentList;
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    @Nonnull
    @Override
    public Map<String, ComponentExporter> getExportedItems() {
        Map<String, ComponentExporter> map = new LinkedHashMap<>();
        Iterator<Resource> children = resource.listChildren();

        while (children.hasNext()) {
            Resource child = children.next();
            ComponentExporter exporter =
                modelFactory.getModelFromWrappedRequest(request, child, ComponentExporter.class);

            if (exporter != null) {
                String name = child.getName();
                if (map.put(name, exporter) != null) {
                    throw new IllegalStateException("Duplicate key: " + name);
                }
            }

        }
        return map;
    }

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        Map<String, ComponentExporter> models = getExportedItems();

        if (models.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    static class ElementImpl implements Element {

        private FragmentRenderService renderService;
        private ContentTypeConverter converter;
        private Resource component;
        private ContentElement element;
        private ContentVariation variation;
        private String htmlValue;

        /**
         *
         * @param renderService the render service to use to render the HTML and paragraphs of text elements
         * @param converter the converter to use to convert the value of text elements to HTML
         * @param component the component resource
         * @param element the original element
         * @param variation the configured variation of the element, or {@code null}
         */
        ElementImpl(@Nonnull FragmentRenderService renderService, @Nonnull ContentTypeConverter converter,
                    @Nonnull Resource component, @Nonnull ContentElement element,
                    @Nullable ContentVariation variation) {
            this.renderService = renderService;
            this.converter = converter;
            this.component = component;
            this.element = element;
            this.variation = variation;
        }

        @Nonnull
        @Override
        public String getName() {
            return element.getName();
        }

        @Nullable
        @Override
        public String getTitle() {
            return element.getTitle();
        }

        private FragmentData getData() {
            if (variation != null) {
                return variation.getValue();
            }
            return element.getValue();
        }

        @Nonnull
        @Override
        public String getDataType() {
            return getData().getDataType().getTypeString();
        }

        @Nullable
        @Override
        public Object getValue() {
            return getData().getValue();
        }

        @Nonnull
        @Override
        public String getExportedType() {
            final FragmentData value = getData();
            // Mimetype for text-based datatypes
            String type = value.getContentType();

            // Datatype for non text-based datatypes
            if (type == null) {
                type = value.getDataType().getTypeString();
            }

            return type;
        }

        private String getContentType() {
            return getData().getContentType();
        }

        @Override
        public boolean isMultiLine() {
            String contentType = getContentType();
            // a text element is defined as a single-valued element with a certain content type (e.g. "text/plain",
            // "text/html", "text/x-markdown", potentially others)
            return contentType != null && contentType.startsWith("text/") && !getData().getDataType().isMultiValue();
        }

        @Nullable
        @Override
        public String getHtml() {
            // restrict this method to text elements
            if (!isMultiLine()) {
                return null;
            }

            String contentType = getContentType();
            String [] values = getData().getValue(String[].class);
            String value = null;
            if (values != null) {
                value = org.apache.commons.lang.StringUtils.join(values, ", ");
            }
            if ("text/html".equals(contentType)) {
                // return HTML as is
                return value;
            } else {
                try {
                    if (htmlValue == null) {
                        // convert element value to HTML
                        htmlValue = converter.convertToHTML(value, contentType);
                    }
                    return htmlValue;
                } catch (ContentFragmentException e) {
                    LOG.warn("Could not convert to HTML", e);
                    return null;
                }
            }
        }

        @Nullable
        @Override
        public String[] getParagraphs() {
            // restrict this method to text elements
            if (!isMultiLine()) {
                return null;
            }

            // render the fragment
            String content = renderService.render(component);
            if (content == null) {
                return null;
            }
            // split into paragraphs
            return content.split("(?=(<p>|<h1>|<h2>|<h3>|<h4>|<h5>|<h6>))");
        }
    }

}
