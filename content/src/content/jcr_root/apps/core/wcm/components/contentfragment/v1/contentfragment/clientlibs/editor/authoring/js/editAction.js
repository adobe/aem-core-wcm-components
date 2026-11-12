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
    // unified shell endpoints for internal stage and for prod
    var UNIFIED_SHELL_STAGE_ENDPOINT = "https://experience-stage.adobe.com/";
    var UNIFIED_SHELL_PROD_ENDPOINT = "https://experience.adobe.com/";
    // identifier of internal stage env
    var AEM_STAGE_ENV = "cmstg";
    // base URL of the old cf editor
    var EDITOR_URL = "/editor.html";
    // feature toggle enabling opening the cf in the new editor
    var FT_USE_NEW_EDITOR = "FT_SITES-19326";

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
                var editorUrl = "";
                // check if the url for the new editor should be used
                if (Granite.Toggles && Granite.Toggles.isEnabled(FT_USE_NEW_EDITOR)) {
                    editorUrl = this.getNewEditorUrl(fragmentPath);
                } else {
                    editorUrl = EDITOR_URL + fragmentPath;
                    var fragment = ns.CFM.Fragments.adaptToFragment(editable.dom);
                    if (fragment && typeof fragment.variation !== "undefined" && fragment.variation !== "master") {
                        editorUrl = editorUrl + "?variation=" + fragment.variation;
                    }
                    editorUrl = Granite.HTTP.externalize(editorUrl);
                }
                // open the editor in a new window
                window.open(editorUrl);
            }
        },

        getNewEditorUrl: function(fragmentPath) {
            var newEditorUrl = "";
            var hostNameAEM = window.location.hostname;
            if (hostNameAEM.indexOf(AEM_STAGE_ENV) !== -1) {
                newEditorUrl = UNIFIED_SHELL_STAGE_ENDPOINT;
            } else {
                newEditorUrl = UNIFIED_SHELL_PROD_ENDPOINT;
            }
            newEditorUrl += "?repo=" + hostNameAEM + "#/aem/cf/editor" + fragmentPath;
            return newEditorUrl;
        }

    });

    ns.editor.register("contentfragment", new ContentFragmentEditor());

})(jQuery, Granite.author);
