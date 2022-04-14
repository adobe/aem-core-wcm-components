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

package com.adobe.cq.wcm.core.components.it.seljup.tests.button.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.button.ButtonEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.apache.sling.testing.clients.util.HttpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_BUTTON_V2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class ButtonIT extends  com.adobe.cq.wcm.core.components.it.seljup.tests.button.v1.ButtonIT {

    private void setupResources() {
        buttonRT = RT_BUTTON_V2;
        linkPropertyName = "linkURL";
    }

    @BeforeEach
    public void setupBefore() throws Exception {
        setupResources();
        setup();
    }

    /**
     * Test: Set button link with target
     *
     * 1. open the edit dialog
     * 2. set the button link
     * 3. set the button link target
     * 4. close the edit dialog
     * 5. verify the button is an anchor tag with the correct href attribute and link target
     */
    @Test
    @DisplayName("Test: Set button link with target")
    void testSetLinkWithTarget() throws TimeoutException, InterruptedException {
        String link = "https://www.adobe.com";
        String target = "_blank";
        ButtonEditDialog buttonEditDialog = getButtonEditDialog();
        buttonEditDialog.clickLinkTarget();
        buttonEditDialog.setLinkField(link, linkPropertyName);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(button.checkLinkPresentWithTarget(link, target),"Button with link " + link + " and target "+ target + " should be present");
    }

    @Test
    @DisplayName("Test: Set button link with Button v1 link property")
    void testSetLink_withButtonV1LinkProperty() throws TimeoutException, InterruptedException, ClientException {
        String url1 = "http://www.google.com";
        String url2 = "http://www.adobe.com";
        setProperty("jcr:title", "button");
        SlingHttpResponse response;
        ButtonEditDialog buttonEditDialog;

        // use-case 1: link: empty / linkURL: empty -> link field: empty
        setProperty("link", "");
        setProperty("linkURL", "");
        buttonEditDialog = getButtonEditDialog();
        assertEquals(buttonEditDialog.getLinkFieldValue(), "");
        Commons.saveConfigureDialog();

        // use-case 2: link: url1 / linkURL: empty -> link field: url1
        setProperty("link", url1);
        setProperty("linkURL", "");
        buttonEditDialog = getButtonEditDialog();
        assertEquals(buttonEditDialog.getLinkFieldValue(), url1);
        Commons.saveConfigureDialog();
        response = adminClient.doGet(cmpPath + "/link", 404);
        assertEquals(404, response.getStatusLine().getStatusCode(), "The Button v1 link property should be removed from the JCR");

        // use-case 3: link: empty / linkURL: url2 -> link field: url2
        setProperty("link", "");
        setProperty("linkURL", url2);
        buttonEditDialog = getButtonEditDialog();
        assertEquals(buttonEditDialog.getLinkFieldValue(), url2);
        Commons.saveConfigureDialog();

        // use-case 4: link: url1 / linkURL: url2 -> link field: url2
        setProperty("link", url1);
        setProperty("linkURL", url2);
        buttonEditDialog = getButtonEditDialog();
        assertEquals(buttonEditDialog.getLinkFieldValue(), url2);
        Commons.saveConfigureDialog();
        response = adminClient.doGet(cmpPath + "/link", 404);
        assertEquals(404, response.getStatusLine().getStatusCode(), "The Button v1 link property should be removed from the JCR");
    }

    private void setProperty(String key, String value) throws ClientException {
        List<NameValuePair> props = new ArrayList<>();
        props.add(new BasicNameValuePair(key, value));
        UrlEncodedFormEntity formEntry = FormEntityBuilder.create().addAllParameters(props).build();
        try {
            adminClient.doPost(cmpPath, formEntry, HttpUtils.getExpectedStatus(200)).getSlingPath();
        } catch (ClientException ex) {
            throw new ClientException("Cannot set link properties for component " + cmpPath + "with error : " + ex, ex);
        }
    }

}
