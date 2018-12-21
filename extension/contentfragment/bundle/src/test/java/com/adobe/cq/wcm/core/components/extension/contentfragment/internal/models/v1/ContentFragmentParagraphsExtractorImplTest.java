/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v1;

import org.apache.sling.commons.html.internal.TagsoupHtmlParser;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ContentFragmentParagraphsExtractorImplTest {

    private static final String EXPECTED_1ST_PARAGRAPH = "<p style=\"text-align: center;\">Multi-<b><span class=\"rte-annotation\" data-annotation=\"annotation-1\">paragraph</span></b> 1</p>";
    private static final String EXPECTED_2ND_PARAGRAPH = "<p><img src=\"/content/dam/we-retail/en/activities/biking/mountain-biking.jpg\" data-assetref=\"mountain-biking-1544048128400\">&nbsp;</p>";

    @Test
    public void testParsedHTML() throws SAXException {
        ContentFragmentImpl.ParagraphsExtractorImpl extractor = new ContentFragmentImpl.ParagraphsExtractorImpl();
        new TagsoupHtmlParser().parse(getClass().getResourceAsStream("/paragraphs/sample_html_snippet.dat"), "utf-8", extractor);
        Assert.assertEquals(10, extractor.getExtractedParagraphs().length);
        Assert.assertEquals( EXPECTED_1ST_PARAGRAPH, extractor.getExtractedParagraphs()[0]);
        Assert.assertEquals( EXPECTED_2ND_PARAGRAPH, extractor.getExtractedParagraphs()[1]);
    }

    @Test
    public void testParsedNonHTML() throws SAXException {
        ContentFragmentImpl.ParagraphsExtractorImpl extractor = new ContentFragmentImpl.ParagraphsExtractorImpl();
        new TagsoupHtmlParser().parse(getClass().getResourceAsStream("/paragraphs/sample_non_html_snippet.dat"), "utf-8", extractor);
        Assert.assertEquals(0, extractor.getExtractedParagraphs().length);
    }
}
