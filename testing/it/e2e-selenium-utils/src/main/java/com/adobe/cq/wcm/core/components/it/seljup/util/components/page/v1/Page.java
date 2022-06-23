/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.page.v1;

import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.testing.selenium.pagewidgets.CalendarPicker;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralMultiField;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralTagList;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.AdvancedTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.ThumbnailTab;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Page {
    private static String onTime = "./onTime";
    private static String offTime = "./offTime";
    private static String designPath = "[name='./cq:designPath']";
    private static String slingVanityPath = "input[name='./sling:vanityPath']";
    private static String allowedTemplates = "input[name='./cq:allowedTemplates']";
    private static String loginPage = "[name='./cq:loginPath']";
    private static String exportTemplate = "[name='./cq:exportTemplate']";
    private static String variantPath = "[name='./variantPath']";
    private static String cloudServiceConfiguration = "coral-select[name='./cq:cloudserviceconfigs']";
    private static String contextHubPath = "[name='./cq:contextHubPath']";
    private static String contextHubSegmentsPath = "[name='./cq:contextHubSegmentsPath']";
    private static String advanceConfig = "[name='./cq:conf']";
    private static String robotsTags = "name='./cq:robotsTags'";
    private static String generateSitemap = "[name='./sling:sitemapRoot']";
    private static String canonicalUrl = "[name='./cq:canonicalUrl']";

    public void setOnTime() throws InterruptedException {
        CalendarPicker calendarPicker = new CalendarPicker(onTime);
        calendarPicker.calendarButton().click();
        calendarPicker.next().click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        calendarPicker.date(1).click();
    }

    public void setOffTime() throws InterruptedException {
        CalendarPicker calendarPicker = new CalendarPicker(offTime);
        calendarPicker.calendarButton().click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        calendarPicker.next().click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        calendarPicker.date(2).click();
    }

    public String getOnTime() {
        return $("input[name='" + onTime + "']").getValue();
    }

    public String getOffTime() {
        return $("input[name='" + offTime + "']").getValue();
    }

    public String getVanityUrlValue(int idx) {
        return $$(slingVanityPath).get(idx).getValue();
    }

    public CoralMultiField.MultiFieldItem addVanityUrl(PropertiesPage.Tabs.Basic basicTab, String vanityURL) {
        CoralMultiField vanityField = basicTab.vanityPath();
        CoralMultiField.MultiFieldItem item = vanityField.add();
        item.input().sendKeys(vanityURL);
        return item;
    }

    public void deleteVanityUrl(CoralMultiField.MultiFieldItem item) {
        item.remove();
    }

    public CoralMultiField.MultiFieldItem addTemplate(AdvancedTab advancedTab, String templatePath) {
        CoralMultiField vanityField = advancedTab.allowedTemplates();
        CoralMultiField.MultiFieldItem item = vanityField.add();
        item.input().sendKeys(templatePath);
        return item;
    }

    public void deleteTemplate(CoralMultiField.MultiFieldItem item) {
        item.remove();
    }

    public void setDesignPath(String path) throws InterruptedException {
        Commons.selectInAutocomplete(designPath, path);
    }

    public String getDesignPath() {
        return $("input" + designPath).getValue();
    }

    public String getAllowTemplate(int idx) {
        return $$(allowedTemplates).get(0).getValue();
    }

    public void setLoginPage(String page) {
        Commons.selectInAutocomplete(loginPage, page);
    }

    public String getLoginPath() {
        return $("input" + loginPage).getValue();
    }

    public void setExportTemplate(String config) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + exportTemplate);
        autoCompleteField.sendKeys(config);
        autoCompleteField.suggestions().selectByValue(config);
    }

    public String getExportTemplate() {
        return $("input" + exportTemplate).getValue();
    }

    public void generateThumbnail(ThumbnailTab thumbnailTab) {
        thumbnailTab.getThumbnailGeneratePreviewActivator().click();
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.invisibilityOf(thumbnailTab.getDefaultThumbnailImg().toWebElement()));
    }

    public void setVariantPath(String path) throws InterruptedException {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + variantPath);
        autoCompleteField.sendKeys(path);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    public String getVariantPath() {
        return $("input" + variantPath).getValue();
    }

    public boolean isCloudServiceConfigAdded() {
        return $(cloudServiceConfiguration).isDisplayed();
    }

    public void setContextHubPath(String path) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + contextHubPath);
        autoCompleteField.sendKeys(path);
    }

    public String getContextHubPath() {
        return $("input" + contextHubPath).getValue();
    }

    public void setContextHubSegmentsPath(String path) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + contextHubSegmentsPath);
        autoCompleteField.sendKeys(path);
    }

    public String getContextHubSegmentsPath() {
        return $("input" + contextHubSegmentsPath).getValue();
    }

    public void setAdvanceConfig(String config) throws InterruptedException {
        String prefix = "/conf";
        String configWithoutPrefix = config.substring(config.indexOf("/conf") + 6);
        Commons.selectInPicker(prefix, advanceConfig, configWithoutPrefix);
    }

    public String getAdvanceConfig() {
        return $(advanceConfig + " input[is='coral-textfield']").getValue();
    }

    public void setRobotsTags(String... values) {
        CoralSelect selectList = new CoralSelect(robotsTags);
        CoralSelectList coralSelectList = selectList.openSelectList();
        for (String value : values) {
            coralSelectList.selectByValue(value);
        }
    }

    public String[] getRobotsTags() {
        CoralTagList tagList = new CoralTagList(robotsTags);
        return tagList.getItems().stream().map(SelenideElement::getValue).toArray(String[]::new);
    }

    public void setGenerateSitemap(boolean enabled) {
        CoralCheckbox coralCheckbox = new CoralCheckbox(generateSitemap);
        coralCheckbox.setSelected(enabled);
    }

    public boolean getGenerateSitemap() {
        CoralCheckbox coralCheckbox = new CoralCheckbox(generateSitemap);
        return coralCheckbox.isChecked();
    }

    public void setCanonicalUrl(String page) {
        Commons.selectInAutocomplete(canonicalUrl, page);
    }

    public String getCanonicalUrl() {
        return $("input" + canonicalUrl).getValue();
    }
}
