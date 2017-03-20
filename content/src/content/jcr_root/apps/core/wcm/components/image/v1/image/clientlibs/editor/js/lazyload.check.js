/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
(function ($) {
    'use strict';

    function toggleLazyLoad(widths) {
        if (widths.className.indexOf("core-image-widths") >= 0) {
            var disableLazy = $(".core-image-disable-lazy");
            if (widths.items && widths.items.length > 0) {
                disableLazy.show();
            } else {
                disableLazy.hide();
            }
        }
    }

    $(document).on("coral-component:attached", ".core-image-widths", function(e) {
        toggleLazyLoad(e.target);
    });

    $(document).on("change", ".core-image-widths", function(e) {
        toggleLazyLoad(e.target);
    });

})(jQuery);