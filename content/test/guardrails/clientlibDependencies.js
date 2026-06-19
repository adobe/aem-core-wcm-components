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
 * Guardrail: clientlibs that can load in Sites Admin (page properties, MSM) must not
 * transitively pull cq.authoring.editor.core — that initializes the Page Editor in the
 * wrong context and breaks nested dialogs / coral-radio state.
 *
 * Run with: node test/guardrails/clientlibDependencies.js
 */
const assert = require("assert");
const fs = require("fs");
const path = require("path");

const COMPONENTS_ROOT = path.join(
    __dirname,
    "..",
    "..",
    "src/content/jcr_root/apps/core/wcm/components"
);

/**
 * @param {string} relativePath path under components/
 * @returns {string}
 */
function readClientlibXml(relativePath) {
    const absolutePath = path.join(COMPONENTS_ROOT, relativePath);
    assert.ok(fs.existsSync(absolutePath), "missing clientlib descriptor: " + relativePath);
    return fs.readFileSync(absolutePath, "utf8");
}

/**
 * @param {string} xml
 * @returns {string[]}
 */
function parseDependencies(xml) {
    const match = xml.match(/dependencies="\[([^\]]*)\]"/);
    if (!match) {
        return [];
    }
    return match[1]
        .split(",")
        .map(function(dep) {
            return dep.trim();
        })
        .filter(Boolean);
}

/**
 * @param {string} xml
 * @returns {string[]}
 */
function parseCategories(xml) {
    const match = xml.match(/categories="\[([^\]]*)\]"/);
    if (!match) {
        return [];
    }
    return match[1]
        .split(",")
        .map(function(category) {
            return category.trim();
        })
        .filter(Boolean);
}

const EDITOR_CORE = "cq.authoring.editor.core";
const AUTHORINGUTILS = "core.wcm.components.commons.editor.authoringutils";

/** Clientlibs that must never declare cq.authoring.editor.core as a dependency. */
const MUST_NOT_DEPEND_ON_EDITOR_CORE = [
    "commons/editor/clientlibs/authoringutils/.content.xml",
    "commons/editor/clientlibs/htmlidvalidator/.content.xml",
    "image/v2/image/clientlibs/editor/.content.xml",
    "image/v3/image/clientlibs/editor/.content.xml",
    "contentfragmentlist/v1/contentfragmentlist/clientlibs/editor/.content.xml"
];

let failures = 0;

function fail(message) {
    console.error("FAIL: " + message);
    failures++;
}

MUST_NOT_DEPEND_ON_EDITOR_CORE.forEach(function(relativePath) {
    const xml = readClientlibXml(relativePath);
    const dependencies = parseDependencies(xml);

    if (dependencies.includes(EDITOR_CORE)) {
        fail(relativePath + " must not depend on " + EDITOR_CORE);
    }
});

(function assertHtmlIdValidatorWiring() {
    const xml = readClientlibXml("commons/editor/clientlibs/htmlidvalidator/.content.xml");
    const dependencies = parseDependencies(xml);
    const categories = parseCategories(xml);

    if (!dependencies.includes(AUTHORINGUTILS)) {
        fail("htmlidvalidator must depend on " + AUTHORINGUTILS);
    }
    if (dependencies.length !== 1) {
        fail("htmlidvalidator should only depend on authoringutils, got: " + dependencies.join(", "));
    }
    if (!categories.includes("cq.siteadmin.admin.properties")) {
        fail("htmlidvalidator must be registered for cq.siteadmin.admin.properties");
    }
})();

(function assertAuthoringutilsMinimalDeps() {
    const xml = readClientlibXml("commons/editor/clientlibs/authoringutils/.content.xml");
    const dependencies = parseDependencies(xml);

    if (!dependencies.includes("jquery")) {
        fail("authoringutils must depend on jquery");
    }
    if (dependencies.length !== 1) {
        fail("authoringutils should only depend on jquery, got: " + dependencies.join(", "));
    }
})();

if (failures > 0) {
    console.error("\n" + failures + " clientlib guardrail(s) failed.");
    process.exit(1);
}

console.log(
    "OK: " + MUST_NOT_DEPEND_ON_EDITOR_CORE.length + " clientlib guardrails passed."
);
