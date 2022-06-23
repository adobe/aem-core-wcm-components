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

package com.adobe.cq.wcm.core.components.it.seljup.util.assertion;

import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.codeborne.selenide.SelenideElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EditableToolbarAssertion {

    private final EditableToolbar editableToolbar;
    private String message;

    public EditableToolbarAssertion(EditableToolbar editableToolbar, String message) {
        this.editableToolbar = editableToolbar;
        this.message = message;
    }

    public EditableToolbarAssertion setMessage(String message) {
        this.message = message;
        return this;
    }

    public EditableToolbarAssertion assertButtonsVisible(Button... buttons) {
        for (Button button : buttons) {
            assertButtonVisible(button.getEditableToolbarAction().getButton(), button.getTitle());
        }
        return this;
    }

    public EditableToolbarAssertion assertButtonsInvisible(Button... buttons) {
        for (Button button : buttons) {
            assertButtonInvisible(button.getEditableToolbarAction().getButton(), button.getTitle());
        }
        return this;
    }

    public EditableToolbarAssertion assertNewlineButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getNewlineButton(), shouldBeVisible, String.format(message, "newLine"));
    }

    public EditableToolbarAssertion assertHideButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getHideButton(), shouldBeVisible, String.format(message, "hide"));
    }

    public EditableToolbarAssertion assertUnhideButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getUnhideButton(), shouldBeVisible, String.format(message, "unhide"));
    }

    public EditableToolbarAssertion assertRestoreAllButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getRestoreAllButton(), shouldBeVisible, String.format(message, "restore all"));
    }

    public EditableToolbarAssertion assertAmountButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getAmountButton(), shouldBeVisible, String.format(message, "amount"));
    }

    public EditableToolbarAssertion assertResetButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getResetButton(), shouldBeVisible, String.format(message, "reset"));
    }

    public EditableToolbarAssertion assertCloseButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getCloseButton(), shouldBeVisible, String.format(message, "close"));
    }

    public EditableToolbarAssertion assertEditButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getEditButton(), shouldBeVisible, String.format(message, "edit"));
    }

    public EditableToolbarAssertion assertParentButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getParentButton(), shouldBeVisible, String.format(message, "parent"));
    }

    public EditableToolbarAssertion assertPanelSelectButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getPanelSelectButton(), shouldBeVisible, String.format(message, "panel_select"));
    }

    public EditableToolbarAssertion assertConfigureButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getConfigureButton(), shouldBeVisible, String.format(message, "configure"));
    }

    public EditableToolbarAssertion assertStylesButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getStylesButton(), shouldBeVisible, String.format(message, "styles"));
    }

    public EditableToolbarAssertion assertCopyButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getCopyButton(), shouldBeVisible, String.format(message, "copy"));
    }

    public EditableToolbarAssertion assertCutButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getCutButton(), shouldBeVisible, String.format(message, "cut"));
    }

    public EditableToolbarAssertion assertDeleteButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getDeleteButton(), shouldBeVisible, String.format(message, "delete"));
    }

    public EditableToolbarAssertion assertInsertButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getInsertButton(), shouldBeVisible, String.format(message, "insert"));
    }

    public EditableToolbarAssertion assertGroupButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getGroupButton(), shouldBeVisible, String.format(message, "group"));
    }

    public EditableToolbarAssertion assertLayoutButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getLayoutButton(), shouldBeVisible, String.format(message, "layout"));
    }

    public EditableToolbarAssertion assertPolicyButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getPolicyButton(), shouldBeVisible, String.format(message, "policy"));
    }

    public EditableToolbarAssertion assertLockStructureButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getLockStructureButton(), shouldBeVisible, String.format(message, "lockstructure"));
    }

    public EditableToolbarAssertion assertUnlockStructureButton(boolean shouldBeVisible) {
        return assertButtonVisibility(editableToolbar.getUnlockStructureButton(), shouldBeVisible, String.format(message, "unlockstructure"));
    }

    private EditableToolbarAssertion assertButtonVisibility(SelenideElement button, boolean shouldBeVisible, String message) {
        if (shouldBeVisible) {
            return assertButtonVisible(button, message);
        } else {
            return assertButtonInvisible(button, message);
        }
    }

    private EditableToolbarAssertion assertButtonVisible(SelenideElement button, String componentName) {
        assertTrue(button.isDisplayed(), String.format(this.message, componentName));
        return this;
    }

    private EditableToolbarAssertion assertButtonInvisible(SelenideElement button, String componentName) {
        assertFalse(button.isDisplayed(), String.format(this.message, componentName));
        return this;
    }

    public enum Button {
        INSERT("insert", EditableToolbar.EditableToolbarAction.INSERT),
        CONFIGURE("configure", EditableToolbar.EditableToolbarAction.CONFIGURE),
        PARENT("parent", EditableToolbar.EditableToolbarAction.PARENT),
        EDIT("edit", EditableToolbar.EditableToolbarAction.EDIT),
        LAYOUT("layout", EditableToolbar.EditableToolbarAction.LAYOUT),
        COPY("copy", EditableToolbar.EditableToolbarAction.COPY),
        CUT("cut", EditableToolbar.EditableToolbarAction.CUT),
        PASTE("paste", EditableToolbar.EditableToolbarAction.PASTE),
        DELETE("delete", EditableToolbar.EditableToolbarAction.DELETE),
        GROUP("group", EditableToolbar.EditableToolbarAction.GROUP),
        STYLE("styles", EditableToolbar.EditableToolbarAction.STYLE),
        POLICY("policy", EditableToolbar.EditableToolbarAction.POLICY),
        UNLOCK_STRUCTURE("unlockstructure", EditableToolbar.EditableToolbarAction.STRUCTURE_OFF),
        LOCK_STRUCTURE("lockstructure", EditableToolbar.EditableToolbarAction.STRUCTURE_ON),
        NEWLINE("newLine", EditableToolbar.EditableToolbarAction.NEWLINE),
        HIDE("hide", EditableToolbar.EditableToolbarAction.HIDE),
        UNHIDE("unhide", EditableToolbar.EditableToolbarAction.UNHIDE),
        RESTORE_ALL("restore all", EditableToolbar.EditableToolbarAction.UNHIDE),
        AMOUNT("amount", EditableToolbar.EditableToolbarAction.AMOUNT),
        RESET("reset", EditableToolbar.EditableToolbarAction.RESET),
        CLOSE("close", EditableToolbar.EditableToolbarAction.CLOSE);

        private String title;
        private EditableToolbar.EditableToolbarAction editableToolbarAction;

        Button (final String title, EditableToolbar.EditableToolbarAction editableToolbarAction) {
            this.title = title;
            this.editableToolbarAction = editableToolbarAction;
        }

        public EditableToolbar.EditableToolbarAction getEditableToolbarAction() {
            return editableToolbarAction;
        }

        public String getTitle() {
            return title;
        }

    }

}
