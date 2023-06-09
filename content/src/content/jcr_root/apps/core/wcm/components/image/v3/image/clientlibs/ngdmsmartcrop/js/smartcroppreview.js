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

    channel.on("change", ".image-v3-dialog-smartcrop-select", function(event, from) {
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

        if (!(from && from === "from-input-fields")) {
            const newLeft = value !== "" ? value.split(":")[0]  : "";
            const newRight = value !== "" ? value.split(":")[1] : "";

            const leftValue = $(".smartcrop-ratio-left").val();
            const rightValue = $(".smartcrop-ratio-right").val();
            if (newLeft !== leftValue || newRight !== rightValue) {
                $(".smartcrop-ratio-left").val(newLeft);
                $(".smartcrop-ratio-right").val(newRight);
            }
        }
    });

    channel.on("click", "button.smartcrop-ratio-swap", function() {
        const leftValue = $(".smartcrop-ratio-left").val();
        const rightValue = $(".smartcrop-ratio-right").val();
        $(".smartcrop-ratio-left").val(rightValue);
        $(".smartcrop-ratio-right").val(leftValue);
        Coral.commons.nextFrame(function() {
            $(".smartcrop-ratio-right").trigger("change");
        });

    });

    function setValueToSelect(value) {
        $(".image-v3-dialog-smartcrop-select").val(value);
        // set custom value
        if (value !== "" && $(".image-v3-dialog-smartcrop-select").val() === "") {
            $(".image-v3-dialog-smartcrop-select")[0].items.getAll()[0].value = value;
            $(".image-v3-dialog-smartcrop-select").val(value);
        }
    }

    channel.on("change", ".smartcrop-ratio-left, .smartcrop-ratio-right", function() {
        const leftValue = $(".smartcrop-ratio-left").val();
        const rightValue = $(".smartcrop-ratio-right").val();
        var value = "";
        if (leftValue !== "" && rightValue !== "" && leftValue > 0 && rightValue > 0) {
            value = leftValue + ":" + rightValue;
            setValueToSelect(value);
        }
        Coral.commons.nextFrame(function() {
            $(".image-v3-dialog-smartcrop-select").trigger("change", ["from-input-fields"]);
        });
    });

    channel.on("foundation-contentloaded", function() {
        Coral.commons.ready($(".smartcrop-ratio-field"), function() {
            let value = $(".smartcrop-ratio-value").val();
            if (value && value !== "") {
                $(".smartcrop-ratio-left").attr("value", value.split(":")[0]);
                $(".smartcrop-ratio-right").attr("value", value.split(":")[1]);
                setValueToSelect(value);
            }
        });
    });

})(jQuery, jQuery(document));
