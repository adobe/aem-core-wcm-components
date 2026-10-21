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
 * Guardrail: a composite (nested/structured) Content Fragment element must render nothing at
 * all on publish (title, wrapper and value all suppressed) and a localized "not supported" hint
 * in edit/preview. No Java unit test or Selenium IT can exercise element.html directly (see
 * SITES-50677), so this locks in the HTL source structure instead: gating only the value
 * expression (and not the whole field) would leave an empty title/wrapper visible on publish,
 * which is the exact mistake this guardrail exists to catch.
 *
 * Run with: node test/guardrails/contentFragmentCompositeRendering.js
 */
const assert = require("assert");
const fs = require("fs");
const path = require("path");

const CONTENTFRAGMENT_DIR = path.join(
    __dirname,
    "..",
    "..",
    "src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment"
);

const ELEMENT_HTML = path.join(CONTENTFRAGMENT_DIR, "element.html");
const TEMPLATES_HTML = path.join(CONTENTFRAGMENT_DIR, "templates.html");

/**
 * The outer field-gating condition must be exactly this OR-of-negations: a composite field
 * renders (with the hint) in author modes and nothing at all on publish. Requiring the literal
 * "||" and "!" here (not just that both substrings appear anywhere in the tag) is deliberate: an
 * incorrectly written "&&" would silently suppress the field in author mode too, and this pattern
 * is what catches that mistake.
 */
const OUTER_TEST_PATTERN = /dataType\s*!=\s*'composite'\s*\|\|\s*!\s*wcmmode\.disabled/;

let failures = 0;

function fail(message) {
    console.error("FAIL: " + message);
    failures++;
}

/**
 * @param {string} absolutePath
 * @returns {string}
 */
function readFile(absolutePath) {
    assert.ok(fs.existsSync(absolutePath), "missing file: " + absolutePath);
    return fs.readFileSync(absolutePath, "utf8");
}

/**
 * Finds, in document order, the outer composite/wcmmode <sly> and the field <div> it must
 * enclose, matching each closing tag to its opener by tag-name nesting depth (not by naive
 * first-match), so the check actually verifies structural enclosure.
 *
 * @param {string} html
 * @returns {{outerSlyOpen: object|null, outerSlyClose: object|null, divOpen: object|null, divClose: object|null}}
 */
function findFieldEnclosure(html) {
    const tagPattern = /<\/?(sly|div)\b[^>]*>/g;
    const stacks = { sly: [], div: [] };
    let outerSlyOpen = null;
    let outerSlyClose = null;
    let divOpen = null;
    let divClose = null;
    let match;

    while ((match = tagPattern.exec(html)) !== null) {
        const fullTag = match[0];
        const tagName = match[1];
        const isClosing = fullTag.charAt(1) === "/";
        const position = { start: match.index, end: match.index + fullTag.length, text: fullTag };

        if (!isClosing) {
            stacks[tagName].push(position);
            continue;
        }

        const opening = stacks[tagName].pop();
        if (!opening) {
            continue;
        }
        if (tagName === "sly" && outerSlyOpen === null && OUTER_TEST_PATTERN.test(opening.text)) {
            outerSlyOpen = opening;
            outerSlyClose = position;
        }
        if (tagName === "div" && divOpen === null && /cmp-contentfragment__element\b/.test(opening.text)) {
            divOpen = opening;
            divClose = position;
        }
    }

    return { outerSlyOpen, outerSlyClose, divOpen, divClose };
}

(function assertWcmmodeParamDeclared() {
    const html = readFile(ELEMENT_HTML);
    const templateTag = html.match(/data-sly-template\.element="\$\{@[^}]*\}"/);
    if (!templateTag || !/\bwcmmode\s*=/.test(templateTag[0])) {
        fail("element.html's \"element\" template must declare a wcmmode parameter");
    }
})();

(function assertCompositeGatingEnclosesWholeField() {
    const html = readFile(ELEMENT_HTML);
    const enclosure = findFieldEnclosure(html);

    if (!enclosure.outerSlyOpen || !enclosure.outerSlyClose) {
        fail("element.html must have an outer <sly> testing " +
            "\"dataType != 'composite' || !wcmmode.disabled\"");
        return;
    }
    if (!enclosure.divOpen || !enclosure.divClose) {
        fail("element.html must have a cmp-contentfragment__element <div>");
        return;
    }

    const opensBeforeDiv = enclosure.outerSlyOpen.start < enclosure.divOpen.start;
    const divClosesBeforeSlyCloses = enclosure.divClose.end <= enclosure.outerSlyClose.start;
    if (!opensBeforeDiv || !divClosesBeforeSlyCloses) {
        fail("the outer composite/wcmmode <sly> must structurally enclose the entire field " +
            "<div> (title and value included) so nothing is emitted on publish, not just the " +
            "value expression");
    }
})();

(function assertCompositeHintUsesI18n() {
    const html = readFile(ELEMENT_HTML);
    const hintPattern = /dataType == 'composite'\}">\s*\$\{'[^']*'\s*@\s*i18n\}\s*<\/sly>/;
    if (!hintPattern.test(html)) {
        fail("element.html's composite hint must be a localized (@ i18n) string");
    }
})();

(function assertGenericValueBranchExcludesComposite() {
    const html = readFile(ELEMENT_HTML);
    const genericBranch = html.match(/<sly data-sly-test="\$\{([^}]*)\}">\$\{\(element\.value\)/);
    if (!genericBranch || !/dataType\s*!=\s*'composite'/.test(genericBranch[1])) {
        fail("element.html's generic value branch must also exclude dataType == 'composite'");
    }
})();

(function assertWcmmodeForwardedFromTemplates() {
    const html = readFile(TEMPLATES_HTML);
    if (!/data-sly-call="\$\{elementTemplate\.element @ element=element,\s*wcmmode=wcmmode\}"/.test(html)) {
        fail("templates.html's \"elements\" template must forward wcmmode=wcmmode into the " +
            "element template call");
    }
})();

if (failures > 0) {
    console.error("\n" + failures + " content fragment composite rendering guardrail(s) failed.");
    process.exit(1);
}

console.log("OK: element.html/templates.html composite field rendering structure verified.");
