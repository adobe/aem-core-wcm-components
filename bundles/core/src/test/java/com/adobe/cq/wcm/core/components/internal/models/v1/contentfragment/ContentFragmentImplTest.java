/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentFragmentImplTest extends AbstractContentFragmentTest<ContentFragment> {

    private Logger cfmLogger;
    private Logger modelLogger;

    @Override
    protected String getTestResourcesParentPath() {
        return "/content/contentfragments/jcr:content/root/responsivegrid";
    }

    @Override
    protected Class<ContentFragment> getClassType() {
        return ContentFragment.class;
    }

    @Before
    public void setUp() throws Exception {
        cfmLogger = spy(LoggerFactory.getLogger("FakeLoggerCFM"));
        setFakeLoggerOnClass(DAMContentFragmentImpl.class, cfmLogger);

        modelLogger = spy(LoggerFactory.getLogger("FakeLoggerModel"));
        setFakeLoggerOnClass(ContentFragmentImpl.class, modelLogger);
    }

    @Test
    public void textOnlyNoPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_NO_PATH);
        verify(modelLogger).warn("Please provide a path for the content fragment component.");
        assertNotNull("Model shouldn't be null when no path is set", fragment);
    }

    @Test
    public void textOnlyNonExistingPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_NON_EXISTING_PATH);
        verify(modelLogger).error("Content Fragment can not be initialized because the '{}' does not exist.", "/content/dam/contentfragments/non-existing");
        assertNotNull("Model shouldn't be null when the path does not exist", fragment);
    }

    @Test
    public void textOnlyInvalidPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_INVALID_PATH);
        verify(cfmLogger).error("Content Fragment can not be initialized because '{}' is not a content fragment.", "/content/dam/contentfragments");
        assertNotNull("Model shouldn't be null when the path is not a content fragment", fragment);
    }

    @Test
    public void textOnly() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, MAIN, SECOND_TEXT_ONLY);
    }

    @Test
    public void textOnlyVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_VARIATION);
        assertContentFragment(fragment, VARIATION_NAME, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_TEXT_ONLY);
    }

    @Test
    public void textOnlyNonExistingVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_NON_EXISTING_VARIATION);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, MAIN, SECOND_TEXT_ONLY);
    }

    @Test
    public void textOnlySingleElement() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_SINGLE_ELEMENT);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, SECOND_TEXT_ONLY);
    }

    @Test
    public void textOnlyMultipleElements() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_MULTIPLE_ELEMENTS);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, SECOND_TEXT_ONLY, MAIN);
    }

    @Test
    public void structuredNoPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NO_PATH);
        assertNotNull("Model shouldn't be null when no path is set", fragment);
    }

    @Test
    public void structuredNonExistingPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NON_EXISTING_PATH);
        assertNotNull("Model shouldn't be null when the path does not exist", fragment);
    }

    @Test
    public void structuredOnlyInvalidPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_INVALID_PATH);
        assertNotNull("Model shouldn't be null when the path is not a content fragment", fragment);
    }

    @Test
    public void structured() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void structuredVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_VARIATION);
        assertContentFragment(fragment, VARIATION_NAME, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void structuredNonExistingVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NON_EXISTING_VARIATION);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void structuredNestedModel() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NESTED_MODEL);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE_NESTED, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void structuredSingleElement() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_SINGLE_ELEMENT);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, SECOND_STRUCTURED);
    }

    @Test
    public void structuredMultipleElements() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_MULTIPLE_ELEMENTS);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, SECOND_STRUCTURED,
            MAIN);
    }

    @Test
    public void getExportedType() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        //assertEquals(DAMContentFragmentImpl.RESOURCE_TYPE, fragment.getExportedType());
    }

    @Test
    public void getElements() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        final Map<String, DAMContentFragment.DAMContentElement> elements = fragment.getExportedElements();
        assertNotNull(elements);
        assertEquals(2, elements.size());
        assertEquals(true, elements.containsKey("main"));
        assertEquals(true, elements.containsKey("second"));
    }

    @Test
    public void getElementsType() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        final Map<String, ? extends ComponentExporter> elements = fragment.getExportedElements();
        assertNotNull(elements);
        final ComponentExporter mainElement = elements.get("main");
        assertEquals("text/html", mainElement.getExportedType());
    }

    @Test
    public void jsonExport() throws IOException {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        Writer writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithView(DAMContentFragmentImpl.class).writeValue(writer, fragment);
        JsonReader jsonReaderOutput = Json.createReader(IOUtils.toInputStream(writer.toString(), StandardCharsets.UTF_8));
        JsonReader jsonReaderExpected = Json.createReader(getClass()
            .getResourceAsStream("/contentfragment/test-expected-content-export.json"));
        assertEquals(jsonReaderExpected.read(), jsonReaderOutput.read());
    }

    @Test
    public void structuredGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_MULTIPLE_ELEMENTS);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/structured\"," +
            "\"elements\":[\"second\",\"non-existing\",\"main\"],\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void structuredWithVariationGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_VARIATION);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/structured\"," +
            "\"variation\":\"teaser\",\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void textOnlyGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_MULTIPLE_ELEMENTS);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/text-only\"" +
            ",\"elements\":[\"second\",\"non-existing\",\"main\"],\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void textOnlyWithVariationGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_VARIATION);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/text-only\"," +
            "\"variation\":\"teaser\",\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void getParagraphsOfMultiDisplayModeIsNull() {
        // Structure CF has displayMode=multi which should not return paragraphs
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED);
        assertThat(fragment.getParagraphs(), is(nullValue()));
    }

    @Test
    public void getParagraphOfSingleTextDisplayMode() {
        final String value = "<p>Main content</p>";

        when(fragmentRenderService.render(any(Resource.class))).thenReturn(value);

        ContentFragment contentFragment = getModelInstanceUnderTest(CF_STRUCTURED_SINGLE_ELEMENT_MAIN);

        assertThat(contentFragment.getParagraphs(), is(new String[]{value}));
    }

    /**
     * Asserts that the content of the specified {@code fragment} corresponds to the expected values using the
     * default variation.
     */
    private void assertContentFragment(ContentFragment fragment, String expectedTitle, String expectedDescription,
                                       String expectedType, String[] expectedAssociatedContent,
                                       MockElement... expectedElements) {
        assertContentFragment(fragment, null, expectedTitle, expectedDescription, expectedType,
            expectedAssociatedContent, expectedElements);
    }

    /**
     * Asserts that the content of the specified {@code fragment} corresponds to the expected values using the
     * specified variation.
     */
    private void assertContentFragment(ContentFragment fragment, String variationName, String expectedTitle,
                                       String expectedDescription, String expectedType,
                                       String[] expectedAssociatedContent, MockElement... expectedElements) {
        assertEquals("Content fragment has wrong title", expectedTitle, fragment.getTitle());
        assertEquals("Content fragment has wrong description", expectedDescription, fragment.getDescription());
        assertEquals("Content fragment has wrong type", expectedType, fragment.getType());
        List<Resource> associatedContent = fragment.getAssociatedContent();
        assertEquals("Content fragment has wrong number of associated content", expectedAssociatedContent.length, associatedContent.size());
        for (int i = 0; i < expectedAssociatedContent.length; i++) {
            Resource resource = associatedContent.get(i);
            assertEquals("Element has wrong associated content", expectedAssociatedContent[i], resource.getPath());
        }
        Map<String, DAMContentFragment.DAMContentElement> elementsMap = fragment.getExportedElements();
        assertNotNull(elementsMap);
        List<DAMContentFragment.DAMContentElement> elements = new ArrayList<>(elementsMap.values());
        assertEquals("Content fragment has wrong number of elements", expectedElements.length, elements.size());
        for (int i = 0; i < expectedElements.length; i++) {
            DAMContentFragment.DAMContentElement element = elements.get(i);
            MockElement expected = expectedElements[i];
            assertEquals("Element has wrong name", expected.name, element.getName());
            assertEquals("Element has wrong title", expected.title, element.getTitle());
            String contentType = expected.contentType;
            boolean isMultiLine = expected.isMultiLine;
            String htmlValue = expected.htmlValue;
            String[] expectedValues = expected.values;
            if (StringUtils.isNotEmpty(variationName)) {
                contentType = expected.variations.get(variationName).contentType;
                expectedValues = expected.variations.get(variationName).values;
                isMultiLine = expected.variations.get(variationName).isMultiLine;
                htmlValue = expected.variations.get(variationName).htmlValue;
            }
            Object elementValue = element.getValue();
            if (elementValue != null && elementValue.getClass().isArray()) {
                assertArrayEquals("Element's values didn't match", expectedValues, (String[]) elementValue);
            } else {
                assertEquals("Element is not single valued", expectedValues.length, 1);
                assertEquals("Element's value didn't match", expectedValues[0], elementValue);
            }
            assertEquals("Element has wrong content type", contentType, element.getExportedType());
            assertEquals("Element has wrong isMultiLine flag", isMultiLine, element.isMultiLine());
            assertEquals("Element has wrong html", htmlValue, element.getHtml());
        }
    }
}
