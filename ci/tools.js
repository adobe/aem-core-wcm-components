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

const e = require("child_process");

module.exports = class Tools {

    /**
     * Run shell command and attach to process stdio.
     */
    sh(command) {
        console.log(command);
        return e.execSync(command, {stdio: "inherit"});
    };

    /**
     * Import GPG key.
     */
    prepareGPGKey() {
        this.sh("echo $GPG_PRIVATE_KEY | base64 --decode | gpg --batch --import");
    };

    /**
     * Remove GPG key.
     */
    removeGPGKey() {
        this.sh("rm -rf /home/circleci/.gnupg");
    }

    /**
     * Print stage name.
     */
    stage(name) {
        console.log("\n------------------------------\n" +
            "--\n" +
            "-- %s\n" +
            "--\n" +
            "------------------------------\n", name);
    };

    /**
     * Configure a git impersonation for the scope of the given function.
     */
    gitImpersonate(user, mail, func) {
        try {
            this.sh('git config --local user.name ' + user + ' && git config --local user.email ' + mail)
            func()
        } finally {
            this.sh('git config --local --unset user.name && git config --local --unset user.email')
        }
    };

    /**
     * Remove git tag.
     */
    removeGitTag(gitTag) {
        this.sh('git push --delete origin ' + gitTag);
    }
}
