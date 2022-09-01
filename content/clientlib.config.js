/*******************************************************************************
 * Copyright 2020 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/* global
    module, __dirname
 */
module.exports = {
    context: __dirname,
    clientLibRoot: "src/content/jcr_root/apps/core/wcm/components/commons/datalayer/v1/clientlibs",
    libs: [
        {
            name: "core.wcm.components.commons.datalayer.v1",
            serializationFormat: "xml",
            allowProxy: true,
            jsProcessor: ["default:none", "min:gcc;compilationLevel=whitespace"],
            assets: {
                js: [
                    "src/scripts/datalayer/v1/polyfill.js",
                    "node_modules/@adobe/adobe-client-data-layer/dist/adobe-client-data-layer.min.js",
                    "src/scripts/datalayer/v1/datalayer.js"
                ]
            }
        }
    ]
};

