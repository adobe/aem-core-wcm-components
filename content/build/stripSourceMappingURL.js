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
/* eslint-env node */
"use strict";

/*
 * The Adobe Client Data Layer is served as distributed (no minification by AEM), but the source map it references is
 * not part of the clientlib. Removes the sourceMappingURL comment so that browsers don't request the missing file.
 */
const fs = require("fs");
const path = require("path");

const FILE = path.join(__dirname, "..", "src/content/jcr_root/apps/core/wcm/components/commons/datalayer/acdl3",
    "core.wcm.components.commons.datalayer.acdl3/js/adobe-client-data-layer.min.js");

const content = fs.readFileSync(FILE, "utf8");
const stripped = content.replace(/\n?\/\/# sourceMappingURL=\S+\s*$/, "\n");
if (stripped === content) {
    console.log("No sourceMappingURL comment found in " + FILE);
} else {
    fs.writeFileSync(FILE, stripped);
    console.log("Removed sourceMappingURL comment from " + FILE);
}
