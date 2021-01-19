/*
 *  Copyright 2020 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * DO NOT MODIFY
 */
let wdio_config = require('./wdio.conf.commons.js').config;
let config = require('./lib/config');

wdio_config.hostname =  config.selenium.hostname;
wdio_config.port = config.selenium.port;
wdio_config.path = '/wd/hub';

let capabilities = {
    maxInstances: 1,
    browserName: config.selenium.browser,
};

// Set common startup arguments to improve stability in Docker
switch(config.selenium.browser) {
case config.CHROME:
    capabilities['goog:chromeOptions'] = {
        args: ['headless', 'disable-gpu', 'no-sandbox', 'disable-dev-shm-usage']
    };
    break;
case config.FIREFOX:
    capabilities['moz:firefoxOptions'] = {
        args: ['-headless']
    };
    break;
}

wdio_config.capabilities = [capabilities];

exports.config = wdio_config;
