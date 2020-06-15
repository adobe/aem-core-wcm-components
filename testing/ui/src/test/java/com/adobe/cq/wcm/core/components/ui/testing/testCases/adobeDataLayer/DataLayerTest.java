package com.adobe.cq.wcm.core.components.ui.testing;
import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.github.bonigarcia.seljup.BrowserType;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.SelenideConfiguration;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;
import com.codeborne.selenide.Condition;

@Tag("demo")
@DisplayName("Adobe Data Layer Test Class")
public class DataLayerTest extends BaseTest {

    private String baseUrl = "http://localhost:4502";
    private String imagePageUrl = baseUrl + "/content/core-components-examples/library/page-authoring/image.html";
    private String tabsPageUrl = baseUrl + "/content/core-components-examples/library/container/tabs.html";
    private String carosuelPageUrl = baseUrl + "/content/core-components-examples/library/container/carousel.html";
    private String accordionPageUrl = baseUrl + "/content/core-components-examples/library/container/accordion.html";
    private String dataLayerEnabledAttr = "data-cmp-data-layer-enabled";

    void waitForDataLayer() {
        d.$("body").shouldHave(Condition.attribute(dataLayerEnabledAttr));
    }

    void waitForAlert(String alertMsg)  {
        WebDriverWait wait = new WebDriverWait(d.getWebDriver(), 5);
        Alert alert = d.switchTo().alert();
        wait.until(ExpectedConditions.alertIsPresent());
        if (alert.getText() == alertMsg) {
            alert.accept();
        }
    }

    @Test
    @DisplayName("Data Layer / data-cmp-data-layer-enabled")
    void openTestPage()  {
        d.open(imagePageUrl);
        waitForDataLayer();
    }

    @Test
    @DisplayName("Data Layer / cmp:show - tabs")
    void testTabCmpShow()  {
        d.open(tabsPageUrl);
        waitForDataLayer();
        d.executeJavaScript("adobeDataLayer.addEventListener('cmp:show', function() { alert('tabs-cmp:show') });");
        waitForAlert("tabs-cmp:show");
        d.$("#tabs-c324455e43-item-6afd3ea640-tab").click();
    }

    @Test
    @DisplayName("Data Layer / cmp:hide - tabs")
    void testTabCmpHide()  {
        d.open(tabsPageUrl);
        waitForDataLayer();
        d.executeJavaScript("adobeDataLayer.addEventListener('cmp:show', function() { alert('tabs-cmp:hide') });");
        waitForAlert("tabs-cmp:hide");
        d.$("#tabs-c324455e43-item-6afd3ea640-tab").click();
    }

    @Test
    @DisplayName("Data Layer / cmp:show - accordion")
    void testAccordionCmpShow()  {
        d.open(accordionPageUrl);
        waitForDataLayer();
        d.executeJavaScript("adobeDataLayer.addEventListener('cmp:show', function() { alert('accordion-cmp:show') });");
        waitForAlert("accordion-cmp:show");
        d.$("#accordion-6c8513b5eb-item-6c119b11cf-button").click();
    }

    @Test
    @DisplayName("Data Layer / cmp:hide - accordion")
    void testAccordionCmpHide()  {
        d.open(accordionPageUrl);
        waitForDataLayer();
        d.executeJavaScript("adobeDataLayer.addEventListener('cmp:show', function() { alert('accordion-cmp:hide') });");
        waitForAlert("accordion-cmp:hide");
        d.$("#accordion-6c8513b5eb-item-6c119b11cf-button").click();
    }

    @Test
    @DisplayName("Data Layer / cmp:loaded")
    void testCmpLoaded()  {
        d.open(tabsPageUrl);
        waitForDataLayer();
        d.executeJavaScript("adobeDataLayer.addEventListener('cmp:loaded', function() { alert('cmp:loaded') });");
        waitForAlert("cmp:loaded");
    }
}
