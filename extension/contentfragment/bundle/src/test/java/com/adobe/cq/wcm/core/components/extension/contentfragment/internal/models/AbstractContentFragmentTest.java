/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.ContentFragmentMockAdapter;
import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.MockElement;
import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.MockVariation;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;

public abstract class AbstractContentFragmentTest<T> {

    /* names of the content fragment component instances to test */

    protected static final String CF_TEXT_ONLY_NO_PATH = "text-only-no-path";
    protected static final String CF_TEXT_ONLY_NON_EXISTING_PATH = "text-only-non-existing-path";
    protected static final String CF_TEXT_ONLY_INVALID_PATH = "text-only-invalid-path";
    protected static final String CF_TEXT_ONLY = "text-only";
    protected static final String CF_TEXT_ONLY_VARIATION = "text-only-variation";
    protected static final String CF_TEXT_ONLY_NON_EXISTING_VARIATION = "text-only-non-existing-variation";
    protected static final String CF_TEXT_ONLY_SINGLE_ELEMENT = "text-only-single-element";
    protected static final String CF_TEXT_ONLY_MULTIPLE_ELEMENTS = "text-only-multiple-elements";

    protected static final String CF_STRUCTURED_NO_PATH = "structured-no-path";
    protected static final String CF_STRUCTURED_NON_EXISTING_PATH = "structured-non-existing-path";
    protected static final String CF_STRUCTURED_INVALID_PATH = "structured-invalid-path";
    protected static final String CF_STRUCTURED = "structured";
    protected static final String CF_STRUCTURED_VARIATION = "structured-variation";
    protected static final String CF_STRUCTURED_NON_EXISTING_VARIATION = "structured-non-existing-variation";
    protected static final String CF_STRUCTURED_NESTED_MODEL = "structured-nested-model";
    protected static final String CF_STRUCTURED_SINGLE_ELEMENT = "structured-single-element";
    protected static final String CF_STRUCTURED_MULTIPLE_ELEMENTS = "structured-multiple-elements";

    /* contents of the text-only and structured content fragments referenced by the above components */

    protected static final String TITLE = "Test Content Fragment";
    protected static final String DESCRIPTION = "This is a test content fragment.";
    protected static final String TEXT_ONLY_TYPE = "/content/dam/contentfragments/text-only/jcr:content/model";
    protected static final String STRUCTURED_TYPE = "global/models/test";
    protected static final String STRUCTURED_TYPE_NESTED = "global/nested/models/test";
    protected static final String[] ASSOCIATED_CONTENT = new String[]{"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test"};

    protected static final MockElement MAIN = new MockElement("main", "Main", "text/html",
        "<p>Main content</p>", true, "<p>Main content</p>", new String[]{"<p>Main content</p>"});
    protected static final MockElement SECOND_TEXT_ONLY = new MockElement("second", "Second", "text/plain", "Second content",
        true, null, new String[]{"Second content"});
    protected static final MockElement SECOND_STRUCTURED = new MockElement("second", "Second", null, new String[]{"one", "two", "three"},
        false, null, null);

    protected static final String VARIATION_NAME = "teaser";

    static {
        MAIN.addVariation(VARIATION_NAME, "Teaser", "text/html", "<p>Main content (teaser)</p>",
            true, "<p>Main content (teaser)</p>", new String[]{"<p>Main content (teaser)</p>"});
        SECOND_TEXT_ONLY.addVariation(VARIATION_NAME, "Teaser", "text/plain", "Second content (teaser)", true,
            null, new String[]{"Second content (teaser)"});
        SECOND_STRUCTURED.addVariation(VARIATION_NAME, "Teaser", null, new String[]{"one (teaser)", "two (teaser)", "three (teaser)"},
            false, null, null);
    }

    protected static FragmentRenderService fragmentRenderService;

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext("/contentfragment", "/content");

    @BeforeClass
    public static void beforeClass() {
        // load the test content fragment model into a top-level and a nested configuration
        AEM_CONTEXT.load().json("/contentfragment/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        AEM_CONTEXT.load().json("/contentfragment/test-content-conf.json", "/conf/global/nested/settings/dam/cfm/models");

        // load the content fragments and collection
        AEM_CONTEXT.load().json("/contentfragment/test-content-dam-contentfragments.json", "/content/dam/contentfragments");
        AEM_CONTEXT.load().json("/contentfragment/test-content-dam-collections.json", "/content/dam/collections");

        // set content element values for the text-only fragment (stored as binary properties)
        String path = "/content/dam/contentfragments/text-only/";
        MockVariation mainVariation = MAIN.variations.get(VARIATION_NAME);
        MockVariation secondVariation = SECOND_TEXT_ONLY.variations.get(VARIATION_NAME);
        AEM_CONTEXT.load().binaryFile(new ByteArrayInputStream(MAIN.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "jcr:content/renditions/original", MAIN.contentType);
        AEM_CONTEXT.load().binaryFile(new ByteArrayInputStream(mainVariation.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "jcr:content/renditions/" + VARIATION_NAME, mainVariation.contentType);
        AEM_CONTEXT.load().binaryFile(new ByteArrayInputStream(SECOND_TEXT_ONLY.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "subassets/second/jcr:content/renditions/original", SECOND_TEXT_ONLY.contentType);
        AEM_CONTEXT.load().binaryFile(new ByteArrayInputStream(secondVariation.values[0].getBytes(StandardCharsets.UTF_8)),
            path + "subassets/second/jcr:content/renditions/" + VARIATION_NAME, secondVariation.contentType);

        // register an adapter that adapts resources to mocks of content fragments
        AEM_CONTEXT.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, ADAPTER);

        // register dummy services to be injected into the model
        fragmentRenderService = mock(FragmentRenderService.class);
        AEM_CONTEXT.registerService(FragmentRenderService.class, fragmentRenderService);
        AEM_CONTEXT.registerService(ContentTypeConverter.class, mock(ContentTypeConverter.class));
    }

    protected void setFakeLoggerOnClass(Class<?> clazz, Logger logger) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField("LOG");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        field.setAccessible(true);
        // remove final modifier from field

        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, logger);
    }

    /**
     * Adapts the specified (content fragment) component to the Sling Model and returns it.
     */
    protected T getTestContentFragment(String resourceName) {
        String path = getTestResourcesParentPath() + "/" + resourceName;
        ResourceResolver resourceResolver = AEM_CONTEXT.resourceResolver();
        Resource resource = resourceResolver.getResource(path);
        MockSlingHttpServletRequest httpServletRequest = new MockSlingHttpServletRequest(resourceResolver, AEM_CONTEXT.bundleContext());
        httpServletRequest.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOLVER, resourceResolver);
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        httpServletRequest.setAttribute(SlingBindings.class.getName(), slingBindings);
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
    private static final java.util.function.Function<Resource, com.adobe.cq.dam.cfm.ContentFragment> CONTENT_FRAGMENT_ADAPTER =
        new ContentFragmentMockAdapter();

    /**
     * Adapts resources to {@link com.adobe.cq.dam.cfm.ContentFragment} objects by mocking parts of their API.
     */
    public static final com.google.common.base.Function<Resource, com.adobe.cq.dam.cfm.ContentFragment> ADAPTER =
        new com.google.common.base.Function<Resource, com.adobe.cq.dam.cfm.ContentFragment>() {
            @Nullable
            @Override
            public com.adobe.cq.dam.cfm.ContentFragment apply(@Nullable Resource resource) {
                return CONTENT_FRAGMENT_ADAPTER.apply(resource);
            }
        };

}
