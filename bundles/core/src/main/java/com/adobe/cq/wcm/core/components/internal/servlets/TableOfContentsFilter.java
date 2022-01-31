/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.*;

@Component(service = Filter.class,
    property = {
        Constants.SERVICE_RANKING + "Integer=999"})
@SlingServletFilter(scope = {SlingServletFilterScope.REQUEST},
    pattern = "/content/.*",
    extensions = {"html"},
    methods = {"GET"})
public class TableOfContentsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponseWrapper) response);

        chain.doFilter(request, wrapper);

        if (wrapper.getContentType().contains("text/html")) {
            String originalContent = wrapper.toString();

            Document document = Jsoup.parse(originalContent);

            int startLevel = 1;
            int stopLevel = 6;
            Set<String> allowedHeadingTags = new HashSet<>();
            for(int level = startLevel; level <= stopLevel; level++) {
                allowedHeadingTags.add("h" + level);
            }

            ListType listType = ListType.getListType("ordered");
            String listTag = listType == ListType.OL ? "ol" : "ul";

            Elements allElements = document.getAllElements();
            List<Element> headingElements = new ArrayList();
            for (Element element : allElements) {
                if(!allowedHeadingTags.contains(element.tagName()) || element.text().length() == 0) {
                    continue;
                }
                headingElements.add(element);
            }

            Element toc = getNestedList(listTag, headingElements.listIterator(), 0);

            Elements tocPlaceholderElements = document.getElementsByClass("table-of-contents-placeholder");
            for (Element tocPlaceholderElement : tocPlaceholderElements) {
                tocPlaceholderElement.appendChild(toc);
            }

            CharArrayWriter charWriter = new CharArrayWriter();
            charWriter.write(document.outerHtml());
            String alteredContent = charWriter.toString();
            response.setContentLength(alteredContent.length());
            response.getWriter().write(alteredContent);
        }
    }

    private Element getNestedList(String listTag, ListIterator<Element> headingElementsIterator,
                                  int parentHeadingLevel) {
        if(!headingElementsIterator.hasNext()) {
            return new Element("");
        }
        Element list = new Element(listTag);
        Element headingElement = headingElementsIterator.next();
        Element listItem = getListItemElement(headingElement);
        int previousHeadingLevel = getHeadingLevel(headingElement);
        list.appendChild(listItem);
        while(headingElementsIterator.hasNext()) {
            headingElement = headingElementsIterator.next();
            int currentHeadingLevel = getHeadingLevel(headingElement);
            if(currentHeadingLevel == previousHeadingLevel ||
                (currentHeadingLevel < previousHeadingLevel && currentHeadingLevel > parentHeadingLevel)) {
                listItem = getListItemElement(headingElement);
                list.appendChild(listItem);
                previousHeadingLevel = currentHeadingLevel;
            } else if(currentHeadingLevel > previousHeadingLevel) {
                headingElementsIterator.previous();
                list.appendChild(getNestedList(listTag, headingElementsIterator, previousHeadingLevel));
            } else if(currentHeadingLevel < previousHeadingLevel && currentHeadingLevel <= parentHeadingLevel) {
                headingElementsIterator.previous();
                return list;
            }
        }
        return list;
    }

    /**
     * Creates list item element from the heading element.
     * Adds 'id' attribute on heading element if not already present.
     */
    private Element getListItemElement(Element headingElement) {
        String id = headingElement.attr("id");
        if("".contentEquals(id)) {
            id = UUID.randomUUID().toString();
            headingElement.attr("id", id);
        }
        Element listItem = new Element("li");
        Element anchorTag = new Element("a");
        anchorTag.attr("href", "#" + id);
        anchorTag.appendText(headingElement.text());
        listItem.appendChild(anchorTag);
        return listItem;
    }

    private int getHeadingLevel(Element headingElement) {
        return headingElement.tagName().charAt(1) - '0';
    }

    public enum ListType {
        OL("ordered"),
        UL("unordered");

        private String value;

        ListType(String value) {
            this.value = value;
        }

        public static ListType getListType(String value) {
            for (ListType listType : values()) {
                if (StringUtils.equalsIgnoreCase(listType.value, value)) {
                    return listType;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }
    }
}
