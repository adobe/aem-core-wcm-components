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
package com.adobe.cq.wcm.core.components.internal;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.factory.ModelFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import com.adobe.cq.export.json.SlingModelFilter;

public class Utils {

    private Utils() {
    }

    /**
     * If the provided {@code path} identifies a {@link Page}, this method will generate the correct URL for the page. Otherwise the
     * original {@code String} is returned.
     *
     * @param request     the current request, used to determine the server's context path
     * @param pageManager the page manager
     * @param path        the page path
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a
     * {@link Page}
     */
    @Nonnull
    public static String getURL(@Nonnull SlingHttpServletRequest request, @Nonnull PageManager pageManager, @Nonnull String path) {
        Page page = pageManager.getPage(path);
        if (page != null) {
            return getURL(request, page);
        }
        return path;
    }

    /**
     * Given a {@link Page}, this method returns the correct URL, taking into account that the provided {@code page} might provide a
     * vanity URL.
     *
     * @param request the current request, used to determine the server's context path
     * @param page    the page
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a
     * {@link Page}
     */
    @Nonnull
    public static String getURL(@Nonnull SlingHttpServletRequest request, @Nonnull Page page) {
        String vanityURL = page.getVanityUrl();
        return StringUtils.isEmpty(vanityURL) ? request.getContextPath() + page.getPath() + ".html" : request.getContextPath() + vanityURL;
    }

    /**
     * Returns a map (resource name => Sling Model class) of the given resource children's Sling Models that can be adapted to {@link T}.
     *
     * @param slingRequest The current request.
     * @param modelClass  The Sling Model class to be adapted to.
     * @return Returns a map (resource name => Sling Model class) of the given resource children's Sling Models that can be adapted to {@link T}.
     */
    @Nonnull
    public static <T> Map<String, T> getChildModels(@Nonnull SlingModelFilter slingModelFilter, @Nonnull ModelFactory modelFactory, @Nonnull SlingHttpServletRequest slingRequest,
                                              @Nonnull Class<T> modelClass) {
        Map<String, T> itemWrappers = new LinkedHashMap<>();

        for (final Resource child : slingModelFilter.filterChildResources(slingRequest.getResource().getChildren())) {
            itemWrappers.put(child.getName(), modelFactory.getModelFromWrappedRequest(slingRequest, child, modelClass));
        }

        return  itemWrappers;
    }

    public enum Heading {

        H1("h1"),
        H2("h2"),
        H3("h3"),
        H4("h4"),
        H5("h5"),
        H6("h6");

        private String element;

        Heading(String element) {
            this.element = element;
        }

        public static Heading getHeading(String value) {
            for (Heading heading : values()) {
                if (StringUtils.equalsIgnoreCase(heading.element, value)) {
                    return heading;
                }
            }
            return null;
        }

        public String getElement() {
            return element;
        }
    }


}
