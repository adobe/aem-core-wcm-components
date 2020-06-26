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
import java.util.*;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ClientLibrariesImplTest {

    private static final String BASE = "/clientlibs";
    private static final String CONTENT_ROOT = "/content";
    private static final String APPS_ROOT = "/apps/core/wcm/components";
    private static final String ROOT_PAGE = "/content/clientlibs";

    private final AemContext context = CoreComponentTestContext.newAemContext();
    private Map<String,String> jsScripts;
    private Map<String,String> cssLinks;
    private Map<String,String> jsInlines;
    private Map<String,String> cssInlines;
    private Map<String,String> jsScriptsWithAttributes;
    private Map<String,String> cssLinksWithAttributes;

    @BeforeEach
    void setUp() {

        jsScripts = new HashMap<>();
        jsScripts.put("core.wcm.components.teaser.v1", "<script src=\"/apps/core/wcm/components/teaser/v1/teaser/clientlib.js\"></script>");
        jsScripts.put("core.wcm.components.accordion.v1", "<script src=\"/apps/core/wcm/components/accordion/v1/accordion/clientlib.js\"></script>");
        jsScripts.put("core.wcm.components.carousel.v1", "<script src=\"/apps/core/wcm/components/carousel/v1/carousel/clientlib.js\"></script>");

        jsScriptsWithAttributes = new HashMap<>();
        jsScriptsWithAttributes.put("core.wcm.components.teaser.v1", "<script async defer crossorigin=\"anonymous\" onload=\"myFunction()\" src=\"/apps/core/wcm/components/teaser/v1/teaser/clientlib.js\"></script>");
        jsScriptsWithAttributes.put("core.wcm.components.accordion.v1", "<script async defer crossorigin=\"anonymous\" onload=\"myFunction()\" src=\"/apps/core/wcm/components/accordion/v1/accordion/clientlib.js\"></script>");
        jsScriptsWithAttributes.put("core.wcm.components.carousel.v1", "<script async defer crossorigin=\"anonymous\" onload=\"myFunction()\" src=\"/apps/core/wcm/components/carousel/v1/carousel/clientlib.js\"></script>");

        cssLinks = new HashMap<>();
        cssLinks.put("core.wcm.components.teaser.v1", "<link rel=\"stylesheet\" href=\"/apps/core/wcm/components/teaser/v1/teaser/clientlib.css\" type=\"text/css\">");
        cssLinks.put("core.wcm.components.accordion.v1", "<link rel=\"stylesheet\" href=\"/apps/core/wcm/components/teaser/v1/accordion/clientlib.css\" type=\"text/css\">");
        cssLinks.put("core.wcm.components.carousel.v1", "<link rel=\"stylesheet\" href=\"/apps/core/wcm/components/teaser/v1/carousel/clientlib.css\" type=\"text/css\">");

        cssLinksWithAttributes = new HashMap<>();
        cssLinksWithAttributes.put("core.wcm.components.teaser.v1", "<link media=\"print\" rel=\"stylesheet\" href=\"/apps/core/wcm/components/teaser/v1/teaser/clientlib.css\" type=\"text/css\">");
        cssLinksWithAttributes.put("core.wcm.components.accordion.v1", "<link media=\"print\" rel=\"stylesheet\" href=\"/apps/core/wcm/components/teaser/v1/accordion/clientlib.css\" type=\"text/css\">");
        cssLinksWithAttributes.put("core.wcm.components.carousel.v1", "<link media=\"print\" rel=\"stylesheet\" href=\"/apps/core/wcm/components/teaser/v1/carousel/clientlib.css\" type=\"text/css\">");

        jsInlines = new HashMap<>();
        jsInlines.put("core.wcm.components.teaser.v1", "console.log('teaser clientlib js');");
        jsInlines.put("core.wcm.components.accordion.v1", "console.log('accordion clientlib js');");
        jsInlines.put("core.wcm.components.carousel.v1", "console.log('carousel clientlib js');");

        cssInlines = new HashMap<>();
        cssInlines.put("core.wcm.components.teaser.v1", "html, .teaser { \n box-sizing: border-box;\n font-size: 14px; \n}");
        cssInlines.put("core.wcm.components.accordion.v1", "html, .accordion { \n box-sizing: border-box;\n font-size: 14px; \n}");
        cssInlines.put("core.wcm.components.carousel.v1", "html, .carousel { \n box-sizing: border-box;\n font-size: 14px; \n}");

        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);

        // Mock ClientLibrary
        ClientLibrary teaserClientLibrary = mock(ClientLibrary.class);
        ClientLibrary accordionClientLibrary = mock(ClientLibrary.class);
        ClientLibrary carouselClientLibrary = mock(ClientLibrary.class);

        String[] teaserCategories = new String[]{"core.wcm.components.teaser.v1"};
        when(teaserClientLibrary.getCategories()).thenReturn(teaserCategories);
        when(teaserClientLibrary.getPath()).thenReturn("/apps/core/wcm/components/teaser/v1/teaser/clientlib");

        String[] accordionCategories = new String[]{"core.wcm.components.accordion.v1"};
        when(accordionClientLibrary.getCategories()).thenReturn(accordionCategories);
        when(accordionClientLibrary.getPath()).thenReturn("/apps/core/wcm/components/accordion/v1/accordion/clientlib");

        String[] carouselCategories = new String[]{"core.wcm.components.carousel.v1"};
        when(carouselClientLibrary.getCategories()).thenReturn(carouselCategories);
        when(carouselClientLibrary.getPath()).thenReturn("/apps/core/wcm/components/carousel/v1/carousel/clientlib");

        // Mock HtmlLibrary
        HtmlLibrary teaserJsHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary accordionJsHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary carouselJsHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary teaserCssHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary accordionCssHtmlLibrary = mock(HtmlLibrary.class);
        HtmlLibrary carouselCssHtmlLibrary = mock(HtmlLibrary.class);

        Resource teaserJsClientLibResource = context.currentResource("/apps/core/wcm/components/teaser/v1/teaser/clientlib/scripts/index.js");
        Resource accordionJsClientLibResource = context.currentResource("/apps/core/wcm/components/accordion/v1/accordion/clientlib/scripts/index.js");
        Resource carouselJsClientLibResource = context.currentResource("/apps/core/wcm/components/carousel/v1/carousel/clientlib/scripts/index.js");
        Resource teaserCssClientLibResource = context.currentResource("/apps/core/wcm/components/teaser/v1/teaser/clientlib/styles/index.css");
        Resource accordionCssClientLibResource = context.currentResource("/apps/core/wcm/components/accordion/v1/accordion/clientlib/styles/index.css");
        Resource carouselCssClientLibResource = context.currentResource("/apps/core/wcm/components/carousel/v1/carousel/clientlib/styles/index.css");

        try {
            when(teaserJsHtmlLibrary.getInputStream(anyBoolean())).thenReturn(teaserJsClientLibResource.adaptTo(InputStream.class));
            when(accordionJsHtmlLibrary.getInputStream(anyBoolean())).thenReturn(accordionJsClientLibResource.adaptTo(InputStream.class));
            when(carouselJsHtmlLibrary.getInputStream(anyBoolean())).thenReturn(carouselJsClientLibResource.adaptTo(InputStream.class));
            when(teaserCssHtmlLibrary.getInputStream(anyBoolean())).thenReturn(teaserCssClientLibResource.adaptTo(InputStream.class));
            when(accordionCssHtmlLibrary.getInputStream(anyBoolean())).thenReturn(accordionCssClientLibResource.adaptTo(InputStream.class));
            when(carouselCssHtmlLibrary.getInputStream(anyBoolean())).thenReturn(carouselCssClientLibResource.adaptTo(InputStream.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mock htmlLibraryManager.getLibraries()
        Map<String, ClientLibrary> allLibraries = new HashMap<>();
        allLibraries.put("/apps/core/wcm/components/teaser/v1/teaser/clientlib", teaserClientLibrary);
        allLibraries.put("/apps/core/wcm/components/accordion/v1/accordion/clientlib", accordionClientLibrary);
        allLibraries.put("/apps/core/wcm/components/carousel/v1/carousel/clientlib", carouselClientLibrary);
        HtmlLibraryManager htmlLibraryManager = context.registerInjectActivateService(mock(MockHtmlLibraryManager.class));
        when(htmlLibraryManager.getLibraries()).thenReturn(allLibraries);

        // Mock htmlLibraryManager.getLibraries(a, b, c, d)
        Collection<ClientLibrary> libraries = new ArrayList<>();
        libraries.add(teaserClientLibrary);
        libraries.add(accordionClientLibrary);
        libraries.add(carouselClientLibrary);
        when(htmlLibraryManager.getLibraries(any(String[].class), any(LibraryType.class), anyBoolean(), anyBoolean())).thenReturn(libraries);

        // Mock htmlLibraryManager.getLibrary(libraryType, clientlib.getPath())
        when(htmlLibraryManager.getLibrary(eq(LibraryType.JS), eq("/apps/core/wcm/components/teaser/v1/teaser/clientlib"))).thenReturn(teaserJsHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.JS), eq("/apps/core/wcm/components/accordion/v1/accordion/clientlib"))).thenReturn(accordionJsHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.JS), eq("/apps/core/wcm/components/carousel/v1/carousel/clientlib"))).thenReturn(carouselJsHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.CSS), eq("/apps/core/wcm/components/teaser/v1/teaser/clientlib"))).thenReturn(teaserCssHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.CSS), eq("/apps/core/wcm/components/accordion/v1/accordion/clientlib"))).thenReturn(accordionCssHtmlLibrary);
        when(htmlLibraryManager.getLibrary(eq(LibraryType.CSS), eq("/apps/core/wcm/components/carousel/v1/carousel/clientlib"))).thenReturn(carouselCssHtmlLibrary);

        // Mock htmlLibraryManager.writeJsInclude
        try {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                StringBuilder scriptIncludes = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String script = jsScripts.get(category);
                    scriptIncludes.append(script);
                }
                ((PrintWriter)args[1]).write(scriptIncludes.toString());
                return null;
            }).when(htmlLibraryManager).writeJsInclude(any(SlingHttpServletRequest.class), any(Writer.class), any(String.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mock htmlLibraryManager.writeCssInclude
        try {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                StringBuilder linkIncludes = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String link = cssLinks.get(category);
                    linkIncludes.append(link);
                }
                ((PrintWriter)args[1]).write(linkIncludes.toString());
                return null;
            }).when(htmlLibraryManager).writeCssInclude(any(SlingHttpServletRequest.class), any(Writer.class), any(String.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mock htmlLibraryManager.writeIncludes
        try {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                StringBuilder includes = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String script = jsScripts.get(category);
                    includes.append(script);
                }
                for (int i = 2; i < args.length; i++) {
                    String category = (String) args[i];
                    String link = cssLinks.get(category);
                    includes.append(link);
                }
                ((PrintWriter)args[1]).write(includes.toString());
                return null;
            }).when(htmlLibraryManager).writeIncludes(any(SlingHttpServletRequest.class), any(Writer.class), any(String.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testGetCategories() {
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE);
        Set<String> categories = new HashSet<>();
        categories.add("core.wcm.components.teaser.v1");
        categories.add("core.wcm.components.accordion.v1");
        categories.add("core.wcm.components.carousel.v1");
        assertArrayEquals(categories.toArray(), clientlibs.getCategories());
    }

    @Test
    void testGetCategoriesWithInjectedFilter() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("filter", ".*teaser.*");
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        Set<String> categories = new HashSet<>();
        categories.add("core.wcm.components.teaser.v1");
        assertArrayEquals(categories.toArray(), clientlibs.getCategories());
    }

    @Test
    void testGetCategoriesWithInjectedCategories() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("categories", "core.wcm.components.teaser.v1,core.wcm.components.accordion.v1");
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        Set<String> categories = new HashSet<>();
        categories.add("core.wcm.components.teaser.v1");
        categories.add("core.wcm.components.accordion.v1");
        assertArrayEquals(categories.toArray(), clientlibs.getCategories());
    }

    @Test
    void testGetCategoriesWithInjectedResourceTypes() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("resourceTypes", "core/wcm/components/accordion/v1/accordion,core/wcm/components/carousel/v1/carousel");
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        Set<String> categories = new HashSet<>();
        categories.add("core.wcm.components.accordion.v1");
        categories.add("core.wcm.components.carousel.v1");
        assertArrayEquals(categories.toArray(), clientlibs.getCategories());
    }

    @Test
    void testGetJsInline() {
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE);
        StringBuilder jsInline = new StringBuilder();
        jsInline.append(jsInlines.get("core.wcm.components.teaser.v1"));
        jsInline.append(jsInlines.get("core.wcm.components.accordion.v1"));
        jsInline.append(jsInlines.get("core.wcm.components.carousel.v1"));
        assertEquals(jsInline.toString(), clientlibs.getJsInline());
    }

    @Test
    void testGetCssInline() {
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE);
        StringBuilder cssInline = new StringBuilder();
        cssInline.append(cssInlines.get("core.wcm.components.teaser.v1"));
        cssInline.append(cssInlines.get("core.wcm.components.accordion.v1"));
        cssInline.append(cssInlines.get("core.wcm.components.carousel.v1"));
        assertEquals(cssInline.toString(), clientlibs.getCssInline());
    }

    @Test
    void testGetJsIncludes() {
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE);
        StringBuilder jsIncludes = new StringBuilder();
        jsIncludes.append(jsScripts.get("core.wcm.components.teaser.v1"));
        jsIncludes.append(jsScripts.get("core.wcm.components.accordion.v1"));
        jsIncludes.append(jsScripts.get("core.wcm.components.carousel.v1"));
        assertEquals(jsIncludes.toString(), clientlibs.getJsIncludes());
    }

    @Test
    void testGetCssIncludes() {
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE);
        StringBuilder cssIncludes = new StringBuilder();
        cssIncludes.append(cssLinks.get("core.wcm.components.teaser.v1"));
        cssIncludes.append(cssLinks.get("core.wcm.components.accordion.v1"));
        cssIncludes.append(cssLinks.get("core.wcm.components.carousel.v1"));
        assertEquals(cssIncludes.toString(), clientlibs.getCssIncludes());
    }

    @Test
    void testGetJsAndCssIncludes() {
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE);
        StringBuilder includes = new StringBuilder();
        includes.append(jsScripts.get("core.wcm.components.teaser.v1"));
        includes.append(jsScripts.get("core.wcm.components.accordion.v1"));
        includes.append(jsScripts.get("core.wcm.components.carousel.v1"));
        includes.append(cssLinks.get("core.wcm.components.teaser.v1"));
        includes.append(cssLinks.get("core.wcm.components.accordion.v1"));
        includes.append(cssLinks.get("core.wcm.components.carousel.v1"));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    @Test
    void testGetJsAndCssIncludesWithInjectedAttributes() {
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("async", true);
        attributes.put("defer", true);
        attributes.put("crossorigin", "anonymous");
        attributes.put("onload", "myFunction()");
        attributes.put("media", "print");
        ClientLibraries clientlibs = getClientLibrariesUnderTest(ROOT_PAGE, attributes);
        StringBuilder includes = new StringBuilder();
        includes.append(jsScriptsWithAttributes.get("core.wcm.components.teaser.v1"));
        includes.append(jsScriptsWithAttributes.get("core.wcm.components.accordion.v1"));
        includes.append(jsScriptsWithAttributes.get("core.wcm.components.carousel.v1"));
        includes.append(cssLinksWithAttributes.get("core.wcm.components.teaser.v1"));
        includes.append(cssLinksWithAttributes.get("core.wcm.components.accordion.v1"));
        includes.append(cssLinksWithAttributes.get("core.wcm.components.carousel.v1"));
        assertEquals(includes.toString(), clientlibs.getJsAndCssIncludes());
    }

    private ClientLibraries getClientLibrariesUnderTest(String path) {
        return getClientLibrariesUnderTest(path, null);
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
