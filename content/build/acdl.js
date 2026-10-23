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
 * Builds the Adobe Client Data Layer 2.x library from the sources of the @adobe/adobe-client-data-layer npm package,
 * the same way as the ACDL project does (browserify, derequire, babel, uglify), but with the Lodash functions taken
 * from the Lodash version declared in package.json instead of the custom Lodash build bundled by the ACDL project.
 *
 * The output (target/acdl/adobe-client-data-layer.min.js) is ES5 and is used by clientlib.config.js and karma.conf.js.
 */
const fs = require("fs");
const path = require("path");
const browserify = require("browserify");
const derequire = require("derequire");
const babel = require("@babel/core");
const uglify = require("uglify-js");

const ROOT = path.join(__dirname, "..");
const ACDL_ROOT = path.dirname(require.resolve("@adobe/adobe-client-data-layer/package.json"));
const OUT_DIR = path.join(ROOT, "target", "acdl");
const OUT_FILE = path.join(OUT_DIR, "adobe-client-data-layer.min.js");

// Lodash functions used by the ACDL 2.x sources (see the "lodash" gulp task of the ACDL project)
const LODASH_FUNCTIONS = [
    "cloneDeep", "cloneDeepWith", "find", "get", "has", "includes", "isArray", "isEmpty", "isEqual", "isNull",
    "isObject", "isPlainObject", "merge", "mergeWith", "omit", "reject"
];

function checkVersions() {
    const pkg = require(path.join(ROOT, "package.json"));
    const acdlVersion = require(path.join(ACDL_ROOT, "package.json")).version;
    const lodashVersion = require("lodash/package.json").version;
    if (acdlVersion !== pkg.dependencies["@adobe/adobe-client-data-layer"]) {
        throw new Error("Unexpected @adobe/adobe-client-data-layer version: " + acdlVersion);
    }
    if (lodashVersion !== pkg.dependencies.lodash) {
        throw new Error("Unexpected lodash version: " + lodashVersion);
    }
    return { acdl: acdlVersion, lodash: lodashVersion };
}

/**
 * Recursively copies a directory, skipping test folders (fs.cpSync is not available in Node 14).
 *
 * @param {String} source the directory to copy
 * @param {String} target the destination directory
 */
function copyDir(source, target) {
    fs.mkdirSync(target, { recursive: true });
    fs.readdirSync(source, { withFileTypes: true }).forEach(function(entry) {
        if (entry.name === "__tests__") {
            return;
        }
        const from = path.join(source, entry.name);
        const to = path.join(target, entry.name);
        if (entry.isDirectory()) {
            copyDir(from, to);
        } else {
            fs.copyFileSync(from, to);
        }
    });
}

/**
 * Copies the ACDL sources and creates the custom-lodash.js module they require.
 *
 * @param {String} workDir the directory to prepare the sources in
 * @returns {String} the entry point of the library
 */
function prepareSources(workDir) {
    fs.rmSync(workDir, { recursive: true, force: true });
    fs.mkdirSync(workDir, { recursive: true });
    copyDir(path.join(ACDL_ROOT, "src"), path.join(workDir, "src"));
    fs.copyFileSync(path.join(ACDL_ROOT, "version.json"), path.join(workDir, "version.json"));
    // resolved from the node_modules folder of this project
    const customLodash = "module.exports = {\n" + LODASH_FUNCTIONS.map(function(name) {
        return "    " + name + ": require(\"lodash/" + name + "\")";
    }).join(",\n") + "\n};\n";
    fs.writeFileSync(path.join(workDir, "custom-lodash.js"), customLodash);
    return path.join(workDir, "src", "index.js");
}

function bundle(entry) {
    return new Promise(function(resolve, reject) {
        browserify({ entries: entry, basedir: path.dirname(entry) }).bundle(function(error, buffer) {
            if (error) {
                reject(error);
            } else {
                resolve(buffer.toString());
            }
        });
    });
}

function transpile(code) {
    return babel.transformSync(code, {
        babelrc: false,
        configFile: false,
        sourceType: "module",
        presets: [[require.resolve("@babel/preset-env"), { targets: "ie 11" }]]
    }).code;
}

function minify(code) {
    // arrows=false: uglify-js would otherwise rewrite functions to arrow functions (not ES5);
    // directives=false: keeps the "use strict" directive, as in the library distributed by the ACDL project
    const result = uglify.minify(code, { compress: { arrows: false, directives: false }, mangle: true });
    if (result.error) {
        throw result.error;
    }
    return result.code;
}

async function build() {
    const versions = checkVersions();
    const entry = prepareSources(path.join(OUT_DIR, "work"));
    let code = await bundle(entry);
    code = derequire(code, [{ from: "require", to: "acdlreq" }]);
    code = minify(transpile(code));
    fs.writeFileSync(OUT_FILE, code + "\n");
    fs.rmSync(path.join(OUT_DIR, "work"), { recursive: true, force: true });
    console.log("Built Adobe Client Data Layer " + versions.acdl + " with Lodash " + versions.lodash + ": " +
        path.relative(ROOT, OUT_FILE) + " (" + code.length + " bytes)");
}

build().catch(function(error) {
    console.error(error);
    process.exit(1);
});
