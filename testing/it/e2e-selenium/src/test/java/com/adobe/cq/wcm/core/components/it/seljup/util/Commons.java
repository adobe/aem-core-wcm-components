/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.apache.sling.testing.clients.util.HttpUtils;
import com.adobe.qe.selenium.pagewidgets.Helpers;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

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

    private static String doneButton = "button[is='coral-button'][title='Done']";

    private static String configButton = "button[data-action='CONFIGURE']";

    public static final String SELECTOR_ITEM_ELEMENT_CONTENT = "coral-selectlist-item-content";

    public static final String selConfigDialog = ".cq-dialog.foundation-form.foundation-layout-form";

    public static final String panelSelect = ".cq-editable-action[data-action='PANEL_SELECT']";

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
     * Creates a CQ page via POST request, the same as send by the create page wizard.
     *
     * @param client CQClient
     * @param pageLabel Mandatory. Page label to be set for the page.
     * @param pageTitle Mandatory. Page title to be set for the page.
     * @param templatePath Mandatory. Path to the template e.g. "/conf/coretest/settings/wcm/templates/content-page"
     * @param parentPath Mandatory. Path to the parent page e.g. "/content/coretest/language-masters/en"
     * @param testPageRT the resource type of the test page
     * @param expectedStatus expected http status code
     * @throws ClientException
     */
    public static String createPage(CQClient client, String pageLabel, String pageTitle, String parentPath, String templatePath, String testPageRT, int... expectedStatus) throws ClientException {
        String pageResType = testPageRT != null ? testPageRT : "core/wcm/tests/components/test-page";
        FormEntityBuilder feb = FormEntityBuilder.create().addParameter("cmd", "createPage").addParameter("parentPath", parentPath)
            .addParameter("pageName", pageLabel).addParameter("title", pageTitle).addParameter("template", templatePath)
            .addParameter("./sling:resourceType",pageResType).addParameter("_charset_", "utf-8").addParameter("./jcr:description", "");
        return client.doPost("/libs/wcm/core/content/sites/createpagewizard/_jcr_content", feb.build(), HttpUtils.getExpectedStatus(200, expectedStatus)).getSlingPath();
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
     * @throws InterruptedException
     */
    public static void openConfigureDialog(String dataPath) throws InterruptedException {
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
        $("button[data-action='CONFIGURE']").click();
        Helpers.waitForElementAnimationFinished($(selConfigDialog));
    }

    /**
     * Open editabletoolbar of component
     *
     * @param dataPath datapath of the component to open the configuration dialog
     * @throws InterruptedException
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

    public static void saveConfigureDialog() {
        $(doneButton).click();
    }

    /**
     * Open panel selector
     * @throws  InterruptedException
     */
    public static void openPanelSelect() throws InterruptedException {
        $(panelSelect).click();
    }

    /**
     * Check if panelselect is present
     */

    public static boolean isPanelSelectPresent() {
        return $(panelSelect).isDisplayed();
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
}
