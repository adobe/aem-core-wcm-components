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

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.DataType;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.ContentFragment;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class ContentFragmentElementImplTest {

    @Test
    public void testElementGetParagraphsNotText() {
        ContentFragment.Element element = getMockedElement( null, "application/pdf", false);
        String[] paragraphs = element.getParagraphs();
        assertNull(paragraphs);
    }

    @Test
    public void testElementGetParagraphsNotMultiValueNullContent() {
        ContentFragment.Element element = getMockedElement( null,"text/html", false);
        String[] paragraphs = element.getParagraphs();
        assertNull(paragraphs);
    }

    @Test
    public void testElementGetParagraphsNotMultiValueWithContent() {
        ContentFragment.Element element = getMockedElement( "simple text","text/plain", false);
        String[] paragraphs = element.getParagraphs();
        assertNotNull(paragraphs);
        assertEquals(1, paragraphs.length);
    }

    @Test
    public void testStyledHtmlParagraphs() throws IOException {
        ContentFragment.Element element = getMockedElement( getContent("sample_html_snippet.dat"),"text/html", false);
        String[] paragraphs = element.getParagraphs();
        assertNotNull(paragraphs);
        assertEquals(10, paragraphs.length);
    }

    @Test
    public void testElementGetParagraphsMultiValue() {
        ContentFragment.Element element = getMockedElement( null,"text/html", true);
        String[] paragraphs = element.getParagraphs();
        assertNull(paragraphs);
    }

    // Helper method for mocking element

    private ContentFragment.Element getMockedElement(String content, String contentType, boolean isMulti) {
        FragmentRenderService renderService = Mockito.mock(FragmentRenderService.class);
        ContentTypeConverter converter = Mockito.mock(ContentTypeConverter.class);
        Resource component = Mockito.mock(Resource.class);
        ContentElement elem = Mockito.mock(ContentElement.class);
        FragmentData fragmentData = Mockito.mock(FragmentData.class);
        DataType dataType = Mockito.mock(DataType.class);
        when(dataType.isMultiValue()).thenReturn(isMulti);
        when(fragmentData.getDataType()).thenReturn(dataType);
        when(fragmentData.getContentType()).thenReturn(contentType);
        when(elem.getValue()).thenReturn(fragmentData);
        when(renderService.render(component)).thenReturn(content);
        return new ContentFragmentImpl.ElementImpl(
                renderService, converter,
                component, elem,
                null
        );
    }

    private String getContent(String filename) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/paragraphs/" + filename), "utf-8");
    }

}
