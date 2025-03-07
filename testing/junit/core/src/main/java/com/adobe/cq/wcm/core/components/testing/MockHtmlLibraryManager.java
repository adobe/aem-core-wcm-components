/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.testing;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;

@Component(
        service = HtmlLibraryManager.class
)
public class MockHtmlLibraryManager implements HtmlLibraryManager {

    private Map<Pair<String, LibraryType>, HtmlLibrary> htmlLibrariesByPathAndType;
    private Collection<ClientLibrary> clientLibraries;
    private Map<String,ClientLibrary> librariesByPath; // a map of (path, library) of all the libraries
    private Map<String,ClientLibrary> librariesByCategory; // a map of (category, library) of all the libraries
    private Map<String,String> jsIncludes; // expected js includes
    private Map<String,String> cssIncludes; // expected css includes

    public MockHtmlLibraryManager(ClientLibrary mockClientLibrary,
                                  Map<Pair<String, LibraryType>, HtmlLibrary> htmlLibrariesByPathAndType,
                                  Map<String,ClientLibrary> librariesByPath,
                                  Map<String,ClientLibrary> librariesByCategory,
                                  Map<String,String> jsIncludes,
                                  Map<String,String> cssIncludes) {
        clientLibraries = Arrays.asList(mockClientLibrary);
        this.htmlLibrariesByPathAndType = htmlLibrariesByPathAndType;
        this.librariesByPath = librariesByPath;
        this.librariesByCategory = librariesByCategory;
        this.jsIncludes = jsIncludes;
        this.cssIncludes = cssIncludes;
    }

    public MockHtmlLibraryManager(ClientLibrary mockClientLibrary) {
        clientLibraries = Arrays.asList(mockClientLibrary);
    }

    @Override
    public Map<String, ClientLibrary> getLibraries() {
        return librariesByPath;
    }

    @Override
    public void writeJsInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
        if (jsIncludes != null) {
            StringBuilder scriptIncludes = new StringBuilder();
            for (String category : strings) {
                String script = jsIncludes.get(category);
                scriptIncludes.append(script);
            }
            writer.write(scriptIncludes.toString());
        }
    }

    @Override
    public void writeJsInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, boolean b, String... strings)
            throws IOException {

    }

    @Override
    public void writeCssInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
        if (cssIncludes != null) {
            StringBuilder linkIncludes = new StringBuilder();
            for (String category : strings) {
                String link = cssIncludes.get(category);
                linkIncludes.append(link);
            }
            writer.write(linkIncludes.toString());
        }
    }

    @Override
    public void writeCssInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, boolean b, String... strings)
            throws IOException {
    }

    @Override
    public void writeThemeInclude(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {

    }

    @Override
    public void writeIncludes(SlingHttpServletRequest slingHttpServletRequest, Writer writer, String... strings) throws IOException {
        if (jsIncludes != null && cssIncludes != null) {
            StringBuilder includes = new StringBuilder();
            for (String category : strings) {
                String script = jsIncludes.get(category);
                includes.append(script);
            }
            for (String category : strings) {
                String link = cssIncludes.get(category);
                includes.append(link);
            }
            writer.write(includes.toString());
        }
    }

    @Override
    public HtmlLibrary getLibrary(LibraryType libraryType, String s) {
        if (htmlLibrariesByPathAndType != null) {
            return htmlLibrariesByPathAndType.get(Pair.of(s, libraryType));
        }
        return null;
    }

    @Override
    public HtmlLibrary getLibrary(SlingHttpServletRequest slingHttpServletRequest) {
        return null;
    }

    @Override
    public boolean isMinifyEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isGzipEnabled() {
        return false;
    }

    @Override
    public Collection<ClientLibrary> getLibraries(String[] strings, LibraryType libraryType, boolean b, boolean b1) {
        if (librariesByCategory != null) {
            Collection<ClientLibrary> libraries = new ArrayList<>();
            for (String category : strings) {
                libraries.add(librariesByCategory.get(category));
            }
            return libraries;
        }
        return Collections.unmodifiableCollection(clientLibraries);
    }

    @Override
    public Collection<ClientLibrary> getThemeLibraries(String[] strings, LibraryType libraryType, String s, boolean b) {
        return null;
    }

    @Override
    public void invalidateOutputCache() throws RepositoryException {

    }

    @Override
    public void ensureCached() throws IOException, RepositoryException {}
}
