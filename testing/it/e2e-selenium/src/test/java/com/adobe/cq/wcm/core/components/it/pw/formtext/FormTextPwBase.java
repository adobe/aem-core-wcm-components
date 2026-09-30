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
package com.adobe.cq.wcm.core.components.it.pw.formtext;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.PlaywrightAuthorBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Shared Playwright fixtures and tests for the v1/v2 FormText Selenium suites.
 */
public abstract class FormTextPwBase extends PlaywrightAuthorBaseTest {

    private static final String ELEM_NAME = "Luigi";
    private static final String LABEL = "It is me, Mario!";
    private static final String DEFAULT_VALUE = "Uncharted";
    private static final String HELP_MESSAGE = "Skyrim";
    private static final String REQUIRED_MESSAGE = "Attack ships on fire off the shoulder of Orion";
    protected String testPage;
    protected String formTextPath;

    protected abstract String formTextResourceType();

    @BeforeEach
    void setupPage() throws Exception {
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        formTextPath = Commons.addComponentWithRetry(authorClient, formTextResourceType(),
            testPage + Commons.relParentCompPath, "formtext");
        openEditor(testPage);
    }

    @AfterEach
    void deletePage() throws Exception {
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS,
            RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
    }

    // ---------------------------------------------------------------- page objects

    private FrameLocator frame() {
        return contentFrame();
    }

    protected Locator field(String type) {
        return "textarea".equals(type)
            ? frame().locator("textarea[name='" + ELEM_NAME + "']")
            : frame().locator("input[type='" + type + "'][name='" + ELEM_NAME + "']");
    }

    private Locator helpBlock(String message) {
        String selector = formTextResourceType().equals(RT_FORMTEXT_V1)
            ? "p[class='help-block']" : "p[class='cmp-form-text__help-block']";
        return frame().locator(selector).filter(new Locator.FilterOptions().setHasText(message));
    }

    protected Locator validationMessage() {
        return frame().locator("[name='" + ELEM_NAME + "'] + .cmp-form-text__validation-message");
    }

    protected void setMandatoryFields() {
        dialog().locator("[name='./name']").fill(ELEM_NAME);
        dialog().locator("[name='./jcr:title']:visible").first().fill(LABEL);
    }

    protected void setOptionType(String type) {
        selectInCoralSelect("[name='./type']", type);
    }

    protected void openTab(String trackingEvent) {
        dialog().locator("coral-tab[data-foundation-tracking-event*='" + trackingEvent + "']").click();
    }

    protected void setOptionTypeAndSave(String type) {
        openEditDialog(formTextPath);
        setOptionType(type);
        saveDialog();
    }

    protected void typeValue(String value) {
        frame().locator("[name='" + ELEM_NAME + "'][data-cmp-hook-form-text='input']").pressSequentially(value);
    }

    // ---------------------------------------------------------------- tests inherited from v1

    @Test
    public void testCheckLabelMandatory() {
        openEditDialog(formTextPath);
        clickDone();
        assertThat(dialog()).isVisible();
        assertThat(dialog().locator("[name='./name']")).hasAttribute("aria-invalid", "true");
        assertThat(dialog().locator("[name='./jcr:title'][invalid='']")).hasCount(1);
    }

