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

    const selectorLeftInput = ".smartcrop-ratio-left";
    const selectorRightInput = ".smartcrop-ratio-right";
    const selectorSmartCropSelect = ".image-v3-dialog-smartcrop-select";

    channel.on("change", ".image-v3-dialog-smartcrop-select", function(event, from) {
        let newUrl;
        const value = this.value;
        const $dialog = $(this).closest("coral-dialog.cq-Dialog");
        const $ele = $dialog.find(".ngdm-smartcrop-thumbnail");
        const src = $ele.attr("src");
        if (src) {
            const cropStr = value === "" ? "" : `&crop=${value},smart`;
            if (src.search(/crop=.+,smart/g) > -1) {
                newUrl = src.replaceAll(/&crop=.+,smart/g, cropStr);
            } else if (value !== "") {
                newUrl = src + "&" + cropStr;
            }
            $("coral-dialog.cq-Dialog .ngdm-smartcrop-thumbnail").attr("src", newUrl);
        }

        if (!(from && from === "from-input-fields")) {
            const newLeft = value !== "" ? value.split(":")[0]  : "";
            const newRight = value !== "" ? value.split(":")[1] : "";

            const $left = $(selectorLeftInput);
            const $right = $(selectorRightInput);
            const leftValue = $left.val();
            const rightValue = $right.val();
            if (newLeft !== leftValue || newRight !== rightValue) {
                $left.val(newLeft);
                $right.val(newRight);
            }
        }
    });

    channel.on("click", "button.smartcrop-ratio-swap", function() {
        const $left = $(selectorLeftInput);
        const $right = $(selectorRightInput);
        const leftValue = $left.val();
        const rightValue = $right.val();
        $left.val(rightValue);
        $right.val(leftValue);
        Coral.commons.nextFrame(function() {
            $(selectorRightInput).trigger("change");
        });

    });

    function setValueToSelect(value) {
        const $smartCropSelect = $(selectorSmartCropSelect);
        $smartCropSelect.val(value);
        // set custom value
        if ($smartCropSelect.val() === "") {
            $smartCropSelect[0].items.getAll()[0].value = value;
            $smartCropSelect.val(value);
        }
    }

    channel.on("change", `${selectorLeftInput}, ${selectorRightInput}`, function() {
        const leftValue = $(selectorLeftInput).val();
        const rightValue = $(selectorRightInput).val();
        let value = "";
        if (leftValue !== "" && rightValue !== "" && leftValue > 0 && rightValue > 0) {
            value = leftValue + ":" + rightValue;
            setValueToSelect(value);
        } else if (leftValue === "" && rightValue === "") {
            // clear crop and show original
            setValueToSelect("");
        }
        Coral.commons.nextFrame(function() {
            $(selectorSmartCropSelect).trigger("change", ["from-input-fields"]);
        });
    });

    channel.on("foundation-contentloaded", function() {
        Coral.commons.ready($(".smartcrop-ratio-field"), function() {
            const $smartCropVal = $(".smartcrop-ratio-value");
            let value = $smartCropVal.val();
            $smartCropVal.removeAttr("name");
            if (value && value !== "") {
                $(selectorLeftInput).attr("value", value.split(":")[0]);
                $(selectorRightInput).attr("value", value.split(":")[1]);
            }
            setValueToSelect(value);
        });
    });

})(jQuery, jQuery(document));
