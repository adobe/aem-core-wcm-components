/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

// Aggregates maven-failsafe JUnit reports (TEST-*.xml) found under the given
// directories and prints a Markdown summary (totals + the list of failing tests)
// to stdout - intended to be appended to $GITHUB_STEP_SUMMARY.
//
// Rerun-aware: a test retried via rerunFailingTestsCount that ultimately passed is
// recorded with <flakyFailure>/<flakyError> (not <failure>/<error>), so it is not
// counted as failing here - matching the suite's own counters - but it is listed
// under "Flaky tests".
//
// Each test is reported per leg (the first directory below a root, i.e. the
// downloaded artifact, which identifies the AEM product and browser group), so the
// same test failing on several products is listed once per product.
//
// Usage: node summarize-it.js <dir> [<dir> ...]

const fs = require("node:fs");
const path = require("node:path");

function findReports(dir, acc) {
    if (!fs.existsSync(dir)) {
        return acc;
    }
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
        const p = path.join(dir, entry.name);
        if (entry.isDirectory()) {
            findReports(p, acc);
        } else if (/^TEST-.*\.xml$/.test(entry.name)) {
            acc.push(p);
        }
    }
    return acc;
}

// The direct <failure>/<error>/<flakyFailure>/<flakyError> children of a <testcase>
// body. Output and nested elements are removed first so that test output that
// merely contains "<error" (e.g. logged HTML) is not mistaken for a result.
function outcomes(body) {
    const stripped = body
        .replace(/<!\[CDATA\[[\s\S]*?\]\]>/g, "")
        .replace(/<(system-out|system-err)\b[^>]*\/>/g, "")
        .replace(/<(system-out|system-err)\b[^>]*>[\s\S]*?<\/\1>/g, "")
        .replace(/<(flakyFailure|flakyError|rerunFailure|rerunError)\b([^>]*?)(\/>|>[\s\S]*?<\/\1>)/g, "<$1/>");
    return {
        error: /<error\b/.test(stripped),
        failure: /<failure\b/.test(stripped),
        flaky: /<flaky(Failure|Error)\b/.test(stripped),
    };
}

function legOf(root, file) {
    const rel = path.relative(root, file).split(path.sep);
    const leg = rel.length > 1 ? rel[0] : path.basename(root);
    return leg.replace(/^it-/, "").replace(/-reports$/, "");
}

const roots = process.argv.slice(2);
const files = [];
for (const root of roots) {
    for (const file of findReports(root, [])) {
        files.push({ root, file });
    }
}

let tests = 0;
let failures = 0;
let errors = 0;
let skipped = 0;
const failing = [];
const flaky = [];
const seen = new Set(); // de-dupe a test that appears in more than one copy of the same leg

for (const { root, file } of files) {
    const xml = fs.readFileSync(file, "utf8");
    const leg = legOf(root, file);

    let sm;
    const suiteRe = /<testsuite\b([^>]*)>/g;
    while ((sm = suiteRe.exec(xml)) !== null) {
        const attrs = sm[1];
        const num = (k) => {
            const r = new RegExp(k + '="([0-9]+)"').exec(attrs);
            return r ? Number.parseInt(r[1], 10) : 0;
        };
        tests += num("tests");
        failures += num("failures");
        errors += num("errors");
        skipped += num("skipped");
    }

    let tm;
    const caseRe = /<testcase\b([^>]*?)(\/>|>([\s\S]*?)<\/testcase>)/g;
    while ((tm = caseRe.exec(xml)) !== null) {
        const attrs = tm[1];
        const body = tm[3] || "";
        // Only genuine failures/errors (final result), never <flakyFailure>/<rerunFailure>.
        const result = outcomes(body);
        if (!result.error && !result.failure && !result.flaky) {
            continue;
        }
        const cls = (/classname="([^"]*)"/.exec(attrs) || [])[1] || "";
        const name = (/\bname="([^"]*)"/.exec(attrs) || [])[1] || "";
        const key = leg + "|" + cls + "#" + name;
        if (seen.has(key)) {
            continue;
        }
        seen.add(key);
        if (result.error || result.failure) {
            failing.push({ leg, cls, name, kind: result.error ? "error" : "failure" });
        } else {
            flaky.push({ leg, cls, name, kind: "flaky" });
        }
    }
}

const passing = tests - failures - errors - skipped;

let out = "";
out += "## Integration Test Results\n\n";
if (files.length === 0) {
    out += "_No test reports were found._\n";
    process.stdout.write(out);
    return;
}
out += "| Total | ✅ Passing | ❌ Failing | ⚠️ Errors | ⏭️ Skipped |\n";
out += "|---:|---:|---:|---:|---:|\n";
out += `| ${tests} | ${passing} | ${failures} | ${errors} | ${skipped} |\n\n`;

function list(title, entries) {
    let text = `### ${title} (${entries.length})\n\n`;
    const byClass = {};
    for (const f of entries) {
        byClass[f.cls] = byClass[f.cls] || [];
        byClass[f.cls].push(f);
    }
    for (const cls of Object.keys(byClass).sort((a, b) => a.localeCompare(b))) {
        text += `- \`${cls}\`\n`;
        const sorted = byClass[cls].sort((a, b) => a.name.localeCompare(b.name) || a.leg.localeCompare(b.leg));
        for (const f of sorted) {
            text += `  - ${f.name} _(${f.kind}, ${f.leg})_\n`;
        }
    }
    return text + "\n";
}

if (failing.length === 0) {
    out += "All tests passed. 🎉\n\n";
} else {
    out += list("Failing tests", failing);
}
if (flaky.length > 0) {
    out += list("Flaky tests (passed on rerun)", flaky);
}

process.stdout.write(out);
