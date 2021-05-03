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

package com.adobe.cq.wcm.core.components.it.seljup.util;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.utils.KeyboardShortCuts;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.constant.Selectors;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.apache.sling.testing.clients.util.HttpUtils;
import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import org.openqa.selenium.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import static com.adobe.cq.testing.selenium.Constants.DEFAULT_SMALL_SIZE;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Commons {

    // the path to the policies
    public static String defaultPolicyPath = "/conf/core-components/settings/wcm/policies/core-component/components";
    // the policy assignment path
    public static String defaultPolicyAssignmentPath = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content/root/responsivegrid/core-component/components";
    // proxy components path
    public static String proxyPath =  "/apps/core-component/components/";
    // relative path from page node to the root layout container
    public static String relParentCompPath = "/jcr:content/root/responsivegrid/";

    // core component resource types
    // accordion component
    public static String  rtAccordion_v1 = "core/wcm/components/accordion/v1/accordion";
    // embed component
    public static String rtEmbed_v1 = "core/wcm/components/embed/v1/embed";
    // text component
    public static String  rtText_v1 = "core/wcm/components/text/v1/text";
    public static String  rtText_v2 = "core/wcm/components/text/v2/text";
    // title component
    public static String rtTitle_v1 = "core/wcm/components/title/v1/title";
    public static String rtTitle_v2 = "core/wcm/components/title/v2/title";
    // list component
    public static String rtList_v1 = "core/wcm/components/list/v1/list";
    public static String rtList_v2 = "core/wcm/components/list/v2/list";
    // image component
    public static String rtImage_v1 = "core/wcm/components/image/v1/image";
    public static String rtImage_v2 = "core/wcm/components/image/v2/image";
    // breadcrumb component
    public static String rtBreadcrumb_v1 = "core/wcm/components/breadcrumb/v1/breadcrumb";
    public static String rtBreadcrumb_v2 = "core/wcm/components/breadcrumb/v2/breadcrumb";
    // button component
    public static String rtButton_v1 = "core/wcm/components/button/v1/button";
    // navigation component
    public static String rtNavigation_v1 = "core/wcm/components/navigation/v1/navigation";
    // language navigation component
    public static String rtLanguageNavigation_v1 = "core/wcm/components/languagenavigation/v1/languagenavigation";
    // search component
    public static String rtSearch_v1 = "core/wcm/components/search/v1/search";
    // teaser component
    public static String rtTeaser_v1 = "core/wcm/components/teaser/v1/teaser";
    // carousel component
    public static String rtCarousel_v1 = "core/wcm/components/carousel/v1/carousel";
    // tabs component
    public static String rtTabs_v1 = "core/wcm/components/tabs/v1/tabs";
    // content fragment component
    public static String rtContentFragment_v1 = "core/wcm/components/contentfragment/v1/contentfragment";
    // content fragment list component
    public static String rtContentFragmentList_v1 = "core/wcm/components/contentfragmentlist/v1/contentfragmentlist";
    // core form container
    public static String rtFormContainer_v1 = "core/wcm/components/form/container/v1/container";
    public static String rtFormContainer_v2 = "core/wcm/components/form/container/v2/container";
    // form button
    public static String rtFormButton_v1 = "core/wcm/components/form/button/v1/button";
    public static String rtFormButton_v2 = "core/wcm/components/form/button/v2/button";
    // form button
    public static String rtFormText_v1 = "core/wcm/components/form/text/v1/text";
    public static String rtFormText_v2 = "core/wcm/components/form/text/v2/text";
    // form option
    public static String rtFormOptions_v1 = "core/wcm/components/form/options/v1/options";
    public static String rtFormOptions_v2 = "core/wcm/components/form/options/v2/options";
    // hidden field
    public static String rtFormHidden_v1 = "core/wcm/components/form/hidden/v1/hidden";
    public static String rtFormHidden_v2 = "core/wcm/components/form/hidden/v2/hidden";



    /**
     * Creates form entity builder
     *
     * @param data map containing data for creating FormEntityBuilder
     *
     */
    private static FormEntityBuilder createFEB(HashMap<String, String> data) {
        FormEntityBuilder feb = FormEntityBuilder.create();
        Iterator hmIterator = data.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            feb.addParameter(mapElement.getKey().toString(), mapElement.getValue().toString());
        }
        return feb;
    }


    /**
     * Create a policy
     *
     * @param client          CQClient
     * @param componentPath   Mandatory. the path to the component policy
     * @param data            Tha policy's data
     * @param policyPath      Policy root path
     * @throws ClientException
     */
    public static String createPolicy(CQClient client, String componentPath, HashMap<String, String> data, String policyPath, int... expectedStatus) throws ClientException {
        String plcyPath = policyPath != null ? policyPath : defaultPolicyPath;
        try {
             return client.doPost(plcyPath + componentPath, createFEB(data).build(), HttpUtils.getExpectedStatus(201, expectedStatus)).getSlingPath();
        } catch (Exception ex) {
            throw new ClientException("Policy creation failed for component " + componentPath + "with error : " + ex, ex);
        }
    }


    /**
     * Assign a policy to a core component
     *
     * @param client                 CQClient
     * @param componentPath          Mandatory. the path to the component policy
     * @param data                   Tha policy's data
     * @param policyAssignmentPath  parent component policy path
     * @throws ClientException
     */

    public static void assignPolicy(CQClient client, String componentPath, HashMap<String, String> data,  String policyAssignmentPath, int... expectedStatus) throws ClientException {
        String plcyAssignmentPath = policyAssignmentPath != null ? policyAssignmentPath : defaultPolicyAssignmentPath;
        try {
            client.doPost(plcyAssignmentPath + componentPath, createFEB(data).build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (Exception ex) {
            throw new ClientException("Policy assignment failed for component " + componentPath, ex);
        }
    }

    /**
     * Deletes a policy.
     *
     * @param client CQClient
     * @param policyPath Mandatory. policyPath path to the policy to be deleted
     * @throws ClientException
     */

    public static void deletePolicy(CQClient client, String componentPath, String policyPath, int... expectedStatus) throws ClientException {
        String plcyPath = policyPath != null ? policyPath : defaultPolicyPath;

        if (componentPath == null) {
            //throw core component exception
        }

        FormEntityBuilder feb = FormEntityBuilder.create().addParameter(":operation", "delete");
        try {
             client.doPost(plcyPath + componentPath, feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (Exception ex) {
            throw new ClientException("Delete policy failed for component " + componentPath + " with error: " + ex, ex);
        }
    }

    public static void deletePolicyAssignment(CQClient client, String componentPath, String policyAssignmentPath, int... expectedStatus) throws ClientException {
        FormEntityBuilder feb = FormEntityBuilder.create().addParameter(":operation","delete");

        try {
            client.doPost(policyAssignmentPath + componentPath, feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (Exception ex) {
            throw new ClientException("Delete policy Assignment failed for component " + componentPath + " with error: " + ex, ex);
        }
    }

    /**
     * Adds a component to a page.
     *
     * @param client            CQClient
     * @param component          mandatory components resource type
     * @param proxyCompPath      mandatory absolute path to the parent component
     * @param title              mandatory the title of the component
     * @param componentGroup     optional the group of the component, if empty, 'Core' is used
     * @throws ClientException
     */

    public static String createProxyComponent(CQClient client, String component, String proxyCompPath,  String title, String componentGroup, int... expectedStatus)
        throws ClientException {

        if (component == null || proxyCompPath == null) {
            //throw core component exception
        }

        // default settings
        if (title == null) {
            title = component.substring(component.lastIndexOf("/") + 1);
        }
        if (componentGroup == null) {
            componentGroup = "Core";
        }
        FormEntityBuilder feb = FormEntityBuilder.create()
            .addParameter("jcr:primaryType", "cq:Component").addParameter("componentGroup", componentGroup).addParameter("jcr:title", title)
            .addParameter("jcr:description", title).addParameter("sling:resourceSuperType", component);

        try {
             return client.doPost(proxyCompPath, feb.build(), HttpUtils.getExpectedStatus(201,  expectedStatus)).getSlingPath();
        } catch (Exception ex) {
            throw new ClientException("Create Proxy failed for component " + component, ex);
        }
    }


    /**
     * Deletes a proxy component.
     *
     * @param client         CQClient
     * @param proxyCompPath  Mandatory. path to the proxy component to be deleted
     * @throws ClientException
     */

    public static void deleteProxyComponent(CQClient client, String proxyCompPath, int... expectedStatus) throws ClientException {
        // mandatory check
        if (proxyPath == null) {
            //throw core component error
        }
        FormEntityBuilder feb = FormEntityBuilder.create().addParameter(":operation", "delete");

        try {
             client.doPost(proxyCompPath, feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (Exception ex) {
            throw new ClientException("Delete  Proxy failed for component " + proxyPath, ex);
        }
    }


    /**
     * Sets properties of a repository node.
     *
     * @param client            CQClient
     * @param componentPath     Mandatory. absolute path to the node
     * @param data              Mandatory. object with properties to be set on the node.
     * @throws ClientException
     */

    public static void editNodeProperties(CQClient client, String componentPath, HashMap<String, String> data, int... expectedStatus) throws ClientException {
        // mandatory check
        if (componentPath == null || data == null) {
            //throw core component error
        }
        try {
            client.doPost( componentPath, createFEB(data).build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (Exception ex) {
            throw new ClientException("Edit properties failed for component " + componentPath, ex);
        }

    }

    /**
     * Adds a component to a page.
     *
     * @param client            CQClient
     * @param component          mandatory components resource type
     * @param parentCompPath     mandatory absolute path to the parent component
     * @param nameHint           optional hint for the component nodes name, if empty component name is taken
     * @param order              optional where to place component e.g. 'before product_grid', if empty, 'last' is used
     * @throws ClientException
     */

    public static String addComponent(CQClient client, String component, String parentCompPath, String nameHint,
        String order, int... expectedStatus) throws ClientException {

        // mandatory check
        if (component == null || parentCompPath == null) {
            //throw core component error
        }
        // default settings
        if (nameHint == null) {
            nameHint = component.substring(component.lastIndexOf("/") + 1);
        }
        if (order == null) {
            order = "last";
        }

        FormEntityBuilder feb = FormEntityBuilder.create()
            .addParameter("./sling:resourceType", component)
            .addParameter("./jcr:created", "").addParameter("./jcr:lastModified", "")
            .addParameter("./cq:lastModified", "").addParameter(":order", order)
            .addParameter("_charset_", "utf-8").addParameter(":nameHint", nameHint);

        try {
            return client.doPost( parentCompPath, feb.build(), HttpUtils.getExpectedStatus(201, expectedStatus)).getSlingPath();
        } catch (Exception ex) {
            throw new ClientException(" failed to add component " + component + " with error: " + ex, ex);
        }
    }

    /**
     * Hide the page in navigation
     *
     * @param client  CQClient
     * @param pagePath path of page to hide
     * @throws ClientException
     */

    public static void hidePage(CQClient client, String pagePath, int... expectedStatus) throws ClientException  {
        FormEntityBuilder feb = FormEntityBuilder.create().addParameter("_charset_", "utf-8")
                                    .addParameter("./jcr:content/hideInNav", "true");
        try {
            client.doPost( pagePath, feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus)).getSlingPath();
        } catch (Exception ex) {
            throw new ClientException(" failed to hide page " + pagePath + " with error: " + ex, ex);
        }
    }

    /**
     * Helper method to assert a resource exist on backend using await as it can take some time the resource is updated on the AEM instance
     *
     * @param client:       The CQClient to be used for the request to the AEM instance
     * @param resourcePath: The path of the resource to test if it exist
     * @param message:      The message for the failed assert
     */
    public static void assertResourceExist(CQClient client, String resourcePath, String message) {
        await().untilAsserted(() -> {
            assertTrue(client.exists(resourcePath), message);
        });
    }

    /**
     * Open configuration of component
     *
     * @param dataPath datapath of the component to open the configuration dialog
     */
    public static void openConfigureDialog(String dataPath) throws InterruptedException {
        openEditableToolbar(dataPath);
        $(Selectors.SELECTOR_CONFIG_BUTTON).click();
        Helpers.waitForElementAnimationFinished($(Selectors.SELECTOR_CONFIG_DIALOG));
    }

    /**
     * Open editabletoolbar of component
     *
     * @param dataPath datapath of the component to open the configuration dialog
     */
    public static void openEditableToolbar(String dataPath) {
        String component = "[data-type='Editable'][data-path='" + dataPath +"']";
        SelenideElement comp = $(component);
        Helpers.waitForElementAnimationFinished(comp);
        comp.shouldBe(Condition.visible);
        int offset =  (comp.getSize().getHeight() * 85) / 200;
        Selenide.actions()
            .moveToElement(comp, 0, offset)
            .click()
            .build()
            .perform();
    }

    /**
     * Save configuration for component
     */

    public static void saveConfigureDialog() throws InterruptedException {
        $(Selectors.SELECTOR_SAVE_CONFIG_BUTTON).click();
        //wait for
        webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    /**
     * Open panel selector
     * @throws  InterruptedException
     */
    public static void openPanelSelect() {
        $(Selectors.SELECTOR_PANEL_SELECT).click();
    }

    /**
     * Check if panelselect is present
     */

    public static boolean isPanelSelectPresent() {
        return $(Selectors.SELECTOR_PANEL_SELECT).isDisplayed();
    }

    /**
     * explicit wait
     *
     * @param delay wait time in ms
     * @throws InterruptedException
     */
    public static void webDriverWait(int delay) throws InterruptedException {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        synchronized (webDriver) {
            webDriver.wait(delay);
        }
    }

    /**
     * switch the context frame
     * @param frame name of the frame
     */
    public static void switchContext(String frame) {
        switchTo().frame(frame);
    }

    /**
     * Switch to default context
     */
    public static void switchToDefaultContext() {
        switchTo().defaultContent();
    }

    /**
     * check if proxy client library present for the component
     *
     * @param client CQClient
     * @param libraryPath component's library path
     * @param expectedStatus
     * @throws ClientException
     */
    public static void checkProxiedClientLibrary(CQClient client,String libraryPath, int... expectedStatus) throws ClientException {
        SlingHttpResponse status = client.doGet("/etc.clientlibs" + libraryPath, HttpUtils.getExpectedStatus(200, expectedStatus));

    }

    public static String creatProxyComponent(CQClient client, String corecomponentPath, String proxyCompoentTitle, String componentName) throws ClientException {
        FormEntityBuilder form = FormEntityBuilder.create()
            .addParameter("./sling:resourceSuperType", corecomponentPath)
            .addParameter("./jcr:title", proxyCompoentTitle)
            .addParameter("./componentGroup", "test.site")
            .addParameter("./jcr:primaryType", "cq:Component");

        SlingHttpResponse exec = client.doPost("/apps/testsite" + RandomStringUtils.randomAlphabetic(DEFAULT_SMALL_SIZE) + "/components/" + componentName, form.build(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
        return exec.getSlingPath();
    }

    /**
     * Selects a value in a Granite UI autocomplete field
     *
     * @param selector {String} Specific selector for the autocomplete (ex. "[name='./myField']")
     * @param value {String} The value to be selected
     *
     * @returns {TestCase} A test case that selects a value in an autocomplete field
     */
    public static void selectInAutocomplete(String selector, String value) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + selector);
        autoCompleteField.sendKeys(value);
        autoCompleteField.suggestions().selectByValue(value);
    }

    /**
     * Get the current browser URL
     * @return current browser URL
     */
    public static String getCurrentUrl() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        return webDriver.getCurrentUrl();
    }

    public static boolean iseditDialogVisible() {
        return $(Selectors.SELECTOR_CONFIG_DIALOG).isDisplayed();
    }

    public static SelenideElement getVisibleElement(ElementsCollection list) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).isDisplayed())
                return list.get(i);
        }
        return null;
    }

    /**
     * Selects a tag in a Granite UI tags autocomplete field
     *
     * @param selector {String} Specific selector for the tags selector (ex. "[name='./tags']")
     * @param value {String} The tag value
     * @returns {TestCase} A test case that selects a tag in a tag selector field
     */
    public static void selectInTags(String selector, String value) throws InterruptedException {
        //AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + selector);
        //autoCompleteField.sendKeys(value);
        //KeyboardShortCuts.keySpace();
        String tagPrefix = "/content/cq:tags";
        String [] path = value.split("/");
        int i;
        String currentPath = tagPrefix;
        $("foundation-autocomplete" + selector).$("button[icon='FolderOpenOutline']").click();
        for(i = 0; i < path.length - 1; i++) {
            currentPath = currentPath + "/" + path[i];
            $("[data-foundation-collection-item-id='" + currentPath + "']").click();
            Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        }
        currentPath = currentPath + "/"  + path[i];
        $("[data-foundation-collection-item-id='" + currentPath + "']").$("coral-checkbox").click();
        $("button.granite-pickerdialog-submit[is='coral-button']").click();
    }


    public static void useDialogSelect(String name, String value) {
        CoralSelect selectList = new CoralSelect("name='" +name+ "'");
        CoralSelectList list = selectList.openSelectList();
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + value + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        list.selectByValue(value);
    }

}
