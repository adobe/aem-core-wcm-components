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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.pdfviewer.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.pdfviewer.PdfViewerEditDialog;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

public class PdfViewer extends BaseComponent {

    private static final String PDF_VIEWER = ".cmp-pdfviewer";
    private static final String PDF_VIEWER_CONTENT = ".cmp-pdfviewer__content";

    public PdfViewer() {
        super(PDF_VIEWER);
    }

    public PdfViewerEditDialog getEditDialog() {
        return new PdfViewerEditDialog();
    }

    public boolean hasContent(String content) {
        SelenideElement viewerContent = currentElement.$(PDF_VIEWER_CONTENT);
        String id = viewerContent.getAttribute("id");

        SelenideElement contentFrame = viewerContent.$("#iframe-" + id);
        Selenide.switchTo().frame(contentFrame);
        boolean found = Selenide.$("body").getText().contains(content);
        Commons.switchToDefaultContext();

        return found;
    }
}
