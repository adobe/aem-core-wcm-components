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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.DataType;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.dam.cfm.VariationDef;
import com.day.cq.commons.jcr.JcrConstants;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DATA;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_MIMETYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.dam.api.DamConstants.NT_DAM_ASSET;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Adapts resources to {@link ContentFragment} objects by mocking parts of their API.
 */
public class ContentFragmentMockAdapter implements Function<Resource, ContentFragment> {

    private final String PATH_DATA = JCR_CONTENT + "/data";
    private final String PATH_MASTER = PATH_DATA + "/master";
    private final String PATH_MODEL = JCR_CONTENT + "/model";
    private final String PATH_MODEL_ELEMENTS = PATH_MODEL + "/elements";
    private final String PATH_MODEL_VARIATIONS = PATH_MODEL + "/variations";
    private final String PATH_MODEL_DIALOG_ITEMS = JCR_CONTENT + "/model/cq:dialog/content/items";
    private final String PATH_ASSOCIATED_CONTENT = JCR_CONTENT + "/associated/sling:members";
    private final String PN_CONTENT_FRAGMENT = "contentFragment";
    private final String PN_MODEL = "cq:model";
    private final String PN_ELEMENT_NAME = "name";
    private final String PN_ELEMENT_TITLE = "fieldLabel";
    private final String PN_VALUE_TYPE = "valueType";
    private final String MAIN_ELEMENT = "main";

