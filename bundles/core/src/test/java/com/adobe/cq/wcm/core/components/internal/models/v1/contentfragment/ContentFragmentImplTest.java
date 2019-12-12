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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ContentFragmentImplTest extends AbstractContentFragmentTest<ContentFragment> {

    private static final String MAIN_CONTENT = "<p>Main content</p>";
    private static final String TEST_BASE = "/contentfragment";

    @Override
    protected String getTestResourcesParentPath() {
        return "/content/contentfragments/jcr:content/root/responsivegrid";
    }

    @Override
    protected Class<ContentFragment> getClassType() {
        return ContentFragment.class;
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp();
    }

    @Test
    void textOnlyInvalidPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_INVALID_PATH);
        assertNotNull("Model shouldn't be null when the path is not a content fragment", fragment);
    }

    @Test
    void textOnly() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, TEXT_ONLY_NAME, ASSOCIATED_CONTENT, MAIN, SECOND_TEXT_ONLY);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_TEXT_ONLY));
    }

    @Test
    void textOnlyVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_VARIATION);
        assertContentFragment(fragment, VARIATION_NAME, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, TEXT_ONLY_NAME, ASSOCIATED_CONTENT, MAIN,
            SECOND_TEXT_ONLY);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_TEXT_ONLY_VARIATION));
    }

    @Test
    void textOnlyNonExistingVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_NON_EXISTING_VARIATION);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, TEXT_ONLY_NAME, ASSOCIATED_CONTENT, MAIN, SECOND_TEXT_ONLY);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_TEXT_ONLY_NON_EXISTING_VARIATION));
    }

    @Test
    void textOnlySingleElement() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_SINGLE_ELEMENT);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, TEXT_ONLY_NAME, ASSOCIATED_CONTENT, SECOND_TEXT_ONLY);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_TEXT_ONLY_SINGLE_ELEMENT));
    }

    @Test
    void textOnlyMultipleElements() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_MULTIPLE_ELEMENTS);
        assertContentFragment(fragment, TITLE, DESCRIPTION, TEXT_ONLY_TYPE, TEXT_ONLY_NAME, ASSOCIATED_CONTENT, SECOND_TEXT_ONLY, MAIN);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_TEXT_ONLY_MULTIPLE_ELEMENTS));
    }

    @Test
    void structuredNoPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NO_PATH);
        assertNotNull("Model shouldn't be null when no path is set", fragment);
    }

    @Test
    void structuredNonExistingPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NON_EXISTING_PATH);
        assertNotNull("Model shouldn't be null when the path does not exist", fragment);
        assertNull(fragment.getTitle());
        assertNull(fragment.getDescription());
        assertNull(fragment.getType());
        assertNull(fragment.getElements());
        assertNull(fragment.getAssociatedContent());
        assertTrue(fragment.getExportedElements().isEmpty());
        assertEquals(0, fragment.getExportedElementsOrder().length);
    }

    @Test
    void structuredOnlyInvalidPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_INVALID_PATH);
        assertNotNull("Model shouldn't be null when the path is not a content fragment", fragment);
    }

    @Test
    void structured() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, STRUCTURED_NAME, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED));
    }

    @Test
    void structuredVariation() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_VARIATION);
        assertContentFragment(fragment, VARIATION_NAME, TITLE, DESCRIPTION, STRUCTURED_TYPE, STRUCTURED_NAME, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED_VARIATION));
    }

    @Test
    void structuredNonExistingVariation() {
        when(fragmentRenderService.render(any(Resource.class))).thenReturn(MAIN_CONTENT);
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NON_EXISTING_VARIATION);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, STRUCTURED_NAME, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED_NON_EXISTING_VARIATION));
    }

    @Test
    void structuredNestedModel() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NESTED_MODEL);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE_NESTED, STRUCTURED_NESTED_NAME, ASSOCIATED_CONTENT, MAIN,
            SECOND_STRUCTURED);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED_NESTED_MODEL));
    }

    @Test
    void structuredSingleElement() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_SINGLE_ELEMENT);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, STRUCTURED_NAME, ASSOCIATED_CONTENT, SECOND_STRUCTURED);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED_SINGLE_ELEMENT));
    }

    @Test
    void structuredMultipleElements() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_MULTIPLE_ELEMENTS);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, STRUCTURED_NAME, ASSOCIATED_CONTENT, SECOND_STRUCTURED,
            MAIN);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED_MULTIPLE_ELEMENTS));
    }

    @Test
    void structuredSingleElementMain() {
        when(fragmentRenderService.render(any(Resource.class))).thenReturn(MAIN_CONTENT);
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_SINGLE_ELEMENT_MAIN);
        assertContentFragment(fragment, TITLE, DESCRIPTION, STRUCTURED_TYPE, STRUCTURED_NAME, ASSOCIATED_CONTENT, MAIN);
        Utils.testJSONExport(fragment, Utils.getTestExporterJSONPath(TEST_BASE, CF_STRUCTURED_SINGLE_ELEMENT_MAIN));
    }

    @Test
    void getExportedType() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        assertEquals(ContentFragmentImpl.RESOURCE_TYPE, fragment.getExportedType());
    }

    @Test
    void getElements() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        final Map<String, DAMContentFragment.DAMContentElement> elements = fragment.getExportedElements();
        assertNotNull(elements);
        assertEquals(2, elements.size());
        assertTrue(elements.containsKey("main"));
        assertTrue(elements.containsKey("second"));
    }

    @Test
    void getElementsType() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        final Map<String, ? extends ComponentExporter> elements = fragment.getExportedElements();
        assertNotNull(elements);
        final ComponentExporter mainElement = elements.get("main");
        assertEquals("text/html", mainElement.getExportedType());
    }

    @Test
    void structuredGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_MULTIPLE_ELEMENTS);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/structured\"," +
            "\"elements\":[\"second\",\"non-existing\",\"main\"],\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    void structuredWithVariationGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_VARIATION);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/structured\"," +
            "\"variation\":\"teaser\",\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    void textOnlyGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_MULTIPLE_ELEMENTS);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/text-only\"" +
            ",\"elements\":[\"second\",\"non-existing\",\"main\"],\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    void textOnlyWithVariationGetEditorJSON() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_VARIATION);
        String expectedJSON = "{\"title\":\"Test Content Fragment\",\"path\":\"/content/dam/contentfragments/text-only\"," +
            "\"variation\":\"teaser\",\"associatedContent\":[{\"title\":\"Test Collection\"" +
            ",\"path\":\"/content/dam/collections/X/X7v6pJAcy5qtkUdXdIxR/test\"}]}";
        assertEquals(fragment.getEditorJSON(), expectedJSON);
    }

    @Test
    void getParagraphsOfMultiDisplayModeIsNull() {
        // Structure CF has displayMode=multi which should not return paragraphs
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED);
        assertThat(fragment.getParagraphs(), is(nullValue()));
    }

    @Test
    void getParagraphOfSingleTextDisplayMode() {
        when(fragmentRenderService.render(any(Resource.class))).thenReturn(MAIN_CONTENT);

        ContentFragment contentFragment = getModelInstanceUnderTest(CF_STRUCTURED_SINGLE_ELEMENT_MAIN);

        assertThat(contentFragment.getParagraphs(), is(new String[]{MAIN_CONTENT}));
    }

    /**
     * Asserts that the content of the specified {@code fragment} corresponds to the expected values using the
     * default variation.
     */
    private void assertContentFragment(ContentFragment fragment, String expectedTitle, String expectedDescription,
                                       String expectedType, String expectedName, String[] expectedAssociatedContent,
                                       MockElement... expectedElements) {
        assertContentFragment(fragment, null, expectedTitle, expectedDescription, expectedType, expectedName,
            expectedAssociatedContent, expectedElements);
    }

    /**
     * Asserts that the content of the specified {@code fragment} corresponds to the expected values using the
     * specified variation.
     */
    private void assertContentFragment(ContentFragment fragment, String variationName, String expectedTitle,
                                       String expectedDescription, String expectedType, String expectedName,
                                       String[] expectedAssociatedContent, MockElement... expectedElements) {
        assertEquals("Content fragment has wrong title", expectedTitle, fragment.getTitle());
        assertEquals("Content fragment has wrong description", expectedDescription, fragment.getDescription());
        assertEquals("Content fragment has wrong type", expectedType, fragment.getType());
        assertEquals("Content fragment has wrong name", expectedName, fragment.getName());
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
