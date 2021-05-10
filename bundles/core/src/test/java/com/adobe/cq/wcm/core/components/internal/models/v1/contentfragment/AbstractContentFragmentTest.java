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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.search.QueryBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;

import static org.mockito.Mockito.mock;

public abstract class AbstractContentFragmentTest<T> {

    private static final String CONTEXT_PATH = "/core";

    /* names of the content fragment component instances to test */

    static final String CF_TEXT_ONLY_NO_PATH = "text-only-no-path";
    static final String CF_TEXT_ONLY_NON_EXISTING_PATH = "text-only-non-existing-path";
    static final String CF_TEXT_ONLY_INVALID_PATH = "text-only-invalid-path";
    static final String CF_TEXT_ONLY = "text-only";
    static final String CF_TEXT_ONLY_VARIATION = "text-only-variation";
    static final String CF_TEXT_ONLY_NON_EXISTING_VARIATION = "text-only-non-existing-variation";
    static final String CF_TEXT_ONLY_SINGLE_ELEMENT = "text-only-single-element";
    static final String CF_TEXT_ONLY_MULTIPLE_ELEMENTS = "text-only-multiple-elements";

    static final String CF_STRUCTURED_NO_PATH = "structured-no-path";
    static final String CF_STRUCTURED_NON_EXISTING_PATH = "structured-non-existing-path";
    static final String CF_STRUCTURED_INVALID_PATH = "structured-invalid-path";
    static final String CF_STRUCTURED = "structured";
    static final String CF_STRUCTURED_VARIATION = "structured-variation";
    static final String CF_STRUCTURED_NON_EXISTING_VARIATION = "structured-non-existing-variation";
    static final String CF_STRUCTURED_NESTED_MODEL = "structured-nested-model";
    static final String CF_STRUCTURED_SINGLE_ELEMENT = "structured-single-element";
    static final String CF_STRUCTURED_SINGLE_ELEMENT_MAIN = "structured-single-element-main";
    static final String CF_STRUCTURED_MULTIPLE_ELEMENTS = "structured-multiple-elements";

    /* contents of the text-only and structured content fragments referenced by the above components */

    static final String TITLE = "Test Content Fragment";
    static final String DESCRIPTION = "This is a test content fragment.";
    static final String TEXT_ONLY_TYPE = "/content/dam/contentfragments/text-only/jcr:content/model";
    static final String TEXT_ONLY_NAME = "text-only";
    static final String STRUCTURED_TYPE = "global/models/test";
    static final String STRUCTURED_NAME = "structured";
    static final String STRUCTURED_TYPE_NESTED = "global/nested/models/test";
    static final String STRUCTURED_NESTED_NAME = "structured-nested-model";
    static final String[] ASSOCIATED_CONTENT = new String[]{"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test"};

    static final MockElement MAIN = new MockElement("main", "Main", "text/html",
        "<p>Main content</p>", true, "<p>Main content</p>");
    static final MockElement SECOND_TEXT_ONLY = new MockElement("second", "Second", "text/plain", "Second content",
        true, null);
    static final MockElement SECOND_STRUCTURED = new MockElement("second", "Second", null, new String[]{"one", "two", "three"},
        false, null);

    static final String VARIATION_NAME = "teaser";

    static {
        MAIN.addVariation(VARIATION_NAME, "Teaser", "text/html", "<p>Main content (teaser)</p>",
            true, "<p>Main content (teaser)</p>");
        SECOND_TEXT_ONLY.addVariation(VARIATION_NAME, "Teaser", "text/plain", "Second content (teaser)", true,
            null);
        SECOND_STRUCTURED.addVariation(VARIATION_NAME, "Teaser", null, new String[]{"one (teaser)", "two (teaser)", "three (teaser)"},
            false, null);
    }

    static FragmentRenderService fragmentRenderService;

    QueryBuilder queryBuilderMock;

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        context.load().json("/contentfragment" + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        // load the test content fragment model into a top-level and a nested configuration
        context.load().json("/contentfragment/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        context.load().json("/contentfragment/test-content-conf.json", "/conf/global/nested/settings/dam/cfm/models");

        // load the content fragments and collection
        context.load().json("/contentfragment/test-content-dam-contentfragments.json", "/content/dam/contentfragments");
        context.load().json("/contentfragment/test-content-dam-collections.json", "/content/dam/collections");

        // set content element values for the text-only fragment (stored as binary properties)
        String path = "/content/dam/contentfragments/text-only/";
        MockVariation mainVariation = MAIN.variations.get(VARIATION_NAME);
        MockVariation secondVariation = SECOND_TEXT_ONLY.variations.get(VARIATION_NAME);
        context.load().binaryFile(new ByteArrayInputStream(MAIN.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "jcr:content/renditions/original", MAIN.contentType);
        context.load().binaryFile(new ByteArrayInputStream(mainVariation.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "jcr:content/renditions/" + VARIATION_NAME, mainVariation.contentType);
        context.load().binaryFile(new ByteArrayInputStream(SECOND_TEXT_ONLY.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "subassets/second/jcr:content/renditions/original", SECOND_TEXT_ONLY.contentType);
        context.load().binaryFile(new ByteArrayInputStream(secondVariation.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "subassets/second/jcr:content/renditions/" + VARIATION_NAME, secondVariation.contentType);

        // register an adapter that adapts resources to mocks of content fragments
        context.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, CONTENT_FRAGMENT_ADAPTER);

        // register dummy services to be injected into the model
        fragmentRenderService = mock(FragmentRenderService.class);
        context.registerService(FragmentRenderService.class, fragmentRenderService);
        context.registerService(ContentTypeConverter.class, mock(ContentTypeConverter.class));

        queryBuilderMock = Mockito.mock(QueryBuilder.class);
    }

    /**
     * Adapts the specified (content fragment) component to the Sling Model and returns it.
     */
    T getModelInstanceUnderTest(String resourceName) {
        String path = getTestResourcesParentPath() + "/" + resourceName;
        ResourceResolver originalResourceResolver = context.resourceResolver();
        // Replace resource resolver with stubbed version of itself so we can verify query builder interaction
        ResourceResolver resourceResolver = Mockito.spy(originalResourceResolver);
        Mockito.doReturn(queryBuilderMock).when(resourceResolver).adaptTo(Mockito.eq(QueryBuilder.class));
        Resource resource = resourceResolver.getResource(path);
        MockSlingHttpServletRequest httpServletRequest = new MockSlingHttpServletRequest(resourceResolver, context.bundleContext());
        httpServletRequest.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOLVER, resourceResolver);
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        httpServletRequest.setAttribute(SlingBindings.class.getName(), slingBindings);
        httpServletRequest.setContextPath(CONTEXT_PATH);
        return httpServletRequest.adaptTo(getClassType());
    }

    /**
     * Returns the {@link Class class} of the model under test. Required because the class cannot be reliably inferred
     * from the generic type.
     */
    protected abstract Class<T> getClassType();

    /**
     * Returns the parent path of all test resources.
     */
    protected abstract String getTestResourcesParentPath();

    /**
     * Adapter using "new" {@link java.util.function.Function}s.
     */
    public static final java.util.function.Function<Resource, com.adobe.cq.dam.cfm.ContentFragment> CONTENT_FRAGMENT_ADAPTER =
        new ContentFragmentMockAdapter();


}
