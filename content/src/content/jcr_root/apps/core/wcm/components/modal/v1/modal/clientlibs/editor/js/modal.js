/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
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
    var CONTENT_FRAGMENT_PATH = "/content/dam";
    var FRAGMENT_SELECT_NAME = "./fragmentType";
    var EXPERIENCE_FRAGMENT_PATH = "/content/experience-fragments";
    var COMPONENT_PATH = "/apps/core/wcm/components/modal/v1/modal";
    var DIALOG_PATH = COMPONENT_PATH + "/cq:dialog";
    var CONTENT_FRAGMENT_VALUE = "cf";
    var PATHFIELD_NAME = "./fragmentPath";

    $document.on("dialog-ready", function() {
        if (DIALOG_PATH === getDialogPath()) {
            var $pathButton = $(this).find("[name='" + PATHFIELD_NAME + "']");
            $(this).find("coral-select[name='" + FRAGMENT_SELECT_NAME + "']").change(function() {
                var value = $(this).find("input[name='" + FRAGMENT_SELECT_NAME + "']").attr("value");
                var rootURL = $pathButton.attr("pickersrc");
                var $pathValue = $pathButton.find("input");
                $pathValue.val("");
                if (value === CONTENT_FRAGMENT_VALUE) {
                    $pathButton.attr("pickersrc", rootURL.replace(/(root=).*?(&)/, "$1" + encodeURIComponent(CONTENT_FRAGMENT_PATH) + "$2"));
                } else {

                    $pathButton.attr("pickersrc", rootURL.replace(/(root=).*?(&)/, "$1" + encodeURIComponent(EXPERIENCE_FRAGMENT_PATH) + "$2"));

                }

            });
        }
    });


    function getDialogPath() {
        var gAuthor = Granite.author;
        var currentDialog = gAuthor.DialogFrame.currentDialog;
        var dialogPath;

        if (currentDialog instanceof gAuthor.actions.PagePropertiesDialog) {
            var dialogSrc = currentDialog.getConfig().src;
            dialogPath = dialogSrc.substring(0, dialogSrc.indexOf(".html"));
        } else {
            var editable = gAuthor.DialogFrame.currentDialog.editable;

            if (!editable) {
                return;
            }

            dialogPath = editable.config.dialog;
        }

        return dialogPath;
    }

}($, $(document)));
