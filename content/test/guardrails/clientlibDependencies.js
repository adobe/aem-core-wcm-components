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
 * Guardrail: Core Components clientlibs that can load in Sites Admin (page properties,
 * MSM) or policy dialogs must not directly or transitively pull cq.authoring.editor.core.
 * Loading the Page Editor clientlib stack in those contexts breaks nested dialogs and
 * coral-radio state on page properties.
 *
 * Builds a category dependency graph from all Core Components clientlib descriptors and
 * checks the transitive closure for each guarded category (e.g. image.v3.editor ->
 * checkboxTextfieldTuple -> cq.authoring.editor.core).
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

const EDITOR_CORE = "cq.authoring.editor.core";
const AUTHORINGUTILS = "core.wcm.components.commons.editor.authoringutils";

/**
 * Clientlibs whose published categories must not reach cq.authoring.editor.core
 * through any chain of Core Components category dependencies.
 */
const GUARDED_CLIENTLIBS = [
    {
        relativePath: "commons/editor/clientlibs/authoringutils/.content.xml",
        label: "authoringutils"
    },
    {
        relativePath: "commons/editor/clientlibs/htmlidvalidator/.content.xml",
        label: "htmlidvalidator"
    },
    {
        relativePath: "commons/v1/clientlibs/editor/checkboxTextfieldTuple/.content.xml",
        label: "checkboxTextfieldTuple"
    },
    {
        relativePath: "image/v2/image/clientlibs/editor/.content.xml",
        label: "image.v2.editor"
    },
    {
        relativePath: "image/v3/image/clientlibs/editor/.content.xml",
        label: "image.v3.editor"
    },
    {
        relativePath: "contentfragmentlist/v1/contentfragmentlist/clientlibs/editor/.content.xml",
        label: "contentfragmentlist.v1.editor"
    }
];

let failures = 0;

function fail(message) {
    console.error("FAIL: " + message);
    failures++;
}

/**
 * @param {string} dir
 * @param {string[]} files
 * @returns {string[]}
 */
function collectClientlibDescriptors(dir, files) {
    if (!fs.existsSync(dir)) {
        return files;
    }
    fs.readdirSync(dir, { withFileTypes: true }).forEach(function(entry) {
        const absolutePath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
            collectClientlibDescriptors(absolutePath, files);
        } else if (entry.name === ".content.xml") {
            files.push(absolutePath);
        }
    });
    return files;
}

/**
 * @param {string} xml
 * @returns {boolean}
 */
function isClientlibFolder(xml) {
    return /jcr:primaryType="cq:ClientLibraryFolder"/.test(xml) && /categories="\[/.test(xml);
}

/**
 * @param {string} xml
 * @returns {string[]}
 */
function parseListAttribute(xml, attributeName) {
    const pattern = new RegExp(attributeName + '="\\[([^\\]]*)\\]"');
    const match = xml.match(pattern);
    if (!match) {
        return [];
    }
    return match[1]
        .split(",")
        .map(function(value) {
            return value.trim();
        })
        .filter(Boolean);
}

/**
 * @param {string} absolutePath
 * @returns {string} path relative to COMPONENTS_ROOT
 */
function toComponentsRelativePath(absolutePath) {
    return path.relative(COMPONENTS_ROOT, absolutePath).split(path.sep).join("/");
}

/**
 * Scans all Core Components clientlib descriptors and maps each published category to
 * its direct dependencies and source descriptor path.
 *
 * @returns {Map<string, { deps: string[], sourcePath: string }>}
 */
function buildCategoryGraph() {
    /** @type {Map<string, { deps: string[], sourcePath: string }>} */
    const byCategory = new Map();
    const descriptors = collectClientlibDescriptors(COMPONENTS_ROOT, []);

    descriptors.forEach(function(absolutePath) {
        const xml = fs.readFileSync(absolutePath, "utf8");
        if (!isClientlibFolder(xml)) {
            return;
        }

        const categories = parseListAttribute(xml, "categories");
        const dependencies = parseListAttribute(xml, "dependencies");
        const sourcePath = toComponentsRelativePath(absolutePath);

        categories.forEach(function(category) {
            const existing = byCategory.get(category);
            if (existing) {
                const mergedDeps = existing.deps.slice();
                dependencies.forEach(function(dep) {
                    if (!mergedDeps.includes(dep)) {
                        mergedDeps.push(dep);
                    }
                });
                existing.deps = mergedDeps;
                existing.sourcePath = existing.sourcePath + ", " + sourcePath;
                return;
            }
            byCategory.set(category, {
                deps: dependencies.slice(),
                sourcePath: sourcePath
            });
        });
    });

    return byCategory;
}

