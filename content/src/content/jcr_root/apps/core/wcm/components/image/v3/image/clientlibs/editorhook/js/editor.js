/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
/* global CQ */
(function($, ns, channel, authorNs) {
    "use strict";
    ns.image.v3.actions.smartCrop = function() {
        const editable = this;
        authorNs.DialogFrame.openDialog(new ns.image.v3.smartCropDialog(editable));
    };

    ns.image.v3.actions.smartCrop.condition = function(editable) {
        var shouldShow = false;
        if (authorNs.pageInfoHelper.canModify()) {
            editable.config.editConfig.actions.forEach(function(action) {
                if (typeof action === "object" && (action.name === "ngdm-smartcrop")) {
                    shouldShow = true;
                }
            });
        }
        return shouldShow;
    };
})(jQuery, CQ.CoreComponents, jQuery(document), Granite.author);
