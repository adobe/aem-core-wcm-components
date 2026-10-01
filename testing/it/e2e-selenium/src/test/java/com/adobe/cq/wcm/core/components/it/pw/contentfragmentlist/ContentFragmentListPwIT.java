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
package com.adobe.cq.wcm.core.components.it.pw.contentfragmentlist;

import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_CONTENTFRAGMENTLIST_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_CONTENTFRAGMENTLIST_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class ContentFragmentListPwIT extends ComponentPwBaseTest {

    private static final String MODEL_PATH = "/conf/core-components/settings/dam/cfm/models/core-component-model";
    private static final String PARENT_PATH = "/content/dam/core-components/contentfragments-tests";
    private static final String TAG_PATH = "/content/cq:tags/core-components/component-type/basic";

    static Stream<String> versions() {
        return Stream.of(RT_CONTENTFRAGMENTLIST_V1, RT_CONTENTFRAGMENTLIST_V2);
    }

    private String addContentFragmentList(String resourceType) throws Exception {
        return addStandaloneComponent(resourceType, "contentfragmentlist");
    }

    private void configureModelAndParent() {
        selectInCoralSelect("[name='./modelPath']", MODEL_PATH);
        selectAutocomplete("[name='./parentPath']", PARENT_PATH);
    }

    private Locator contentFragments() {
        return contentFrame().locator(".cmp-contentfragmentlist .cmp-contentfragment");
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testSetParentPath(String resourceType) throws Exception {
        String path = addContentFragmentList(resourceType);
        openEditDialog(path);
        configureModelAndParent();
        saveDialog();

        assertThat(contentFrame().locator(".cmp-contentfragmentlist")).hasCount(1);
        assertThat(contentFragments()).hasCount(3);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(12);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")
            .filter(new Locator.FilterOptions().setHasText("Image Fragment"))).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")
            .filter(new Locator.FilterOptions().setHasText("Text Fragment"))).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testSetTagNames(String resourceType) throws Exception {
        String path = addContentFragmentList(resourceType);
        openEditDialog(path);
        configureModelAndParent();
        selectInPicker("/content/cq:tags", "[name='./tagNames']", TAG_PATH);
        saveDialog();

        assertThat(contentFragments()).hasCount(2);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(8);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")
            .filter(new Locator.FilterOptions().setHasText("Image Fragment"))).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")
            .filter(new Locator.FilterOptions().setHasText("Text Fragment"))).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("versions")
    public void testSetElementNames(String resourceType) throws Exception {
        String path = addContentFragmentList(resourceType);
        openEditDialog(path);
        configureModelAndParent();
        dialog().locator("coral-tab[data-foundation-tracking-event*='elements']").click();
        addElement("component-title");
        addElement("component-type");
        saveDialog();

        assertThat(contentFragments()).hasCount(3);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(6);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")
            .filter(new Locator.FilterOptions().setHasText("Carousel Fragment"))).hasCount(1);
    }

    private void addElement(String name) {
        dialog().locator("[coral-multifield-add]").click();
        Locator button = dialog().locator("coral-multifield-item").last()
            .locator("coral-select[name='./elementNames'] > button");
        button.click();
        Locator list = page.locator("#" + button.getAttribute("aria-controls"));
        list.locator("coral-selectlist-item[value='" + name + "']").click();
    }
}
