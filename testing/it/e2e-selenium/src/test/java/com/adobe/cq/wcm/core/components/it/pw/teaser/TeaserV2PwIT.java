/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ You may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.teaser;

import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEASER_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class TeaserV2PwIT extends TeaserV1PwIT {

    private static final String CLIMBING = "/content/dam/core-components/AdobeStock_140634652_climbing.jpeg";
    private static final String SURFING = "/content/dam/core-components/AdobeStock_175749320_surfing.jpg";
    private static final String SKIING = "/content/dam/core-components/AdobeStock_185234795_skiing.jpeg";
    private static final String CLIMBING_ALT = "Rock Climbing and Bouldering above the lake and mountains";
    private static final String SURFING_ALT = "Surfers. Balangan beach. Bali, Indonesia.";
    private static final String SKIING_ALT = "A skier does action skiing at the Rolle Pass in the Dolomites, Italy.";

    @Override
    protected String teaserResourceType() {
        return RT_TEASER_V2;
    }

    @Override
    protected String teaserClientlib() {
        return Commons.CLIENTLIBS_TEASER_V2;
    }

    private Locator tabs() {
        return dialog().locator(".cmp-teaser__editor coral-tab");
    }

    private Locator rendered(String selector) {
        return contentFrame().locator(selector);
    }

    private void setPageImage(String targetPage, String asset, String alt) throws Exception {
        authorClient.setPageProperty(targetPage, "sling:resourceType", "core/wcm/components/page/v3/page", 200);
        Commons.editNodeProperties(authorClient, targetPage + "/jcr:content",
            new HashMap<>(Map.of("image/fileReference", asset, "image/alt", alt)));
    }

    private void setLinkedPage() {
        tabs().nth(0).click();
        selectAutocomplete("[name='./linkURL']", secondPage);
    }

    private void selectActionLink(String path) {
        dialog().locator("[data-cmp-teaser-v2-dialog-edit-hook='actionLink'] input").last().fill(path);
        page.locator("button[is='coral-buttonlist-item'][value='" + path + "']").click();
    }

    @Test
    @Override
    public void testFullyConfiguredTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        tabs().nth(0).click();
        selectAutocomplete("[name='./linkURL']", testPage);
        dialog().locator("coral-checkbox[name='./linkTarget'] input").check();
        tabs().nth(1).click();
        dialog().locator("[name='./pretitle']").fill(PRETITLE);
        dialog().locator("[name='./titleFromPage']").check();
        dialog().locator("[name='./descriptionFromPage']").check();
        dialog().locator("[name='./jcr:title']").fill(TITLE);
        dialog().locator("[name='./jcr:description']").fill(DESCRIPTION);
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).isVisible();
        assertThat(rendered(".cmp-teaser__pretitle")).hasText(PRETITLE);
        assertThat(rendered(".cmp-teaser__title-link")).containsText(TITLE);
        assertThat(rendered(".cmp-teaser__title-link")).hasAttribute("target", "_blank");
        assertThat(rendered(".cmp-teaser__description")).containsText(DESCRIPTION);
    }

    @Test
    @Override
    public void testInheritedPropertiesTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        setLinkedPage();
        saveDialog();
        assertThat(rendered(".cmp-teaser__title-link")).containsText(SECOND_TITLE);
        assertThat(rendered(".cmp-teaser__description")).isVisible();
    }

    @Test
    @Override
    public void testNoImageTeaser() throws Exception {
        createTeaser();
        openEditDialog(teaserPath);
        tabs().nth(1).click();
        dialog().locator("[name='./titleFromPage']").check();
        dialog().locator("[name='./jcr:title']").fill(TITLE);
        dialog().locator("[name='./descriptionFromPage']").check();
        dialog().locator("[name='./jcr:description']").fill(DESCRIPTION);
        tabs().nth(0).click();
        clickDone();
        assertThat(dialog().locator(".cmp-image__editor-alt .coral-Form-errorlabel, "
            + ".cmp-image__editor-alt coral-tooltip[variant='error'] > coral-tooltip-content").first())
            .hasText("Error: Please provide an asset which has a description that can be used as alt text.");
        tabs().nth(2).click();
        dialog().locator("[name='./imageFromPageImage']").check();
        dialog().locator("[name='./altValueFromDAM']").check();
        dialog().locator("[name='./alt']").fill("alt");
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).hasCount(0);
        assertThat(rendered(".cmp-teaser__title")).containsText(TITLE);
        assertThat(rendered(".cmp-teaser__description")).containsText(DESCRIPTION);
    }

    @Test
    @Override
    public void testHideElementsTeaser() throws Exception {
        createTeaser();
        createComponentPolicy("/teaser-v2", Map.of("titleHidden", "true", "descriptionHidden", "true"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        tabs().nth(1).click();
        assertThat(dialog().locator("[name='./titleFromPage']")).hasCount(0);
        assertThat(dialog().locator("[name='./descriptionFromPage']")).hasCount(0);
    }

    @Test
    @Override
    public void testLinksToElementsTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        tabs().nth(2).click();
        selectAutocomplete("[name='./linkURL']", testPage);
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).isVisible();
        assertThat(rendered(".cmp-teaser__title-link")).hasCount(0);
    }

    @Test
    @Override
    public void testDisableActionsTeaser() throws Exception {
        createTeaser();
        createComponentPolicy("/teaser-v2", Map.of("actionsDisabled", "true"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        tabs().nth(1).click();
        assertThat(dialog().locator("[data-cmp-teaser-v2-dialog-edit-hook='actionLink']")).hasCount(0);
    }

    @Test
    @Override
    public void testWithActionsTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        tabs().nth(0).click();
        dialog().locator("[coral-multifield-add]").click();
        selectActionLink(testPage);
        dialog().locator("[coral-multifield-add]").click();
        selectActionLink(secondPage);
        saveDialog();
        assertThat(rendered(".cmp-teaser__action-link")).hasCount(2);
        assertThat(rendered(".cmp-teaser__action-link").nth(0)).containsText("Test Page Title");
        assertThat(rendered(".cmp-teaser__action-link").nth(1)).containsText(SECOND_TITLE);
    }

    @Test
    @Override
    public void testWithExternalActionsTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        tabs().nth(0).click();
        dialog().locator("[coral-multifield-add]").click();
        dialog().locator("[data-cmp-teaser-v2-dialog-edit-hook='actionLink'] input").last().fill("http://www.adobe.com");
        dialog().locator("[data-cmp-teaser-v2-dialog-edit-hook='actionTitle'] input").last().fill("Adobe");
        dialog().locator("[coral-multifield-add]").click();
        selectActionLink(secondPage);
        dialog().locator("[data-cmp-teaser-v2-dialog-edit-hook='actionTitle'] input").last().fill("Action Text 2");
        dialog().locator(".cmp-teaser__editor-actionField-linkTarget").last().click();
        saveDialog();
        assertThat(rendered(".cmp-teaser__action-link")).hasCount(2);
        assertThat(rendered(".cmp-teaser__action-link").first()).containsText("Adobe");
        assertThat(rendered(".cmp-teaser__action-link").nth(1)).containsText("Action Text 2");
        assertThat(rendered(".cmp-teaser__action-link").nth(1)).hasAttribute("target", "_blank");
    }

    @Test
    @Override
    public void testCheckboxTextfieldTuple() throws Exception {
        createTeaser();
        openEditDialog(teaserPath);
        tabs().nth(1).click();
        dialog().locator("[name='./jcr:title']").fill(TITLE);
        setLinkedPage();
        assertThat(dialog().locator("[name='./jcr:title']")).hasValue(TITLE);
        dialog().locator("[name='./titleFromPage']").check();
        assertThat(dialog().locator("[name='./jcr:title']")).isDisabled();
        dialog().locator("[name='./titleFromPage']").uncheck();
        assertThat(dialog().locator("[name='./jcr:title']")).hasValue(TITLE);
    }

    @Test
    public void testWithLinkAndImageTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        setLinkedPage();
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).isVisible();
        assertThat(rendered(".cmp-teaser__title")).isVisible();
        assertThat(rendered(".cmp-teaser__description")).isVisible();
    }

    @Test
    public void testInheritImageFromCurrentPage() throws Exception {
        createTeaser();
        setPageImage(testPage, CLIMBING, CLIMBING_ALT);
        Commons.editNodeProperties(authorClient, teaserPath, new HashMap<>(Map.of("imageFromPageImage", "true")));
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-teaser img")).hasAttribute("alt", CLIMBING_ALT);
        assertTrue(page.locator(".cmp-teaser img").getAttribute("src").contains("climbing"));
    }

    @Test
    public void testInheritImageFromCurrentPage_isDecorative() throws Exception {
        createTeaser();
        setPageImage(testPage, CLIMBING, CLIMBING_ALT);
        openEditDialog(teaserPath);
        tabs().nth(2).click();
        dialog().locator("coral-checkbox[name='./isDecorative'] input").check();
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).hasAttribute("alt", "");
    }

    @Test
    public void testInheritImageFromLinkedPage() throws Exception {
        createTeaser();
        setPageImage(secondPage, SURFING, SURFING_ALT);
        openEditDialog(teaserPath);
        tabs().nth(2).click();
        selectAutocomplete("[name='./linkURL']", secondPage);
        dialog().locator("coral-checkbox[name='./imageFromPageImage'] input").check();
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).hasAttribute("alt", SURFING_ALT);
    }

    @Test
    public void testInheritImageFromLinkedPage_altNotInherited() throws Exception {
        createTeaser();
        setPageImage(secondPage, SURFING, SURFING_ALT);
        openEditDialog(teaserPath);
        tabs().nth(2).click();
        selectAutocomplete("[name='./linkURL']", secondPage);
        dialog().locator("coral-checkbox[name='./imageFromPageImage'] input").check();
        dialog().locator("[name='./alt']").fill("Teaser alt text");
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).hasAttribute("alt", "Teaser alt text");
    }

    @Test
    public void testInheritImageFromAction() throws Exception {
        createTeaser();
        setPageImage(thirdPage, SKIING, SKIING_ALT);
        openEditDialog(teaserPath);
        tabs().nth(0).click();
        dialog().locator("[coral-multifield-add]").click();
        selectActionLink(thirdPage);
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).hasAttribute("alt", SKIING_ALT);
    }

    @Test
    public void testInheritImageFromAction_altNotInherited() throws Exception {
        createTeaser();
        setPageImage(thirdPage, SKIING, SKIING_ALT);
        openEditDialog(teaserPath);
        tabs().nth(0).click();
        dialog().locator("[coral-multifield-add]").click();
        selectActionLink(thirdPage);
        tabs().nth(2).click();
        dialog().locator("[name='./alt']").fill("Teaser alt text");
        saveDialog();
        assertThat(rendered(".cmp-teaser img")).hasAttribute("alt", "Teaser alt text");
    }
}