/**
 * Finds every transitive path from {@code category} through Core Components clientlib
 * categories to cq.authoring.editor.core.
 *
 * @param {string} category
 * @param {Map<string, { deps: string[], sourcePath: string }>} byCategory
 * @returns {string[]}
 */
function findEditorCorePaths(category, byCategory) {
    /** @type {string[]} */
    const violations = [];
    /** @type {Set<string>} */
    const visiting = new Set();

    /**
     * @param {string} currentCategory
     * @param {string[]} chain
     */
    function walk(currentCategory, chain) {
        if (visiting.has(currentCategory)) {
            return;
        }
        visiting.add(currentCategory);

        const node = byCategory.get(currentCategory);
        if (!node) {
            visiting.delete(currentCategory);
            return;
        }

        node.deps.forEach(function(dep) {
            const nextChain = chain.concat([currentCategory + " -> " + dep]);
            if (dep === EDITOR_CORE) {
                violations.push(nextChain.join(", "));
                return;
            }
            if (byCategory.has(dep)) {
                walk(dep, nextChain);
            }
        });

        visiting.delete(currentCategory);
    }

    if (!byCategory.has(category)) {
        fail("guarded category is missing from clientlib graph: " + category);
        return violations;
    }

    walk(category, []);
    return violations;
}

/**
 * @param {string} relativePath path under components/
 * @returns {string}
 */
function readClientlibXml(relativePath) {
    const absolutePath = path.join(COMPONENTS_ROOT, relativePath);
    assert.ok(fs.existsSync(absolutePath), "missing clientlib descriptor: " + relativePath);
    return fs.readFileSync(absolutePath, "utf8");
}

const byCategory = buildCategoryGraph();
let guardedCategoryCount = 0;

GUARDED_CLIENTLIBS.forEach(function(entry) {
    const xml = readClientlibXml(entry.relativePath);
    const categories = parseListAttribute(xml, "categories");

    if (categories.length === 0) {
        fail(entry.label + " must publish at least one clientlib category");
        return;
    }

    categories.forEach(function(category) {
        guardedCategoryCount++;
        const violations = findEditorCorePaths(category, byCategory);
        violations.forEach(function(violationPath) {
            fail(
                entry.label + " (" + category + ") transitively pulls " + EDITOR_CORE +
                " via " + violationPath
            );
        });
    });
});

(function assertHtmlIdValidatorWiring() {
    const xml = readClientlibXml("commons/editor/clientlibs/htmlidvalidator/.content.xml");
    const dependencies = parseListAttribute(xml, "dependencies");
    const categories = parseListAttribute(xml, "categories");

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
    const dependencies = parseListAttribute(xml, "dependencies");

    if (!dependencies.includes("jquery")) {
        fail("authoringutils must depend on jquery");
    }
    if (dependencies.length !== 1) {
        fail("authoringutils should only depend on jquery, got: " + dependencies.join(", "));
    }
})();

(function assertTransitiveGraphSanity() {
    const checkboxCategory = "core.wcm.components.commons.v1.editor.checkboxTextfieldTuple";
    const simulated = new Map(byCategory);
    simulated.set(checkboxCategory, {
        deps: ["jquery", EDITOR_CORE],
        sourcePath: "simulated"
    });

    const imageCategory = "core.wcm.components.image.v3.editor";
    const simulatedViolations = findEditorCorePaths(imageCategory, simulated);
    if (simulatedViolations.length === 0) {
        fail("transitive graph check did not detect simulated " + EDITOR_CORE + " via checkboxTextfieldTuple");
    }
})();

if (failures > 0) {
    console.error("\n" + failures + " clientlib guardrail(s) failed.");
    process.exit(1);
}

console.log(
    "OK: " + guardedCategoryCount + " guarded categories checked across " +
    GUARDED_CLIENTLIBS.length + " clientlibs (" + byCategory.size +
    " categories in graph); no transitive path to " + EDITOR_CORE + "."
);
