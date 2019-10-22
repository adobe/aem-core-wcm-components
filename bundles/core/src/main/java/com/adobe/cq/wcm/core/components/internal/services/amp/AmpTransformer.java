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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpUtil.AMP_ONLY;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpUtil.AMP_SELECTOR;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpUtil.DOT;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpUtil.NO_AMP;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Adds AMP specific elements to the page head based on the request's selectors and the configured AMP mode.
 */
public class AmpTransformer implements Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(AmpTransformer.class);

    private static final String AMP_REL = "amphtml";

    private static final String HREF_ATTR = "href";

    private static final String HTML = "html";

    private static final String HTML_REL = "canonical";

    private static final String LINK_ELEMENT = "link";

    private static final String PAIRED_AMP = "pairedAmp";

    private static final String REL_ATTR = "rel";

    private String ampMode;

    private AmpTransformerFactory.Cfg cfg;

    private ContentHandler contentHandler;

    private boolean isAmp;

    private boolean jsAppended;

    private String jsContent;

    private SlingHttpServletRequest slingRequest;

    AmpTransformer(AmpTransformerFactory.Cfg cfg) {
        this.cfg = cfg;
    }

    @Override
    public void init(ProcessingContext processingContext,
                     ProcessingComponentConfiguration processingComponentConfiguration) {

        slingRequest = processingContext.getRequest();

        ampMode = AmpUtil.getAmpMode(slingRequest);

        isAmp = ampMode != null && !ampMode.isEmpty() && !ampMode.equals(NO_AMP);

        if (isAmp) {
            jsContent = getJsContent(processingContext.getRequest());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        // Append AMP sibling page link element.
        if (isAmp) {

            PageManager pageManager = slingRequest.getResourceResolver().adaptTo(PageManager.class);
            if (pageManager != null) {

                Page page = pageManager.getContainingPage(slingRequest.getResource());
                if (page != null) {

                    AttributesImpl attributes = getLinkAttributes(page.getPath());
                    if (attributes != null) {
                        contentHandler.startElement("", LINK_ELEMENT, LINK_ELEMENT, attributes);
                    }
                }
            }
        }

        contentHandler.startElement(uri, localName, qName, atts);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        // Append the aggregated js to the head.
        if (isAmp && !jsAppended) {
            contentHandler.characters(jsContent.toCharArray(), 0, jsContent.length());
            jsAppended = true;
        }

        contentHandler.characters(ch, start, length);
    }

    /**
     * Constructs the AMP link element attributes needed for the current AMP mode and request selectors.
     * @param pagePath The path of the requested page.
     * @return The AMP specific link element attributes.
     */
    private AttributesImpl getLinkAttributes(String pagePath) {

        AttributesImpl attributes = new AttributesImpl();

        boolean isAmpSelector = Arrays.asList(slingRequest.getRequestPathInfo().getSelectors()).contains(AMP_SELECTOR);

        // Set the link element attributes based on the AMP mode and presence of the 'amp' request selector.
        if (ampMode.equals(PAIRED_AMP)) {
            if (isAmpSelector) {
                attributes.addAttribute("", REL_ATTR, REL_ATTR, "", AMP_REL);
                attributes.addAttribute("", HREF_ATTR, HREF_ATTR, "", pagePath + DOT + HTML);
            } else {
                attributes.addAttribute("", REL_ATTR, REL_ATTR, "", HTML_REL);
                attributes.addAttribute("", HREF_ATTR, HREF_ATTR, "", pagePath + DOT + AMP_SELECTOR + DOT + HTML);
            }
        } else if (ampMode.equals(AMP_ONLY)) {
            attributes.addAttribute("", REL_ATTR, REL_ATTR, "", AMP_REL);
            attributes.addAttribute("", HREF_ATTR, HREF_ATTR, "", pagePath + DOT + AMP_SELECTOR + DOT + HTML);
        } else {
            return null;
        }

        return attributes;
    }

    /**
     * Aggregates the AMP specific component js.
     * @param request The request used to resolve the page and its resources.
     * @return The aggregated AMP component js.
     */
    private String getJsContent(SlingHttpServletRequest request) {

        if (StringUtils.isBlank(cfg.getHeadlibName())) {
            LOG.error("Headlib name not defined. Failed to aggregate AMP component js.");
            return "";
        }

        StringBuilder output = new StringBuilder();

        // Retrieve a set of the component's resource types.
        Set<String> resourceTypes = getResourceTypes(request.getResource(), new HashSet<>());

        // Iterate through each resource type and read its AMP headlib.
        for (String resourceType : resourceTypes) {

            // Resolve the resource type's AMP headlib.
            Resource headLibResource;
            String headLibPath = resourceType + "/" + cfg.getHeadlibName() + "/" + JcrConstants.JCR_CONTENT;
            headLibResource = resolveHeadLibs(request.getResourceResolver(), headLibPath);
            if (headLibResource == null) {
                LOG.trace("No custom headlib for resource type {}.", resourceType);
                continue;
            }

            // Read the input stream from the resource type's AMP headlib.
            InputStream is = headLibResource.adaptTo(InputStream.class);
            if (is == null) {
                LOG.debug("Failed to read input stream from {}.", headLibPath);
                continue;
            }

            // Read the headlib input stream and append its data to the output.
            try {
                output.append(IOUtils.toString(is, StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOG.error("Failed to read headlib content.", e);
            }
        }

        return output.toString();
    }

    /**
     * Retrieves the resource types of the given resource and all of its child resources.
     * @param resource The resource to start retrieving resources types from.
     * @param resourceTypes String set to append resource type values to.
     * @return String set of resource type values found.
     */
    private Set<String> getResourceTypes(Resource resource, Set<String> resourceTypes) {

        if (resource == null) {
            return resourceTypes;
        }

        // Add resource type to return set if allowed by the resource type regex.
        String resourceType = resource.getResourceType();
        if (StringUtils.isBlank(cfg.getHeadlibResourceTypeRegex())
            || resourceType.matches(cfg.getHeadlibResourceTypeRegex())) {
            resourceTypes.add(resourceType);
        }

        // Iterate through the resource's children and recurse through them for resource types.
        for (Resource child : resource.getChildren()) {
            getResourceTypes(child, resourceTypes);
        }

        return resourceTypes;
    }

    /**
     * Resolves the resource of a headlib.
     * @param resolver Provides search paths used to turn relative paths to full paths and resolves the resource.
     * @param headlibPath The path of the headlib to resolve.
     * @return The resource of the headlib path.
     */
    private Resource resolveHeadLibs(ResourceResolver resolver, String headlibPath) {

        // Resolve absolute headlib path.
        if (headlibPath.startsWith("/")) {
            return resolver.getResource(headlibPath);
        }

        // Resolve relative headlib path.
        for (String path : resolver.getSearchPath()) {

            Resource resource = resolver.getResource(path + headlibPath);
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    @Override
    public void dispose() { }

    @Override
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        contentHandler.endElement(uri, localName, qName);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        contentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        contentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        contentHandler.processingInstruction(target, data);
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        contentHandler.setDocumentLocator(locator);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        contentHandler.skippedEntity(name);
    }

    @Override
    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        contentHandler.startPrefixMapping(prefix, uri);
    }
}
