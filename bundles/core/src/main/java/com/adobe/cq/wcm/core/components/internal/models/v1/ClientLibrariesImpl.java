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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {ClientLibraries.class}
)
public class ClientLibrariesImpl implements ClientLibraries {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLibrariesImpl.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Named(OPTION_CATEGORIES)
    private String categoriesCsv;

    @Inject
    @Named(OPTION_ASYNC)
    @Optional
    @Nullable
    private boolean async;

    @Inject
    @Named(OPTION_DEFER)
    @Optional
    @Nullable
    private boolean defer;

    @Inject
    @Named(OPTION_CROSSORIGIN)
    @Optional
    @Nullable
    private String crossorigin;

    @Inject
    @Named(OPTION_ONLOAD)
    @Optional
    @Nullable
    private String onload;

    @Inject
    @Named(OPTION_MEDIA)
    @Optional
    @Nullable
    private String media;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    private String[] categoriesArray;

    @PostConstruct
    protected void initModel() {
        Set<String> categoriesSet = new HashSet<>();

        if (StringUtils.isNotBlank(categoriesCsv)) {
            if (categoriesCsv.contains(",")) {
                Collections.addAll(categoriesSet, categoriesCsv.split(","));
            } else {
                categoriesSet.add(categoriesCsv);
            }
        }

        categoriesArray = categoriesSet.toArray(new String[0]);
    }

    @NotNull
    @Override
    public String getJsInline() {
        return getInline(LibraryType.JS);
    }

    @NotNull
    @Override
    public String getCssInline() {
        return getInline(LibraryType.CSS);
    }

    @Override
    public String getJsIncludes() {
        return getLibIncludes(LibraryType.JS);
    }

    @Override
    public String getCssIncludes() {
        return getLibIncludes(LibraryType.CSS);
    }

    @Override
    public String getJsAndCssIncludes() {
        return getLibIncludes(null);
    }

    private String getLibIncludes(LibraryType type) {
        StringWriter sw = new StringWriter();
        try {
            if (categoriesArray == null || categoriesArray.length == 0)  {
                LOG.error("'categories' option might be missing from the invocation of the /libs/granite/sightly/templates/clientlib.html" +
                    "client libraries template library. Please provide a CSV list or an array of categories to include.");
            } else {
                PrintWriter out = new PrintWriter(sw);
                if (type == LibraryType.JS) {
                    htmlLibraryManager.writeJsInclude(request, out, categoriesArray);
                } else if (type == LibraryType.CSS) {
                    htmlLibraryManager.writeCssInclude(request, out, categoriesArray);
                } else {
                    htmlLibraryManager.writeIncludes(request, out, categoriesArray);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to include client libraries {}", categoriesArray);
        }

        String html = sw.toString();
        // inject attributes from HTL into the JS and CSS HTML tags
        return getHtmlWithInjectedAttributes(html);
    }

    private String getHtmlWithInjectedAttributes(String html) {
        StringBuilder jsAttributes = new StringBuilder();
        jsAttributes.append(getHtmlAttr(OPTION_ASYNC, async));
        jsAttributes.append(getHtmlAttr(OPTION_DEFER, defer));
        jsAttributes.append(getHtmlAttr(OPTION_CROSSORIGIN, crossorigin));
        jsAttributes.append(getHtmlAttr(OPTION_ONLOAD, onload));
        StringBuilder cssAttributes = new StringBuilder();
        cssAttributes.append(getHtmlAttr(OPTION_MEDIA, media));
        String updatedHtml = StringUtils.replace(html,"<script ", "<script " + jsAttributes.toString());
        return StringUtils.replace(updatedHtml,"<link ", "<link " + cssAttributes.toString());
    }

    private String getHtmlAttr(String name, boolean include) {
        if (include) {
            return name + " ";
        }
        return "";
    }

    private String getHtmlAttr(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            return name + "=\"" + value + "\" ";
        }
        return "";
    }

    private String getInline(LibraryType libraryType) {
        Collection<ClientLibrary> clientlibs = htmlLibraryManager.getLibraries(categoriesArray, libraryType, true, false);
        // Iterate through the clientlibs and aggregate their content.
        StringBuilder output = new StringBuilder();
        for (ClientLibrary clientlib : clientlibs) {
            HtmlLibrary htmlLibrary = htmlLibraryManager.getLibrary(libraryType, clientlib.getPath());
            if (htmlLibrary != null) {
                try {
                    output.append(IOUtils.toString(htmlLibrary.getInputStream(htmlLibraryManager.isMinifyEnabled()),
                        StandardCharsets.UTF_8));
                } catch (IOException e) {
                    LOG.error("Error getting input stream from clientlib with path '{}'.", clientlib.getPath());
                }
            }
        }
        return output.toString();
    }

}
