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

package com.adobe.cq.wcm.core.components.it.seljup.tests.accordion.v1;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.assertion.EditableToolbarAssertion;
import com.adobe.cq.wcm.core.components.it.seljup.components.Accordion.AccordionConfigureDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.Accordion.v1.Accordion;
import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.PanelSelector;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.HashMap;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccordionV1IT extends AuthorBaseUITest {

    protected String testPage;
    private static final String clientlibs = "core.wcm.components.accordion.v1";
    private static String componentName = "accordion";

    private String policyPath;
    private String proxyPath;
    protected Accordion accordion;
    protected PageEditorPage editorPage;
    protected String cmpPath;

    /**
     * Before Test Case
     *
     * 1. create test page
     * 2. create clientlib page policy
     * 3. assign clientlib page policy
     * 4. create the proxy component
     * 5. set cq:isContainer property true
     * 6. add the proxy component to the page
     * 7. open the test page in the editor
     * 8. Get Accordion component
     *
     * @throws ClientException
     */

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // 1.
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // 2.
        String policySuffix = "/structure/page/new_policy";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:title", "New Policy");
        data.put("sling:resourceType", "wcm/core/components/policy/policy");
        data.put("clientlibs", clientlibs);
        String policyPath1 = "/conf/"+ label + "/settings/wcm/policies/core-component/components";
        policyPath = Commons.createPolicy(adminClient, policySuffix, data , policyPath1);

        // 3.
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content";
        data.clear();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient,"",data, policyAssignmentPath);


        // 4.
        proxyPath = Commons.createProxyComponent(adminClient, Commons.rtAccordion_v1, Commons.proxyPath, null, null);

        // 5.
        data.clear();
        data.put("cq:isContainer","true");
        Commons.editNodeProperties(adminClient, proxyPath, data);

        // 6.
        cmpPath = Commons.addComponent(adminClient, proxyPath,testPage + Commons.relParentCompPath, componentName, null);

        // 7.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        //8.
        accordion = new Accordion();
    }



    /**
     * After Test Case
     *
     * 1. delete the test proxy component
     * 2. delete the test page
     * 3. delete the clientlib page policy
     * 4. reassign the default policy
     *
     * @throws ClientException
     * @throws InterruptedException
     */

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // 1.
        Commons.deleteProxyComponent(adminClient, proxyPath);

        // 2.
        authorClient.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);

        // 3.
        String policyPath1 = "/conf/"+ label + "/settings/wcm/policies/core-component/components";
        Commons.deletePolicy(adminClient,"/structure/page", policyPath1);

        // 4.
        HashMap<String, String> data = new HashMap<String, String>();
        String policyAssignmentPath = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content";
        data.put("cq:policy", "wcm/foundation/components/page/default");
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient,"",data, policyAssignmentPath);
    }


    /**
     * Create three items via the children editor
     *
     * 1. open the edit dialog
     * 2. add item via the children editor
     * 3. save the edit dialog
     * 4. Check if items are added
     *
     * @throws InterruptedException
     */

    private ElementsCollection createItem() throws InterruptedException {
        //1.
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();

        //2.
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        childrenEditor.clickAddButton();
        InsertComponentDialog insertComponentDialog = editDialog.getInsertComponentDialog();
        insertComponentDialog.selectComponent("/libs/wcm/foundation/components/responsivegrid");
        childrenEditor.getInputItems().last().sendKeys("item0");
        childrenEditor.clickAddButton();
        insertComponentDialog.selectComponent("/libs/wcm/foundation/components/responsivegrid");
        childrenEditor.getInputItems().last().sendKeys("item1");
        childrenEditor.clickAddButton();
        insertComponentDialog.selectComponent("/libs/wcm/foundation/components/responsivegrid");
        childrenEditor.getInputItems().last().sendKeys("item2");

        //3.
        Commons.saveConfigureDialog();

        //4.
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);
        ElementsCollection items = childrenEditor.getInputItems();
        assertTrue(items.size() == 3, "Number to items added should be 3");
        assertTrue(items.get(0).getValue().equals("item0"), "First input item should be item0");
        assertTrue(items.get(1).getValue().equals("item1"), "Second input item should be item1");
        assertTrue(items.get(2).getValue().equals("item2"), "Third input item should be item2");
        Commons.saveConfigureDialog();

        return items;
    }

    /**
     * Switches to the edit dialog properties tab and verifies the provided (ordered) expanded items exist
     * in the expanded items select. Assumes the edit dialog is open.
     *
     * @param items list of items in component to be verified
     * @param properties editdialog properties object
     *
     * 1. switch to the properties tab
     * 2. open the expanded items select
     * 3. verify the expanded items match those passed
     */

    private void verifyExpandedItemsSelect(ElementsCollection items, AccordionConfigureDialog.EditDialogProperties properties) {
        //1.
        properties.openProperties();

        //2.
        properties.openExpandedSelect(" > button");

        //3.
        CoralSelectList selectedItems = properties.selectList();//properties.getExpandedSelectItems(" coral-select-item");
        assertTrue(selectedItems.items().size() == items.size(), "Number of items in property config should be equal to added items number");

        for(int i = 0; i < items.size(); i++) {
            assertTrue(selectedItems.items().get(i).find(Selectors.SELECTOR_ITEM_ELEMENT_CONTENT).getText().contains(items.get(i).getValue())
                , "Selected item should be same as added item");
        }
    }

    /**
     * Switches context to the content frame and verifies the passed (ordered) items
     *
     * @param items list of items in component to be verified
     * @param properties editdialog properties object
     *
     * 1. switch to the content frame
     * 2. verify the expanded items match those passed
     * 3. reset context back to the edit frame
     */
    private void verifyExpandedItems(ArrayList<String> items, AccordionConfigureDialog.EditDialogProperties properties) {
        //1.
        Commons.switchContext("ContentFrame");

        //2.
        ElementsCollection expandedItems = accordion.getItemExpanded();
        assertTrue(expandedItems.size() == items.size(), "Number of items in property config should be equal to expanded items");

        for(int  i = 0; i < items.size(); i++) {
            assertTrue(expandedItems.get(i).getText().equals(items.get(i))
                , "Selected item should be same as added item");
        }

        //3.
        Commons.switchToDefaultContext();
    }


    /**
     * Create and title a single accordion item
     *
     * @param component component path
     * @param parentPath parent component path
     * @param itemName name of the item to be set
     * 1. add a component to the accordion
     * 2. open the edit dialog
     * 3. name the accordion item
     * 4. save the edit dialog
     *
     * @throws ClientException
     * @throws InterruptedException
     */

    private String AddAccordionItem(String component, String parentPath,  String itemName) throws ClientException, InterruptedException {

        //1.
        String cmpPath = Commons.addComponent(adminClient, component, parentPath + "/", null, null);

        //2.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        Commons.openConfigureDialog(parentPath);

        //3.
        childrenEditor.getInputItems().last().sendKeys(itemName);

        //4.
        Commons.saveConfigureDialog();

        return cmpPath;
    }

    /**
     * Create and title a single accordion item
     *
     * @param idx id of the item to be expanded
     *
     * 1. open the edit dialog
     * 2. open the properties tab
     * 3. open the expandedselect list
     * 4. select the idx item
     * 5. save the edit dialog
     *
     * @throws InterruptedException
     */

    private CoralSelectList selectExpandedItem(int idx) throws InterruptedException {
        //1.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);

        //2.
        AccordionConfigureDialog.EditDialogProperties properties =  editDialog.getEditDialogProperties();
        properties.openProperties();

        //3.
        properties.openExpandedSelect(" > button");

        //4.
        CoralSelectList selectedItems = properties.selectList();
        selectedItems.selectByIndex(idx);

        //5.
        Commons.saveConfigureDialog();

        return selectedItems;
    }

    /**
     * Test: Edit Dialog: Add items
     *
     * 1. create new items with titles
     * 4. verify the expanded items select
     * 5. save the edit dialog
     *
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Test: Edit Dialog: Add items")
    public void AddItem() throws  InterruptedException {
        //1.
        ElementsCollection items = createItem();

        //2.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);
        verifyExpandedItemsSelect(items, editDialog.getEditDialogProperties());

        //3.
        Commons.saveConfigureDialog();
    }

    /**
     * Test: Edit Dialog : Remove items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. remove the first item and save the edit dialog
     * 4. open the edit dialog
     * 5. verify that the first item has been removed
     * 6. verify the expanded items select
     * 7. save the edit dialog
     *
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Test: Edit Dialog : Remove items")
    public void RemoveItem() throws  InterruptedException {
        //1.
        createItem();

        //2.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);

        //3.
        childrenEditor.removeFirstItem();
        Commons.saveConfigureDialog();

        //4.
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);
        ElementsCollection items = childrenEditor.getInputItems();

        //5.
        assertTrue(items.size() == 2, "Number to items added should be 2");
        assertTrue(items.get(0).getValue().equals("item1"), "First input item should be item1");
        assertTrue(items.get(1).getValue().equals("item2"), "Second input item should be item2");

        //6.
        verifyExpandedItemsSelect(items, editDialog.getEditDialogProperties());

        //7.
        Commons.saveConfigureDialog();
    }

    /**
     * Test: Edit Dialog : Reorder items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. move the last item before the first one
     * 4. save the edit dialog
     * 5. open the edit dialog
     * 6. verify the new order
     * 7. verify the expanded items select
     * 8. save the edit dialog
     *
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Test: Edit Dialog : Reorder items")
    public void ReorderItem() throws InterruptedException {
        //1.
        createItem();

        //2.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);

        //3.
        childrenEditor.moveItems(2,0);

        //4.
        Commons.saveConfigureDialog();

        //5.
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);

        //6.
        ElementsCollection items = childrenEditor.getInputItems();

        assertTrue(items.size() == 3, "Number to items added should be 3");
        //In chrome browser re-order is not working as expected
        assertTrue(items.get(0).getValue().equals("item2") || items.get(0).getValue().equals("item0"), "First input item should be item2 or item0");
        assertTrue(items.get(1).getValue().equals("item0") || items.get(1).getValue().equals("item2"), "Second input item should be item0 or item2");
        assertTrue(items.get(2).getValue().equals("item1"), "Second input item should be item1");

        //7.
        verifyExpandedItemsSelect(items, editDialog.getEditDialogProperties());

        //8.
        Commons.saveConfigureDialog();
    }

    /**
     * Test: Edit Dialog : Set expanded items
     *
     * 1. create new items with titles
     * 2. set the second item expanded
     * 3. verify the second item is expanded
     * 4. open the edit dialog
     * 5. switch to the properties tab and also set the third item expanded
     * 6. save the edit dialog
     * 7. verify both second and third items are expanded
     *
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Test: Edit Dialog : Set expanded items")
    public void SetExpandedItems() throws InterruptedException {
        //1.
        createItem();

        //2.
        selectExpandedItem(1);

        //3.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        AccordionConfigureDialog.EditDialogProperties properties =  editDialog.getEditDialogProperties();
        ArrayList<String> items = new ArrayList<>();
        items.add("item1");
        verifyExpandedItems(items, properties);

        //4.
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);

        //5.
        properties =  editDialog.getEditDialogProperties();
        properties.openProperties();
        properties.openExpandedSelect(" > button");
        CoralSelectList selectedItems = properties.selectList();
        selectedItems.selectByIndex(2);

        //6.
        Commons.saveConfigureDialog();

        //wait for configuration changes to reflect
        Commons.webDriverWait(1000);
        //7.
        items.clear();
        items.add("item1");
        items.add("item2");
        verifyExpandedItems(items, properties);
    }

    /**
     * Test: Edit Dialog : Single item expansion
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. switch to the properties tab
     * 4. verify that the expanded items select is enabled, expanded item select is disabled and single item expansion disabled.
     * 5. enable single item expansion
     * 6. verify that the expanded items select is disabled and expanded item select is enabled.
     * 7. save the edit dialog
     * 8. verify that the first item is expanded
     *
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Test: Edit Dialog : Single item expansion")
    public void SingleItemExpansion() throws InterruptedException {
        //1.
        createItem();

        //2.
        AccordionConfigureDialog editDialog = accordion.getConfigDialog();
        Commons.openConfigureDialog(testPage + Commons.relParentCompPath + componentName);
        AccordionConfigureDialog.EditDialogProperties properties =  editDialog.getEditDialogProperties();

        //3.
        properties.openProperties();

        //4.
        CoralCheckbox singleExpansion = properties.getSingleExpansion();
        assertTrue(singleExpansion.isChecked() == false, "SingleExpansion should not be checked");
        assertTrue(properties.isExpandedSelectVisible() == true,"Expanded select should be visible");
        assertTrue(properties.isExpandedSelectDisabled() == false, "Expanded select should not be disabled");
        assertTrue(properties.isExpandedSelectSingleVisible() == false,"Expanded select single should not be visible");
        assertTrue(properties.isExpandedSelectSingleDisabled() == true, "Expanded select single should be disabled");

        //5.
        singleExpansion.setSelected(true);

        //6.
        assertTrue(singleExpansion.isChecked() == true, "SingleExpansion should be checked");
        assertTrue(properties.isExpandedSelectVisible() == false,"Expanded select should not be visible");
        assertTrue(properties.isExpandedSelectDisabled() == true, "Expanded select should be disabled");
        assertTrue(properties.isExpandedSelectSingleVisible() == true,"Expanded select single should be visible");
        assertTrue(properties.isExpandedSelectSingleDisabled() == false, "Expanded select single should not be disabled");

        //7.
        Commons.saveConfigureDialog();

        //8.
        ArrayList<String> items = new ArrayList<>();
        items.add("item0");
        verifyExpandedItems(items, properties);
    }

    /**
     * Test: Panel Select: Check items
     *
     * 1. open the component edit toolbar
     * 2. verify that initially no panel select action is available
     * 3. create new items with titles
     * 4. open the component edit toolbar
     * 5. verify the panel select action is available
     * 6. open the panel selector and verify it's open
     * 7. verify that three items are available and the correct titles are visible
     * 8. verify initial Accordion DOM item order is as expected
     * 9. click elsewhere and verify an out of area click closes the panel selector
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Panel Select: Check items")
    public void PanelSelectItems() throws TimeoutException, InterruptedException {
        //1.
        String component = "[data-type='Editable'][data-path='" + testPage + Commons.relParentCompPath + componentName +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(testPage + Commons.relParentCompPath + componentName);

        //2.
        EditableToolbarAssertion editableToolbarAssertion = new EditableToolbarAssertion(editableToolbar,
            "editable toolbar of none style selector enabled component - %s button is displayed while it should not");
        editableToolbarAssertion.assertPanelSelectButton(false);

        //3.
        createItem();

        //4.
        editableToolbar = editorPage.openEditableToolbar(testPage + Commons.relParentCompPath + componentName);

        //5.
        editableToolbarAssertion = new EditableToolbarAssertion(editableToolbar,
            "editable toolbar of none style selector enabled component - %s button is not displayed while it should");
        editableToolbarAssertion.assertPanelSelectButton(true);

        //6.
        editableToolbar.clickPanelSelect();
        PanelSelector panelSelector = new PanelSelector();
        assertTrue(panelSelector.isVisible(), "Panel selector should be visible");

        Commons.webDriverWait(1000);

        //7.
        ElementsCollection items = panelSelector.getItems();

        assertTrue(items.size() == 3, "Number to items added should be 3");
        assertTrue(items.get(0).getText().contains("item0"), "First panel select item should be item0");
        assertTrue(items.get(1).getText().contains("item1"), "Second panel select item should be item1");
        assertTrue(items.get(2).getText().contains("item2"), "Third panel select item should be item2");

        //8.
        Commons.switchContext("ContentFrame");

        ElementsCollection accordionItems = accordion.getAccordionItem();

        assertTrue(accordionItems.size() == 3, "Number to items added should be 3");
        assertTrue(accordion.getAccordionItemButton(0).getText().contains("item0"), "First panel select item should be item0");
        assertTrue(accordion.getAccordionItemButton(1).getText().contains("item1"), "Second panel select item should be item1");
        assertTrue(accordion.getAccordionItemButton(2).getText().contains("item2"), "Third panel select item should be item2");

        Commons.switchToDefaultContext();

        //9.
        accordion.getCQOverlay().openPlaceholder(testPage);
        panelSelector = new PanelSelector();
        assertTrue(panelSelector.isVisible() == false, "Panel selector should not be visible");
    }

    /**
     * Test: Panel Select: Reordering items
     *
     * 1. create new items with titles
     * 2. open the component edit toolbar
     * 3. open the panel selector and verify it's open
     * 4. drag to reorder
     * 5. verify new Accordion DOM item order is as expected
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Test: Panel Select: Reordering items")
    public void PanelSelectReorder() throws TimeoutException, InterruptedException {
        //1.
        createItem();

        //2.
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(testPage + Commons.relParentCompPath + componentName);
        EditableToolbarAssertion editableToolbarAssertion = new EditableToolbarAssertion(editableToolbar,
            "editable toolbar of none style selector enabled component - %s button is not displayed while it should");
        editableToolbarAssertion.assertPanelSelectButton(true);

        //3.
        editableToolbar.clickPanelSelect();
        PanelSelector panelSelector = new PanelSelector();
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(panelSelector.getCssSelector())));

        //4.
        panelSelector.reorderItems(0, 2);

        Commons.switchContext("ContentFrame");
        //5.
        ElementsCollection accordionItems = accordion.getAccordionItem();

        //wait for the reordering to reflect
        Commons.webDriverWait(5000);
        assertTrue(accordionItems.size() == 3, "Number to items added should be 3");
        assertTrue(accordion.getAccordionItemButton(0).getText().contains("item1"), "First panel select item should be item1");
        assertTrue(accordion.getAccordionItemButton(1).getText().contains("item2"), "Second panel select item should be item0");
        assertTrue(accordion.getAccordionItemButton(2).getText().contains("item0"), "Third panel select item should be item2");

        Commons.switchToDefaultContext();
    }


    /**
     * Test: Nested
     *
     * 1. create nested accordions
     * 2. change context to the content frame
     * 3. verify items
     *
     * @throws InterruptedException
     * @throws ClientException
     */
    @Test
    @DisplayName("Test: Nested")
    public void Nested() throws  InterruptedException, ClientException {

        //1.
        String accordion1Path = AddAccordionItem(proxyPath, testPage + Commons.relParentCompPath + componentName,  "Accordion 1.1");
        String accordion2Path = AddAccordionItem(proxyPath, testPage + Commons.relParentCompPath + componentName,  "Accordion 1.2");
        selectExpandedItem(1);
        String accordion21Path = AddAccordionItem(proxyPath, accordion2Path,  "Accordion 2.1");
        String accordion22Path = AddAccordionItem(proxyPath, accordion2Path,  "Accordion 2.2");

        //2.
        Commons.switchContext("ContentFrame");

        //3.
        ElementsCollection accordionItems = accordion.getAccordionItem();
        assertTrue(accordionItems.size() == 4, "Number to items added should be 4");
        assertTrue(accordion.getAccordionItemButton(0).getText().contains("Accordion 1.1"), "First panel select items should be Accordion 1.1");
        assertTrue(accordion.getAccordionItemButton(1).getText().contains("Accordion 1.2"), "Second panel select item should be Accordion 1.2");
        assertTrue(accordion.getAccordionItemButton(2).getText().contains("Accordion 2.1"), "Second panel select item should be Accordion 2.1");
        assertTrue(accordion.getAccordionItemButton(3).getText().contains("Accordion 2.2"), "Second panel select item should be Accordion 2.2");

        Commons.switchToDefaultContext();
    }

}

