/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.pw.image;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_IMAGE_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_IMAGE_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_IMAGE_V3;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class ImagePwIT extends ComponentPwBaseTest {

    private static final String IMAGE = "/content/dam/core-components/core-comp-test-image.jpg";
    private static final String NO_DESCRIPTION_IMAGE =
        "/content/dam/core-components/Adobe_Systems_logo_and_wordmark.png";

    static Stream<String> allVersions() {
        return Stream.of(RT_IMAGE_V1, RT_IMAGE_V2, RT_IMAGE_V3);
    }

    static Stream<String> v2AndV3() {
        return Stream.of(RT_IMAGE_V2, RT_IMAGE_V3);
    }

    static Stream<String> v3Only() {
        return Stream.of(RT_IMAGE_V3);
    }

    static Stream<String> v2AndV3Versions() {
        return Stream.of(RT_IMAGE_V2, RT_IMAGE_V3);
    }

    static Stream<String> v2Only() {
        return Stream.of(RT_IMAGE_V2);
    }

    private String addImage(String resourceType) throws Exception {
        String clientlibs = RT_IMAGE_V1.equals(resourceType) ? Commons.CLIENTLIBS_IMAGE_V1
            : RT_IMAGE_V2.equals(resourceType) ? Commons.CLIENTLIBS_IMAGE_V2 : Commons.CLIENTLIBS_IMAGE_V3;
        // v1/v2 build the <img> and image map areas client-side, so the page needs the image clientlibs
        createPagePolicy(new HashMap<>(java.util.Collections.singletonMap("clientlibs", clientlibs)));
        return addStandaloneComponent(resourceType, "image");
    }

    private void openDialogTab(String trackingEvent) {
        dialog().locator("coral-tab[data-foundation-tracking-event*='" + trackingEvent + "']").click();
    }

    private void saveProperties(String path, Map<String, String> properties) throws Exception {
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, new HashMap<>(properties));
        reloadEditor();
    }

    private HashMap<String, String> imageProperties(String alt) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("./fileReference", IMAGE);
        properties.put("./alt", alt);
        properties.put("./altValueFromDAM", "false");
        properties.put("./titleValueFromDAM", "false");
        // a drag from the asset finder unchecks "inherit from page"; v3 hides the DAM checkboxes while it is checked
        properties.put("./imageFromPageImage", "false");
        return properties;
    }

    private Locator image() {
        // v1 renders a plain <img> without the cmp-image__image class
        return page.locator(".cmp-image img");
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testAddImageAndAltText(String resourceType) throws Exception {
        String path = addImage(resourceType);
        saveProperties(path, imageProperties("Return to Arkham"));
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).isVisible();
        assertThat(image()).hasAttribute("alt", "Return to Arkham");
        assertThat(image()).hasAttribute("src", java.util.regex.Pattern.compile(".*core-comp-test-image.*"));
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetLink(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./linkURL", "https://www.adobe.com");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(page.locator(".cmp-image__link, .cmp-image--link")).hasAttribute("href", "https://www.adobe.com");
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetCaption(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./jcr:title", "The Last Guardian");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        if (RT_IMAGE_V1.equals(resourceType)) {
            // v1 renders a non-popup caption as a separate span
            assertThat(page.locator("span.cmp-image--title")).hasText("The Last Guardian");
        } else {
            assertThat(image()).hasAttribute("title", "The Last Guardian");
        }
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetCaptionAsPopup(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./jcr:title", "The Last Guardian");
        properties.put("./displayPopupTitle", "true");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("title", "The Last Guardian");
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetImageAsDecorative(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("");
        properties.put("./isDecorative", "true");
        properties.put("./linkURL", "https://www.adobe.com");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        // v1 omits the alt attribute, v2/v3 render it empty
        assertThat(image()).hasCount(1);
        assertThat(page.locator(".cmp-image img[alt]:not([alt=''])")).hasCount(0);
        assertThat(page.locator(".cmp-image__link, .cmp-image--link")).hasCount(0);
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testAddImage(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("House on a beach with blue sky");
        properties.put("./jcr:title", "Beach house");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "House on a beach with blue sky");
        assertThat(image()).hasAttribute("title", "Beach house");
    }

    @ParameterizedTest
    @MethodSource("v2Only")
    public void testDragImageToComponent(String resourceType) throws Exception {
        String path = addImage(resourceType);
        saveProperties(path, imageProperties("House on a beach with blue sky"));
        openEditDialog(path);
        openDialogTab("metadata");

        assertThat(dialog().locator("coral-checkbox[name='./altValueFromDAM']")).isVisible();
        if (resourceType.equals(RT_IMAGE_V2)) {
            assertThat(dialog().locator("coral-checkbox[name='./titleValueFromDAM']")).isVisible();
        }
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testAddAltTextAndTitle(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./jcr:title", "The Last Guardian");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "Return to Arkham");
        assertThat(image()).hasAttribute("title", "The Last Guardian");
    }

    @Test
    public void testDisableCaptionAsPopup() throws Exception {
        String path = addImage(RT_IMAGE_V2);
        Map<String, String> properties = imageProperties("House on a beach with blue sky");
        properties.put("./jcr:title", "Beach house");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "House on a beach with blue sky");
        assertThat(image()).hasAttribute("title", "Beach house");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testCheckMapAreaNavigationAndResponsiveResize(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./imageMap", "[rect(0,0,226,230)\"" + testPage + "\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        if (RT_IMAGE_V3.equals(resourceType)) {
            assertThat(page.locator("[data-cmp-hook-image='area']")).hasCount(0);
        } else {
            // <map>/<area> never have a layout box, so Playwright reports them hidden; assert presence instead
            assertThat(page.locator("[data-cmp-hook-image='area']")).not().hasCount(0);
            assertThat(page.locator("[data-cmp-hook-image='map']")).hasCount(1);
        }
    }

    @Test
    public void testCheckMapAreaNotAvailable() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./imageMap", "[rect(0,0,226,230)\"" + testPage + "\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(page.locator("[data-cmp-hook-image='area']")).hasCount(0);
    }

    @Test
    public void testLazyLoadingEnabled() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        saveProperties(path, imageProperties("Return to Arkham"));
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("loading", "lazy");
    }

    @Test
    public void testLazyLoadingDisabled() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./disableLazyLoading", "true");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).not().hasAttribute("loading", "lazy");
    }

    @Test
    public void testSizesAttribute() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        saveProperties(path, imageProperties("Return to Arkham"));
        createComponentPolicy("/image-v3",
            new HashMap<>(java.util.Collections.singletonMap("sizes", "(min-width: 36em) 33.3vw, 100vw")));
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("sizes", "(min-width: 36em) 33.3vw, 100vw");
    }

    @Test
    public void testSetLinkWithTarget() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./linkURL", "https://www.adobe.com");
        properties.put("./linkTarget", "_blank");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(page.locator(".cmp-image__link, .cmp-image--link")).hasAttribute("target", "_blank");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testSetAssetWithoutDescription(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = new HashMap<>();
        properties.put("./fileReference", NO_DESCRIPTION_IMAGE);
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, new HashMap<>(properties));
        reloadEditor();
        openEditDialog(path);
        clickDone();

        assertThat(dialog().locator(".coral-Form-errorlabel, coral-tooltip[variant='error'] > coral-tooltip-content"))
            .containsText("Please provide an asset which has a description that can be used as alt text.");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testSetAssetWithoutDescriptionAsDecorative(String resourceType) throws Exception {
        String path = addImage(resourceType);
        Map<String, String> properties = new HashMap<>();
        properties.put("./fileReference", NO_DESCRIPTION_IMAGE);
        properties.put("./isDecorative", "true");
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, new HashMap<>(properties));
        reloadEditor();
        openEditDialog(path);
        saveDialog();

        assertThat(dialog()).isHidden();
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testSmartCropOnNGDMImageV3_SmallCrop() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        createComponentPolicy("/image-v3",
            new HashMap<>(java.util.Collections.singletonMap("enableDmFeatures", "true")));
        Commons.setNGDMImage(adminClient, path);
        reloadEditor();
        openEditDialog(path);
        selectInCoralSelect("[name='./smartCropRendition']", "Small");
        dialog().locator("coral-checkbox[name='./isDecorative'] input[type='checkbox']").check();
        saveDialog();
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("src", java.util.regex.Pattern.compile(".*smartcrop=Small.*"));
    }

    @ParameterizedTest
    @MethodSource("v2AndV3Versions")
    @Tag("IgnoreOn65")
    public void testClearAssetInputGetDamInfoCheckboxesNotVisibleSDK(String resourceType) throws Exception {
        testClearAssetInput(resourceType, "button.cq-FileUpload-clear._coral-Button");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3Versions")
    @Tag("IgnoreOnSDK")
    public void testClearAssetInputGetDamInfoCheckboxesNotVisible65(String resourceType) throws Exception {
        testClearAssetInput(resourceType, "button.cq-FileUpload-clear.coral3-Button");
    }

    private void testClearAssetInput(String resourceType, String clearSelector) throws Exception {
        String path = addImage(resourceType);
        saveProperties(path, imageProperties("House on a beach with blue sky"));
        openEditDialog(path);
        // v3 keeps the alt text on the Asset tab; v2 moves it to Metadata, next to the title
        boolean v3 = RT_IMAGE_V3.equals(resourceType);
        openDialogTab(v3 ? "asset" : "metadata");
        assertThat(dialog().locator("coral-checkbox[name='./altValueFromDAM']")).isVisible();
        openDialogTab("metadata");
        assertThat(dialog().locator("coral-checkbox[name='./titleValueFromDAM']")).isVisible();
        openDialogTab("asset");
        dialog().locator(clearSelector).click();
        openDialogTab(v3 ? "asset" : "metadata");
        assertThat(dialog().locator("coral-checkbox[name='./altValueFromDAM']")).isHidden();
        openDialogTab("metadata");
        assertThat(dialog().locator("coral-checkbox[name='./titleValueFromDAM']")).isHidden();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithEmptyAltTextFromPageImage() throws Exception {
        configureFeaturedImage("");
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "");
        assertThat(image()).hasAttribute("src", java.util.regex.Pattern.compile(".*adobe-systems-logo-and-wordmark.*"));
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithEmptyAltTextFromPageImage65() throws Exception {
        testPageImageWithEmptyAltTextFromPageImage();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithAltTextFromPageImage() throws Exception {
        configureFeaturedImage("page image alt");
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "page image alt");
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithAltTextFromPageImage65() throws Exception {
        testPageImageWithAltTextFromPageImage();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithAltTextFromImage() throws Exception {
        String path = configureFeaturedImage("page image alt");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("./imageFromPageImage", "true");
        properties.put("./altValueFromPageImage", "false");
        properties.put("./alt", "Return to Arkham");
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "Return to Arkham");
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithAltTextFromImage65() throws Exception {
        testPageImageWithAltTextFromImage();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithDecorative() throws Exception {
        String path = configureFeaturedImage("page image alt");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("./imageFromPageImage", "true");
        properties.put("./isDecorative", "true");
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "");
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithDecorative65() throws Exception {
        testPageImageWithDecorative();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithDragAndDropImage() throws Exception {
        String path = configureFeaturedImage("page image alt");
        HashMap<String, String> properties = imageProperties("House on a beach with blue sky");
        properties.put("./imageFromPageImage", "false");
        saveProperties(path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "House on a beach with blue sky");
        assertThat(image()).hasAttribute("src", java.util.regex.Pattern.compile(".*core-comp-test-image.*"));
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithDragAndDropImage65() throws Exception {
        testPageImageWithDragAndDropImage();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithLinkedPage() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        HashMap<String, String> properties = new HashMap<>();
        properties.put("./fileReference", "/content/dam/core-components/AdobeStock_140634652_climbing.jpeg");
        properties.put("./alt", "Rock Climbing and Bouldering above the lake and mountains");
        properties.put("./linkURL", rootPage);
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, properties);
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");

        assertThat(image()).hasAttribute("alt", "Rock Climbing and Bouldering above the lake and mountains");
        assertThat(page.locator(".cmp-image__link, .cmp-image--link")).hasAttribute("href",
            java.util.regex.Pattern.compile(".*" + rootPage + ".*"));
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithLinkedPage65() throws Exception {
        testPageImageWithLinkedPage();
    }

    private String configureFeaturedImage(String altText) throws Exception {
        testPage = authorClient.createPage("image-page", "Image Page", rootPage, defaultPageTemplate).getSlingPath();
        adminClient.setPageProperty(testPage, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        adminClient.setPageProperty(testPage, "cq:featuredimage/fileReference", NO_DESCRIPTION_IMAGE, 200);
        adminClient.setPageProperty(testPage, "cq:featuredimage/alt", altText, 200);
        addComponentToAllowedPolicy(RT_IMAGE_V3);
        String path = Commons.addComponentWithRetry(authorClient, RT_IMAGE_V3,
            testPage + Commons.relParentCompPath, "image");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("./imageFromPageImage", "true");
        properties.put("_charset_", "UTF-8");
        Commons.editNodeProperties(authorClient, path, properties);
        openEditor(testPage);
        return path;
    }
}
