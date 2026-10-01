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
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.teaser;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEASER_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class TeaserV1PwIT extends ComponentPwBaseTest {

    protected static final String ASSET = "/content/dam/core-components/core-comp-test-image.jpg";
    protected static final String TITLE = "Teaser Title";
    protected static final String DESCRIPTION = "Teaser Description";
    protected static final String PRETITLE = "Teaser PreTitle";
    protected static final String SECOND_TITLE = "teaser_second_page";
    protected String secondPage;
    protected String thirdPage;
    protected String teaserPath;

    protected String teaserResourceType() {
        return RT_TEASER_V1;
    }

    protected String teaserClientlib() {
        return Commons.CLIENTLIBS_TEASER_V1;
    }

    protected String setTitleAllowedTypes(String... allowedTypes) throws Exception {
        String policy = createComponentPolicy(policyPath(),
            Map.of("titleType", "h4", "showTitleType", "true"));
        adminClient.setPropertyStringArray(policy, "allowedTypes", Arrays.asList(allowedTypes), 200);
        return policy;
    }

    protected void createTeaser() throws Exception {
        createPagePolicy(Map.of("clientlibs", teaserClientlib()));
        teaserPath = addStandaloneComponent(teaserResourceType(), "teaser");
        secondPage = authorClient.createPage("teaser-second-page", SECOND_TITLE, testPage, defaultPageTemplate).getSlingPath();
        thirdPage = authorClient.createPage("teaser-third-page", "teaser_third_page", testPage, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(testPage, "jcr:description", "teaser page description", 200);
    }

    protected void setImage() throws Exception {
        Commons.editNodeProperties(authorClient, teaserPath, new HashMap<>(Map.of("fileReference", ASSET)));
        reloadEditor();
    }

    protected Locator tabs() {
        return dialog().locator(".cmp-teaser__editor coral-tab");
    }

    protected void openTextTab() {
        tabs().nth(1).click();
    }

    protected void openLinkTab() {
        tabs().nth(2).click();
    }

    protected String actionHook(String name) {
        return "[data-cmp-teaser-v1-dialog-edit-hook='" + name + "']";
    }

    protected Locator checkbox(String name) {
        return dialog().locator("coral-checkbox[name='" + name + "'] input[type='checkbox']").first();
    }

    protected Locator titleInput() {
        return dialog().locator("input[name='./jcr:title']");
    }

    protected void setText(String title, String description) {
        openTextTab();
        if (title != null) {
            titleInput().fill(title);
        }
        if (description != null) {
            dialog().locator("div[name='./jcr:description']").fill(description);
        }
    }

    private Locator teaser() {
        return contentFrame().locator(".cmp-teaser");
    }

    /** Enabling actions may already add an empty item; reuse it instead of leaving it to fail validation. */
    protected Locator nextActionLinkInput() {
        Locator links = dialog().locator(actionHook("actionLink"));
        if (links.count() == 0 || !links.last().locator("input:not([type='hidden'])").first().inputValue().isEmpty()) {
            dialog().locator("[coral-multifield-add]").click();
        }
        return links.last().locator("input:not([type='hidden'])").first();
    }

    protected void addActionLink(String path) {
        nextActionLinkInput().fill(path);
        page.locator("button[is='coral-buttonlist-item'][value='" + path + "']:visible").first().click();
    }

    protected void setActionTitle(String title) {
        dialog().locator(actionHook("actionTitle")).last().fill(title);
    }

    protected String policyPath() {
        return teaserResourceType().substring(teaserResourceType().lastIndexOf("/"));
    }

    @Test
    public void testFullyConfiguredTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        openLinkTab();
        selectAutocomplete("[name='./linkURL']", testPage);
        setText(TITLE, DESCRIPTION);
        dialog().locator("[name='./pretitle']").fill(PRETITLE);
        saveDialog();
        assertThat(teaser().locator("img")).isVisible();
        assertThat(teaser().locator(".cmp-teaser__pretitle")).hasText(PRETITLE);
        assertThat(teaser().locator(".cmp-teaser__title-link")).containsText(TITLE);
        assertThat(teaser().locator(".cmp-teaser__description")).containsText(DESCRIPTION);
    }

    @Test
    public void testInheritedPropertiesTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        openLinkTab();
        selectAutocomplete("[name='./linkURL']", testPage);
        openTextTab();
        checkbox("./titleFromPage").check();
        checkbox("./descriptionFromPage").check();
        saveDialog();
        assertThat(teaser().locator(".cmp-teaser__title-link")).containsText("Test Page Title");
        assertThat(teaser().locator(".cmp-teaser__description")).containsText("teaser page description");
    }

    @Test
    public void testNoImageTeaser() throws Exception {
        createTeaser();
        openEditDialog(teaserPath);
        setText(TITLE, DESCRIPTION);
        saveDialog();
        assertThat(teaser().locator("img")).hasCount(0);
        assertThat(teaser().locator(".cmp-teaser__title")).containsText(TITLE);
        assertThat(teaser().locator(".cmp-teaser__description")).containsText(DESCRIPTION);
    }

    @Test
    public void testHideElementsTeaser() throws Exception {
        createTeaser();
        createComponentPolicy(policyPath(),
            Map.of("titleHidden", "true", "descriptionHidden", "true"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        assertThat(dialog().locator("[name='./titleFromPage']")).hasCount(0);
        assertThat(dialog().locator("[name='./descriptionFromPage']")).hasCount(0);
    }

    @Test
    public void testLinksToElementsTeaser() throws Exception {
        createTeaser();
        setImage();
        createComponentPolicy(policyPath(),
            Map.of("titleLinkHidden", "true", "imageLinkHidden", "true"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        openLinkTab();
        selectAutocomplete("[name='./linkURL']", testPage);
        saveDialog();
        assertThat(teaser().locator("img")).isVisible();
        assertThat(teaser().locator(".cmp-teaser__title-link")).hasCount(0);
    }

    @Test
    public void testDisableActionsTeaser() throws Exception {
        createTeaser();
        createComponentPolicy(policyPath(),
            Map.of("actionsDisabled", "true"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        openLinkTab();
        assertNotNull(dialog().locator("coral-checkbox[name='./actionsEnabled']").first().getAttribute("disabled"));
        assertFalse(checkbox("./actionsEnabled").isChecked());
    }

    @Test
    public void testWithActionsTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        openTextTab();
        checkbox("./titleFromPage").check();
        checkbox("./descriptionFromPage").check();
        openLinkTab();
        checkbox("./actionsEnabled").check();
        addActionLink(testPage);
        addActionLink(secondPage);
        saveDialog();
        assertThat(teaser().locator(".cmp-teaser__action-link")).hasCount(2);
        assertThat(teaser().locator(".cmp-teaser__action-link").nth(0)).containsText("Test Page Title");
        assertThat(teaser().locator(".cmp-teaser__action-link").nth(1)).containsText(SECOND_TITLE);
    }

    @Test
    public void testWithExternalActionsTeaser() throws Exception {
        createTeaser();
        setImage();
        openEditDialog(teaserPath);
        openLinkTab();
        checkbox("./actionsEnabled").check();
        nextActionLinkInput().fill("http://www.adobe.com");
        setActionTitle("Adobe");
        addActionLink(secondPage);
        setActionTitle("Action Text 2");
        saveDialog();
        assertThat(teaser().locator(".cmp-teaser__action-link")).hasCount(2);
        assertThat(teaser().locator(".cmp-teaser__action-link").first()).hasAttribute("href", "http://www.adobe.com");
        assertThat(teaser().locator(".cmp-teaser__action-link").first()).containsText("Adobe");
        assertThat(teaser().locator(".cmp-teaser__action-link").nth(1)).containsText("Action Text 2");
    }

    @Test
    public void testCheckboxTextfieldTuple() throws Exception {
        createTeaser();
        openEditDialog(teaserPath);
        setText(TITLE, null);
        openLinkTab();
        selectAutocomplete("[name='./linkURL']", testPage);
        openTextTab();
        assertThat(titleInput()).hasValue(TITLE);
        assertThat(titleInput()).isEnabled();
        checkbox("./titleFromPage").check();
        assertThat(titleInput()).isDisabled();
        checkbox("./titleFromPage").uncheck();
        assertThat(titleInput()).hasValue(TITLE);
    }

    @Test
    public void testNoTitleTypeSelectDropdownDisplayed() throws Exception {
        createTeaser();
        createComponentPolicy(policyPath(), Map.of("titleType", "h4", "showTitleType", "false"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        assertThat(dialog().locator("coral-select[name='./titleType']")).hasCount(0);
    }

    @Test
    public void testTitleTypeSelectDropdownValueUsingValidOption() throws Exception {
        createTeaser();
        setTitleAllowedTypes("h1", "h2", "h3", "h4", "h6");
        openEditor(testPage);
        openEditDialog(teaserPath);
        assertThat(dialog().locator("coral-select[name='./titleType'] coral-select-item[selected]")).containsText("h4");
    }

    @Test
    public void testTitleTypeSelectDropdownValueUsingInvalidOption() throws Exception {
        createTeaser();
        String policy = createComponentPolicy(policyPath(),
            Map.of("titleType", "h5", "showTitleType", "true"));
        adminClient.setPropertyStringArray(policy, "allowedTypes", Arrays.asList("h3", "h4", "h6"), 200);
        openEditor(testPage);
        openEditDialog(teaserPath);
        assertThat(dialog().locator("coral-select[name='./titleType'] coral-select-item[selected]")).containsText("h3");
    }

    @Test
    public void testTypeTypeSelectDropdownNoAllowedTypes() throws Exception {
        createTeaser();
        createComponentPolicy(policyPath(), Map.of("showTitleType", "true"));
        openEditor(testPage);
        openEditDialog(teaserPath);
        assertThat(dialog().locator("coral-select[name='./titleType'] coral-select-item[selected]")).containsText("(default)");
    }
}
