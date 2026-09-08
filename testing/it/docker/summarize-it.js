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
// counted as failing here - matching the suite's own counters.
//
// Usage: node summarize-it.js <dir> [<dir> ...]

const fs = require("fs");
const path = require("path");

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

const roots = process.argv.slice(2);
const files = [];
roots.forEach((r) => findReports(r, files));

let tests = 0;
let failures = 0;
let errors = 0;
let skipped = 0;
const failing = [];
const seen = new Set(); // de-dupe a test that appears in more than one downloaded copy

for (const file of files) {
    const xml = fs.readFileSync(file, "utf8");

    let sm;
    const suiteRe = /<testsuite\b([^>]*)>/g;
    while ((sm = suiteRe.exec(xml)) !== null) {
        const attrs = sm[1];
        const num = (k) => {
            const r = new RegExp(k + '="([0-9]+)"').exec(attrs);
            return r ? parseInt(r[1], 10) : 0;
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
        const isError = /<error\b/.test(body);
        const isFailure = /<failure\b/.test(body);
        if (isError || isFailure) {
            const cls = (/classname="([^"]*)"/.exec(attrs) || [])[1] || "";
            const name = (/\bname="([^"]*)"/.exec(attrs) || [])[1] || "";
            const key = cls + "#" + name;
            if (!seen.has(key)) {
                seen.add(key);
                failing.push({ cls, name, kind: isError ? "error" : "failure" });
            }
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

if (failing.length === 0) {
    out += "All tests passed. 🎉\n";
} else {
    out += `### Failing tests (${failing.length})\n\n`;
    const byClass = {};
    for (const f of failing) {
        (byClass[f.cls] = byClass[f.cls] || []).push(f);
    }
    for (const cls of Object.keys(byClass).sort()) {
        out += `- \`${cls}\`\n`;
        for (const f of byClass[cls].sort((a, b) => a.name.localeCompare(b.name))) {
            out += `  - ${f.name} _(${f.kind})_\n`;
        }
    }
}

process.stdout.write(out);
