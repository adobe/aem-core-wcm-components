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

import com.day.cq.wcm.api.WCMMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

@Component(
    service = Filter.class,
    property = {Constants.SERVICE_RANKING + "Integer=999"}
)
@SlingServletFilter(
    scope = {SlingServletFilterScope.REQUEST},
    pattern = "/content/.*",
    resourceTypes = "cq:Page",
    extensions = {"html"},
    methods = {"GET"}
)
public class TableOfContentsFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableOfContentsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponseWrapper) response);
        chain.doFilter(request, responseWrapper);
        String originalContent = responseWrapper.toString();
        Boolean containsTableOfContents = (Boolean) request.getAttribute("contains-table-of-contents");
        if (responseWrapper.getContentType().contains("text/html") &&
            (containsTableOfContents != null && containsTableOfContents)) {

            Document document = Jsoup.parse(originalContent);

            Elements tocPlaceholderElements = document.getElementsByClass("table-of-contents-placeholder");
            for (Element tocPlaceholderElement : tocPlaceholderElements) {
                Element tableOfContents = getTableOfContents(tocPlaceholderElement);
                tocPlaceholderElement.empty();
                tocPlaceholderElement.clearAttributes();
                tocPlaceholderElement.addClass("table-of-contents");
                if(tableOfContents != null) {
                    tocPlaceholderElement.appendChild(tableOfContents);
                    WCMMode wcmMode = WCMMode.fromRequest(request);
                    if(wcmMode == WCMMode.EDIT || wcmMode == WCMMode.PREVIEW) {
                        Elements tocTemplatePlaceholderElement = tocPlaceholderElement
                            .parent()
                            .select(".table-of-contents-template-placeholder");
                        tocTemplatePlaceholderElement.remove();
                    }
                }
            }

            CharArrayWriter charWriter = new CharArrayWriter();
            charWriter.write(document.outerHtml());
            String alteredContent = charWriter.toString();

            response.setContentLength(alteredContent.length());
            response.getWriter().write(alteredContent);
        } else {
            response.setContentLength(originalContent.length());
            response.getWriter().write(originalContent);
        }
    }

    private Element getTableOfContents(Element tocPlaceholderElement) {
        String listType = tocPlaceholderElement.hasAttr("data-list-type")
            ? tocPlaceholderElement.attr("data-list-type")
            : "unordered";
        String listTag = "ordered".contentEquals(listType) ? "ol" : "ul";
        int startLevel = tocPlaceholderElement.hasAttr("data-start-level")
            ? Math.max(1, Integer.parseInt(tocPlaceholderElement.attr("data-start-level")))
            : 1;
        int stopLevel = tocPlaceholderElement.hasAttr("data-stop-level")
            ? Math.min(6, Integer.parseInt(tocPlaceholderElement.attr("data-stop-level")))
            : 6;
        String[] includeClasses = tocPlaceholderElement.hasAttr("data-include-classes")
            ? tocPlaceholderElement.attr("data-include-classes").split(",")
            : null;
        String[] ignoreClasses = tocPlaceholderElement.hasAttr("data-ignore-classes")
            ? tocPlaceholderElement.attr("data-ignore-classes").split(",")
            : null;

        Document document = tocPlaceholderElement.ownerDocument();

        String includeCssSelector;
        if(includeClasses == null || includeClasses.length == 0) {
            List<String> selectors = new ArrayList<>();
            for(int level = startLevel; level <= stopLevel; level++) {
                selectors.add("h" + level);
            }
            includeCssSelector = StringUtils.join(selectors, ",");
        } else {
            includeCssSelector = getCssSelectorString(includeClasses, startLevel, stopLevel);
        }
        Elements includeElements = document.select(includeCssSelector);

        if(ignoreClasses == null || ignoreClasses.length == 0) {
            return getNestedList(listTag, includeElements.listIterator(), 0);
        }
        String ignoreCssSelector = getCssSelectorString(ignoreClasses, startLevel, stopLevel);
        Elements ignoreElements = document.select(ignoreCssSelector);

        Set<Element> ignoreElementsSet = new HashSet<>(ignoreElements);

        List<Element> validElements = new ArrayList<>();
        for(Element element : includeElements) {
            if(!ignoreElementsSet.contains(element)
                && !"".contentEquals(element.text().trim())) {
                validElements.add(element);
            }
        }
        return getNestedList(listTag, validElements.listIterator(), 0);
    }

    private String getCssSelectorString(String[] classNames, int startLevel, int stopLevel) {
        if(classNames == null || classNames.length == 0) {
            return "";
        }
        List<String> selectors = new ArrayList<>();
        for(String className: classNames) {
            for(int level = startLevel; level <= stopLevel; level++) {
                selectors.add("." + className + " h" + level);
                selectors.add("h" + level + "." + className);
            }
        }
        return StringUtils.join(selectors, ",");
    }

    private Element getNestedList(String listTag, ListIterator<Element> headingElementsIterator,
                                  int parentHeadingLevel) {
        if(!headingElementsIterator.hasNext()) {
            return null;
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
            id = headingElement.text()
                .trim()
                .toLowerCase()
                .replaceAll("\\s", "-");
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
}
