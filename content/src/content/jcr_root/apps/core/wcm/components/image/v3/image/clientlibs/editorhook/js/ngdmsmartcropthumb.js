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
(function($, channel) {
    "use strict";

    channel.on("change", ".image-v3-dialog-smartcrop-field", function() {
        var value = this.value;
        var $dialog = $(this).closest("coral-dialog.cq-Dialog");
        var $ele = $dialog.find(".ngdm-smartcrop-thumbnail");
        var src = $ele.attr("src");
        if (src) {
            var cropStr = value === "" ? "" : `&crop=${value},smart`;
            if (src.search(/crop=.+,smart/g) > -1) {
                var newUrl = src.replaceAll(/&crop=.+,smart/g, cropStr);
            } else if (value !== "") {
                newUrl = src + "&" + cropStr;
            }
            $("coral-dialog.cq-Dialog .ngdm-smartcrop-thumbnail").attr("src", newUrl);
        }
    });

})(jQuery, jQuery(document));
