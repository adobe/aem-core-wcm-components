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
package apps.core.wcm.components.title.v1.title;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

public class Title extends WCMUsePojo {

    public static final String PROP_TITLE = "jcr:title";
    public static final String PROP_PAGE_TITLE = "pageTitle";
    public static final String PROP_TYPE = "type";
    public static final String PROP_DEFAULT_TYPE = "defaultType";

    private String element;
    private String text;

    public String getElement() {
        return element;
    }

    public String getText() {
        return text;
    }

    @Override
    public void activate() throws Exception {
        ValueMap currentResourceProperties = getProperties();
        text = currentResourceProperties.get(PROP_TITLE, String.class);
        if (StringUtils.isEmpty(text)) {
            Page resourcePage = getResourcePage();
            if (resourcePage != null) {
                ValueMap resourcePageProperties = resourcePage.getProperties();
                text = resourcePageProperties.get(PROP_TITLE, resourcePageProperties.get(PROP_PAGE_TITLE, String.class));
            }
        }
        if (StringUtils.isEmpty(text)) {
            ValueMap currentPageProperties = getPageProperties();
            if (currentPageProperties != null) {
                text = currentPageProperties.get(PROP_TITLE, currentPageProperties.get(PROP_PAGE_TITLE, String.class));
            }
        }
        if (StringUtils.isEmpty(text)) {
            Page currentPage = getCurrentPage();
            if (currentPage != null) {
                text = currentPage.getName();
            }
        }
        element = currentResourceProperties.get(PROP_TYPE, String.class);
        if (StringUtils.isNotEmpty(element)) {
            Heading heading = Heading.getHeading(element);
            if (heading == null) {
                element = null;
            }
        } else {
            Style style = getCurrentStyle();
            if (style != null) {
                element = getCurrentStyle().get(PROP_DEFAULT_TYPE, String.class);
                if (StringUtils.isNotEmpty(element)) {
                    Heading heading = Heading.getHeading(element);
                    if (heading == null) {
                        element = null;
                    }
                }
            }
        }

    }

    private enum Heading {

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

        private static Heading getHeading(String value) {
            for (Heading heading : values()) {
                if (heading.element.equalsIgnoreCase(value)) {
                    return heading;
                }
            }
            return null;
        }
    }

}
