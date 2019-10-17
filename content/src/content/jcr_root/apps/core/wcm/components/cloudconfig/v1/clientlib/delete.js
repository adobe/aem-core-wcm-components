/*
 *  Copyright 2019 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
(function(window, document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");
    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "core.wcm.components.clientlib.delete",
        handler: function(name, el, config, collection, selections) {
            var message = $("<div/>");
            var intro = $("<p/>").appendTo(message);
            intro.text(Granite.I18n.get("You are going to delete the selected item"));

            ui.prompt(Granite.I18n.get("Delete"), message.html(), "notice", [{
                text: Granite.I18n.get("Cancel")
            }, {
                text: Granite.I18n.get("Delete"),
                warning: true,
                handler: function() {
                    doDelete(config.data.path);
                }
            }]);
        }
    });

    function doDelete(path) {
        ui.wait();

        $.ajax({
            url: path,
            type: "POST",
            data: {
                _charset_: "UTF-8",
                ":operation": "delete"
            }
        }).done(function(data, textStatus, jqXHR) {
            window.location.reload();
        }).fail(function(jqXHR, textStatus, errorThrown) {
            var message = Granite.I18n.getVar($(jqXHR.responseText).find(".foundation-form-response-description").next().html());
            ui.alert(Granite.I18n.get("Error"), message, "error");
        }).always(function() {
            ui.clearWait();
        });
    }
})(window, document, Granite.$, Granite);
