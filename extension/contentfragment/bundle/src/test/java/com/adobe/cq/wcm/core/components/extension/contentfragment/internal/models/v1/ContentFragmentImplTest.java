/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.MockElement;
import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.AbstractContentFragmentTest;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.ContentFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContentFragmentImplTest extends AbstractContentFragmentTest<ContentFragment> {

    private Logger cfmLogger;

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
        cfmLogger = spy(LoggerFactory.getLogger("FakeLogger"));
        setFakeLoggerOnClass(ContentFragmentImpl.class, cfmLogger);
    }

    @Test
    public void testTextOnlyNoPath() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_NO_PATH);
        verify(cfmLogger).warn("Please provide a path for the content fragment component.");
        assertNotNull("Model shouldn't be null when no path is set", fragment);
    }

    @Test
    public void testTextOnlyNonExistingPath() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_NON_EXISTING_PATH);
        verify(cfmLogger).error("Content Fragment can not be initialized because the '{}' does not exist.", "/content/dam/contentfragments/non-existing");
        assertNotNull("Model shouldn't be null when the path does not exist", fragment);
    }

    @Test
    public void testTextOnlyInvalidPath() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_INVALID_PATH);
        verify(cfmLogger).error("Content Fragment can not be initialized because '{}' is not a content fragment.", "/content/dam/contentfragments");
        assertNotNull("Model shouldn't be null when the path is not a content fragment", fragment);
    }

    @Test
    public void testTextOnly() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, MAIN, SECOND_TEXT_ONLY);
    }

    @Test
    public void testTextOnlyVariation() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_VARIATION);
        assertContentFragment(fragment, VARIATION_NAME, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_TEXT_ONLY);
    }

    @Test
    public void testTextOnlyNonExistingVariation() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_NON_EXISTING_VARIATION);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, MAIN, SECOND_TEXT_ONLY);
    }

    @Test
    public void testTextOnlySingleElement() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_SINGLE_ELEMENT);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, SECOND_TEXT_ONLY);
    }

    @Test
    public void testTextOnlyMultipleElements() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_MULTIPLE_ELEMENTS);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, ASSOCIATED_CONTENT, SECOND_TEXT_ONLY, MAIN);
    }

    @Test
    public void testStructuredNoPath() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_NO_PATH);
        assertNotNull("Model shouldn't be null when no path is set", fragment);
    }

    @Test
    public void testStructuredNonExistingPath() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_NON_EXISTING_PATH);
        assertNotNull("Model shouldn't be null when the path does not exist", fragment);
    }

    @Test
    public void testStructuredOnlyInvalidPath() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_INVALID_PATH);
        assertNotNull("Model shouldn't be null when the path is not a content fragment", fragment);
    }

    @Test
    public void testStructured() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void testStructuredVariation() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_VARIATION);
        assertContentFragment(fragment, VARIATION_NAME, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void testStructuredNonExistingVariation() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_NON_EXISTING_VARIATION);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void testStructuredNestedModel() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_NESTED_MODEL);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE_NESTED, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
    }

    @Test
    public void testStructuredSingleElement() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_SINGLE_ELEMENT);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, SECOND_STRUCTURED);
    }

    @Test
    public void testStructuredMultipleElements() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_MULTIPLE_ELEMENTS);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, ASSOCIATED_CONTENT, SECOND_STRUCTURED,
            MAIN);
    }

    @Test
    public void testGetExportedType() {
        ContentFragmentImpl fragment = (ContentFragmentImpl) getTestContentFragment(CF_TEXT_ONLY);
        assertEquals(ContentFragmentImpl.RESOURCE_TYPE, fragment.getExportedType());
    }

    @Test
    public void testGetElements() {
        ContentFragmentImpl fragment = (ContentFragmentImpl) getTestContentFragment(CF_TEXT_ONLY);
        final Map<String, ContentFragment.Element> elements = fragment.getExportedElements();
        assertNotNull(elements);
        assertEquals(2, elements.size());
        assertEquals(true, elements.containsKey("main"));
        assertEquals(true, elements.containsKey("second"));
    }

    @Test
    public void testGetElementsType() {
        ContentFragmentImpl fragment = (ContentFragmentImpl) getTestContentFragment(CF_TEXT_ONLY);
        final Map<String, ? extends ComponentExporter> elements = fragment.getExportedElements();
        assertNotNull(elements);
        final ComponentExporter mainElement = elements.get("main");
        assertEquals("text/html", mainElement.getExportedType());
    }

    @Test
    public void testJSONExport() throws IOException {
        ContentFragmentImpl fragment = (ContentFragmentImpl) getTestContentFragment(CF_TEXT_ONLY);
        Writer writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithView(ContentFragmentImpl.class).writeValue(writer, fragment);
        JsonReader jsonReaderOutput = Json.createReader(IOUtils.toInputStream(writer.toString()));
        JsonReader jsonReaderExpected = Json.createReader(Thread.currentThread().getContextClassLoader().getClass()
            .getResourceAsStream("/contentfragment/test-expected-content-export.json"));
        assertEquals(jsonReaderExpected.read(), jsonReaderOutput.read());
    }

    @Test
    public void testStructuredGetEditorJSON() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_MULTIPLE_ELEMENTS);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/structured\"," +
            "\"elements\":[\"second\",\"non-existing\",\"main\"],\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void testStructuredWithVariationGetEditorJSON() {
        ContentFragment fragment = getTestContentFragment(CF_STRUCTURED_VARIATION);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/structured\"," +
            "\"variation\":\"teaser\",\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void testTextOnlyGetEditorJSON() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_MULTIPLE_ELEMENTS);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/text-only\"" +
            ",\"elements\":[\"second\",\"non-existing\",\"main\"],\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    public void testTextOnlyWithVariationGetEditorJSON() {
        ContentFragment fragment = getTestContentFragment(CF_TEXT_ONLY_VARIATION);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/text-only\"," +
            "\"variation\":\"teaser\",\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
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
        Map<String, ContentFragment.Element> elementsMap = fragment.getExportedElements();
        assertNotNull(elementsMap);
        List<ContentFragment.Element> elements = new ArrayList<>(elementsMap.values());
        assertEquals("Content fragment has wrong number of elements", expectedElements.length, elements.size());
        for (int i = 0; i < expectedElements.length; i++) {
            ContentFragment.Element element = elements.get(i);
            Resource component;
            try {
                Field componentField = ContentFragmentImpl.ElementImpl.class.getDeclaredField("component");
                componentField.setAccessible(true);
                component = (Resource) componentField.get(element);
                String value = element.getValue() != null ? element.getValue().toString() : null;
                when(fragmentRenderService.render(component)).thenReturn(value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            MockElement expected = expectedElements[i];
            assertEquals("Element has wrong name", expected.name, element.getName());
            assertEquals("Element has wrong title", expected.title, element.getTitle());
            String contentType = expected.contentType;
            boolean isMultiLine = expected.isMultiLine;
            String htmlValue = expected.htmlValue;
            String[] paragraphs = expected.paragraphs;
            String[] expectedValues = expected.values;
            if (StringUtils.isNotEmpty(variationName)) {
                contentType = expected.variations.get(variationName).contentType;
                expectedValues = expected.variations.get(variationName).values;
                isMultiLine = expected.variations.get(variationName).isMultiLine;
                htmlValue = expected.variations.get(variationName).htmlValue;
                paragraphs = expected.variations.get(variationName).paragraphs;
            }
            Object elementValue = element.getValue();
            if (elementValue != null && elementValue.getClass().isArray()) {
                assertArrayEquals("Element's values didn't match", expectedValues, (String[]) elementValue);
            } else {
                assertEquals("Element is not single valued", expectedValues.length, 1);
                assertEquals("Element's value didn't match", expectedValues[0], elementValue);
            }
            assertEquals("Element has wrong isMultiLine flag", isMultiLine, element.isMultiLine());
            assertEquals("Element has wrong html", htmlValue, element.getHtml());
            assertArrayEquals("ELement has wrong paragraphs", paragraphs, element.getParagraphs());
        }
    }
}
