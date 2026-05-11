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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.json.Json;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ContentFragmentImplTest extends AbstractContentFragmentTest<ContentFragment> {

    private static final String MAIN_CONTENT = "<p>Main content</p>";
    private static final String TEST_BASE = "/contentfragment";

    /** UUID of text-only test DAM asset (matches test JSON). */
    private static final String VCF_TEXT_FRAG_UUID = "5037ca42-4dab-4a55-aaa8-1a3db1f2e2c4";
    /** UUID under jcr:content for structured test fragment. */
    private static final String VCF_STRUCT_FRAG_UUID = "b2a7f9c1-3e5d-4f8a-9c1e-d7b3a2f5e8c4";
    /** Matches test JSON; JCR property {@link ContentFragment#PN_VCF_TEMPLATE}. */
    private static final String VCF_TEMPLATE_ID = "hero-banner";

    private static final String VCF_Q_TEMPLATE = "templateId=" + VCF_TEMPLATE_ID;
    private static final String VCF_Q_TEMPLATE_VAR = VCF_Q_TEMPLATE + "&variation=" + VARIATION_NAME;

    private static final String LEGACY_AUTHOR_PREVIEW =
        LEGACY_VCF_TEST_BASE + "/sites/cf/fragments/" + VCF_TEXT_FRAG_UUID + "/preview";
    private static final String GA_AUTHOR_PREVIEW = GA_VCF_CONTENT_ROOT + "/" + VCF_TEXT_FRAG_UUID + "/preview";
    private static final String LEGACY_PUBLISH_PREFIX = LEGACY_VCF_TEST_BASE + "/contentFragments/";
    private static final String GA_PUBLISH_PREFIX = GA_VCF_CONTENT_ROOT + "/";

    @Override
    protected String getTestResourcesParentPath() {
        return "/content/contentfragments/jcr:content/root/responsivegrid";
    }

    @Override
    protected Class<ContentFragment> getClassType() {
        return ContentFragment.class;
    }

    private static String withQuery(String path, String query) {
        return query.isEmpty() ? path : path + "?" + query;
    }

    private static String publishHtml(String pathBeforeTemplate, String fragmentUuid, String htmlFile) {
        return pathBeforeTemplate + VCF_TEMPLATE_ID + "/" + fragmentUuid + "/" + htmlFile;
    }

    /**
     * GA VCF URL stubs ({@link AbstractContentFragmentTest#configureGaVcfUrls()}), then adapts the model.
     *
     * @param authorMode {@code true} to run as author (preview URLs)
     */
    private ContentFragment gaVcfModel(String componentName, boolean authorMode) {
        configureGaVcfUrls();
        if (authorMode) {
            context.runMode("author");
        }
        return getModelInstanceUnderTest(componentName);
    }

    private ContentFragment gaVcfModel(String componentName) {
        return gaVcfModel(componentName, false);
    }

    private ContentFragment gaVcfModelAuthor(String componentName) {
        return gaVcfModel(componentName, true);
    }

    /** Legacy VCF stubs (from {@code @BeforeEach}) with author run mode. */
    private ContentFragment legacyVcfModelAuthor(String componentName) {
        context.runMode("author");
        return getModelInstanceUnderTest(componentName);
    }

    @Test
    void textOnlyInvalidPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY_INVALID_PATH);
        assertNotNull(fragment, "Model shouldn't be null when the path is not a content fragment");
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
        assertNotNull(fragment, "Model shouldn't be null when no path is set");
    }

    @Test
    void structuredNonExistingPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NON_EXISTING_PATH);
        assertNotNull(fragment, "Model shouldn't be null when the path does not exist");
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
        assertNotNull(fragment, "Model shouldn't be null when the path is not a content fragment");
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
        assertNull(fragment.getParagraphs());
    }

    @Test
    void getParagraphOfSingleTextDisplayMode() {
        when(fragmentRenderService.render(any(Resource.class))).thenReturn(MAIN_CONTENT);

        ContentFragment contentFragment = getModelInstanceUnderTest(CF_STRUCTURED_SINGLE_ELEMENT_MAIN);

        assertArrayEquals(new String[]{MAIN_CONTENT}, contentFragment.getParagraphs());
    }

    // Author mode tests (override SlingSettingsService to remove "publish" run mode)

    @Test
    void vcfAuthorRenderUrlWithTemplate() {
        ContentFragment fragment = legacyVcfModelAuthor("vcf-with-template");
        assertEquals(withQuery(LEGACY_AUTHOR_PREVIEW, VCF_Q_TEMPLATE), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfAuthorRenderUrlWithVariation() {
        ContentFragment fragment = legacyVcfModelAuthor("vcf-with-template-and-variation");
        assertEquals(
            withQuery(LEGACY_AUTHOR_PREVIEW, VCF_Q_TEMPLATE_VAR),
            fragment.getVcfRenderUrl());
    }

    @Test
    void vcfAuthorRenderUrlMasterVariation() {
        ContentFragment fragment = legacyVcfModelAuthor("vcf-with-master-variation");
        assertEquals(withQuery(LEGACY_AUTHOR_PREVIEW, VCF_Q_TEMPLATE), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfAuthorRenderUrlWithoutTemplate() {
        ContentFragment fragment = legacyVcfModelAuthor("vcf-without-template");
        assertEquals(withQuery(LEGACY_AUTHOR_PREVIEW, ""), fragment.getVcfRenderUrl());
    }

    // Publish mode tests (default mock context has "publish" run mode)

    @Test
    void vcfRenderUrlWithTemplate() {
        ContentFragment fragment = getModelInstanceUnderTest("vcf-with-template");
        assertEquals(publishHtml(LEGACY_PUBLISH_PREFIX, VCF_TEXT_FRAG_UUID, "main.html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfRenderUrlWithVariation() {
        ContentFragment fragment = getModelInstanceUnderTest("vcf-with-template-and-variation");
        assertEquals(publishHtml(LEGACY_PUBLISH_PREFIX, VCF_TEXT_FRAG_UUID, VARIATION_NAME + ".html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfRenderUrlWithoutTemplate() {
        ContentFragment fragment = getModelInstanceUnderTest("vcf-without-template");
        assertNull(fragment.getVcfRenderUrl());
    }

    @Test
    void vcfRenderUrlNonVcfMode() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        assertNull(fragment.getVcfRenderUrl());
    }

    @Test
    void vcfRenderUrlMasterVariationMapsToMain() {
        ContentFragment fragment = getModelInstanceUnderTest("vcf-with-master-variation");
        assertEquals(publishHtml(LEGACY_PUBLISH_PREFIX, VCF_TEXT_FRAG_UUID, "main.html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfRenderUrlWithJcrContentUuid() {
        ContentFragment fragment = getModelInstanceUnderTest("vcf-with-structured-fragment");
        assertEquals(VCF_STRUCT_FRAG_UUID, fragment.getFragmentId());
        assertEquals(publishHtml(LEGACY_PUBLISH_PREFIX, VCF_STRUCT_FRAG_UUID, "main.html"), fragment.getVcfRenderUrl());
    }

    // ====== GA (FT_CFVS_GA enabled) Author mode tests ======

    @Test
    void vcfGaAuthorRenderUrlWithTemplate() {
        ContentFragment fragment = gaVcfModelAuthor("vcf-with-template");
        assertEquals(withQuery(GA_AUTHOR_PREVIEW, VCF_Q_TEMPLATE), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaAuthorRenderUrlWithVariation() {
        ContentFragment fragment = gaVcfModelAuthor("vcf-with-template-and-variation");
        assertEquals(
            withQuery(GA_AUTHOR_PREVIEW, VCF_Q_TEMPLATE_VAR),
            fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaAuthorRenderUrlMasterVariation() {
        ContentFragment fragment = gaVcfModelAuthor("vcf-with-master-variation");
        assertEquals(withQuery(GA_AUTHOR_PREVIEW, VCF_Q_TEMPLATE), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaAuthorRenderUrlWithoutTemplate() {
        ContentFragment fragment = gaVcfModelAuthor("vcf-without-template");
        assertEquals(withQuery(GA_AUTHOR_PREVIEW, ""), fragment.getVcfRenderUrl());
    }

    // ====== GA (FT_CFVS_GA enabled) Publish mode tests ======

    @Test
    void vcfGaRenderUrlWithTemplate() {
        ContentFragment fragment = gaVcfModel("vcf-with-template");
        assertEquals(publishHtml(GA_PUBLISH_PREFIX, VCF_TEXT_FRAG_UUID, "main.html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaRenderUrlWithVariation() {
        ContentFragment fragment = gaVcfModel("vcf-with-template-and-variation");
        assertEquals(publishHtml(GA_PUBLISH_PREFIX, VCF_TEXT_FRAG_UUID, VARIATION_NAME + ".html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaRenderUrlWithoutTemplate() {
        assertNull(gaVcfModel("vcf-without-template").getVcfRenderUrl());
    }

    @Test
    void vcfGaRenderUrlMasterVariationMapsToMain() {
        ContentFragment fragment = gaVcfModel("vcf-with-master-variation");
        assertEquals(publishHtml(GA_PUBLISH_PREFIX, VCF_TEXT_FRAG_UUID, "main.html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaRenderUrlWithJcrContentUuid() {
        ContentFragment fragment = gaVcfModel("vcf-with-structured-fragment");
        assertEquals(VCF_STRUCT_FRAG_UUID, fragment.getFragmentId());
        assertEquals(publishHtml(GA_PUBLISH_PREFIX, VCF_STRUCT_FRAG_UUID, "main.html"), fragment.getVcfRenderUrl());
    }

    @Test
    void vcfGaTemplatesApiBase() {
        assertEquals(GA_VCF_CONTENT_ROOT + "/models", gaVcfModel("vcf-with-template").getVcfTemplatesApiBase());
    }

    // ====== Fragment ID tests ======

    @Test
    void fragmentIdFromDirectNode() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        assertEquals(VCF_TEXT_FRAG_UUID, fragment.getFragmentId());
    }

    @Test
    void fragmentIdFromJcrContent() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED);
        assertEquals(VCF_STRUCT_FRAG_UUID, fragment.getFragmentId());
    }

    @Test
    void fragmentIdNullWhenNoPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NO_PATH);
        assertNull(fragment.getFragmentId());
    }

    @Test
    void vcfRenderUrlNullWhenNoFragmentId() {
        ContentFragment fragment = getModelInstanceUnderTest("vcf-no-fragment-id");
        assertNull(fragment.getFragmentId());
        assertNull(fragment.getVcfRenderUrl());
    }

    @Test
    void fragmentIdNullWhenInvalidPath() {
        ContentFragment fragment = getModelInstanceUnderTest(CF_STRUCTURED_NON_EXISTING_PATH);
        assertNull(fragment.getFragmentId());
    }

    @Test
    void testDataLayerJson() {
        Utils.enableDataLayer(context, true);
        String expected = "{\"contentfragment-bb4058160c\":{\"@type\":\"core/wcm/components/contentfragment/v1/contentfragment\",\"dc:title\":\"Test Content Fragment\",\"elements\":[{\"xdm:text\":\"<p>Main content</p>\",\"xdm:title\":\"Main\"},{\"xdm:text\":\"Second content\",\"xdm:title\":\"Second\"}]}}";
        ContentFragment fragment = getModelInstanceUnderTest(CF_TEXT_ONLY);
        assertEquals(Json.createReader(new StringReader(expected)).read(),
            Json.createReader(new StringReader(fragment.getData().getJson())).read());
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
        assertEquals(expectedTitle, fragment.getTitle(), "Content fragment has wrong title");
        assertEquals(expectedDescription, fragment.getDescription(), "Content fragment has wrong description");
        assertEquals(expectedType, fragment.getType(), "Content fragment has wrong type");
        assertEquals(expectedName, fragment.getName(), "Content fragment has wrong name");
        List<Resource> associatedContent = Objects.requireNonNull(fragment.getAssociatedContent());
        assertEquals(expectedAssociatedContent.length, associatedContent.size(), "Content fragment has wrong number of associated content");
        for (int i = 0; i < expectedAssociatedContent.length; i++) {
            Resource resource = associatedContent.get(i);
            assertEquals(expectedAssociatedContent[i], resource.getPath(), "Element has wrong associated content");
        }
        Map<String, DAMContentFragment.DAMContentElement> elementsMap = fragment.getExportedElements();
        assertNotNull(elementsMap);
        List<DAMContentFragment.DAMContentElement> elements = new ArrayList<>(elementsMap.values());
        assertEquals(expectedElements.length, elements.size(), "Content fragment has wrong number of elements");
        for (int i = 0; i < expectedElements.length; i++) {
            DAMContentFragment.DAMContentElement element = elements.get(i);
            MockElement expected = expectedElements[i];
            assertEquals(expected.name, element.getName(), "Element has wrong name");
            assertEquals(expected.title, element.getTitle(), "Element has wrong title");
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
                assertArrayEquals(expectedValues, (String[]) elementValue, "Element's values didn't match");
            } else {
                assertEquals(expectedValues.length, 1, "Element is not single valued");
                assertEquals(expectedValues[0], elementValue, "Element's value didn't match");
            }
            assertEquals(contentType, element.getExportedType(), "Element has wrong content type");
            assertEquals(isMultiLine, element.isMultiLine(), "Element has wrong isMultiLine flag");
            assertEquals(htmlValue, element.getHtml(), "Element has wrong html");
        }
    }
}