    @Test
    public void testSetLabel() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        saveDialog();
        assertThat(frame().locator("label").first()).hasText(LABEL);
    }

    @Test
    public void testHideLabel() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        checkCoralCheckbox("./hideTitle");
        saveDialog();
        assertThat(frame().locator("input[type='text'][name='" + ELEM_NAME + "'][aria-label='" + LABEL + "']")).isVisible();
        assertThat(frame().locator("label").first()).isHidden();
        setOptionTypeAndSave("textarea");
        assertThat(frame().locator("textarea[name='" + ELEM_NAME + "'][aria-label='" + LABEL + "']")).isVisible();
    }

    @Test
    public void testSetElementName() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        saveDialog();
        assertThat(field("text")).isVisible();
        setOptionTypeAndSave("textarea");
        assertThat(field("textarea")).isVisible();
    }

    @Test
    public void testSetValue() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        dialog().locator("[name='./value']").fill(DEFAULT_VALUE);
        saveDialog();
        assertThat(frame().locator("input[type='text'][value='" + DEFAULT_VALUE + "']")).isVisible();
        setOptionTypeAndSave("textarea");
        assertThat(field("textarea")).hasValue(DEFAULT_VALUE);
    }

    private void createFieldOfType(String type) {
        openEditDialog(formTextPath);
        setMandatoryFields();
        setOptionType(type);
        saveDialog();
        assertThat(field(type)).isVisible();
    }

    @Test
    public void testCreateTextInput() {
        createFieldOfType("text");
    }

    @Test
    public void testCreateTextarea() {
        createFieldOfType("textarea");
    }

    @Test
    public void testCreateEmail() {
        createFieldOfType("email");
    }

    @Test
    public void testCreateTel() {
        createFieldOfType("tel");
    }

    @Test
    public void testCreateDate() {
        createFieldOfType("date");
    }

    @Test
    public void testCreateNumber() {
        createFieldOfType("number");
    }

    @Test
    public void testCreatePassword() {
        createFieldOfType("password");
    }

    @Test
    public void testSetHelpMessage() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        openTab("about");
        dialog().locator("input[name='./helpMessage']").fill(HELP_MESSAGE);
        saveDialog();
        assertThat(helpBlock(HELP_MESSAGE)).isVisible();
    }

    @Test
    public void testSetHelpMessageAsPlaceholder() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        openTab("about");
        dialog().locator("input[name='./helpMessage']").fill(HELP_MESSAGE);
        checkCoralCheckbox("./usePlaceholder");
        saveDialog();
        assertThat(frame().locator("input[type='text'][name='" + ELEM_NAME + "'][placeholder='" + HELP_MESSAGE + "']")).isVisible();
    }

    @Test
    public void testCheckAvailableConstraints() {
        openEditDialog(formTextPath);
        Locator list = openCoralSelect("[name='./type']");
        for (String type : new String[] {"text", "textarea", "email", "tel", "date", "number", "password"}) {
            assertThat(list.locator("coral-selectlist-item[value='" + type + "']")).isVisible();
        }
    }

    @Test
    public void testSetReadOnly() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        openTab("constraints");
        checkCoralCheckbox("./readOnly");
        saveDialog();
        assertThat(frame().locator("input[type='text'][name='" + ELEM_NAME + "'][readonly]")).isVisible();
        setOptionTypeAndSave("textarea");
        assertThat(frame().locator("textarea[name='" + ELEM_NAME + "'][readonly]")).isVisible();
    }

    @Test
    public void testSetRequired() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        openTab("constraints");
        checkCoralCheckbox("./required");
        dialog().locator("textarea[name='./requiredMessage']").fill(REQUIRED_MESSAGE);
        saveDialog();
        assertThat(frame().locator("input[type='text'][name='" + ELEM_NAME + "'][required]")).isVisible();
        setOptionTypeAndSave("textarea");
        assertThat(frame().locator("textarea[name='" + ELEM_NAME + "'][required]")).isVisible();
        if (RT_FORMTEXT_V1.equals(formTextResourceType())) {
            assertThat(frame().locator("textarea[name='" + ELEM_NAME + "'][data-cmp-required='" + REQUIRED_MESSAGE + "']"))
                .isVisible();
        } else {
            assertThat(frame().locator(".cmp-form-text[data-cmp-required-message='" + REQUIRED_MESSAGE + "']")).isVisible();
        }
    }

    @Test
    public void testSetConstraintMessage() {
        openEditDialog(formTextPath);
        setMandatoryFields();
        setOptionType("email");
        openTab("constraints");
        dialog().locator("textarea[name='./constraintMessage']").fill(REQUIRED_MESSAGE);
        saveDialog();
        if (RT_FORMTEXT_V1.equals(formTextResourceType())) {
            assertThat(frame().locator("input[name='" + ELEM_NAME + "'][data-cmp-constraint='" + REQUIRED_MESSAGE + "']"))
                .isVisible();
        } else {
            assertThat(frame().locator(".cmp-form-text[data-cmp-constraint-message='" + REQUIRED_MESSAGE + "']")).isVisible();
        }
    }

    protected void saveWithHelpMessage(String type, String helpMessage) {
        openEditDialog(formTextPath);
        setOptionType(type);
        setMandatoryFields();
        openTab("about");
        if (helpMessage != null) {
            dialog().locator("input[name='./helpMessage']").fill(helpMessage);
        }
        saveDialog();
        assertThat(field(type)).isVisible();
    }

    private void assertDescribedByHelp(String type) {
        Locator help = helpBlock(HELP_MESSAGE);
        assertThat(help).isVisible();
        assertEquals(help.getAttribute("id"), field(type).getAttribute("aria-describedby"));
    }

    @Test
    public void testTextareaAccessibilityWhenHelpMessageIsSet() {
        saveWithHelpMessage("textarea", HELP_MESSAGE);
        assertDescribedByHelp("textarea");
    }

    @Test
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnTextarea() {
        saveWithHelpMessage("textarea", null);
        assertNull(field("textarea").getAttribute("aria-describedby"));
    }

    @Test
    public void testInputAccessibilityWhenHelpMessageIsSet() {
        saveWithHelpMessage("text", HELP_MESSAGE);
        assertDescribedByHelp("text");
    }

    @Test
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnInput() {
        saveWithHelpMessage("text", null);
        assertNull(field("text").getAttribute("aria-describedby"));
    }

}
