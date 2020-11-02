/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ClientLibrariesImplTest {

    private static final String TEST_CONTENT_XF_JSON = "/test-content-xf.json";
    private static final String TEST_CONF_TEMPLATES_JSON = "/test-conf-templates.json";

    private static final String APPS_ROOT = "/apps/core/wcm/components";
    private static final String XF_ROOT = "/content/experience-fragments";
    private static final String TEMPLATES_ROOT = "/conf/templates";

    private static final String BASE = "/clientlibs";
    private static final String CONTENT_ROOT = "/content";
    private static final String ROOT_PAGE = "/content/clientlibs";
    private static final String PAGE_WITH_TEMPLATE = ROOT_PAGE + "/page-with-template";
    private static final String ACCORDION_PATH = ROOT_PAGE + "/jcr:content/root/responsivegrid/accordion-1";
    private static final String EXPERIENCE_FRAGMENT_PATH = ROOT_PAGE + "/jcr:content/root/responsivegrid/experiencefragment-1";

    private static final String TEASER_CATEGORY = "core.wcm.components.teaser.v1";
    private static final String ACCORDION_CATEGORY = "core.wcm.components.accordion.v1";
    private static final String CAROUSEL_CATEGORY = "core.wcm.components.carousel.v1";

    private static final String TEASER_CLIENTLIB_PATH = "/apps/core/wcm/components/teaser/v1/teaser/clientlib";
    private static final String ACCORDION_CLIENTLIB_PATH = "/apps/core/wcm/components/accordion/v1/accordion/clientlib";
    private static final String CAROUSEL_CLIENTLIB_PATH = "/apps/core/wcm/components/carousel/v1/carousel/clientlib";

    private static final String JS_FILE_REL_PATH = "/scripts/index.js";
    private static final String CSS_FILE_REL_PATH = "/styles/index.css";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private Map<String,ClientLibrary> allLibraries; // a map of (path, library) of all the libraries
    private Map<String,ClientLibrary> librariesMap; // a map of (category, library) of all the libraries
    private Map<String,String> jsIncludes; // expected js includes
    private Map<String,String> cssIncludes; // expected css includes
    private Map<String,String> jsIncludesWithAttributes; // expected js includes when injecting attributes
    private Map<String,String> cssIncludesWithAttributes; // expected css includes when injecting attributes
    private Map<String,String> jsInlines; // expected js inlines
    private Map<String,String> cssInlines; // expected css inlines

    @BeforeEach
    void setUp() {
        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);
        context.load().json(BASE + TEST_CONTENT_XF_JSON, XF_ROOT);
        context.load().json(BASE + TEST_CONF_TEMPLATES_JSON, TEMPLATES_ROOT);

        jsIncludes = new HashMap<>();
        jsIncludes.put(TEASER_CATEGORY, "<script src=\"" + TEASER_CLIENTLIB_PATH + ".js\"></script>");
        jsIncludes.put(ACCORDION_CATEGORY, "<script src=\"" + ACCORDION_CLIENTLIB_PATH + ".js\"></script>");
        jsIncludes.put(CAROUSEL_CATEGORY, "<script src=\"" + CAROUSEL_CLIENTLIB_PATH + ".js\"></script>");

        jsIncludesWithAttributes = new HashMap<>();
        jsIncludesWithAttributes.put(TEASER_CATEGORY, "<script async defer crossorigin=\"anonymous\" onload=\"myFunction()\" src=\"" + TEASER_CLIENTLIB_PATH + ".js\"></script>");
        jsIncludesWithAttributes.put(ACCORDION_CATEGORY, "<script async defer crossorigin=\"anonymous\" onload=\"myFunction()\" src=\"" + ACCORDION_CLIENTLIB_PATH + ".js\"></script>");
        jsIncludesWithAttributes.put(CAROUSEL_CATEGORY, "<script async defer crossorigin=\"anonymous\" onload=\"myFunction()\" src=\"" +
                CAROUSEL_CLIENTLIB_PATH + ".js\"></script>");

        cssIncludes = new HashMap<>();
        cssIncludes.put(TEASER_CATEGORY, "<link rel=\"stylesheet\" href=\"" + TEASER_CLIENTLIB_PATH + ".css\" type=\"text/css\">");
        cssIncludes.put(ACCORDION_CATEGORY, "<link rel=\"stylesheet\" href=\"" + ACCORDION_CLIENTLIB_PATH + ".css\" type=\"text/css\">");
        cssIncludes.put(CAROUSEL_CATEGORY, "<link rel=\"stylesheet\" href=\"" + CAROUSEL_CLIENTLIB_PATH + ".css\" type=\"text/css\">");

        cssIncludesWithAttributes = new HashMap<>();
        cssIncludesWithAttributes.put(TEASER_CATEGORY, "<link media=\"print\" rel=\"stylesheet\" href=\"" + TEASER_CLIENTLIB_PATH + ".css\" type=\"text/css\">");
        cssIncludesWithAttributes.put(ACCORDION_CATEGORY, "<link media=\"print\" rel=\"stylesheet\" href=\"" + ACCORDION_CLIENTLIB_PATH + ".css\" type=\"text/css\">");
        cssIncludesWithAttributes.put(CAROUSEL_CATEGORY, "<link media=\"print\" rel=\"stylesheet\" href=\"" + CAROUSEL_CLIENTLIB_PATH + ".css\" type=\"text/css\">");

        jsInlines = new HashMap<>();
        jsInlines.put(TEASER_CATEGORY, "console.log('teaser clientlib js');");
        jsInlines.put(ACCORDION_CATEGORY, "console.log('accordion clientlib js');");
        jsInlines.put(CAROUSEL_CATEGORY, "console.log('carousel clientlib js');");

        cssInlines = new HashMap<>();
        cssInlines.put(TEASER_CATEGORY, "html, .teaser { \n box-sizing: border-box;\n font-size: 14px; \n}");
        cssInlines.put(ACCORDION_CATEGORY, "html, .accordion { \n box-sizing: border-box;\n font-size: 14px; \n}");
        cssInlines.put(CAROUSEL_CATEGORY, "html, .carousel { \n box-sizing: border-box;\n font-size: 14px; \n}");

        // Mock ClientLibrary
        ClientLibrary teaserClientLibrary = mock(ClientLibrary.class);
        ClientLibrary accordionClientLibrary = mock(ClientLibrary.class);
        ClientLibrary carouselClientLibrary = mock(ClientLibrary.class);

        String[] teaserCategories = new String[]{TEASER_CATEGORY};
        when(teaserClientLibrary.getCategories()).thenReturn(teaserCategories);
        when(teaserClientLibrary.getPath()).thenReturn(TEASER_CLIENTLIB_PATH);

        String[] accordionCategories = new String[]{ACCORDION_CATEGORY};
        when(accordionClientLibrary.getCategories()).thenReturn(accordionCategories);
        when(accordionClientLibrary.getPath()).thenReturn(ACCORDION_CLIENTLIB_PATH);

        String[] carouselCategories = new String[]{CAROUSEL_CATEGORY};
        when(carouselClientLibrary.getCategories()).thenReturn(carouselCategories);
        when(carouselClientLibrary.getPath()).thenReturn(CAROUSEL_CLIENTLIB_PATH);

        librariesMap = new HashMap<>();
        librariesMap.put(TEASER_CATEGORY, teaserClientLibrary);
        librariesMap.put(ACCORDION_CATEGORY, accordionClientLibrary);
        librariesMap.put(CAROUSEL_CATEGORY, carouselClientLibrary);

        // Mock HtmlLibrary
        HtmlLibrary teaserJsHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary accordionJsHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary carouselJsHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary teaserCssHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary accordionCssHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary carouselCssHtmlLibrary = mock(HtmlLibrary.class);

        Resource teaserJsClientLibResource = context.currentResource(TEASER_CLIENTLIB_PATH + JS_FILE_REL_PATH);
        Resource accordionJsClientLibResource = context.currentResource(ACCORDION_CLIENTLIB_PATH + JS_FILE_REL_PATH);
        Resource carouselJsClientLibResource = context.currentResource(CAROUSEL_CLIENTLIB_PATH + JS_FILE_REL_PATH);
        Resource teaserCssClientLibResource = context.currentResource(TEASER_CLIENTLIB_PATH + CSS_FILE_REL_PATH);
        Resource accordionCssClientLibResource = context.currentResource(ACCORDION_CLIENTLIB_PATH + CSS_FILE_REL_PATH);
        Resource carouselCssClientLibResource = context.currentResource(CAROUSEL_CLIENTLIB_PATH + CSS_FILE_REL_PATH);

        try {
            when(teaserJsHtmlLibrary.getInputStream(anyBoolean())).thenReturn(teaserJsClientLibResource.adaptTo(InputStream.class));
            when(accordionJsHtmlLibrary.getInputStream(anyBoolean())).thenReturn(accordionJsClientLibResource.adaptTo(InputStream.class));
            when(carouselJsHtmlLibrary.getInputStream(anyBoolean())).thenReturn(carouselJsClientLibResource.adaptTo(InputStream.class));
            when(teaserCssHtmlLibrary.getInputStream(anyBoolean())).thenReturn(teaserCssClientLibResource.adaptTo(InputStream.class));
            when(accordionCssHtmlLibrary.getInputStream(anyBoolean())).thenReturn(accordionCssClientLibResource.adaptTo(InputStream.class));
            when(carouselCssHtmlLibrary.getInputStream(anyBoolean())).thenReturn(carouselCssClientLibResource.adaptTo(InputStream.class));
        } catch (IOException e) {
            fail(String.format("Unable to get input stream from the library: %s", e.getMessage()));
        }

        // Mock htmlLibraryManager.getLibraries()
        allLibraries = new HashMap<>();
        allLibraries.put(TEASER_CLIENTLIB_PATH, teaserClientLibrary);
        allLibraries.put(ACCORDION_CLIENTLIB_PATH, accordionClientLibrary);
        allLibraries.put(CAROUSEL_CLIENTLIB_PATH, carouselClientLibrary);
        HtmlLibraryManager htmlLibraryManager = context.registerInjectActivateService(mock(MockHtmlLibraryManager.class));
        when(htmlLibraryManager.getLibraries()).thenReturn(allLibraries);

        // Mock htmlLibraryManager.getLibraries(a, b, c, d)
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            String[] categories = (String[]) args[0];
            Collection<ClientLibrary> libraries = new ArrayList<>();
            for (String category : categories) {
                libraries.add(librariesMap.get(category));
            }
            return libraries;
        }).when(htmlLibraryManager).getLibraries(any(String[].class), any(LibraryType.class), anyBoolean(), anyBoolean());

        // Mock htmlLibraryManager.getLibrary(libraryType, clientlib.getPath())
        when(htmlLibraryManager.getLibrary(eq(LibraryType.JS), eq(TEASER_CLIENTLIB_PATH))).thenReturn(teaserJsHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.JS), eq(ACCORDION_CLIENTLIB_PATH))).thenReturn(accordionJsHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.JS), eq(CAROUSEL_CLIENTLIB_PATH))).thenReturn(carouselJsHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.CSS), eq(TEASER_CLIENTLIB_PATH))).thenReturn(teaserCssHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.CSS), eq(ACCORDION_CLIENTLIB_PATH))).thenReturn(accordionCssHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.CSS), eq(CAROUSEL_CLIENTLIB_PATH))).thenReturn(carouselCssHtmlLibrary);

        // Mock htmlLibraryManager.writeJsInclude
        try {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                StringBuilder scriptIncludes = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String script = jsIncludes.get(category);
                    scriptIncludes.append(script);
                }
                ((PrintWriter)args[1]).write(scriptIncludes.toString());
                return null;
            }).when(htmlLibraryManager).writeJsInclude(any(SlingHttpServletRequest.class), any(Writer.class), any(String.class));
        } catch (IOException e) {
            fail(String.format("Unable to write JS include: %s", e.getMessage()));
        }

        // Mock htmlLibraryManager.writeCssInclude
        try {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                StringBuilder linkIncludes = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String link = cssIncludes.get(category);
                    linkIncludes.append(link);
                }
                ((PrintWriter)args[1]).write(linkIncludes.toString());
                return null;
            }).when(htmlLibraryManager).writeCssInclude(any(SlingHttpServletRequest.class), any(Writer.class), any(String.class));
        } catch (IOException e) {
            fail(String.format("Unable to write CSS include: %s", e.getMessage()));
        }

        // Mock htmlLibraryManager.writeIncludes
        try {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                StringBuilder includes = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String script = jsIncludes.get(category);
                    includes.append(script);
                }
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String link = cssIncludes.get(category);
                    includes.append(link);
                }
                ((PrintWriter)args[1]).write(includes.toString());
                return null;
            }).when(htmlLibraryManager).writeIncludes(any(SlingHttpServletRequest.class), any(Writer.class), any(String.class));
        } catch (IOException e) {
            fail(String.format("Unable to write include: %s", e.getMessage()));
        }

    }

    @Test
    void testGetCategories() {
        PageManager pageManager = context.pageManager();
        Page page = pageManager.getPage(ROOT_PAGE);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ClientLibraries.OPTION_RESOURCE_TYPES, Utils.getPageResourceTypes(page, context.request(), mock(ModelFactory.class)));
        ClientLibrariesImpl clientlibs = Objects.requireNonNull((ClientLibrariesImpl) getClientLibrariesUnderTest(ROOT_PAGE, attributes));

        Set<String> categories = new HashSet<>();
        categories.add(TEASER_CATEGORY);
        categories.add(ACCORDION_CATEGORY);
        categories.add(CAROUSEL_CATEGORY);
        assertEquals(categories, clientlibs.getCategoriesFromComponents());
    }

    /**
     * Same as {@link #testGetCategories()} however a login exception will occur when fetching the
     * resource resolver.
     */
    @Test
    void testGetCategoriesWithLoginException() throws Exception {
        PageManager pageManager = context.pageManager();
        Page page = pageManager.getPage(ROOT_PAGE);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ClientLibraries.OPTION_RESOURCE_TYPES, Utils.getPageResourceTypes(page, context.request(), mock(ModelFactory.class)));
        ClientLibrariesImpl clientlibs = Objects.requireNonNull((ClientLibrariesImpl) getClientLibrariesUnderTest(ROOT_PAGE, attributes));

        ResourceResolverFactory factory = mock(ResourceResolverFactory.class);
        doThrow(new LoginException()).when(factory).getServiceResourceResolver(anyMap());
        clientlibs.resolverFactory = factory;
        assertEquals(new HashSet<>(), clientlibs.getCategoriesFromComponents());
    }

    @Test
    void testGetCategoriesForComponent() {
        Resource accordionResource = context.currentResource(ACCORDION_PATH);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ClientLibraries.OPTION_RESOURCE_TYPES, Utils.getResourceTypes(accordionResource, context.request(), mock(ModelFactory.class)));
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ACCORDION_PATH, attributes);
        Set<String> categories = new HashSet<>();
        categories.add(TEASER_CATEGORY);
        categories.add(ACCORDION_CATEGORY);
        assertEquals(categories, ((ClientLibrariesImpl)clientlibs).getCategoriesFromComponents());
    }

    @Test
    void testGetCategoriesWithInjectedFilter() {
        PageManager pageManager = context.resourceResolver().adaptTo(PageManager.class);
        Page page = pageManager.getPage(ROOT_PAGE);
        Map<String,Object> attributes = new HashMap<>();
        attributes.put(ClientLibraries.OPTION_RESOURCE_TYPES, Utils.getPageResourceTypes(page, context.request(), mock(ModelFactory.class)));
        attributes.put("filter", ".*teaser.*");
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        Set<String> categories = new HashSet<>();
        categories.add(TEASER_CATEGORY);
        assertEquals(categories, ((ClientLibrariesImpl)clientlibs).getCategoriesFromComponents());
    }

    @Test
    void testGetCategoriesWithInjectedCategory() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories",  TEASER_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludes.get(TEASER_CATEGORY));
        includes.append(cssIncludes.get(TEASER_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testGetCategoriesWithInjectedCategories() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludes.get(TEASER_CATEGORY));
        includes.append(jsIncludes.get(ACCORDION_CATEGORY));
        includes.append(cssIncludes.get(TEASER_CATEGORY));
        includes.append(cssIncludes.get(ACCORDION_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testGetCategoriesWithInjectedResourceType() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("resourceTypes", new HashSet<String>() {{
            add("core/wcm/components/accordion/v1/accordion");
        }});
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludes.get(ACCORDION_CATEGORY));
        includes.append(cssIncludes.get(ACCORDION_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testGetCategoriesWithInjectedResourceTypesAndInheritance() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("resourceTypes", new HashSet<String>() {{
            add("core/wcm/components/accordion/v1/accordion");
            add("core/wcm/components/carousel/v3/carousel");
        }});
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludes.get(ACCORDION_CATEGORY));
        includes.append(jsIncludes.get(CAROUSEL_CATEGORY));
        includes.append(cssIncludes.get(ACCORDION_CATEGORY));
        includes.append(cssIncludes.get(CAROUSEL_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testGetCategoriesWithInjectedResourceTypesAndInheritanceDisabled() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("resourceTypes", new HashSet<String>() {{
            add("core/wcm/components/accordion/v1/accordion");
            add("core/wcm/components/carousel/v3/carousel");
        }});
        attributes.put("inherited", false);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludes.get(ACCORDION_CATEGORY));
        includes.append(cssIncludes.get(ACCORDION_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testJsInline() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY + "," + CAROUSEL_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder jsInline = new StringBuilder();
        jsInline.append(jsInlines.get(TEASER_CATEGORY));
        jsInline.append(jsInlines.get(ACCORDION_CATEGORY));
        jsInline.append(jsInlines.get(CAROUSEL_CATEGORY));
        assertEquals(jsInline.toString(), clientlibs.getJsInline());
    }

    @Test
    void testGetCssInline() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY + "," + CAROUSEL_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder cssInline = new StringBuilder();
        cssInline.append(cssInlines.get(TEASER_CATEGORY));
        cssInline.append(cssInlines.get(ACCORDION_CATEGORY));
        cssInline.append(cssInlines.get(CAROUSEL_CATEGORY));
        assertEquals(cssInline.toString(), clientlibs.getCssInline());
    }

    @Test
    void testGetJsIncludes() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY + "," + CAROUSEL_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder jsInclude = new StringBuilder();
        jsInclude.append(jsIncludes.get(TEASER_CATEGORY));
        jsInclude.append(jsIncludes.get(ACCORDION_CATEGORY));
        jsInclude.append(jsIncludes.get(CAROUSEL_CATEGORY));
        assertEquals(jsInclude.toString(), clientlibs.getJsIncludes());
    }

    @Test
    void testGetCssIncludes() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY + "," + CAROUSEL_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder cssInclude = new StringBuilder();
        cssInclude.append(cssIncludes.get(TEASER_CATEGORY));
        cssInclude.append(cssIncludes.get(ACCORDION_CATEGORY));
        cssInclude.append(cssIncludes.get(CAROUSEL_CATEGORY));
        assertEquals(cssInclude.toString(), clientlibs.getCssIncludes());
    }

    @Test
    void testGetJsAndCssIncludes() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY + "," + CAROUSEL_CATEGORY);
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludes.get(TEASER_CATEGORY));
        includes.append(jsIncludes.get(ACCORDION_CATEGORY));
        includes.append(jsIncludes.get(CAROUSEL_CATEGORY));
        includes.append(cssIncludes.get(TEASER_CATEGORY));
        includes.append(cssIncludes.get(ACCORDION_CATEGORY));
        includes.append(cssIncludes.get(CAROUSEL_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testGetJsAndCssIncludesWithInjectedAttributes() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", TEASER_CATEGORY + "," + ACCORDION_CATEGORY + "," + CAROUSEL_CATEGORY);
        attributes.put("async", true);
        attributes.put("defer", true);
        attributes.put("crossorigin", "anonymous");
        attributes.put("onload", "myFunction()");
        attributes.put("media", "print");
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsIncludesWithAttributes.get(TEASER_CATEGORY));
        includes.append(jsIncludesWithAttributes.get(ACCORDION_CATEGORY));
        includes.append(jsIncludesWithAttributes.get(CAROUSEL_CATEGORY));
        includes.append(cssIncludesWithAttributes.get(TEASER_CATEGORY));
        includes.append(cssIncludesWithAttributes.get(ACCORDION_CATEGORY));
        includes.append(cssIncludesWithAttributes.get(CAROUSEL_CATEGORY));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    //
    // Below are tests for the specific methods in com.adobe.cq.wcm.core.components.internal.Utils
    //

    @Test
    void testUtilsGetXFResourceTypes() {
        Resource xfResource = context.currentResource(EXPERIENCE_FRAGMENT_PATH);
        ModelFactory modelFactory = mock(ModelFactory.class);
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Resource resource = (Resource) args[1];
            String fragmentPath = resource.getValueMap().get("fragmentVariationPath", String.class);
            ExperienceFragment experienceFragment = mock(ExperienceFragment.class);
            when(experienceFragment.getLocalizedFragmentVariationPath()).thenReturn(fragmentPath);
            return experienceFragment;
        }).when(modelFactory).getModelFromWrappedRequest(any(SlingHttpServletRequest.class), any(Resource.class), eq(ExperienceFragment.class));
        Set<String> resourceTypes = Utils.getXFResourceTypes(xfResource, context.request(), modelFactory);
        Set<String> expectedResourceTypes = new HashSet<>(Arrays.asList("core/wcm/components/page/v2/page", "cq:Page", "core/wcm/components/teaser/v1/teaser", "wcm/foundation/components/responsivegrid"));
        assertEquals(expectedResourceTypes, resourceTypes);
    }

    @Test
    void testUtilsGetTemplateResourceTypes() {
        Page page = context.currentPage(PAGE_WITH_TEMPLATE);
        ModelFactory modelFactory = mock(ModelFactory.class);
        Set<String> resourceTypes = Utils.getTemplateResourceTypes(page, context.request(), modelFactory);
        Set<String> expectedResourceTypes = new HashSet<>(Arrays.asList("core/wcm/components/page/v2/page", "wcm/foundation/components/responsivegrid", "core/wcm/components/text/v1/text"));
        assertEquals(expectedResourceTypes, resourceTypes);
    }

    private ClientLibraries getClientLibrariesUnderTest(String path, Map<String,Object> attributes) {
        Resource resource = context.currentResource(path);
        if (resource != null) {
            if (attributes != null) {
                SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
                for (Map.Entry<String,Object> entry : attributes.entrySet()) {
                    slingBindings.put(entry.getKey(), entry.getValue());
                }
                context.request().setAttribute(SlingBindings.class.getName(), slingBindings);
            }
            context.request().setResource(resource);
            return context.request().adaptTo(ClientLibraries.class);
        }
        return null;
    }

}
