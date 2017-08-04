/*
 *  Copyright 2016 Adobe Systems Incorporated
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

(function(window, document, Granite, $) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");

    var contextPath = Granite.HTTP.getContextPath();

    $(document).on("reset", "form", function() {
        var component = $(this).find(".cq-wcm-pagethumbnail");

        restoreImage(component);
        clearValue(component);
        toggleUploadButton(component, true);
    });

    $(document).on("coral-fileupload:fileadded", ".cq-wcm-pagethumbnail .cq-wcm-fileupload", function(e) {
        var fileUploadEl = $(this);
        var fileUpload = e.originalEvent.target;
        var file = e.originalEvent.detail.item.file;
        var component = fileUploadEl.closest(".cq-wcm-pagethumbnail");

        wait(component);

        // use attr() instead of prop() for action, so that no domain is appended
        var base = component.closest("form").attr("action");

        fileUploadEl
            .one("fileuploadload", function(e) {
                var status = $(e.content).find("#Status").text();
                e.fileUpload._internalOnUploadLoad(e, e.item, status, e.content);
            })
            .one("coral-fileupload:load", function(e) {
                var url = base + (function() {
                        var name = fileUpload.name;
                        if (name.indexOf(".") === 0) {
                            name = name.substring(1);
                        }
                        return name;
                    })();

                clearWait(component, url + "?ck=" + new Date().getTime());
                fileUploadEl.removeClass("disabled");
                setValue(component, fileUpload.name.replace(".sftmp", "@MoveFrom"), url);
            });


        fileUpload.action = base;
        fileUpload.upload(file.name);

        return true;
    });


    function generate(component) {
        var activator = component.find(".cq-wcm-pagethumbnail-activator").prop("disabled", true);
        wait(component);

        var path = component.data("cqWcmPagethumbnailPath");
        var isTemplate = component.data("isTemplate") || false;
        var dest = isTemplate ? "thumbnail.png.sftmp" : "file.sftmp";

        var pgen = new CQ.Siteadmin.PagePreview();
        pgen.generatePreview(path, dest, isTemplate, function(data) {
            // use attr() instead of prop() for action, so that no domain is appended
            if(isTemplate) {
                setValue(component, "./thumbnail.png@MoveFrom", component.closest("form").attr("action") + "/" + dest);
            } else {
                setValue(component, "./image/file@MoveFrom", component.closest("form").attr("action") + "/image/" + dest);
            }
            clearWait(component, data);
            activator.prop("disabled", false);
        });
    }

    function wait(component) {
        ui.wait(component.find(".cq-wcm-pagethumbnail-image").parent()[0]);
    }

    function clearWait(component, src) {
        var original = component.find(".cq-wcm-pagethumbnail-image");

        if (original.length) {
            component.data("cq-wcm-pagethumbnail.internal.original", original);
        }

        original.replaceWith($('<img class="cq-wcm-pagethumbnail-preview">').prop("src", src));

        toggleUploadButton(component, false);

        ui.clearWait();
    }

    function restoreImage(component) {
        component.find(".cq-wcm-pagethumbnail-preview").replaceWith(component.data("cq-wcm-pagethumbnail.internal.original"));
    }

    function toggleUploadButton(component, show) {
        component
            .find('.cq-wcm-fileupload').toggle(show)
            .end()
            .find('button[type="reset"]').toggle(!show)
    }

    function setValue(component, name, url) {
        var hidden;

        hidden = component.find("input[name=\"" + name + "\"]");

        if (hidden.length == 0) {
            hidden = component.find(".cq-wcm-pagethumbnail-hidden");

            if (!hidden.length) {
                hidden = $('<input class="cq-wcm-pagethumbnail-hidden" type="hidden">').appendTo(component);
            }
        }

        hidden.prop("name", name).val(url.replace(new RegExp("^" + contextPath), "").replace("_jcr_content", "jcr:content")).prop("disabled", false);
    }

    function clearValue(component) {
        component.find(".cq-wcm-pagethumbnail-hidden").remove();
    }

    $(document).on("click", ".cq-wcm-pagethumbnail-activator", function(e) {
        generate($(this).closest(".cq-wcm-pagethumbnail"));
    });
})(window, document, Granite, Granite.$);