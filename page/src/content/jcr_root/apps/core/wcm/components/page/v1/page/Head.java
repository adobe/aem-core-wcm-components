/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.page.v1.page;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Design;

public class Head extends WCMUsePojo {

    public String[] keywords;
    public String designPathCSS;
    public String icoFavicon;
    public String pngFavicon;
    public String title;
    public String staticDesignPath;

    @Override
    public void activate() throws Exception {
        Page currentPage = getCurrentPage();
        Tag[] tags = currentPage.getTags();
        ResourceResolver resourceResolver = getResourceResolver();
        keywords = new String[tags.length];
        int index = 0;
        for (Tag tag : tags) {
            keywords[index++] = tag.getTitle();
        }
        Design design = getCurrentDesign();
        if (design != null) {
            String designPath = design.getPath();
            if (StringUtils.isNotEmpty(designPath)) {
                designPathCSS = designPath + ".css";
            }
            icoFavicon = designPath + "/favicon.ico";
            pngFavicon = designPath + "/favicon.png";
            if (resourceResolver.getResource(icoFavicon) == null) {
                icoFavicon = null;
            }
            if (resourceResolver.getResource(pngFavicon) == null) {
                pngFavicon = null;
            }
            if (resourceResolver.getResource(designPath + "/static.css") != null) {
                staticDesignPath = designPath + "/static.css";
            }
        }
        title = currentPage.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = currentPage.getName();
        }
    }

    /**
     * Returns an array with the page's keywords.
     *
     * @return an array of keywords represented as {@link String}s; the array can be empty if no keywords have been defined for the page
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Retrieves the page's design path.
     *
     * @return the design path as a {@link String}
     */
    public String getDesignPath() {
        return designPathCSS;
    }

    /**
     * Retrieves the static design path if {@code static.css} exists in the design path.
     *
     * @return the static design path if it exists, {@code null} otherwise
     */
    public String getStaticDesignPath() {
        return staticDesignPath;
    }

    /**
     * Retrieves the ICO favicon's path ({@code favicon.ico} file), relative to the page's design path.
     *
     * @return the path to the {@code favicon.ico} file relative to the page's design path, if the file exists; {@code null} otherwise
     */
    public String getICOFavicon() {
        return icoFavicon;
    }

    /**
     * Retrieves the PNG favicon's path ({@code favicon.png} file), relative to the page's design path.
     *
     * @return the path to the {@code favicon.png} file relative to the page's design path, if the file exists; {@code null} otherwise
     */
    public String getPNGFavicon() {
        return pngFavicon;
    }

    /**
     * Retrieves the page's title.
     *
     * @return the page title
     */
    public String getTitle() {
        return title;
    }
}