    @Nullable
    @Override
    public ContentFragment apply(@Nullable Resource resource) {
        // check if the resource is valid and an asset
        if (resource == null || !resource.isResourceType(NT_DAM_ASSET)) {
            return null;
        }

        // check if the resource is a content fragment
        Resource content = resource.getChild(JCR_CONTENT);
        ValueMap contentProperties = content.getValueMap();
        if (!contentProperties.get(PN_CONTENT_FRAGMENT, Boolean.FALSE)) {
            return null;
        }

        // check if the content fragment is text-only or structured
        Resource data = resource.getChild(PATH_DATA);
        boolean isStructured = data != null;

        /* get content fragment properties, model and elements */

        String title = contentProperties.get(JCR_TITLE, String.class);
        String description = contentProperties.get(JCR_DESCRIPTION, String.class);
        String cfName = resource.getName();
        Resource model;
        Resource modelAdaptee;
        List<ContentElement> elements = new LinkedList<>();

        if (isStructured) {
            // get the model (referenced in the property)
            model = resource.getResourceResolver().getResource(data.getValueMap().get(PN_MODEL, String.class));
            // for the 'adaptTo' mock below we use the jcr:content child to mimick the real behavior
            modelAdaptee = model.getChild(JCR_CONTENT);
            // create an element mock for each property on the master node
            Resource master = resource.getChild(PATH_MASTER);
            for (String name : master.getValueMap().keySet()) {
                // skip the primary type and content type properties
                if (JcrConstants.JCR_PRIMARYTYPE.equals(name) || name.endsWith("@ContentType")) {
                    continue;
                }
                elements.add(getMockElement(resource, name, model));
            }
        } else {
            // get the model (stored in the fragment itself)
            model = resource.getChild(PATH_MODEL);
            modelAdaptee = model;
            // add the "main" element to the list
            elements.add(getMockElement(resource, null, null));
            // create an element mock for each subasset
            Resource subassets = resource.getChild("subassets");
            if (subassets != null) {
                for (Resource subasset : subassets.getChildren()) {
                    elements.add(getMockElement(resource, subasset.getName(), null));
                }
            }
        }

        /* create mock objects */

        ContentFragment fragment = mock(ContentFragment.class, withSettings().lenient());
        when(fragment.getTitle()).thenReturn(title);
        when(fragment.getDescription()).thenReturn(description);
        when(fragment.getName()).thenReturn(cfName);
        when(fragment.adaptTo(Resource.class)).thenReturn(resource);
        when(fragment.getElement(isNull())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return getMockElement(resource, name, isStructured ? model : null);
        });
        when(fragment.getElement(any(String.class))).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return getMockElement(resource, name, isStructured ? model : null);
        });
        when(fragment.hasElement(any(String.class))).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return fragment.getElement(name) != null;
        });
        when(fragment.getElements()).thenReturn(elements.iterator());

        List<VariationDef> variations = new LinkedList<>();
        ContentElement main = fragment.getElement(null);
        Iterator<ContentVariation> iterator = main.getVariations();
        while (iterator.hasNext()) {
            ContentVariation variation = iterator.next();
            variations.add(new VariationDef() {
                @Override
                public String getName() {
                    return variation.getName();
                }

                @Override
                public String getTitle() {
                    return variation.getTitle();
                }

                @Override
                public String getDescription() {
                    return variation.getDescription();
                }
            });
        }
        when(fragment.listAllVariations()).thenReturn(variations.iterator());

        FragmentTemplate template = mock(FragmentTemplate.class, withSettings().lenient());
        when(template.adaptTo(Resource.class)).thenReturn(modelAdaptee);
        when(fragment.getTemplate()).thenReturn(template);

        Iterator<Resource> associatedContent = getAssociatedContent(resource);
        when(fragment.getAssociatedContent()).thenReturn(associatedContent);

        return fragment;
    }

    /**
     * Creates a mock of a content element for a text-only (if {@code model} is {@code null}) or structured
     * (if {@code model} is not {@code null}) content fragment.
     */
    private ContentElement getMockElement(Resource resource, String name, Resource model) {
        // get the respective element
        MockElement element;
        if (model == null) {
            element = getTextOnlyElement(resource, name);
        } else {
            element = getStructuredElement(resource, model, name);
        }
        if (element == null) {
            return null;
        }

        /* create mock objects */

        // mock data type
        DataType dataType = mock(DataType.class, withSettings().lenient());
        when(dataType.isMultiValue()).thenReturn(element.isMultiValued);
        when(dataType.getTypeString()).thenReturn(element.typeString);

        // mock fragment data
        FragmentData data = mock(FragmentData.class, withSettings().lenient());
        when(data.getValue()).thenReturn(element.isMultiValued ? element.values : element.values[0]);
        when(data.getValue(String.class)).thenReturn(element.values[0]);
        when(data.getValue(String[].class)).thenReturn(element.values);
        when(data.getContentType()).thenReturn(element.contentType);
        when(data.getDataType()).thenReturn(dataType);

        // mock content element
        ContentElement contentElement = mock(ContentElement.class, withSettings().lenient());
        when(contentElement.getName()).thenReturn(element.name);
        when(contentElement.getTitle()).thenReturn(element.title);
        when(contentElement.getContent()).thenReturn(element.values[0]);
        when(contentElement.getContentType()).thenReturn(element.contentType);
        when(contentElement.getValue()).thenReturn(data);

        // mock variations
        Map<String, ContentVariation> variations = new LinkedHashMap<>();
        for (MockVariation variation : element.variations.values()) {
            FragmentData variationData = mock(FragmentData.class, withSettings().lenient());
            when(variationData.getValue()).thenReturn(element.isMultiValued ? variation.values : variation.values[0]);
            when(variationData.getValue(String.class)).thenReturn(variation.values[0]);
            when(variationData.getValue(String[].class)).thenReturn(variation.values);
            when(variationData.getContentType()).thenReturn(variation.contentType);
            when(variationData.getDataType()).thenReturn(dataType);

            ContentVariation contentVariation = mock(ContentVariation.class, withSettings().lenient());
            when(contentVariation.getName()).thenReturn(variation.name);
            when(contentVariation.getTitle()).thenReturn(variation.title);
            when(contentVariation.getContent()).thenReturn(variation.values[0]);
            when(contentVariation.getContentType()).thenReturn(variation.contentType);
            when(contentVariation.getValue()).thenReturn(variationData);
            variations.put(variation.name, contentVariation);
        }
        when(contentElement.getVariations()).thenReturn(variations.values().iterator());
        when(contentElement.getVariation(any(String.class))).thenAnswer(invocation -> {
            String variationName = invocation.getArgument(0);
            return variations.get(variationName);
        });

        return contentElement;
    }

    /**
     * Collects and returns the information of a content element for text-only content fragment.
     */
    private MockElement getTextOnlyElement(Resource resource, String name) {
        MockElement element = new MockElement();
        // text-only elements are never multi-valued
        element.isMultiValued = false;
        // if the name is null we use the main element
        element.name = name == null ? MAIN_ELEMENT : name;

        // loop through element definitions in the model and find the matching one
        boolean found = false;
        Resource elements = resource.getChild(PATH_MODEL_ELEMENTS);
        for (Resource elementResource : elements.getChildren()) {
            ValueMap properties = elementResource.getValueMap();
            if (element.name.equals(properties.get(PN_ELEMENT_NAME))) {
                // set the element title
                element.title = properties.get(JCR_TITLE, String.class);
                element.typeString = properties.get(PN_VALUE_TYPE, String.class);
                found = true;
                break;
            }
        }
        // return if we didn't find an element with the given name
        if (!found) {
            return null;
        }

        try {
            // get path to the asset resource (main element or correct subasset)
            String path = MAIN_ELEMENT.equals(element.name) ? "" : "subassets/" + element.name + "/";
            Resource renditions = resource.getChild(path + JCR_CONTENT + "/renditions");
            // loop over the renditions (i.e. variations)
            for (Resource rendition : renditions.getChildren()) {
                // get content and content type
                ValueMap properties = rendition.getChild(JCR_CONTENT).getValueMap();
                String content = IOUtils.toString(properties.get(JCR_DATA, InputStream.class), UTF_8);
                String contentType = properties.get(JCR_MIMETYPE, String.class);

                // get variation definition from model
                Resource variation = resource.getChild(PATH_MODEL_VARIATIONS + "/" + rendition.getName());
                if (variation != null) {
                    String title = variation.getValueMap().get(JCR_TITLE, String.class);
                    element.addVariation(rendition.getName(), title, contentType, new String[]{content},
                            true, content);
                } else {
                    element.values = new String[]{content};
                    element.contentType = contentType;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return element;
    }

    /**
     * Collects and returns the information of a content element for structured content fragment.
     */
    private MockElement getStructuredElement(Resource resource, Resource model, String name) {
        MockElement element = new MockElement();
        element.name = name;

        // loop through element definitions in the model and find the matching one (or first one, if name is null)
        boolean found = false;
        Resource items = model.getChild(PATH_MODEL_DIALOG_ITEMS);
        for (Resource item : items.getChildren()) {
            ValueMap properties = item.getValueMap();
            String elementName = properties.get(PN_ELEMENT_NAME, String.class);
            if (element.name == null || element.name.equals(elementName)) {
                // set the element name (in case it was null)
                element.name = elementName;
                // set the element title
                element.title = properties.get(PN_ELEMENT_TITLE, String.class);
                // determine if the element is multi-valued (if the value type is e.g. "string[]")
                element.isMultiValued = properties.get(PN_VALUE_TYPE, "").endsWith("[]");
                element.typeString = properties.get(PN_VALUE_TYPE, String.class);
                found = true;
                break;
            }
        }
        // return if we didn't find an element with the given name
        if (!found) {
            return null;
        }

        // loop over the data nodes
        for (Resource data : resource.getChild(PATH_DATA).getChildren()) {
            ValueMap properties = data.getValueMap();
            String[] values = properties.get(element.name, String[].class);
            String contentType = properties.get(element.name + "@ContentType", String.class);
            if ("master".equals(data.getName())) {
                element.values = values;
                element.contentType = contentType;
                element.typeString = properties.get(PN_VALUE_TYPE, String.class);
            } else {
                properties = resource.getChild(PATH_MODEL_VARIATIONS + "/" + data.getName()).getValueMap();
                String title = properties.get(JCR_TITLE, String.class);
                element.addVariation(data.getName(), title, contentType, values, true, values[0]);
            }
        }

        return element;
    }

    /**
     * Returns a list of resources representing the associated content for a content fragment.
     */
    private Iterator<Resource> getAssociatedContent(Resource resource) {
        List<Resource> associatedContent = new LinkedList<>();
        ResourceResolver resolver = resource.getResourceResolver();
        Resource members = resource.getChild(PATH_ASSOCIATED_CONTENT);
        if (resource != null) {
            String[] paths = members.getValueMap().get("sling:resources", String[].class);
            if (paths != null) {
                for (String path : paths) {
                    associatedContent.add(resolver.getResource(path));
                }
            }
        }
        return associatedContent.iterator();
    }
}
