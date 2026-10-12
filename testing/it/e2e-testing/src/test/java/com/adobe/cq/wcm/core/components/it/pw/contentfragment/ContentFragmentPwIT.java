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
package com.adobe.cq.wcm.core.components.it.pw.contentfragment;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_CONTENTFRAGMENT_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class ContentFragmentPwIT extends ComponentPwBaseTest {

    private static final String SIMPLE_FRAGMENT = "/content/dam/core-components/contentfragments-tests/simple-fragment";
    private static final String IMAGE_FRAGMENT = "/content/dam/core-components/contentfragments-tests/image-fragment";

    private String addContentFragment() throws Exception {
        String path = addStandaloneComponent(RT_CONTENTFRAGMENT_V1, "contentfragment");
        openEditDialog(path);
        return path;
    }

    private void selectFragment(String fragmentPath) {
        selectInPicker("/content/dam", "[name='./fragmentPath']", fragmentPath);
    }

    private void chooseElement(String value) {
        Locator items = dialog().locator("coral-multifield-item");
        int before = items.count();
        dialog().locator("[coral-multifield-add]").click();
        assertThat(items).hasCount(before + 1);
        Locator button = items.last().locator("coral-select[name='./elementNames'] > button");
        button.click();
        Locator list = page.locator("[id='" + button.getAttribute("aria-controls") + "']");
        assertThat(list).isVisible();
        Locator item = list.locator("coral-selectlist-item[value='" + value + "']");
        item.scrollIntoViewIfNeeded();
        item.click();
    }

    @Test
    public void testSetFragmentPath() throws Exception {
        addContentFragment();
        selectFragment(SIMPLE_FRAGMENT);
        saveDialog();

        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasText("Simple Fragment");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasText("Main");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value h2")).hasText("Master variation");
    }

    @Test
    public void testSetVariationName() throws Exception {
        addContentFragment();
        selectFragment(SIMPLE_FRAGMENT);
        selectInCoralSelect("[name='./variationName']", "short");
        saveDialog();

        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasText("Simple Fragment");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasText("Main");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value h2")).hasText("Short variation");
    }

    @Test
    public void testSetStructuredContentFragment() throws Exception {
        addContentFragment();
        selectFragment(IMAGE_FRAGMENT);
        saveDialog();

        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasText("Image Fragment");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(4);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value")).hasCount(4);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title").nth(0)).hasText("Title");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value").nth(0)).hasText("Image");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title").nth(1)).hasText("Description");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title").nth(2)).hasText("Latest Version");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value").nth(2)).hasText("2");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title").nth(3)).hasText("Type");
    }

    @Test
    public void testSetElementNames() throws Exception {
        String path = addContentFragment();
        selectFragment(IMAGE_FRAGMENT);
        saveDialog();
        openEditDialog(path);
        chooseElement("component-title");
        chooseElement("component-type");
        saveDialog();

        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasText("Image Fragment");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasCount(2);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value")).hasCount(2);
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title").nth(0)).hasText("Title");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value").nth(0)).hasText("Image");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title").nth(1)).hasText("Type");
    }

    @Test
    public void testSetSingleElement() throws Exception {
        addContentFragment();
        selectFragment(SIMPLE_FRAGMENT);
        dialog().locator("coral-radio[name='./displayMode'][value='singleText']").click();
        clickDone();
        selectFragment(IMAGE_FRAGMENT);
        com.microsoft.playwright.Locator replacementDialog = page.locator("[role='alertdialog']:visible").last();
        assertThat(replacementDialog).isVisible();
        replacementDialog.locator("button[variant='primary']").click();
        assertThat(replacementDialog).isHidden();
        clickDone();
        assertThat(dialog().locator("label.coral-Form-errorlabel, coral-tooltip[variant='error']")).hasCount(1);
        selectInCoralSelect("[name='./elementNames']", "component-title");
        assertThat(dialog().locator("label.coral-Form-errorlabel, coral-tooltip[variant='error']")).hasCount(0);
        clickDone();

        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-contentfragment__title")).hasText("Image Fragment");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-title")).hasText("Title");
        assertThat(contentFrame().locator(".cmp-contentfragment__element-value")).hasText("Image");
    }

    @Test
    public void testVcfClearsSingleTextElementValidationError() throws Exception {
        addContentFragment();
        selectFragment(IMAGE_FRAGMENT);
        dialog().locator("coral-radio[name='./displayMode'][value='singleText']").click();
        clickDone();
        assertThat(dialog().locator("label.coral-Form-errorlabel, coral-tooltip[variant='error']")).hasCount(1);

        dialog().locator("coral-radio[name='./displayMode'][value='vcf']").click();
        assertThat(dialog().locator("label.coral-Form-errorlabel, coral-tooltip[variant='error']")).hasCount(0);
    }

    @Test
    public void testVcfDisplayModeShowsVcfTemplateField() throws Exception {
        addContentFragment();
        selectFragment(SIMPLE_FRAGMENT);
        dialog().locator("coral-radio[name='./displayMode'][value='vcf']").click();

        assertThat(dialog().locator("coral-radio[name='./displayMode'][value='vcf']")).isVisible();
        assertThat(dialog().locator("coral-select[name='./vcfTemplate']")).isVisible();
    }

    @Test
    public void testVcfDisplayModeRendersWrapperInPreview() throws Exception {
        addContentFragment();
        selectFragment(SIMPLE_FRAGMENT);
        dialog().locator("coral-radio[name='./displayMode'][value='vcf']").click();
        saveDialog();
        page.evaluate("() => Granite.author.layerManager.activate('Preview')");

        assertThat(contentFrame().locator(".cmp-contentfragment--vcf")).isVisible();
    }
}
