/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents.TableOfContentsEditDialog;

import static com.codeborne.selenide.Selenide.$;

public class TableOfContents extends BaseComponent {

    public static final String COMPONENT_NAME = "Table of Contents (v1)";

    private static String tableOfContentsPlaceholder = ".cmp-toc__placeholder";
    private static String tableOfContents = ".cmp-toc__content";

    public TableOfContents() {
        super("");
    }

    public TableOfContentsEditDialog getEditDialog() {
        return new TableOfContentsEditDialog();
    }

    public String getListType() {
        return $(tableOfContentsPlaceholder).attr("data-cmp-toc-list-type");
    }

    public int getStartLevel() {
        return Integer.parseInt(
            $(tableOfContentsPlaceholder).attr("data-cmp-toc-start-level")
        );
    }

    public int getStopLevel() {
        return Integer.parseInt(
            $(tableOfContentsPlaceholder).attr("data-cmp-toc-stop-level")
        );
    }

    public String[] getIncludeClasses() {
        return $(tableOfContentsPlaceholder).attr("data-cmp-toc-include-classes").split(",");
    }

    public String[] getIgnoreClasses() {
        return $(tableOfContentsPlaceholder).attr("data-cmp-toc-ignore-classes").split(",");
    }

    public String getId() {
        return $(tableOfContentsPlaceholder).attr("id");
    }

    public boolean isTocPlaceholderExists() {
        return $(tableOfContentsPlaceholder).exists();
    }

    public boolean isTocPlaceholderVisible() {
        return $(tableOfContentsPlaceholder).isDisplayed();
    }

    public boolean isActualTocContentExists() {
        return $(tableOfContents).exists();
    }

    public boolean isTextPresentInActualTocContent(String text) {
        return $(tableOfContents).getText().contains(text);
    }
}
