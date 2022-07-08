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
package com.adobe.cq.wcm.core.components.it.seljup.util.components.image;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;


import static com.codeborne.selenide.Selenide.$;

public class BaseImage extends BaseComponent {
    private static String assetPath = "[name='assetfilter_image_path']";
    private static String imageTag = "div.cmp-image img[src*='%s/_jcr_content/root/responsivegrid/image.img.']";
    private static String altText = "div.cmp-image img[alt='%s']";
    private static String image = "div.cmp-image";
    public static String imageWithLazyLoadedEnabled = "div.cmp-image img[loading='lazy']";

    private static String imgWithTitle = "div.cmp-image img[title='%s']";

    private static String imageLink = ".cmp-image__link";

    private static String element = ".cmp-image";
    private static String imageElement = ".cmp-image__image";
    public static String mapElement = "[data-cmp-hook-image='map']";
    public static String areaElement = "[data-cmp-hook-image='area']";
    public static String imageWithAltTextAndTitle = ".cmp-image__image[src*='%s/_jcr_content/root/responsivegrid/image.coreimg.'][alt='%s'][title='%s']";
    public static String imageWithAltText = ".cmp-image__image[src*='%s/_jcr_content/root/responsivegrid/image.coreimg.'][alt='%s']";
    public static String imageWithFileName = ".cmp-image__image[src*='/%s']";

    protected String title;
    protected String imgWithAltText;

    public BaseImage() {
        super("");
    }

    public String getAssetPath() {
        return assetPath;
    }

    public ImageEditDialog getEditDialog() {
        return new ImageEditDialog();
    }

    public boolean isImageSet(String pagePath) {
        return $(String.format(imageTag, pagePath)).isDisplayed();
    }

    public boolean isAltTextSet(String text) {
        return $(String.format(altText, text)).isDisplayed();
    }

    public boolean isTitleSet(String text) {
        return $(title).getText().trim().contains(text);
    }

    public void imageClick() {
        $(image).click();
    }

    public boolean isImageWithTitle(String title) {
        return $(String.format(imgWithTitle,title)).isDisplayed();
    }

    public boolean isImageWithAltText() {
        return $(imgWithAltText).isDisplayed();
    }

    public boolean isImagePresentWithAltTextAndTitle(String pagePath, String altText, String title) {
        return $(String.format(imageWithAltTextAndTitle, pagePath, altText, title)).isDisplayed();
    }

    public boolean isImagePresentWithAltText(String pagePath, String altText) {
        return $(String.format(imageWithAltText, pagePath, altText)).isDisplayed();
    }

    public boolean isImagePresentWithFileName(String fileName) {
        return $(String.format(imageWithFileName, fileName)).isDisplayed();
    }

    public boolean isLinkSet() {
        return $(imageLink).isDisplayed();
    }

    public boolean isMapElementPresent() {
        return $(mapElement).isDisplayed();
    }

    public boolean isAreaElementPresent() {
        return $(areaElement).isDisplayed();
    }

    public void clickAreaElement() {
        $(areaElement).click();
    }

    public void resizeImageElementWidth(int size) throws InterruptedException {
        String sizePx = size + "px";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(imageElement));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].style.width= arguments[1]", element, sizePx);
        webDriver.manage().window().setSize(new Dimension(size, size));
        webDriver.manage().window().maximize();
    }

    public boolean isAreaCoordinatesCorrectlySet(String [] coordinates) {
        String [] currentCoords = $(areaElement).getAttribute("coords").split(",");
        if(currentCoords.length != coordinates.length)
            return false;
        for(int i = 0; i < coordinates.length; i++) {
            if(!currentCoords[i].equals(coordinates[i]))
                return false;
        }
        return true;
    }

    public boolean isImageWithLazyLoadingEnabled() {
        return $(imageWithLazyLoadedEnabled).isDisplayed();
    }

    public boolean checkLinkPresentWithTarget(String link, String target) {
        return $("a[href='" + link + "'][target='" + target + "']").isDisplayed();
    }
}
