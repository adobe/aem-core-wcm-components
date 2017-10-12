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
package com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.internal.models.v1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models.ContentFragment;
import com.day.text.Text;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.internal.models.v1.ContentFragmentImpl.RESOURCE_TYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static org.apache.sling.models.annotations.injectorspecific.InjectionStrategy.OPTIONAL;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ContentFragment.class, ComponentExporter.class}, resourceType = RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonSerialize(as = ContentFragment.class)
public class ContentFragmentImpl implements ContentFragment {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentImpl.class);

    /**
     * The resource type of the component associated with this Sling model.
     */
    public static final String RESOURCE_TYPE = "core/wcm/extension/sandbox/components/contentfragment/v1/contentfragment";

    @ScriptVariable
    private ResourceResolver resolver;

    @ValueMapValue(name = ContentFragment.PN_PATH, injectionStrategy = OPTIONAL)
    private String path;

    @ValueMapValue(name = ContentFragment.PN_ELEMENT_NAMES, injectionStrategy = OPTIONAL)
    private String[] elementNames;

    private com.adobe.cq.dam.cfm.ContentFragment fragment;
    private String type;
    private List<Element> elements;

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

    @Nonnull
    @Override
    public String getType() {
        if (type == null) {
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
                        prefix.append(segments[i] + "/");
                    }
                    type = prefix.toString() + "models/" + templateResource.getName();
                }
            }
        }
        return type;
    }

    @Nonnull
    @Override
    public List<Element> getElements() {
        if (elements == null) {
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

            // wrap and return elements
            elements = new LinkedList<>();
            while (iterator.hasNext()) {
                ContentElement element = iterator.next();
                elements.add(new ElementImpl(element));
            }
        }
        return elements;
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    @Nonnull
    @Override
    public Map<String, ComponentExporter> getExportedItems() {
        Map<String, ComponentExporter> map = new HashMap<>();
        for (Element e : getElements()) {
            if (map.put(e.getName(), (ComponentExporter) e) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        Map<String, ? extends ComponentExporter> models = getExportedItems();

        if (models.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private static class ElementImpl implements Element {

        private ContentElement element;

        ElementImpl(ContentElement element) {
            this.element = element;
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
            return element.getValue();
        }

        @Override
        public boolean isMultiValued() {
            return getData().getDataType().isMultiValue();
        }

        @Nullable
        @Override
        public String getContentType() {
            return getData().getContentType();
        }

        @Nullable
        @Override
        public Object getValue() {
            return getData().getValue();
        }

        @Nullable
        @Override
        public String getDisplayValue() {
            String[] values = getDisplayValues();
            if (values == null) {
                return null;
            }
            return StringUtils.join(values, ", ");
        }

        @Nullable
        @Override
        public String[] getDisplayValues() {
            return getData().getValue(String[].class);
        }

        @Nonnull
        @Override
        public String getExportedType() {
            final FragmentData value = element.getValue();
            // Mimetype for text-based datatypes
            String type = value.getContentType();

            // Datatype for non text-based datatypes
            if (type == null) {
                type = value.getDataType().getTypeString();
            }

            return type;
        }

    }

}
