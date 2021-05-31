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

import com.adobe.cq.wcm.core.components.it.seljup.components.title.TitleEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TitleIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.title.v1.TitleIT {

    public void setupResources() {
        clientlibs = "core.wcm.components.title.v2";
        titleRT = Commons.rtTitle_v2;
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

        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        title.clickLink();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchToDefaultContext();
        assertTrue(Commons.getCurrentUrl().endsWith(redirectPage+".html"), "Current page should be Root page after navigation");
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
        String policySuffix = "/title/new_policy";
        java.util.List<NameValuePair> props = new ArrayList();
        props.add(new BasicNameValuePair("jcr:title","New Policy"));
        props.add(new BasicNameValuePair("sling:resourceType","wcm/core/components/policy/policy"));
        props.add(new BasicNameValuePair("type","h2"));
        props.add(new BasicNameValuePair("allowedTypes","h2"));
        props.add(new BasicNameValuePair("allowedTypes","h3"));
        props.add(new BasicNameValuePair("allowedTypes","h4"));
        props.add(new BasicNameValuePair("allowedTypes","h6"));
        props.add(new BasicNameValuePair("allowedTypes@TypeHint","String[]"));
        String policyPath1 = "/conf/"+ label + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, props, policyPath1);

        // add a policy for teaser component
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content/root/responsivegrid/core-component/components";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient,"/title", data, policyAssignmentPath, 200, 201);

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        assertTrue(editDialog.isTitleTypesPresent(new String[]{"2", "3", "4", "6"}), "h2, h3, h4, h6 title types should be present");
        assertTrue(!editDialog.isTitleTypePresent("5"), "h5 title type should not be present");

        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(title.isTitleWithTypePresent("2"), "Title with type h2 should be present");

    }

    /**
     * Test: Check the existence of all available title types defined in a policy.
     * @throws ClientException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the existence of all available title types defined in a policy.")
    public void testCheckExistenceOfOneTypeUsingPolicy() throws ClientException, TimeoutException, InterruptedException {
        String policySuffix = "/title/new_policy";
        java.util.List<NameValuePair> props = new ArrayList();
        props.add(new BasicNameValuePair("jcr:title","New Policy"));
        props.add(new BasicNameValuePair("sling:resourceType","wcm/core/components/policy/policy"));
        props.add(new BasicNameValuePair("type","h1"));
        props.add(new BasicNameValuePair("allowedTypes","h1"));
        props.add(new BasicNameValuePair("allowedTypes@TypeHint","String[]"));
        String policyPath1 = "/conf/"+ label + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, props, policyPath1);

        // add a policy for teaser component
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content/root/responsivegrid/core-component/components";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient,"/title", data, policyAssignmentPath, 200, 201);

        editorPage.refresh();

        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        assertTrue(!editDialog.isTitleTypeSelectPresent(), "Title select dropdown should not be present");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(title.isTitleWithTypePresent("1"), "Title with type h2 should be present");
    }
}
