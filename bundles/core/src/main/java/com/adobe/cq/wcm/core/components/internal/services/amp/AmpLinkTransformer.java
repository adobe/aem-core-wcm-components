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

import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.AMP_ONLY;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.AMP_SELECTOR;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.DOT;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.NO_AMP;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
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
import java.util.Arrays;

/**
 * Adds AMP specific link elements to the page head based on the request's selectors and the configured AMP mode.
 */
public class AmpLinkTransformer implements Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(AmpLinkTransformer.class);

    private static final String AMP_REL = "amphtml";

    private static final String HREF_ATTR = "href";

    private static final String HTML = "html";

    private static final String HTML_REL = "canonical";

    private static final String LINK_ELEMENT = "link";

    private static final String PAIRED_AMP = "pairedAmp";

    private static final String REL_ATTR = "rel";

    private String ampMode;

    private ContentHandler contentHandler;

    private SlingHttpServletRequest slingRequest;

    @Override
    public void init(ProcessingContext processingContext,
                     ProcessingComponentConfiguration processingComponentConfiguration)
        throws IOException {

        slingRequest = processingContext.getRequest();

        ampMode = AmpHelperUtil.getAmpMode(slingRequest);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        // Append AMP sibling page link element.
        if (ampMode != null && !ampMode.isEmpty() && !ampMode.equals(NO_AMP)) {

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

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Override
    public void dispose() { }

    @Override
    public void setDocumentLocator(Locator locator) {
        contentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        contentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        contentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        contentHandler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        contentHandler.characters(ch, start, length);
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
    public void skippedEntity(String name) throws SAXException {
        contentHandler.skippedEntity(name);
    }
}
