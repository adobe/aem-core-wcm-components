/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.components.Accordion;

import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.ChildrenEditor;
import com.adobe.qe.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.qe.selenium.pagewidgets.coral.CoralPopOver;
import com.adobe.qe.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.qe.selenium.pagewidgets.coral.Dialog;
import com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static com.adobe.qe.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.$;

/**
 * Class for Accordion Configuration Dialog
 */
public class AccordionConfigureDialog extends Dialog  {

    public AccordionConfigureDialog() {

    }

    /**
     * Returns the edit dialog properties object
     * @return EditDialogProperties
     */
    public EditDialogProperties getEditDialogProperties() {
        return new EditDialogProperties();
    }

    /**
     * Returns ChildrenEditor object
     * @return ChildrenEditor
     */
    public ChildrenEditor getChildrenEditor() {
        return new ChildrenEditor();
    }

    /**
     * Returns InsertComponent dialog
     * @return InsertComponentDialog
     */
    public InsertComponentDialog getInsertComponentDialog() {
        return new InsertComponentDialog();
    }

    public static final class EditDialogProperties {
        private static SelenideElement properties = $("coral-tab[data-foundation-tracking-event*='properties']");
        private static String expandedSelectSingle ="[data-cmp-accordion-v1-dialog-edit-hook='expandedSelectSingle']";
        private static String expandedSelect = "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect']";
        private static String singleExpansion = "[data-cmp-accordion-v1-dialog-edit-hook='singleExpansion']";

        private EditDialogProperties() {

        }

        /**
         * Opens the properties tab in editor dialog
         */
        public void openProperties() {
            properties.click();
        }

        /**
         * Open the expanded select list
         * @param suffix
         */
        public void openExpandedSelect(String suffix) {
            $(expandedSelect + " " + suffix).click();
        }

        /**
         * Check if expanded select list is visible
         * @return true if expanded select list is visible otherwise false
         */
        public boolean isExpandedSelectVisible() {
            return $(expandedSelect).isDisplayed();
        }

        /**
         * Check if expanded select list is disabled
         * @return true if expanded select list is disabled otherwise false
         */
        public boolean isExpandedSelectDisabled() {
            final WebDriver webDriver = WebDriverRunner.getWebDriver();
            return (Boolean) ((JavascriptExecutor) webDriver).executeScript("return arguments[0].hasAttribute(\"disabled\");", $(expandedSelect));
        }

        /**
         * Check if single expanded select item is visible
         * @return true if single expanded select item is visible otherwise false
         */
        public boolean isExpandedSelectSingleVisible() {
            return $(expandedSelectSingle).isDisplayed();
        }

        /**
         * Check if single expanded select item is disabled
         * @return true if single expanded select item is disabled otherwise false
         */
        public boolean isExpandedSelectSingleDisabled() {
            final WebDriver webDriver = WebDriverRunner.getWebDriver();
            return (Boolean) ((JavascriptExecutor) webDriver).executeScript("return arguments[0].hasAttribute(\"disabled\");", $(expandedSelectSingle));
        }

        /**
         * Returns single expansion checkbox
         * @return single expansion checkbox
         */
        public CoralCheckbox getSingleExpansion() {
            return new CoralCheckbox(singleExpansion);
        }

        /**
         * @return list of the related coral popover that is opened.
         */
        public CoralSelectList selectList() {
            CoralSelectList list = null;
            CoralPopOver popOver = CoralPopOver.firstOpened();
            popOver.waitVisible();
            waitForElementAnimationFinished(popOver.getCssSelector());
            return new CoralSelectList(popOver.element());
        }
    }

}
