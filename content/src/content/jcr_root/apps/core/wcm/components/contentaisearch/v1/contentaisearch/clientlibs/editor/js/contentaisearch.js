/*******************************************************************************
 * Copyright 2026 Adobe
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
(function(window, $, channel, Granite, Coral) {
    "use strict";

    var CLASS_EDIT_DIALOG = "cmp-contentaisearch__editor";
    var SELECTOR_CONTENT_SOURCE_TYPE = "coral-select[name='./contentSourceType']";
    var SELECTOR_CONTENT_SOURCES = "coral-select[name='./contentSources']";

    /**
     * Refreshes the content sources dropdown when the content source type changes.
     *
     * @param {HTMLElement} dialog - the edit dialog root element
     */
    function initialize(dialog) {
        var contentSourceTypeField = dialog.querySelector(SELECTOR_CONTENT_SOURCE_TYPE);
        var contentSourcesField = dialog.querySelector(SELECTOR_CONTENT_SOURCES);

        if (!contentSourceTypeField || !contentSourcesField || !contentSourcesField.dataset.cmpFieldPath) {
            return;
        }

        $(contentSourceTypeField).on("foundation-field-change", function() {
            refreshContentSources(contentSourcesField, contentSourceTypeField.value);
        });
    }

    /**
     * Loads datasource options for the selected content source type.
     *
     * @param {Coral.Select} contentSourcesField - the multi-select field to update
     * @param {String} contentSourceType - selected content source type
     */
    function refreshContentSources(contentSourcesField, contentSourceType) {
        var url = Granite.HTTP.externalize(contentSourcesField.dataset.cmpFieldPath) + ".html";

        $.get({
            url: url,
            data: {
                contentSourceType: contentSourceType
            }
        }).done(function(html) {
            var doc = new DOMParser().parseFromString(html, "text/html");
            var items = doc.querySelectorAll("coral-select-item");
            var selectedValues = contentSourcesField.values || [];

            contentSourcesField.items.clear();
            items.forEach(function(item) {
                contentSourcesField.items.add({
                    value: item.value,
                    content: {
                        textContent: item.textContent
                    },
                    selected: selectedValues.indexOf(item.value) !== -1
                });
            });
        });
    }

    channel.on("foundation-contentloaded", function(e) {
        if (e.target.getElementsByClassName(CLASS_EDIT_DIALOG).length > 0) {
            Coral.commons.ready(e.target, function(dialog) {
                initialize(dialog);
            });
        }
    });

})(window, jQuery, jQuery(document), Granite, Coral);
