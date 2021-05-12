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

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.formtext.v1.FormText;
import com.adobe.cq.wcm.core.components.it.seljup.tests.formtext.FormTextTests;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

@Tag("group1")
public class FormTextIT extends AuthorBaseUITest {

    protected FormTextTests formTextTests;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        formTextTests = new FormTextTests();
        formTextTests.setup(adminClient, Commons.rtFormText_v1, rootPage, defaultPageTemplate, new FormText());
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        formTextTests.cleanup(adminClient);
    }

    /**
     * Test: Check the mandatory fields
     */
    @Test
    @DisplayName("Test: Check the mandatory fields")
    public void checkMandatoryFields() throws InterruptedException, TimeoutException {
        formTextTests.checkLabelMandatory();
    }

    /**
     * Test: Set text input label
     */
    @Test
    @DisplayName("Test: Set text input label")
    public void setLabel() throws InterruptedException, TimeoutException {
        formTextTests.setLabel();
    }

    /**
     * Test: Hide input label
     */
    @Test
    @DisplayName("Test: Hide input label")
    public void hideLabel() throws InterruptedException, TimeoutException {
        formTextTests.hideLabel();
    }

    /**
     * Test: Set element name
     */
    @Test
    @DisplayName("Test: Set element name")
    public void setElementName() throws InterruptedException, TimeoutException {
        formTextTests.setElementName();
    }

    /**
     * Test: Set element value
     */
    @Test
    @DisplayName("Test: Set element value")
    public void setValue() throws InterruptedException, TimeoutException {
        formTextTests.setValue();
    }

    /**
     * Test : Create a text input field
     */
    @Test
    @DisplayName("Test : Create a text input field")
    public void createTextInput() throws InterruptedException, TimeoutException {
        formTextTests.createTextInput();
    }

    /**
     * Test : Create a text area
     */
    @Test
    @DisplayName("Test : Create a text area")
    public void createTextarea() throws InterruptedException, TimeoutException {
        formTextTests.createTextarea();
    }

    /**
     * Test : Create a email input field
     */
    @Test
    @DisplayName("Test : Create a email input field")
    public void createEmail() throws InterruptedException, TimeoutException {
        formTextTests.createEmail();
    }

    /**
     * Test : Create a telephone input field
     */
    @Test
    @DisplayName("Test : Create a telephone input field")
    public void createTel() throws InterruptedException, TimeoutException {
        formTextTests.createTel();
    }

    /**
     * Test : Create a date input field
     */
    @Test
    @DisplayName("Test : Create a date input field")
    public void createDate() throws InterruptedException, TimeoutException {
        formTextTests.createDate();
    }

    /**
     * Test : Create a number input field
     */
    @Test
    @DisplayName("Test : Create a number input field")
    public void createNumber() throws InterruptedException, TimeoutException {
        formTextTests.createNumber();
    }

    /**
     * Test : Create a password input field
     */
    @Test
    @DisplayName("Test : Create a password input field")
    public void createPassword() throws InterruptedException, TimeoutException {
        formTextTests.createPassword();
    }

    /**
     * Test : set Help message as tooltip
     */
    @Test
    @DisplayName("Test : set Help message as tooltip")
    public void setHelpMessage() throws InterruptedException, TimeoutException {
        formTextTests.setHelpMessage();
    }

    /**
     * Test : set Help message as placeholder
     */
    @Test
    @DisplayName("Test : set Help message as placeholder")
    public void setHelpMessageAsPlaceholder() throws InterruptedException, TimeoutException {
        formTextTests.setHelpMessageAsPlaceholder();
    }

    /**
     * Test: check available constraints element name
     */
    @Test
    @DisplayName("Test: check available constraints element name")
    public void checkAvailableConstraints() throws TimeoutException, InterruptedException {
        formTextTests.checkAvailableConstraints();
    }

    /**
     * Test : test read only setting
     */
    @Test
    @DisplayName("Test : test read only setting")
    public void setReadOnly() throws TimeoutException, InterruptedException {
        formTextTests.setReadOnly();
    }

    /**
     * Test : test required setting
     */
    @Test
    @DisplayName("Test : test required setting")
    public void setRequired() throws TimeoutException, InterruptedException {
        formTextTests.setRequired();
    }

    /**
     * Test : test constraint message
     */
    @Test
    @DisplayName("Test : test constraint message")
    public void setConstraintMessage() throws TimeoutException, InterruptedException {
        formTextTests.setConstraintMessage();
    }
}
