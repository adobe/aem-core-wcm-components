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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.tabs.v1;

import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tabs.TabsEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Tabs extends BaseComponent {
    private static String tabs = ".cmp-tabs";
    private static String tab = ".cmp-tabs__tab";
    private static String activeTab = ".cmp-tabs__tab--active";
    private static String tabPanel = ".cmp-tabs__tabpanel";
    private static String activeTabPanel = ".cmp-tabs__tabpanel--active";

    public Tabs() {
        super(tabs);
    }

    /**
     * Open tabs edit dialog
     *
     * @param dataPath datapath of the component to open the configuration dialog
     *
     * @return Tabs EditDialog
     */
    public TabsEditDialog openEditDialog(String dataPath) {
        Commons.openEditableToolbar(dataPath);
        $(Selectors.SELECTOR_CONFIG_BUTTON).click();
        Helpers.waitForElementAnimationFinished($(Selectors.SELECTOR_CONFIG_DIALOG));
        return new TabsEditDialog();
    }

    public boolean isTabActive(String tabName) {
        return $$(activeTab).size() == 1 &&  $$(activeTab).get(0).getText().trim().contains(tabName);
    }

    public boolean isTabPanelActive(int idx) {
        return $$(activeTabPanel).size() == 1 &&  $$(tabPanel).get(idx).getAttribute("class").contains("cmp-tabs__tabpanel--active");
    }

    public ElementsCollection getTabItems() {
        return $$(tab);
    }

    public void clickTab(int idx) {
        getTabItems().get(idx).click();
    }

}
