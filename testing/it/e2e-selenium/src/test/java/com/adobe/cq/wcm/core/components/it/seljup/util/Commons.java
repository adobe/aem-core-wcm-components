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
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InlineEditor;
import com.adobe.cq.testing.selenium.pagewidgets.sidepanel.SidePanel;
import com.adobe.cq.testing.selenium.utils.ElementUtils;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.constant.Selectors;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.apache.sling.testing.clients.util.HttpUtils;
import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
    // the template defined in the test content package
    public static String template = "/conf/core-components/settings/wcm/templates/core-components";
    // core component resource types
    // accordion component
    public static String  rtAccordion_v1 = "core/wcm/components/accordion/v1/accordion";
    // embed component
    public static String rtEmbed_v1 = "core/wcm/components/embed/v1/embed";
    public static String rtEmbed_v2 = "core/wcm/components/embed/v2/embed";
    // text component
    public static String  rtText_v1 = "core/wcm/components/text/v1/text";
    public static String  rtText_v2 = "core/wcm/components/text/v2/text";
    // title component
    public static String rtTitle_v1 = "core/wcm/components/title/v1/title";
    public static String rtTitle_v2 = "core/wcm/components/title/v2/title";
    public static String rtTitle_v3 = "core/wcm/components/title/v3/title";
    // list component
    public static String rtList_v1 = "core/wcm/components/list/v1/list";
    public static String rtList_v2 = "core/wcm/components/list/v2/list";
    public static String rtList_v3 = "core/wcm/components/list/v3/list";
    // image component
    public static String rtImage_v1 = "core/wcm/components/image/v1/image";
    public static String rtImage_v2 = "core/wcm/components/image/v2/image";
    public static String rtImage_v3 = "core/wcm/components/image/v3/image";
    // breadcrumb component
    public static String rtBreadcrumb_v1 = "core/wcm/components/breadcrumb/v1/breadcrumb";
    public static String rtBreadcrumb_v2 = "core/wcm/components/breadcrumb/v2/breadcrumb";
    public static String rtBreadcrumb_v3 = "core/wcm/components/breadcrumb/v3/breadcrumb";
    // button component
    public static String rtButton_v1 = "core/wcm/components/button/v1/button";
    public static String rtButton_v2 = "core/wcm/components/button/v2/button";
    // navigation component
    public static String rtNavigation_v1 = "core/wcm/components/navigation/v1/navigation";
    public static String rtNavigation_v2 = "core/wcm/components/navigation/v2/navigation";
    // language navigation component
    public static String rtLanguageNavigation_v1 = "core/wcm/components/languagenavigation/v1/languagenavigation";
    public static String rtLanguageNavigation_v2 = "core/wcm/components/languagenavigation/v2/languagenavigation";
    // search component
    public static String rtSearch_v1 = "core/wcm/components/search/v1/search";
    // teaser component
    public static String rtTeaser_v1 = "core/wcm/components/teaser/v1/teaser";
    public static String rtTeaser_v2 = "core/wcm/components/teaser/v2/teaser";
    // carousel component
    public static String rtCarousel_v1 = "core/wcm/components/carousel/v1/carousel";
    // tabs component
    public static String rtTabs_v1 = "core/wcm/components/tabs/v1/tabs";
    // video component
    public static String rtVideo_v1 = "core/wcm/components/video/v1/video";
    // content fragment component
    public static String rtContentFragment_v1 = "core/wcm/components/contentfragment/v1/contentfragment";
    // content fragment list component
    public static String rtContentFragmentList_v1 = "core/wcm/components/contentfragmentlist/v1/contentfragmentlist";
    public static String rtContentFragmentList_v2 = "core/wcm/components/contentfragmentlist/v2/contentfragmentlist";
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

    public static void selectInPicker(String prefix, String selector, String value) throws  InterruptedException {
        if(value.startsWith("/")) {
            value = value.substring(1);
        }
        String[] path = value.split("/");
        int i;
        String currentPath = prefix;
        $("foundation-autocomplete" + selector).$("button").click();
        for(i = 0; i < path.length - 1; i++) {
            currentPath = currentPath + "/" + path[i];
            $("[data-foundation-collection-item-id='" + currentPath + "']").click();
            Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        }
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        currentPath = currentPath + "/"  + path[i];
        if($("[data-foundation-collection-item-id='" + currentPath + "']").$("coral-checkbox").isDisplayed()) {
            ElementUtils.clickableClick($("[data-foundation-collection-item-id='" + currentPath + "']").$("coral-checkbox"));
        }
        else {
            $("[data-foundation-collection-item-id='" + currentPath + "']").$("coral-icon").click();
        }
        $("button.granite-pickerdialog-submit[is='coral-button']").click();
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
        } catch (ClientException ex) {
            throw new ClientException("Policy creation failed for component " + componentPath + "with error : " + ex, ex);
        }
    }

    /**
     * Create a policy
     *
     * @param client          CQClient
     * @param componentPath   Mandatory. the path to the component policy
     * @param props           Tha policy's properties
     * @param policyPath      Policy root path
     * @throws ClientException
     */
    public static String createPolicy(CQClient client, String componentPath, List<NameValuePair> props, String policyPath, int... expectedStatus) throws ClientException {
        String plcyPath = policyPath != null ? policyPath : defaultPolicyPath;
        UrlEncodedFormEntity formEntry = FormEntityBuilder.create().addAllParameters(props).build();
        try {
            return client.doPost(plcyPath + componentPath, formEntry, HttpUtils.getExpectedStatus(201, expectedStatus)).getSlingPath();
        } catch (ClientException ex) {
            throw new ClientException("Policy creation failed for component " + componentPath + "with error : " + ex, ex);
        }
    }

    /**
     * Create page
     *
     * @param client
     * @param templatePath
     * @param parentPath
     * @param pageName
     * @param pageTitle
     * @param pageRT
     * @param description
     * @param expectedStatus
     * @return
     * @throws ClientException
     */
    public static String createPage(CQClient client, String templatePath, String parentPath, String pageName, String pageTitle,String pageRT, String description, int... expectedStatus) throws ClientException {
        if(pageRT == null || pageRT.isEmpty()) {
            pageRT = "core/wcm/tests/components/test-page";
        }
        FormEntityBuilder feb = FormEntityBuilder.create();
        feb.addParameter("template", templatePath);
        feb.addParameter("parentPath", parentPath);
        feb.addParameter("_charset_", "utf-8");
        feb.addParameter("./jcr:title", pageTitle);
        feb.addParameter("pageName", pageName);
        feb.addParameter("./sling:resourceType", pageRT);
        feb.addParameter("./jcr:description", description);

        try {
            return client.doPost("/libs/wcm/core/content/sites/createpagewizard/_jcr_content", feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus)).getSlingPath();
        } catch(ClientException ex) {
            throw new ClientException("Unable to create new page " + pageName + " at " + parentPath + " with error : " + ex, ex);
        }
    }

    public static String createLiveCopy(CQClient client, String srcPath, String destPath, String title, String label, int... expectedStatus) throws ClientException {
        FormEntityBuilder feb = FormEntityBuilder.create();

        feb.addParameter("cmd", "createLiveCopy");
        feb.addParameter("srcPath", srcPath);
        feb.addParameter("destPath", destPath);
        feb.addParameter("_charset_", "utf-8");
        feb.addParameter("title", title);
        feb.addParameter("label", label);

        try {
            return client.doPost("/bin/wcmcommand", feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus)).getSlingPath();
        } catch(ClientException ex) {
            throw new ClientException("Unable to create livecopy of " + srcPath + " at " + destPath + " with error : " + ex, ex);
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
        } catch (ClientException ex) {
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
        } catch (ClientException ex) {
            throw new ClientException("Delete policy failed for component " + componentPath + " with error: " + ex, ex);
        }
    }

    public static void deletePolicyAssignment(CQClient client, String componentPath, String policyAssignmentPath, int... expectedStatus) throws ClientException {
        FormEntityBuilder feb = FormEntityBuilder.create().addParameter(":operation","delete");

        try {
            client.doPost(policyAssignmentPath + componentPath, feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (ClientException ex) {
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
        } catch (ClientException ex) {
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
        } catch (ClientException ex) {
            throw new ClientException("Delete  Proxy failed for component " + proxyPath, ex);
        }
    }


    /**
     * Sets properties of a repository node.
     *
     * @param client            CQClient
     * @param path     Mandatory. absolute path to the node
     * @param data              Mandatory. object with properties to be set on the node.
     * @throws ClientException
     */

    public static void editNodeProperties(CQClient client, String path, HashMap<String, String> data, int... expectedStatus) throws ClientException {
        // mandatory check
        if (path == null || data == null) {
            //throw core component error
        }
        try {
            client.doPost( path, createFEB(data).build(), HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (ClientException ex) {
            throw new ClientException("Edit properties failed for path " + path, ex);
        }
    }

    public static void setPageProperties(CQClient client, String pagePath, List<NameValuePair> props, int... expectedStatus) throws ClientException {
        try {
            client.setPageProperties(pagePath, props, HttpUtils.getExpectedStatus(200, expectedStatus));
        } catch (ClientException ex) {
            throw new ClientException("Set page properties failed for path " + pagePath, ex);
        }
    }

    public static void setTagsToPage(CQClient client, String pagePath, String[] tags, int... expectedStatus) throws ClientException {
        java.util.List<NameValuePair> props = new ArrayList();
        for(int i = 0; i < tags.length; i++) {
            props.add(new BasicNameValuePair("./cq:tags",tags[i]));
        }
        if(tags.length > 1) {
            props.add(new BasicNameValuePair("./cq:tags@TypeHint", "String[]"));
        }
        setPageProperties(client, pagePath, props, 200);
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
     * Open editabletoolbar of component
     *
     * @param dataPath datapath of the component to open the configuration dialog
     */
    public static void openEditableToolbar(String dataPath) {
        String component = "[data-type='Editable'][data-path='" + dataPath +"']";
        SelenideElement comp = $(component);
        Helpers.waitForElementAnimationFinished(comp);
        comp.shouldBe(Condition.visible);
        int offset =  (comp.getSize().getHeight() * 90) / 200;
        Selenide.actions()
            .moveToElement(comp, 0, offset)
            .click()
            .build()
            .perform();
    }

    public static void openEditDialog(EditorPage editorPage, String compPath) throws TimeoutException, InterruptedException {
        String component = "[data-type='Editable'][data-path='" + compPath +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, CoreComponentConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);
        editableToolbar.clickConfigure();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
    }


    /**
     * Opens the inline editor for a component.
     * @param editorPage
     * @param compPath
     * @return
     * @throws TimeoutException
     */
    public static InlineEditor openInlineEditor(EditorPage editorPage, String compPath) throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + compPath +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, CoreComponentConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);
        return editableToolbar.clickEdit();
    }

    /**
     * Closes any previously opened inline editor by clicking on the save button
     */
    public static void saveInlineEditor() {
        $(Selectors.SELECTOR_SAVE_BUTTON).click();
    }

    /**
     * Save configuration for component
     */

    public static void saveConfigureDialog() throws InterruptedException {
        $(Selectors.SELECTOR_DONE_CONFIG_BUTTON).click();
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
        try {
            SlingHttpResponse exec = client.doPost("/apps/testsite" + RandomStringUtils.randomAlphabetic(DEFAULT_SMALL_SIZE) + "/components/" + componentName, form.build(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
            return exec.getSlingPath();
        }catch (Exception ex) {
            throw new ClientException(" failed to create proxy component for " + corecomponentPath + " component with error: " + ex, ex);
        }
    }

    /**
     * Adds a tag in default namespace
     *
     * @param client CQClient
     * @param tag   Mandatory. the tag to be added
     * @throws ClientException
     */
    public static String addTag(CQClient client, String tag) throws ClientException {
        FormEntityBuilder form = FormEntityBuilder.create()
            .addParameter("cmd", "createTagByTitle")
            .addParameter("tag", tag)
            .addParameter("locale", "en")
            .addParameter("_charset_", "utf-8");
        try {
            return client.doPost("/bin/tagcommand" , form.build(), HttpStatus.SC_OK, HttpStatus.SC_CREATED).getSlingPath();
        } catch (Exception ex) {
            throw new ClientException(" failed to add tag " + tag + " with error: " + ex, ex);
        }
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
        String tagPrefix = "/content/cq:tags";
        selectInPicker(tagPrefix, selector, value);
    }

    /**
     * Selects a asset in a Granite UI autocomplete field
     *
     * @param selector {String} Specific selector for the tags selector (ex. "[name='./tags']")
     * @param value {String} The tag value
     * @returns {TestCase} A test case that selects a tag in a tag selector field
     */
    public static void selectInDam(String selector, String value) throws InterruptedException {
        String tagPrefix = "/content/dam";
        selectInPicker(tagPrefix, selector, value);
    }


    public static void useDialogSelect(String name, String value) throws InterruptedException {
        $( "[name='" + name + "'] > button").click();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        CoralSelectList coralSelectList = new CoralSelectList($("[name='" + name + "']"));
        if(!coralSelectList.isVisible()) {
            CoralSelect selectList = new CoralSelect("name='" + name + "'");
            coralSelectList = selectList.openSelectList();
        }

        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + value + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        coralSelectList.selectByValue(value);
    }

    public static void openSidePanel() {
        SidePanel sidePanel = new SidePanel();
        if(sidePanel.isHidden()) {
            sidePanel.show();
        }
    }

    public static void closeSidePanel() {
        SidePanel sidePanel = new SidePanel();
        if(sidePanel.isShown()) {
            sidePanel.hide();
        }
    }

    public static boolean isComponentPresentInInsertDialog(String component) {
        return $("coral-selectlist-item[value='" + component + "']").isDisplayed();
    }

    public static void makeInlineEditorEditable() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        ((JavascriptExecutor) webDriver).executeScript("document.getElementsByName('actionUpdate')[0].style.display='inline'");
    }
}
