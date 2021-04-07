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

package com.adobe.cq.wcm.core.components.it.seljup.components.Accordion.v1;


import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.CQOverlay;
import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.PanelSelector;
import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;
import com.adobe.qe.selenium.pagewidgets.coral.CoralPopOver;
import com.adobe.qe.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog;
import com.adobe.qe.selenium.pagewidgets.coral.CoralCheckbox;
import com.codeborne.selenide.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static com.adobe.qe.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Accordion Component
 */
public class Accordion extends BaseComponent {

    private static String accordionItem = "[data-cmp-hook-accordion='item']";
    private static String itemExpanded = "[data-cmp-hook-accordion='item'][data-cmp-expanded]";
    private static String itemButton = "[data-cmp-hook-accordion='button']";

    public Accordion() {
        super(".cmp-accordion");
    }

    /**
     * Returns the accordion Items
     *
     * @returns the accordion Items
     */
    public ElementsCollection getAccordionItem() {
        return $$(accordionItem);
    }

    /**
     * Returns accordion item button at position idx
     * @param idx the position of the accordion item to return
     * @return accordion item at idx
     */
    public SelenideElement getAccordionItemButton(int idx) {
        return $$(accordionItem).get(idx).find(itemButton);
    }

    /**
     * Returns expanded items collection
     *
     * @return expanded items collection
     */
    public static ElementsCollection getItemExpanded() {
        return $$(itemExpanded);
    }

    /**
     * Returns accordion edit dialog object
     * @return Accordion EditDialog
     */
    public EditDialog getEditDialog() {
        return new EditDialog();
    }

    /**
     * Returns cq-overlay
     * @returncq cq-overlay
     */
    public CQOverlay getCQOverlay() {
        return new CQOverlay();
    }

    /**
     * Class for Accordion Edit Dialog
     */
    public static final class EditDialog {

        private EditDialog() {

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

        /**
         * Returns panel selector object
         * @return PanelSelector
         */
        public PanelSelector getPanelSelector() {
            return new PanelSelector();
        }
        //private EditableToolbar editableToolbar = new EditableToolbar(this); //will get from EditorPage

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

}
