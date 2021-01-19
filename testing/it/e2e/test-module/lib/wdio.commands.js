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

browser.addCommand('AEMLogin', function(username, password) {
    // Check presence of local sign-in Accordion
    if ($('[class*="Accordion"] form').isExisting()) {
        try {
            $('#username').setValue(username);
        }
        // Form field not interactable, not visible
        // Need to open the Accordion
        catch (e) {
            $('[class*="Accordion"] button').click();
            browser.pause(500);
        }
    }

    $('#username').setValue(username);
    $('#password').setValue(password);

    $('form [type="submit"]').click();

    $('coral-shell-content').waitForExist(5000);
});

browser.addCommand('AEMForceLogout', function() {
    browser.url('/');

    if (browser.getTitle() != 'AEM Sign In') {
        browser.url('/system/sling/logout.html');
    }

    $('form[name="login"]').waitForExist();
});
