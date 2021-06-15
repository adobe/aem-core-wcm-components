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
(function(window, document, $, Granite) {
    "use strict";

    /**
     * Handler to show/hide the MSM action buttons according to the selected tab.
     */
    $(document).on("coral-panelstack:change", ".cq-siteadmin-admin-properties-tabs", function(e) {
        var $target = $(e.target.selectedItem);

        var $actionBar = $("coral-actionbar");

        if ($target.find(".cq-siteadmin-admin-properties-blueprint").length > 0) {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-blueprint").removeClass("hide");
        } else {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-blueprint").addClass("hide");
        }

        if ($target.find(".cq-siteadmin-admin-properties-livecopy").length > 0) {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-livecopy").removeClass("hide");
        } else {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-livecopy").addClass("hide");
        }

        if ($target.find(".js-cq-sites-PermissionsProperties").length > 0) {
            $actionBar.find(".js-cq-sites-PermissionsProperties-action").removeClass("hide");
        } else {
            $actionBar.find(".js-cq-sites-PermissionsProperties-action").addClass("hide");
        }
    });

})(window, document, Granite.$, Granite);
