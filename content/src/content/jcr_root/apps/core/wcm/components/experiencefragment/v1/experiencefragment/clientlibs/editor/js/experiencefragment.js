/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
(function($, ns) {
    "use strict";

    var MSG_NO_FRAGMENT_PATH = Granite.I18n.get("This experience fragment component doesn't have an associated variation");

    ns.experiencefragment.v1.actions.edit = function() {
        var ui = $(window).adaptTo("foundation-ui");
        $.get(this.path + ".model.json")
            .then(function(response) {
                var path = response["localizedFragmentVariationPath"];
                if (typeof path === "string" && path.length > 0) {
                    window.open(Granite.HTTP.externalize("/editor.html" +
                        path.substring(0, path.lastIndexOf("/")) + ".html"));
                } else {
                    ui.notify("", MSG_NO_FRAGMENT_PATH, "notice");
                }
            });
    };
})(jQuery, CQ.CoreComponents);
