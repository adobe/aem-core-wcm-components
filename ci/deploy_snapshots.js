/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

"use strict";

const Tools = require("./tools.js");
const tools = new Tools();

let gitTag = process.env.CIRCLE_TAG;
if (gitTag != "@deploy-snapshot") {
    throw "Cannot release without a valid git tag";
}

try {
    tools.stage("DEPLOY SNAPSHOTS");
    tools.prepareGPGKey();
    tools.sh("mvn deploy -B -s ci/settings.xml -Prelease -Padobe-public");
    tools.stage("DEPLOY SNAPSHOTS DONE");
} finally {
    tools.removeGitTag(gitTag);
    tools.removeGPGKey();
}
