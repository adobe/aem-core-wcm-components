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

/**
 * Local-development helper for Content Fragment Visualization.
 *
 * Intercepts AJAX requests to the Content Fragment Visualization API
 * ({@code /adobe/experimental/.../sites/cf/}) and redirects them to the local
 * mock Sling servlet at {@code /bin/mock/cfvisualization}.
 *
 * This clientlib is part of the {@code testing/it/it.ui.apps} package, which
 * is deployed only to local AEM SDK instances — never to AEM Cloud Service.
 *
 * Rewrites:
 *   .../sites/cf/models/{modelId}/templates?limit=50
 *     → /bin/mock/cfvisualization.templates.json?modelId={modelId}&limit=50
 *
 *   .../sites/cf/fragments/{fragmentId}/preview?templateId=...&variation=...
 *     → /bin/mock/cfvisualization.vcf.html?fragmentId={fragmentId}&templateId=...&variation=...
 */
(function($) {
    "use strict";

    var VCF_API_PREFIX = "/adobe/experimental/";
    var MOCK_BASE = "/bin/mock/cfvisualization";

    var TEMPLATES_RE = /\/sites\/cf\/models\/([^?]+)\/templates/;
    var FRAGMENT_RE = /\/sites\/cf\/fragments\/([^?]+)\/preview/;

    $.ajaxPrefilter(function(options) {
        if (!options.url || options.url.indexOf(VCF_API_PREFIX) !== 0) {
            return;
        }

        var fullUrl = options.url;
        var queryIdx = fullUrl.indexOf("?");
        var path = queryIdx >= 0 ? fullUrl.substring(0, queryIdx) : fullUrl;
        var queryStr = queryIdx >= 0 ? fullUrl.substring(queryIdx + 1) : "";

        var m;
        var newUrl;

        m = TEMPLATES_RE.exec(path);
        if (m) {
            newUrl = MOCK_BASE + ".templates.json?modelId=" + m[1];
            if (queryStr) {
                newUrl += "&" + queryStr;
            }
            options.url = newUrl;
            return;
        }

        m = FRAGMENT_RE.exec(path);
        if (m) {
            newUrl = MOCK_BASE + ".vcf.html?fragmentId=" + m[1];
            if (queryStr) {
                newUrl += "&" + queryStr;
            }
            options.url = newUrl;
            return;
        }
    });

})(jQuery);
