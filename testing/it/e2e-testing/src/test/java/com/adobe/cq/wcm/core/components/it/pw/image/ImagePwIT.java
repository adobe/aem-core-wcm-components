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
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.util.Commons;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.TimeoutError;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_IMAGE_V1;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_IMAGE_V2;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_IMAGE_V3;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class ImagePwIT extends ComponentPwBaseTest {

    private static final String IMAGE = "/content/dam/core-components/core-comp-test-image.jpg";
    private static final String NO_DESCRIPTION_IMAGE =
        "/content/dam/core-components/Adobe_Systems_logo_and_wordmark.png";
    private static final String NO_DESCRIPTION_ERROR =
        "Error: Please provide an asset which has a description that can be used as alt text.";
    private static final String DAM_ALT = "House on a beach with blue sky";

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
        properties.put("./imageFromPageImage", "false");
        return properties;
    }

    private Locator image() {
        // v1 renders a plain <img> without the cmp-image__image class
        return page.locator(".cmp-image img");
    }

    private Locator editorImage() {
        return contentFrame().locator(".cmp-image img");
    }

    private void openPreview() {
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");
    }

    /** Escapes regex metacharacters; Pattern.quote output ({@code \Q..\E}) is not valid in the browser's JS regex engine. */
    private static String regex(String literal) {
        return literal.replaceAll("[\\\\^$.|?*+()\\[\\]{}]", "\\\\$0");
    }

    private String createRedirectPage() throws Exception {
        return authorClient.createPage("redirectPage", "Redirect Test Page Title", rootPage, defaultPageTemplate)
            .getSlingPath();
    }

    /**
     * Finds a dialog field wherever the version lays it out (v1: single tab, v2/v3: asset or metadata tab) and
     * switches to the tab holding it.
     */
    private Locator field(String resourceType, String selector) {
        Locator target = dialog().locator(selector).first();
        if (RT_IMAGE_V1.equals(resourceType) || target.isVisible()) {
            return target;
        }
        for (String tab : new String[] {"asset", "metadata"}) {
            openDialogTab(tab);
            try {
                target.waitFor(new Locator.WaitForOptions().setTimeout(1_500));
                return target;
            } catch (TimeoutError e) {
                // try the next tab
            }
        }
        return target;
    }

    private Locator checkboxInput(String resourceType, String name) {
        Locator box = field(resourceType, "coral-checkbox[name='" + name + "']");
        assertThat(box).isVisible();
        return box.locator("input[type='checkbox']");
    }

    private void setCheckbox(String resourceType, String name, boolean checked) {
        checkboxInput(resourceType, name).setChecked(checked);
    }

    /** Applies the state only when the checkbox exists and is currently shown by the dialog. */
    private void setCheckboxIfShown(String resourceType, String name, boolean checked) {
        if (dialog().locator("coral-checkbox[name='" + name + "']").count() == 0) {
            return;
        }
        Locator box = field(resourceType, "coral-checkbox[name='" + name + "']");
        if (box.isVisible()) {
            box.locator("input[type='checkbox']").setChecked(checked);
        }
    }

    private void setAlt(String resourceType, String alt) {
        setCheckboxIfShown(resourceType, "./altValueFromDAM", false);
        setCheckboxIfShown(resourceType, "./altValueFromPageImage", false);
        Locator altInput = field(resourceType, "input[name='./alt']");
        assertThat(altInput).isEnabled();
        altInput.fill(alt);
    }

    private void setTitle(String resourceType, String title) {
        setCheckboxIfShown(resourceType, "./titleValueFromDAM", false);
        field(resourceType, "input[name='./jcr:title']").fill(title);
    }

    private void setLink(String resourceType, String link) {
        field(resourceType, "[name='./linkURL']");
        fillPathAutocomplete(dialog(), "./linkURL", link);
    }

    /** Drags the asset from the side panel's asset finder into the open dialog's file upload. */
    private void dragImage(String path, String asset) {
        ImageSidePanelHelper.openSidePanel(page);
        ImageSidePanelHelper.setAssetFilter(page, ImageSidePanelHelper.TEST_ASSETS_PATH);
        openEditDialog(path);
        uncheckInheritFromPageImage();
        ImageSidePanelHelper.dragAssetToFileUpload(page, dialog(), dialog().locator("coral-fileupload[name='./file']")
            .first(), asset);
        // the v2/v3 editor loads the asset's DAM metadata ($.ajax) after the drop and then, in an async jQuery
        // callback, re-initialises the "from DAM" checkboxes (which disable the alt/caption fields); wait for the
        // request to complete and flush a task so that callback has run before the dialog is edited
        page.waitForFunction("() => !window.jQuery || window.jQuery.active === 0");
        page.evaluate("() => new Promise(resolve => setTimeout(resolve, 0))");
    }

    private void uncheckInheritFromPageImage() {
        Locator inherit = dialog().locator("coral-checkbox[name='./imageFromPageImage'] input[type='checkbox']");
        if (inherit.count() > 0 && inherit.isChecked()) {
            inherit.uncheck();
        }
    }

    private void cancelDialog() {
        dialog().locator("button.cq-dialog-cancel, button[is='coral-button'][title='Cancel']").first().click();
        assertThat(dialog()).isHidden();
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testAddImageAndAltText(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, IMAGE);
        setAlt(resourceType, "Return to Arkham");
        saveDialog();

        assertThat(editorImage()).isVisible();
        assertThat(editorImage()).hasAttribute("alt", "Return to Arkham");
        assertThat(editorImage()).hasAttribute("src", Pattern.compile(".*" + regex(testPage)
            + "/_jcr_content/root/responsivegrid/image\\.(core)?img\\..*"));
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetLink(String resourceType) throws Exception {
        String path = addImage(resourceType);
        String redirectPage = createRedirectPage();
        dragImage(path, IMAGE);
        setAlt(resourceType, "Return to Arkham");
        setLink(resourceType, redirectPage);
        saveDialog();
        openPreview();

        assertThat(page.locator(".cmp-image__link, .cmp-image--link")).hasAttribute("href", contextPath + redirectPage + ".html");
        image().click();
        assertThat(page).hasURL(Pattern.compile(".*" + regex(redirectPage) + "\\.html$"));
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetCaption(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, IMAGE);
        setAlt(resourceType, "Return to Arkham");
        setTitle(resourceType, "The Last Guardian");
        setCheckbox(resourceType, "./displayPopupTitle", false);
        saveDialog();

        if (RT_IMAGE_V1.equals(resourceType)) {
            assertThat(contentFrame().locator("span.cmp-image--title")).hasText("The Last Guardian");
        } else {
            assertThat(contentFrame().locator(".cmp-image__title")).hasText("The Last Guardian");
        }
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetCaptionAsPopup(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, IMAGE);
        setAlt(resourceType, "Return to Arkham");
        setTitle(resourceType, "The Last Guardian");
        setCheckbox(resourceType, "./displayPopupTitle", true);
        saveDialog();

        assertThat(editorImage()).hasAttribute("title", "The Last Guardian");
    }

    @ParameterizedTest
    @MethodSource("allVersions")
    public void testSetImageAsDecorative(String resourceType) throws Exception {
        String path = addImage(resourceType);
        String redirectPage = createRedirectPage();
        dragImage(path, IMAGE);
        setLink(resourceType, redirectPage);
        setCheckbox(resourceType, "./isDecorative", true);
        assertThat(checkboxInput(resourceType, "./isDecorative")).isChecked();
        saveDialog();
        openPreview();

        // v1 omits the alt attribute, v2/v3 render it empty
        assertThat(image()).hasCount(1);
        assertThat(page.locator(".cmp-image img[alt]:not([alt=''])")).hasCount(0);
        assertThat(page.locator(".cmp-image__link, .cmp-image--link")).hasCount(0);
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testAddImage(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, IMAGE);
        setAlt(resourceType, DAM_ALT);
        setTitle(resourceType, "Beach house");
        setCheckbox(resourceType, "./displayPopupTitle", true);
        saveDialog();

        assertThat(editorImage()).hasAttribute("alt", DAM_ALT);
        assertThat(editorImage()).hasAttribute("title", "Beach house");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testDragImageToComponent(String resourceType) throws Exception {
        String path = addImage(resourceType);
        ImageSidePanelHelper.openSidePanel(page);
        ImageSidePanelHelper.setAssetFilter(page, ImageSidePanelHelper.TEST_ASSETS_PATH);
        ImageSidePanelHelper.dragSidePanelImageToComponent(page, IMAGE, path);
        ImageSidePanelHelper.closeSidePanel(page);
        openEditDialog(path);

        assertThat(checkboxInput(resourceType, "./altValueFromDAM")).isChecked();
        if (RT_IMAGE_V2.equals(resourceType)) {
            assertThat(checkboxInput(resourceType, "./titleValueFromDAM")).isChecked();
            assertThat(checkboxInput(resourceType, "./displayPopupTitle")).isChecked();
        }
        cancelDialog();

        assertThat(editorImage()).hasAttribute("alt", DAM_ALT);
        assertThat(editorImage()).hasAttribute("title", "Beach house");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testAddAltTextAndTitle(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, IMAGE);
        setAlt(resourceType, "Return to Arkham");
        setTitle(resourceType, "The Last Guardian");
        setCheckbox(resourceType, "./displayPopupTitle", true);
        saveDialog();

        assertThat(editorImage()).hasAttribute("alt", "Return to Arkham");
        assertThat(editorImage()).hasAttribute("title", "The Last Guardian");
    }

    @Test
    public void testDisableCaptionAsPopup() throws Exception {
        String path = addImage(RT_IMAGE_V2);
        dragImage(path, IMAGE);
        setCheckbox(RT_IMAGE_V2, "./displayPopupTitle", false);
        saveDialog();

        assertThat(editorImage()).hasAttribute("alt", DAM_ALT);
        assertThat(contentFrame().locator(".cmp-image__title")).containsText("Beach house");
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testCheckMapAreaNavigationAndResponsiveResize(String resourceType) throws Exception {
        String path = addImage(resourceType);
        String redirectPage = createRedirectPage();
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./imageMap", "[rect(0,0,226,230)\"" + redirectPage
            + "\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]");
        saveProperties(path, properties);
        openPreview();

        if (RT_IMAGE_V3.equals(resourceType)) {
            assertThat(page.locator("[data-cmp-hook-image='area']")).hasCount(0);
            return;
        }
        // <map>/<area> never have a layout box, so Playwright reports them hidden; assert presence instead
        Locator area = page.locator("[data-cmp-hook-image='area']");
        assertThat(area).not().hasCount(0);
        assertThat(page.locator("[data-cmp-hook-image='map']")).hasCount(1);

        // the area covers the top-left corner of the image, so clicking there follows the area link
        image().click(new Locator.ClickOptions().setPosition(5, 5));
        assertThat(page).hasURL(Pattern.compile(".*" + regex(redirectPage) + ".*"));

        openPreview();
        assertThat(area).not().hasCount(0);
        image().evaluate("img => { img.style.width = '300px'; }");
        page.setViewportSize(300, 600);
        page.evaluate("() => window.dispatchEvent(new Event('resize'))");
        assertThat(area.first()).hasAttribute("coords", "0,0,58,38");
    }

    @Test
    public void testCheckMapAreaNotAvailable() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        Map<String, String> properties = imageProperties("Return to Arkham");
        properties.put("./imageMap", "[rect(0,0,226,230)\"" + testPage
            + "\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]");
        saveProperties(path, properties);
        openPreview();

        assertThat(page.locator("[data-cmp-hook-image='area']")).hasCount(0);
    }

    @Test
    public void testLazyLoadingEnabled() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        dragImage(path, IMAGE);
        setAlt(RT_IMAGE_V3, "Return to Arkham");
        saveDialog();
        openPreview();

        assertThat(image()).hasAttribute("loading", "lazy");
    }

    @Test
    public void testLazyLoadingDisabled() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        dragImage(path, IMAGE);
        setAlt(RT_IMAGE_V3, "Return to Arkham");
        setCheckbox(RT_IMAGE_V3, "./disableLazyLoading", true);
        saveDialog();
        openPreview();

        assertThat(image()).not().hasAttribute("loading", "lazy");
    }

    @Test
    public void testSizesAttribute() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        createComponentPolicy("/image-v3",
            new HashMap<>(java.util.Collections.singletonMap("sizes", "(min-width: 36em) 33.3vw, 100vw")));
        dragImage(path, IMAGE);
        setAlt(RT_IMAGE_V3, "Return to Arkham");
        saveDialog();
        openPreview();

        assertThat(image()).hasAttribute("sizes", "(min-width: 36em) 33.3vw, 100vw");
    }

    @Test
    public void testSetLinkWithTarget() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        String redirectPage = createRedirectPage();
        dragImage(path, IMAGE);
        setAlt(RT_IMAGE_V3, "Return to Arkham");
        setLink(RT_IMAGE_V3, redirectPage);
        setCheckbox(RT_IMAGE_V3, "./linkTarget", true);
        saveDialog();
        openPreview();

        assertThat(page.locator("a[href='" + contextPath + redirectPage + ".html'][target='_blank']")).isVisible();
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testSetAssetWithoutDescription(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, NO_DESCRIPTION_IMAGE);
        if (RT_IMAGE_V2.equals(resourceType)) {
            openDialogTab("metadata");
        }
        clickDone();

        assertThat(dialog().locator(".coral-Form-errorlabel, coral-tooltip[variant='error'] > coral-tooltip-content")
            .first()).hasText(NO_DESCRIPTION_ERROR);
        if (RT_IMAGE_V3.equals(resourceType)) {
            // the alt field is flagged with an error icon; newer Coral Spectrum styles hide the icon itself, so
            // assert its presence and the invalid state of the field rather than the icon's rendering
            Locator alt = dialog().locator("input[name='./alt']");
            assertThat(alt).hasAttribute("aria-invalid", "true");
            Locator alertIcon = dialog().locator("input[name='./alt'] + coral-icon[icon='alert']");
            assertThat(alertIcon).hasCount(1);
            assertThat(alertIcon).hasAttribute("alt", NO_DESCRIPTION_ERROR);
        }
    }

    @ParameterizedTest
    @MethodSource("v2AndV3")
    public void testSetAssetWithoutDescriptionAsDecorative(String resourceType) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, NO_DESCRIPTION_IMAGE);
        setCheckbox(resourceType, "./isDecorative", true);
        saveDialog();

        assertThat(dialog()).isHidden();
    }

    @Test
    // Next-Gen Dynamic Media is only available on AEM as a Cloud Service (not the SDK)
    // so this runs on none of the CI products; NGDM smart-crop coverage is tracked in
    // https://github.com/adobe/aem-core-wcm-components/issues/3100
    @Tag("IgnoreOnSDK")
    @Tag("IgnoreOn65")
    @Tag("IgnoreOnLTS")
    public void testSmartCropOnNGDMImageV3_SmallCrop() throws Exception {
        String path = addImage(RT_IMAGE_V3);
        createComponentPolicy("/image-v3",
            new HashMap<>(java.util.Collections.singletonMap("enableDmFeatures", "true")));
        Commons.setNGDMImage(adminClient, path);
        reloadEditor();
        openEditDialog(path);
        uncheckInheritFromPageImage();
        selectInCoralSelect("[name='./smartCropRendition']", "Small");
        dialog().locator("coral-checkbox[name='./isDecorative'] input[type='checkbox']").check();
        saveDialog();
        openPreview();

        assertThat(image()).hasAttribute("src", Pattern.compile(".*smartcrop=Small.*"));
    }

    @ParameterizedTest
    @MethodSource("v2AndV3Versions")
    public void testClearAssetInputGetDamInfoCheckboxesNotVisible(String resourceType) throws Exception {
        // the clear button is styled with Coral Spectrum on Cloud Service and Coral 3 on 6.5/LTS
        testClearAssetInput(resourceType, "button.cq-FileUpload-clear:visible");
    }

    private void testClearAssetInput(String resourceType, String clearSelector) throws Exception {
        String path = addImage(resourceType);
        dragImage(path, IMAGE);
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
        configureFeaturedImage(NO_DESCRIPTION_IMAGE, "", true);
        openPreview();

        assertThat(image()).hasAttribute("alt", "");
        assertThat(image()).hasAttribute("src", Pattern.compile(".*adobe-systems-logo-and-wordmark.*"));
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithEmptyAltTextFromPageImage65() throws Exception {
        testPageImageWithEmptyAltTextFromPageImage();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithAltTextFromPageImage() throws Exception {
        configureFeaturedImage(NO_DESCRIPTION_IMAGE, "page image alt", false);
        openPreview();

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
        String path = configureFeaturedImage(NO_DESCRIPTION_IMAGE, "page image alt", false);
        openEditDialog(path);
        setCheckboxIfShown(RT_IMAGE_V3, "./altValueFromPageImage", false);
        field(RT_IMAGE_V3, "input[name='./alt']").fill("Return to Arkham");
        saveDialog();
        openPreview();

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
        String path = configureFeaturedImage(NO_DESCRIPTION_IMAGE, "page image alt", false);
        openEditDialog(path);
        setCheckbox(RT_IMAGE_V3, "./isDecorative", true);
        saveDialog();
        openPreview();

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
        String path = configureFeaturedImage(NO_DESCRIPTION_IMAGE, "page image alt", false);
        dragImage(path, IMAGE);
        saveDialog();
        openPreview();

        assertThat(image()).hasAttribute("alt", DAM_ALT);
        assertThat(image()).hasAttribute("src", Pattern.compile(".*core-comp-test-image.*"));
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithDragAndDropImage65() throws Exception {
        testPageImageWithDragAndDropImage();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testPageImageWithLinkedPage() throws Exception {
        String redirectPage = createRedirectPage();
        adminClient.setPageProperty(redirectPage, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        ImageSidePanelHelper.setPageFeaturedImage(page, baseUrl, redirectPage, "AdobeStock_140634652_climbing.jpeg",
            true);
        String path = configureFeaturedImage(NO_DESCRIPTION_IMAGE, null, true);
        openEditDialog(path);
        setLink(RT_IMAGE_V3, redirectPage);
        saveDialog();
        openPreview();

        assertThat(image()).hasAttribute("alt", "Rock Climbing and Bouldering above the lake and mountains");
        assertThat(image()).hasAttribute("src", Pattern.compile(".*adobestock-140634652-climbing\\.jpeg.*"));
        image().click();
        assertThat(page).hasURL(Pattern.compile(".*" + regex(redirectPage) + "\\.html$"));
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testPageImageWithLinkedPage65() throws Exception {
        testPageImageWithLinkedPage();
    }

    /**
     * Creates a v3 page whose featured image is set through the page properties UI and adds an image component that
     * inherits it.
     */
    private String configureFeaturedImage(String assetPath, String altText, boolean altFromDam) throws Exception {
        testPage = authorClient.createPage("image-page", "Image Page", rootPage, defaultPageTemplate).getSlingPath();
        adminClient.setPageProperty(testPage, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        ImageSidePanelHelper.setPageFeaturedImage(page, baseUrl, testPage,
            assetPath.substring(assetPath.lastIndexOf('/') + 1), altFromDam);
        if (altText != null && !altFromDam) {
            ImageSidePanelHelper.setPageFeaturedImageAlt(page, baseUrl, testPage, altText);
        }
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
