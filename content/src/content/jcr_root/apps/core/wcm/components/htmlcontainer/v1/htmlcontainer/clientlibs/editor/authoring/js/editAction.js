/*******************************************************************************
 * Copyright 2019 Adobe
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
(function($, ns) {
    "use strict";

    // class of the content fragment
    var CLASS_CONTENTFRAGMENT = "cmp-contentfragment";
    // name of the attribute on the content fragment storing its path
    var ATTRIBUTE_PATH = "data-cmp-contentfragment-path";
    // base URL of the editor
    var EDITOR_URL = "/editor.html";

    var ContentFragmentEditor = ns.util.createClass({

        constructor: function() {
        },

        canEdit: function(editable) {
            // return true if the editable contains a content fragment having a path attribute
            return $(editable.dom).find("." + CLASS_CONTENTFRAGMENT + "[" + ATTRIBUTE_PATH + "]").length > 0;
        },

        setUp: function(editable) {
            // get the path of the content fragment
            var fragmentPath = $(editable.dom).find("." + CLASS_CONTENTFRAGMENT).attr(ATTRIBUTE_PATH);
            if (fragmentPath) {
                var fragmentEditUrl = EDITOR_URL + fragmentPath;
                var fragment = ns.CFM.Fragments.adaptToFragment(editable.dom);
                if (fragment && typeof fragment.variation !== "undefined" && fragment.variation !== "master") {
                    fragmentEditUrl = fragmentEditUrl + "?variation=" + fragment.variation;
                }
                // open the editor in a new window
                window.open(Granite.HTTP.externalize(fragmentEditUrl));
            }
        }

    });

    ns.editor.register("contentfragment", new ContentFragmentEditor());

})(jQuery, Granite.author);
