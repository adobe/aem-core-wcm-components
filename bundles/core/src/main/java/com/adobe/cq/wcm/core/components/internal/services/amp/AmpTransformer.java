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

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Adds AMP specific elements to the page head based on the request's selectors and the configured AMP mode.
 */
public class AmpTransformer implements Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(AmpTransformer.class);

    private static final String AMP_REL = "amphtml";

    private static final String HTML = "html";

    private static final String HTML_REL = "canonical";

    private static final String PAIRED_AMP = "pairedAmp";

    private static final String SCRIPT_TAG_REGEX = "(?=<script)";

    private static final String SCRIPT_TAG_CLOSE = "</script>";

    private static final String LINK_ELEMENT = "<link rel=\"%s\" href=\"%s\">";

    private String ampMode;

    private boolean appendAmp;

    private AmpTransformerFactory.Cfg cfg;

    private ContentHandler contentHandler;

    private boolean isAmpSelector;

    private Page page;

    private ResourceResolverFactory resolverFactory;

    private SlingHttpServletRequest slingRequest;

    AmpTransformer(AmpTransformerFactory.Cfg cfg, ResourceResolverFactory resolverFactory) {
        this.cfg = cfg;
        this.resolverFactory = resolverFactory;
    }

    @Override
    public void init(ProcessingContext processingContext,
                     ProcessingComponentConfiguration processingComponentConfiguration) {

        slingRequest = processingContext.getRequest();

        ampMode = AmpUtil.getAmpMode(slingRequest);

        isAmpSelector = Arrays.asList(slingRequest.getRequestPathInfo().getSelectors()).contains(AMP_SELECTOR);

        appendAmp = ampMode != null && !ampMode.isEmpty() && !ampMode.equals(NO_AMP);

        PageManager pageManager = slingRequest.getResourceResolver().adaptTo(PageManager.class);
        if (pageManager == null) {
            LOG.error("Failed to resolve page manager while initializing AMP transformer.");
            return;
        }

        page = pageManager.getContainingPage(slingRequest.getResource());
        if (page == null) {
            LOG.error("Failed to resolve page while initializing AMP transformer");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (appendAmp && localName.equals("head")) {

            appendAmp = false;

            String content = "\n" + getLinkContent();

            if (isAmpSelector) {
                content +="\n" + getJsContent();
            }

            contentHandler.characters(content.toCharArray(), 0, content.length());
        }

        contentHandler.endElement(uri, localName, qName);
    }

    /**
     * Constructs the markup for the AMP link element needed for the current AMP mode and request selectors.
     * @return The AMP specific link element markup.
     */
    private String getLinkContent() {

        String content = "";

        if (page == null) {
            return content;
        }

        // Set the link element attributes based on the AMP mode and presence of the 'amp' request selector.
        if (ampMode.equals(PAIRED_AMP)) {
            if (isAmpSelector) {
                content = String.format(LINK_ELEMENT, HTML_REL, page.getPath() + DOT + HTML);
            } else {
                content = String.format(LINK_ELEMENT, AMP_REL, page.getPath() + DOT + AMP_SELECTOR + DOT + HTML);
            }
        } else if (ampMode.equals(AMP_ONLY)) {
            content = String.format(LINK_ELEMENT, HTML_REL, page.getPath() + DOT + HTML);
        }

        return content;
    }

    /**
     * Aggregates the AMP specific component js.
     * @return The aggregated AMP component js.
     */
    private String getJsContent() {

        if (StringUtils.isBlank(cfg.getHeadlibName())) {
            LOG.error("Headlib name not defined. Failed to aggregate AMP component js.");
            return "";
        }

        StringBuilder output = new StringBuilder();

        // Retrieve a set of the component's resource types.
        Set<String> resourceTypes =
            Utils.getResourceTypes(slingRequest.getResource(), cfg.getHeadlibResourceTypeRegex(), new HashSet<>());

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(
            Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, Utils.CLIENTLIB_SUBSERVICE))) {

            if (page != null) {
                Utils.getTemplateResourceTypes(page, cfg.getHeadlibResourceTypeRegex(), resolver, resourceTypes);
            }

            // Last part of any headlib path.
            String headLibRelPath = "/" + cfg.getHeadlibName() + "/" + JcrConstants.JCR_CONTENT;

            // Iterate through each resource type and read its AMP headlib.
            for (String resourceType : resourceTypes) {

                // Resolve the resource type's AMP headlib.
                Resource headLibResource;
                String headLibPath = resourceType + headLibRelPath;
                headLibResource = Utils.resolveResource(resolver, headLibPath);
                if (headLibResource == null) {
                    LOG.trace("No custom headlib for resource type {}.", resourceType);

                    // Get headLibResource from resource superType.
                    headLibResource = getHeadlibResourceSuperType(resolver, resourceType, headLibRelPath);
                    if (headLibResource == null) {
                        LOG.trace("No custom headlib for resource superType from resource type {}.", resourceType);
                        continue;
                    }
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
        } catch (LoginException e) {
            LOG.error("Unable to get the service resource resolver.");
        }

        return formatJsOutput(output.toString());
    }

    private Resource getHeadlibResourceSuperType(ResourceResolver resolver, String resourceType, String headLibRelPath) {
        Resource resource = Utils.resolveResource(resolver, resourceType);
        if (resource == null) {
            LOG.debug("Can't access resource from resource type {}.", resourceType);
            System.out.println("Nothing for " + resourceType);
            return null;
        }
        // Get resource superType path from the resource type.
        String superTypePath = resource.getResourceSuperType();
        if (superTypePath == null) {
            LOG.trace("No resource superType from resource type {}.", resourceType);
            return null;
        }
        // Get headLibResource from resource superType.
        Resource headLibResource = Utils.resolveResource(resolver, superTypePath + headLibRelPath);
        // Return next superType or headLibResource.
        return headLibResource == null ?
          getHeadlibResourceSuperType(resolver, superTypePath, headLibRelPath) :
          headLibResource;
    }

    private String formatJsOutput(String jsContent) {

        Set<String> scriptTags = new HashSet<>();
        for (String scriptTag : jsContent.split(SCRIPT_TAG_REGEX)) {
            int endIndex = scriptTag.indexOf(SCRIPT_TAG_CLOSE);
            if (endIndex > -1) {
                scriptTags.add(scriptTag.substring(0, endIndex + SCRIPT_TAG_CLOSE.length()));
            }
        }

        StringBuilder output = new StringBuilder();
        for (String scriptTag : scriptTags) {
            output.append(scriptTag);
            output.append("\n");
        }

        return output.toString();
    }

    @Override
    public void dispose() { }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        contentHandler.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
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
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        contentHandler.startElement(uri, localName, qName, atts);
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
