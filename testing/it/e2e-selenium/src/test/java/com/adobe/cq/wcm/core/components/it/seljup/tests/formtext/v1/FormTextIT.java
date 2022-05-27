/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formtext.v1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext.v1.FormText;
import com.adobe.cq.wcm.core.components.it.seljup.tests.formtext.FormTextTests;
import org.apache.sling.testing.clients.ClientException;

import java.util.concurrent.TimeoutException;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V1;

@Tag("group1")
public class FormTextIT extends AuthorBaseUITest {

    protected FormTextTests formTextTests;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        formTextTests = new FormTextTests();
        formTextTests.setup(authorClient, RT_FORMTEXT_V1, rootPage, defaultPageTemplate, new FormText());
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        formTextTests.cleanup(authorClient);
    }

    /**
     * Test: Check the mandatory fields
     */
    @Test
    @DisplayName("Test: Check the mandatory fields")
    public void testCheckLabelMandatory() throws InterruptedException, TimeoutException {
        formTextTests.testCheckLabelMandatory();
    }

    /**
     * Test: Set text input label
     */
    @Test
    @DisplayName("Test: Set text input label")
    public void testSetLabel() throws InterruptedException, TimeoutException {
        formTextTests.testSetLabel();
    }

    /**
     * Test: Hide input label
     */
    @Test
    @DisplayName("Test: Hide input label")
    public void testHideLabel() throws InterruptedException, TimeoutException {
        formTextTests.testHideLabel();
    }

    /**
     * Test: Set element name
     */
    @Test
    @DisplayName("Test: Set element name")
    public void testSetElementName() throws InterruptedException, TimeoutException {
        formTextTests.testSetElementName();
    }

    /**
     * Test: Set element value
     */
    @Test
    @DisplayName("Test: Set element value")
    public void testSetValue() throws InterruptedException, TimeoutException {
        formTextTests.testSetValue();
    }

    /**
     * Test : Create a text input field
     */
    @Test
    @DisplayName("Test : Create a text input field")
    public void testCreateTextInput() throws InterruptedException, TimeoutException {
        formTextTests.testCreateTextInput();
    }

    /**
     * Test : Create a text area
     */
    @Test
    @DisplayName("Test : Create a text area")
    public void testCreateTextarea() throws InterruptedException, TimeoutException {
        formTextTests.testCreateTextarea();
    }

    /**
     * Test : Create a email input field
     */
    @Test
    @DisplayName("Test : Create a email input field")
    public void testCreateEmail() throws InterruptedException, TimeoutException {
        formTextTests.testCreateEmail();
    }

    /**
     * Test : Create a telephone input field
     */
    @Test
    @DisplayName("Test : Create a telephone input field")
    public void testCreateTel() throws InterruptedException, TimeoutException {
        formTextTests.testCreateTel();
    }

    /**
     * Test : Create a date input field
     */
    @Test
    @DisplayName("Test : Create a date input field")
    public void testCreateDate() throws InterruptedException, TimeoutException {
        formTextTests.testCreateDate();
    }

    /**
     * Test : Create a number input field
     */
    @Test
    @DisplayName("Test : Create a number input field")
    public void testCreateNumber() throws InterruptedException, TimeoutException {
        formTextTests.testCreateNumber();
    }

    /**
     * Test : Create a password input field
     */
    @Test
    @DisplayName("Test : Create a password input field")
    public void testCreatePassword() throws InterruptedException, TimeoutException {
        formTextTests.testCreatePassword();
    }

    /**
     * Test : set Help message as tooltip
     */
    @Test
    @DisplayName("Test : set Help message as tooltip")
    public void testSetHelpMessage() throws InterruptedException, TimeoutException {
        formTextTests.testSetHelpMessage();
    }

    /**
     * Test : set Help message as placeholder
     */
    @Test
    @DisplayName("Test : set Help message as placeholder")
    public void testSetHelpMessageAsPlaceholder() throws InterruptedException, TimeoutException {
        formTextTests.testSetHelpMessageAsPlaceholder();
    }

    /**
     * Test: check available constraints element name
     */
    @Test
    @DisplayName("Test: check available constraints element name")
    public void testCheckAvailableConstraints() throws TimeoutException, InterruptedException {
        formTextTests.testCheckAvailableConstraints();
    }

    /**
     * Test : test read only setting
     */
    @Test
    @DisplayName("Test : test read only setting")
    public void testSetReadOnly() throws TimeoutException, InterruptedException {
        formTextTests.testSetReadOnly();
    }

    /**
     * Test : test required setting
     */
    @Test
    @DisplayName("Test : test required setting")
    public void testSetRequired() throws TimeoutException, InterruptedException {
        formTextTests.testSetRequired();
    }

    /**
     * Test : test constraint message
     */
    @Test
    @DisplayName("Test : test constraint message")
    public void testSetConstraintMessage() throws TimeoutException, InterruptedException {
        formTextTests.testSetConstraintMessage();
    }

    /**
     * Test : set the help message and verify the textarea element to have the aria-describedby attribute equal with the help message id
     */
    @Test
    @DisplayName("Test : set the help message and verify the textarea element to have the aria-describedby attribute equal with the help message id")
    public void testTextareaAccessibilityWhenHelpMessageIsSet() throws TimeoutException, InterruptedException {
        formTextTests.testTextareaAccessibilityWhenHelpMessageIsSet();
    }

    /**
     * Test: without setting a help message, verify the textarea element to have no aria-describedby attribute
     */
    @Test
    @DisplayName("Test : without setting a help message, verify the textarea element to have no aria-describedby attribute")
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnTextarea() throws InterruptedException, TimeoutException {
        formTextTests.testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnTextarea();
    }

    /**
     * Test : set the help message and verify the input element to have the aria-describedby attribute equal with the help message id
     */
    @Test
    @DisplayName("Test : set the help message and verify the input element to have the aria-describedby attribute equal with the help message id")
    public void testInputAccessibilityWhenHelpMessageIsSet() throws TimeoutException, InterruptedException {
        formTextTests.testInputAccessibilityWhenHelpMessageIsSet();
    }

    /**
     * Test: without setting a help message, verify the input element to have no aria-describedby attribute
     */
    @Test
    @DisplayName("Test : without setting a help message, verify the input element to have no aria-describedby attribute")
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnInput() throws InterruptedException, TimeoutException {
        formTextTests.testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnInput();
    }
}
