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
const path = require('path');
const config = require('../../lib/config');
const commons = require('../../lib/commons');
const { expect } = require('chai');

describe('AEM Sites Console', () => {

    // AEM Login
    beforeEach(() => {
        browser.maximizeWindow();
        // Logout/Login dance
        browser.AEMForceLogout();
        browser.url(config.aem.author.base_url);
        browser.AEMLogin(config.aem.author.username, config.aem.author.password);
    });

    let onboardingHdler;

    before(function() {
        // Enable helper to handle onboarding dialog popup
        onboardingHdler = new commons.OnboardingDialogHandler(browser);
        onboardingHdler.enable();
    });

    after(function() {
        // Disable helper to handle onboarding dialog popup
        onboardingHdler.disable();
    });



    it('should allow toggling accordion items included via experience fragment', () => {
        browser.url(config.aem.author.base_url + "/content/core-components/xf-page.html");
        expect($(".cmp-accordion__panel*=A1 text").isDisplayed()).to.not.be.true;
        expect($(".cmp-accordion__panel*=A2 text").isDisplayed()).to.not.be.true;
        $(".cmp-accordion__button*=A1").click();
        expect($(".cmp-accordion__panel*=A1 text").isDisplayed()).to.be.true;
        $(".cmp-accordion__button*=A2").click();
        expect($(".cmp-accordion__panel*=A2 text").isDisplayed()).to.be.true;
    });


});