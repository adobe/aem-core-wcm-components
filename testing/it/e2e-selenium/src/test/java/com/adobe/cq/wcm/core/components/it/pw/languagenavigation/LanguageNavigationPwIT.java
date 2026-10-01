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
package com.adobe.cq.wcm.core.components.it.pw.languagenavigation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_LANGUAGE_NAVIGATION_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_LANGUAGE_NAVIGATION_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class LanguageNavigationPwIT extends ComponentPwBaseTest {

    private String siteRoot;
    private String noStructure;

    static Stream<String> versions() {
        return Stream.of(RT_LANGUAGE_NAVIGATION_V1, RT_LANGUAGE_NAVIGATION_V2);
    }

    private String addLanguageNavigation(String resourceType) throws Exception {
        testPage = authorClient.createPage("site_root", "site_root", rootPage, defaultPageTemplate).getSlingPath();
        siteRoot = testPage;
        String locale1 = createPage("LOCALE_1", "LOCALE 1", siteRoot);
        String locale31 = createPage("LOCALE_3", "LOCALE 3 1", locale1);
        createPage("about", "About Us", locale31);
        createPage("LOCALE_4", "LOCALE 4", locale1);
        String locale2 = createPage("LOCALE_2", "LOCALE 2", siteRoot);
        String locale32 = createPage("LOCALE_3", "LOCALE 3 2", locale2);
        createPage("about", "About Us", locale32);
        createPage("LOCALE_5", "LOCALE 5", locale2);
        String hidden = authorClient.createPage("hideInNav", "hideInNav", siteRoot, defaultPageTemplate)
            .getSlingPath();
        setNavTitle(hidden, "hideInNav");
        HashMap<String, String> hiddenProps = new HashMap<>();
        hiddenProps.put("_charset_", "UTF-8");
        hiddenProps.put("./jcr:content/hideInNav", "true");
        Commons.editNodeProperties(authorClient, hidden, hiddenProps);
        noStructure = authorClient.createPage("no_structure", "No Structure", rootPage, defaultPageTemplate)
            .getSlingPath();
        String aboutPage = locale31 + "/about";
        addComponentToAllowedPolicy(resourceType);
        String componentPath = Commons.addComponentWithRetry(authorClient, resourceType,
            aboutPage + Commons.relParentCompPath, "languagenavigation");
        openEditor(aboutPage);
        return componentPath;
    }

    private String createPage(String name, String title, String parent) throws Exception {
        String path = authorClient.createPage(name, title, parent, defaultPageTemplate).getSlingPath();
        setNavTitle(path, title);
        return path;
    }

    private void setNavTitle(String pagePath, String navTitle) throws Exception {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("_charset_", "UTF-8");
        properties.put("./jcr:content/navTitle", navTitle);
        Commons.editNodeProperties(authorClient, pagePath, properties);
    }

    private void configureRoot(String componentPath, String pagePath, String structureDepth) {
        openEditDialog(componentPath);
        selectAutocomplete("[name='./navigationRoot']", pagePath);
        if (structureDepth != null) {
            dialog().locator("input[name='./structureDepth']").fill(structureDepth);
        }
    }

    private Locator navigation() {
        return contentFrame().locator(".cmp-languagenavigation");
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testDefaultConfiguration(String resourceType) throws Exception {
        String componentPath = addLanguageNavigation(resourceType);
        configureRoot(componentPath, siteRoot, null);
        saveDialog();

        assertThat(navigation()).containsText("LOCALE 1");
        assertThat(navigation()).containsText("LOCALE 2");
        assertThat(navigation()).not().containsText("hideInNav");
        assertThat(navigation()).not().containsText("LOCALE 3 1");
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testChangeStructureDepth(String resourceType) throws Exception {
        String componentPath = addLanguageNavigation(resourceType);
        configureRoot(componentPath, siteRoot, "2");
        saveDialog();

        assertThat(navigation()).containsText("LOCALE 3 1");
        assertThat(navigation()).containsText("LOCALE 3 2");
        assertThat(navigation()).containsText("LOCALE 4");
        assertThat(navigation()).containsText("LOCALE 5");
        assertThat(navigation()).not().containsText("About Us");
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testSetStructureDepthZero(String resourceType) throws Exception {
        String componentPath = addLanguageNavigation(resourceType);
        configureRoot(componentPath, siteRoot, "0");
        clickDone();

        assertThat(dialog()).isVisible();
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testNavigationRootNoStructure(String resourceType) throws Exception {
        String componentPath = addLanguageNavigation(resourceType);
        configureRoot(componentPath, noStructure, null);
        saveDialog();

        assertThat(navigation().locator("a")).hasCount(0);
        assertThat(navigation().locator(".cmp-languagenavigation__placeholder")).isVisible();
    }
}
