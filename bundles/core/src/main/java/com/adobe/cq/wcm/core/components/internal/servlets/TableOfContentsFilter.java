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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.models.v1.TableOfContentsImpl;
import com.adobe.cq.wcm.core.components.models.TableOfContents;
import com.day.cq.wcm.api.WCMMode;

/**
 * Intercepts all the HTTP requests made to /editor.html or a html page inside /content/.
 * Creates a response wrapper - {@link CharResponseWrapper} in which all the servlets/filters called after this filter,
 * store the response content.
 * Gets the response content from this wrapper, modifies it and copies it into the original response object.
 */
@Designate(
    ocd = TableOfContentsFilter.Config.class
)
@Component(
    configurationPolicy = ConfigurationPolicy.REQUIRE,
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

    private boolean enabled;

    @ObjectClassDefinition(
        name = "Core Components TableOfContentsFilter Config",
        description = "Configuration for enabling TableOfContentsFilter"
    )
    public @interface Config {
        @AttributeDefinition(
            name = "Enabled",
            description = "Whether TableOfContentsFilter component will be activated or not"
        )
        boolean enabled() default false;
    }

    @Activate
    protected void activate(Config config) {
        enabled = config.enabled();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("Initialising {}", TableOfContentsFilter.class.getName());
    }

    @Override
    public void destroy() {
        LOGGER.debug("Destroying {}", TableOfContentsFilter.class.getName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        if (!enabled) {
            LOGGER.debug("{} not enabled, bypassing it", TableOfContentsFilter.class.getName());
            chain.doFilter(request, response);
            return;
        }

        CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, responseWrapper);
        String originalContent = responseWrapper.toString();
        Boolean containsTableOfContents = (Boolean) request.getAttribute(TableOfContentsImpl.TOC_REQUEST_ATTR_FLAG);
        if (responseWrapper.getContentType() != null && responseWrapper.getContentType().contains("text/html") &&
            (containsTableOfContents != null && containsTableOfContents)) {

            Document document = Jsoup.parse(originalContent);

            Elements tocPlaceholderElements = document.getElementsByClass(TableOfContents.TOC_PLACEHOLDER_CLASS);
            Map<String, Integer> customIDs = new HashMap<String, Integer>();
            for (Element tocPlaceholderElement : tocPlaceholderElements) {
                Element tableOfContents = getTableOfContents(tocPlaceholderElement, customIDs);
                String id = tocPlaceholderElement.id();
                tocPlaceholderElement.empty();
                tocPlaceholderElement.clearAttributes();
                tocPlaceholderElement.addClass(TableOfContents.TOC_CONTENT_CLASS);
                if(!id.isEmpty()) {
                    tocPlaceholderElement.id(id);
                }
                if(tableOfContents != null) {
                    tocPlaceholderElement.appendChild(tableOfContents);
                    WCMMode wcmMode = WCMMode.fromRequest(request);
                    if(wcmMode == WCMMode.EDIT || wcmMode == WCMMode.PREVIEW) {
                        Elements tocTemplatePlaceholderElement = tocPlaceholderElement
                            .parent()
                            .select("." + TableOfContents.TOC_TEMPLATE_PLACEHOLDER_CLASS);
                        tocTemplatePlaceholderElement.remove();
                    }
                }
            }

            CharArrayWriter charWriter = new CharArrayWriter();
            charWriter.write(document.outerHtml());
            String alteredContent = charWriter.toString();

            response.getWriter().write(alteredContent);
        } else {
            LOGGER.debug("{} component not present on page, so not parsing the page output",
                TableOfContents.class.getName());
            response.getWriter().write(originalContent);
        }
    }

    /**
     * Creates a TOC {@link Element} by getting all the TOC config from the
     * TOC placeholder DOM {@link Element}' data attributes.
     * @param tocPlaceholderElement - TOC placeholder dom element
     * @param customIDs - A map of custom generated IDs to their count in the dom
     * @return Independent TOC element, not attached to the DOM
     */
    private Element getTableOfContents(Element tocPlaceholderElement, Map<String, Integer> customIDs) {
        TableOfContents.ListType listType = tocPlaceholderElement.hasAttr(TableOfContents.TOC_DATA_ATTR_LIST_TYPE)
            ? TableOfContents.ListType.fromString(
                tocPlaceholderElement.attr(TableOfContents.TOC_DATA_ATTR_LIST_TYPE))
            : TableOfContentsImpl.DEFAULT_LIST_TYPE;
        String listTag = listType.getTagName();
        TableOfContents.HeadingLevel startLevel =
            tocPlaceholderElement.hasAttr(TableOfContents.TOC_DATA_ATTR_START_LEVEL)
                ? TableOfContents.HeadingLevel.fromStringOrDefault(
                    tocPlaceholderElement.attr(TableOfContents.TOC_DATA_ATTR_START_LEVEL),
                    TableOfContentsImpl.DEFAULT_START_LEVEL)
                : TableOfContentsImpl.DEFAULT_START_LEVEL;
        TableOfContents.HeadingLevel stopLevel =
            tocPlaceholderElement.hasAttr(TableOfContents.TOC_DATA_ATTR_STOP_LEVEL)
                ? TableOfContents.HeadingLevel.fromStringOrDefault(
                    tocPlaceholderElement.attr(TableOfContents.TOC_DATA_ATTR_STOP_LEVEL),
                    TableOfContentsImpl.DEFAULT_STOP_LEVEL)
                : TableOfContentsImpl.DEFAULT_STOP_LEVEL;
        String[] includeClasses = tocPlaceholderElement.hasAttr(TableOfContents.TOC_DATA_ATTR_INCLUDE_CLASSES)
            ? tocPlaceholderElement.attr(TableOfContents.TOC_DATA_ATTR_INCLUDE_CLASSES).split(",")
            : null;
        String[] ignoreClasses = tocPlaceholderElement.hasAttr(TableOfContents.TOC_DATA_ATTR_IGNORE_CLASSES)
            ? tocPlaceholderElement.attr(TableOfContents.TOC_DATA_ATTR_IGNORE_CLASSES).split(",")
            : null;

        if(startLevel.getIntValue() > stopLevel.getIntValue()) {
            LOGGER.warn("Invalid start and stop levels, startLevel={}, stopLevel={}",
                startLevel.getValue(), stopLevel.getValue());
            return null;
        }

        Document document = tocPlaceholderElement.ownerDocument();

        String includeCssSelector;
        if(includeClasses == null || includeClasses.length == 0) {
            List<String> selectors = new ArrayList<>();
            for(int level = startLevel.getIntValue(); level <= stopLevel.getIntValue(); level++) {
                selectors.add(getHeadingTagName(level));
            }
            includeCssSelector = StringUtils.join(selectors, ",");
        } else {
            includeCssSelector = getCssSelectorString(
                includeClasses, startLevel.getIntValue(), stopLevel.getIntValue()
            );
        }
        Elements includeElements = document.select(includeCssSelector);

        Set<Element> ignoreElementsSet = new HashSet<>();

        if(ignoreClasses != null && ignoreClasses.length != 0) {
            String ignoreCssSelector = getCssSelectorString(
                ignoreClasses, startLevel.getIntValue(), stopLevel.getIntValue()
            );
            Elements ignoreElements = document.select(ignoreCssSelector);
            ignoreElementsSet = new HashSet<>(ignoreElements);
        }

        List<Element> validElements = new ArrayList<>();
        for(Element element : includeElements) {
            if(!ignoreElementsSet.contains(element)
                && !"".contentEquals(element.text().trim())) {
                validElements.add(element);
            }
        }
        return getNestedList(listTag, validElements.listIterator(), 0, customIDs);
    }

    /**
     * Converts a list of ignore/include class names, heading start level and heading stop level
     * into a CSS selector string
     * @param classNames - a non-empty array of include or ignore class names of the TOC
     * @param startLevel - heading start level of the TOC
     * @param stopLevel - heading stop level of the TOC
     * @return CSS selector string
     */
    private String getCssSelectorString(String[] classNames, int startLevel, int stopLevel) {
        List<String> selectors = new ArrayList<>();
        for(String className: classNames) {
            for(int level = startLevel; level <= stopLevel; level++) {
                selectors.add("." + className + " " + getHeadingTagName(level));
                selectors.add(getHeadingTagName(level) + "." + className);
            }
        }
        return StringUtils.join(selectors, ",");
    }

    /**
     * Recursive method to create a nested list of TOC depending upon the heading levels of consecutive heading elements
     * @param listTag - 'ul' for unordered list or 'ol' for ordered list
     * @param headingElementsIterator - Iterator of list of heading elements to be included in TOC
     * @param parentHeadingLevel - Heading level of the parent of the current nesting level
     * @param customIDs - A map of custom generated IDs to their count in the dom
     * @return Current nested TOC element containing all heading elements with levels >= parent heading level
     */
    private Element getNestedList(String listTag, ListIterator<Element> headingElementsIterator,
                                  int parentHeadingLevel, Map<String, Integer> customIDs) {
        if(!headingElementsIterator.hasNext()) {
            return null;
        }
        Element list = new Element(listTag);
        Element headingElement = headingElementsIterator.next();
        Element listItem = getListItemElement(headingElement, customIDs);
        int previousHeadingLevel = getHeadingLevel(headingElement);
        list.appendChild(listItem);
        while(headingElementsIterator.hasNext()) {
            headingElement = headingElementsIterator.next();
            int currentHeadingLevel = getHeadingLevel(headingElement);
            if(currentHeadingLevel == previousHeadingLevel ||
                (currentHeadingLevel < previousHeadingLevel && currentHeadingLevel > parentHeadingLevel)) {
                listItem = getListItemElement(headingElement, customIDs);
                list.appendChild(listItem);
                previousHeadingLevel = currentHeadingLevel;
            } else if(currentHeadingLevel > previousHeadingLevel) {
                headingElementsIterator.previous();
                list.children().last().appendChild(
                    getNestedList(listTag, headingElementsIterator, previousHeadingLevel, customIDs)
                );
            } else {
                headingElementsIterator.previous();
                return list;
            }
        }
        return list;
    }

    /**
     * Creates list item element('li') from the heading element.
     * Adds an internal link on the 'li' element to the provided heading element.
     * Adds 'id' attribute on heading element if not already present, using its text content.
     * @param headingElement - DOM heading element
     * @param customIDs - A map of custom generated IDs to their count in the dom
     * @return Independent 'li' element, not attached to the DOM
     */
    private Element getListItemElement(Element headingElement, Map<String, Integer> customIDs) {
        String id = headingElement.attr("id");
        if("".contentEquals(id)) {
            id = headingElement.text()
                .trim()
                .toLowerCase()
                .replaceAll("\\s", "-");
            customIDs.put(id, 1 + customIDs.getOrDefault(id, 0));
            id += customIDs.get(id) == 1
                ? ""
                : "-" + (customIDs.get(id) - 1);
            headingElement.attr("id", id);
        }
        Element listItem = new Element("li");
        Element anchorTag = new Element("a");
        anchorTag.attr("href", "#" + id);
        anchorTag.appendText(headingElement.text());
        listItem.appendChild(anchorTag);
        return listItem;
    }

    /**
     * Returns heading level('1' to '6') of the heading element
     * @param headingElement DOM heading element
     * @return Integer representing the heading level of the given heading element
     */
    private int getHeadingLevel(Element headingElement) {
        return headingElement.tagName().charAt(1) - '0';
    }

    /**
     * Returns heading tag name('h1' to 'h6') from integer level
     * @param level Integer representing the heading level
     * @return Heading tag name
     */
    private String getHeadingTagName(int level) {
        return "h" + level;
    }
}
