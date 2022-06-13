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

package com.adobe.cq.wcm.core.components.it.seljup.tests.title.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.title.TitleEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.*;

@Tag("group3")
public class TitleIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.title.v1.TitleIT {

    public void setupResources() {
        clientlibs = Commons.CLIENTLIBS_TITLE_V2;
        titleRT = Commons.RT_TITLE_V2;
    }

    /**
     * Test: set link on title
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: set link on title")
    public void testSetLink() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, cmpPath);
        title.getEditDialog().setLinkURL(redirectPage);
        Commons.saveConfigureDialog();

        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        title.clickLink();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchToDefaultContext();
        assertTrue(Commons.getCurrentUrl().endsWith(redirectPage + ".html"), "Current page should be Root page after navigation");
    }

    /**
     * Test: Check the existence of all available title types defined in a policy.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the existence of all available title types defined in a policy.")
    public void testCheckExistenceOfTypesUsingPolicy() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(titleRT.substring(titleRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("type", "h2"));
            add(new BasicNameValuePair("allowedTypes", "h2"));
            add(new BasicNameValuePair("allowedTypes", "h3"));
            add(new BasicNameValuePair("allowedTypes", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h6"));
            add(new BasicNameValuePair("allowedTypes@TypeHint", "String[]"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        assertTrue(editDialog.isTitleTypesPresent(new String[]{"2", "3", "4", "6"}), "h2, h3, h4, h6 title types should be present");
        assertFalse(editDialog.isTitleTypePresent("5"), "h5 title type should not be present");

        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(title.isTitleWithTypePresent("2"), "Title with type h2 should be present");

    }

    /**
     * Test: Check the absence of the title type select dropdown if only one title type is defined in a policy.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the absence of the title type select dropdown if only one title type is defined in a policy.")
    public void testCheckExistenceOfOneTypeUsingPolicy() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(titleRT.substring(titleRT.lastIndexOf("/")), new HashMap<String, String>() {{
            put("type", "h1");
            put("allowedTypes", "h1");
            put("allowedTypes@TypeHint", "String[]");
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        assertFalse(editDialog.isTitleTypeSelectPresent(), "Title select dropdown should not be present");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(title.isTitleWithTypePresent("1"), "Title with type h1 should be present");
    }

    /**
     * Test: Check the default option selected in the title type select dropdown if the default type set in a policy is a valid option.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the default option selected in the title type select dropdown if the default type set in a policy is a valid option.")
    public void testDefaultSelectedDropdownValueUsingValidOption() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(titleRT.substring(titleRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("type", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h1"));
            add(new BasicNameValuePair("allowedTypes", "h2"));
            add(new BasicNameValuePair("allowedTypes", "h3"));
            add(new BasicNameValuePair("allowedTypes", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h6"));
            add(new BasicNameValuePair("allowedTypes@TypeHint", "String[]"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        assertEquals(editDialog.getTitleTypeDropdownDefaultSelectedText(), "h4");
    }

    /**
     * Test: Check the default option selected in the title type select dropdown if the default type set in a policy is an invalid option.
     *
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the default option selected in the title type select dropdown if the default type set in a policy is an invalid option.")
    public void testDefaultSelectedDropdownValueUsingInvalidOption() throws ClientException, TimeoutException, InterruptedException {
        createComponentPolicy(titleRT.substring(titleRT.lastIndexOf("/")), new ArrayList<NameValuePair>() {{
            add(new BasicNameValuePair("type", "h5"));
            add(new BasicNameValuePair("allowedTypes", "h3"));
            add(new BasicNameValuePair("allowedTypes", "h4"));
            add(new BasicNameValuePair("allowedTypes", "h6"));
            add(new BasicNameValuePair("allowedTypes@TypeHint", "String[]"));
        }});

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        assertEquals(editDialog.getTitleTypeDropdownDefaultSelectedText(), "h3");
    }
}
