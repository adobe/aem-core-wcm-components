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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes=" + AllowedHeadingElementsDataSourceServlet.RESOURCE_TYPE_V1,
                "sling.servlet.resourceTypes=" + AllowedHeadingElementsDataSourceServlet.RESOURCE_TYPE_TITLE_V1,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class AllowedHeadingElementsDataSourceServlet extends SlingSafeMethodsServlet {

    public final static String RESOURCE_TYPE_V1 = "core/wcm/components/commons/datasources/allowedheadingelements/v1";
    public final static String RESOURCE_TYPE_TITLE_V1 = "core/wcm/components/title/v1/datasource/allowedtypes";
    public final static String PN_ALLOWED_HEADING_ELEMENTS = "allowedHeadingElements";
    public final static String PN_DEFAULT_HEADING_ELEMENT = "headingElement";
    public final static String PN_ALLOWED_TYPES = "allowedTypes";
    public final static String PN_DEFAULT_TYPE = "type";
    public final static String PN_DEFAULT_TITLE_TYPE = "titleType";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        SimpleDataSource allowedHeadingElementsDataSource = new SimpleDataSource(getAllowedHeadingElements(request).iterator());
        request.setAttribute(DataSource.class.getName(), allowedHeadingElementsDataSource);
    }

    private List<Resource> getAllowedHeadingElements(@NotNull SlingHttpServletRequest request) {
        List<Resource> allowedHeadingElements = new ArrayList<>();
        ResourceResolver resolver = request.getResourceResolver();
        Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        if (contentResource != null && policyManager != null) {
            ContentPolicy policy = policyManager.getPolicy(contentResource);
            if (policy != null) {
                ValueMap props = policy.getProperties();
                if (props != null) {
                    String[] headingElements = props.get(PN_ALLOWED_HEADING_ELEMENTS, String[].class);
                    String[] allowedTypes = props.get(PN_ALLOWED_TYPES, String[].class);
                    String defaultHeadingElement = props.get(PN_DEFAULT_HEADING_ELEMENT, props.get(PN_DEFAULT_TYPE, StringUtils.EMPTY));
                    if (defaultHeadingElement.isEmpty() ) {
                        defaultHeadingElement = props.get(PN_DEFAULT_TITLE_TYPE, StringUtils.EMPTY);
                    }
                    if (headingElements == null || headingElements.length == 0) {
                        headingElements = allowedTypes;
                    }
                    if (headingElements != null && headingElements.length > 0) {
                        for (String headingElement : headingElements) {
                            allowedHeadingElements.add(new HeadingElementResource(headingElement,
                                    StringUtils.equals(headingElement, defaultHeadingElement), resolver));
                        }
                    }
                }
            }
        }
        return allowedHeadingElements;
    }

    private static class HeadingElementResource extends TextValueDataResourceSource {

        private final String elementName;
        private final boolean selected;

        HeadingElementResource(String headingElement, boolean defaultElement, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, RESOURCE_TYPE_NON_EXISTING);
            this.elementName = headingElement;
            this.selected = defaultElement;
        }

        @Override
        public String getText() {
            Heading heading = Heading.getHeading(elementName);
            if (heading != null) {
                return heading.getElement();
            }
            return null;
        }

        @Override
        public String getValue() {
            return elementName;
        }

        @Override
        public boolean getSelected() {
            return selected;
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
