/*******************************************************************************
 * Copyright 2016 Adobe
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
(function($) {
    "use strict";

    function toggleInputs(isDecorative) {
        var alt = $(".cmp-image--editor-alt");
        var link = $(".cmp-image--editor-link");
        if (isDecorative.checked) {
            alt.addClass("hide");
            alt.find("input").attr("aria-required", "false");
            link.addClass("hide");
        } else {
            alt.removeClass("hide");
            alt.find("input").attr("aria-required", "true");
            link.removeClass("hide");
        }
    }

    $(document).on("coral-component:attached", ".cmp-image--editor-decorative", function(e) {
        toggleInputs(e.target);
    });

    $(document).on("change", ".cmp-image--editor-decorative", function(e) {
        toggleInputs(e.target);
    });
})(jQuery);
