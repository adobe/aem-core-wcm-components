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
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents.TableOfContentsEditDialog65;

import static com.codeborne.selenide.Selenide.$;

public class TableOfContents extends BaseComponent {

    public static final String COMPONENT_NAME = "Table of Contents (v1)";

    private static String tocPlaceholder = ".cmp-toc__placeholder";
    private static String tocTemplatePlaceholder = ".cmp-toc__template-placeholder";
    private static String tocContent = ".cmp-toc__content";

    public TableOfContents() {
        super("");
    }

    public TableOfContentsEditDialog getEditDialog() {
        return new TableOfContentsEditDialog();
    }

    public TableOfContentsEditDialog65 getEditDialog65() {
        return new TableOfContentsEditDialog65();
    }

    public String getId() {
        return $(tocContent).attr("id");
    }

    public boolean isTocPlaceholderExists() {
        return $(tocPlaceholder).exists();
    }

    public boolean isTocTemplatePlaceholderVisible() {
        return $(tocTemplatePlaceholder).isDisplayed();
    }

    public boolean isTocContentExists() {
        return $(tocContent).exists();
    }

    public boolean isTextPresentInTocContent(String text) {
        return $(tocContent).getText().contains(text);
    }
}
