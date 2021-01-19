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

wdio_config.hostname = 'localhost';
wdio_config.services = [
    ['selenium-standalone', {
        logPath: config.reports_path}
    ]
];

// Define capabilities based on configuration
let capabilities = {};

switch(config.selenium.browser) {
case config.CHROME:
    capabilities = {
        maxInstances: 1,
        browserName: 'chrome',
        'goog:chromeOptions': {
            'excludeSwitches': ['enable-automation'],
            'prefs': {
                'credentials_enable_service': false,
                'profile.password_manager_enabled': false
            }
        }
    };
    if (config.selenium.headless === true) {
        capabilities['goog:chromeOptions'].args = ['headless'];
    }
    break;
case config.FIREFOX:
    capabilities = {
        maxInstances: 1,
        browserName: 'firefox',
        'moz:firefoxOptions': {
            prefs: {
                // Prevent opening the extension tabs on startup
                'extensions.enabledScopes': 0
            }
        }
    };
    if (config.selenium.headless === true) {
        capabilities['moz:firefoxOptions'].args = ['-headless'];
    }
    break;
default:
    throw new Error('Unsupported browser defined in configuration!');
}

wdio_config.capabilities = [capabilities];

exports.config = wdio_config;
