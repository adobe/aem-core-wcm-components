/*******************************************************************************
 * Copyright 2020 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function($, $document) {
    "use strict";

    var selectors;
    var type;
    var tabs;
    var controls;

    $document.on("dialog-loaded", function(event) {
        type = $(".pdfviewer-type-selector");
        tabs = $(".pdfviewer-tabs");
        controls = $(".pdfviewer-showPageControls");

        type.on("change", function() {
            onTypeChange();
        });

        tabs.on("click", function() {
            onTypeChange();
        });

        controls.on("change", function() {
            onPageControlChange();
        });

        selectors = {
            defaultViewMode: $(".pdfviewer-defaultViewMode").parent(),
            borderless: $(".pdfviewer-borderless").parent(),
            annotationTools: $(".pdfviewer-showAnnotationTools").parent(),
            showFullScreen: $(".pdfviewer-showFullScreen").parent(),
            leftHandPanel: $(".pdfviewer-showLeftHandPanel").parent(),
            downloadPdf: $(".pdfviewer-showDownloadPdf").parent(),
            printPdf: $(".pdfviewer-showPrintPdf").parent(),
            pageControls: $(".pdfviewer-showPageControls").parent(),
            dockPageControls: $(".pdfviewer-dockPageControls")
        };
    });

    function onPageControlChange() {
        var checked = controls.attr("checked");
        if (checked) {
            selectors.dockPageControls.find("input").attr("disabled", false);
        } else {
            selectors.dockPageControls.find("input").attr("disabled", true);
        }
    }

    function onTypeChange() {
        var value = type.find("coral-select-item:selected")[0].value;
        controls.attr("disabled", false);
        switch (value) {
            case "SIZED_CONTAINER":
                showControls(["showFullScreen", "downloadPdf", "printPdf", "pageControls", "dockPageControls"]);
                controls.attr("checked", true);
                controls.attr("disabled", true);
                break;
            case "IN_LINE":
                showControls(["downloadPdf", "printPdf"]);
                break;
            case "FULL_WINDOW":
            default:
                showControls(["defaultViewMode", "borderless", "annotationTools", "leftHandPanel", "downloadPdf", "printPdf", "pageControls", "dockPageControls"]);
        }
        onPageControlChange();
    }

    function showControls(arr) {
        Object.keys(selectors).forEach(function(key) {
            arr.indexOf(key) > -1 ? selectors[key].show() : selectors[key].hide();
        });
    }

})($, $(document));
