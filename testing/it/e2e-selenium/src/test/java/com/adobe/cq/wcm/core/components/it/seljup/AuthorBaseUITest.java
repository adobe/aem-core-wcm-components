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

package com.adobe.cq.wcm.core.components.it.seljup;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.UIAbstractTest;
import com.adobe.cq.testing.selenium.junit.annotations.Author;
import com.adobe.cq.testing.selenium.junit.extensions.TestContentExtension;
import com.adobe.cq.testing.selenium.pageobject.granite.LoginPage;
import com.adobe.cq.testing.selenium.utils.DisableTour;
import com.adobe.cq.testing.selenium.utils.TestContentBuilder;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.testing.selenium.Constants.GROUPID_CONTENT_AUTHORS;
import static com.adobe.cq.testing.selenium.Constants.RUNMODE_AUTHOR;
import static com.adobe.cq.testing.selenium.pagewidgets.Helpers.setAffinityCookie;

@Execution(ExecutionMode.CONCURRENT)
public abstract class AuthorBaseUITest extends UIAbstractTest {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorBaseUITest.class);

    public final String randomPassword = RandomStringUtils.randomAlphabetic(8);
    public String rootPage;
    public CQClient authorClient;
    public String defaultPageTemplate;
    public String testLabel;
    public String responsiveGridPath;
    public String configPath;
    public String contextPath;
    public static CQClient adminClient;
    public  String label;

    @RegisterExtension
    protected TestContentExtension testContentAuthor = new TestContentExtension(RUNMODE_AUTHOR);

    @BeforeEach
    public void loginBeforeEach(@Author final CQClient adminAuthor, final TestContentBuilder testContentBuilder, final URI baseURI)
        throws ClientException, InterruptedException, IOException, TimeoutException {
        testContentBuilder.withUser(randomPassword, getUserGroupMembership());
        testContentBuilder.build();

        adminClient = adminAuthor;
        authorClient = testContentBuilder.getDefaultUserClient();
        rootPage = testContentBuilder.getContentRootPath();

        defaultPageTemplate = testContentBuilder.getDefaultPageTemplatePath();
        responsiveGridPath = testContentBuilder.getTopLevelComponentPath();
        configPath = testContentBuilder.getConfigPath();
        label = testContentBuilder.getLabel();

        testLabel = testContentBuilder.getLabel();
        contextPath = adminClient.getUrl().getPath().substring(0,adminClient.getUrl().getPath().length()-1);
        new DisableTour(authorClient).disableDefaultTours();

        LoginPage loginPage = new LoginPage(baseURI);
        loginPage.loginAs(authorClient.getUser(), authorClient.getPassword());

        setAffinityCookie(authorClient);
    }

    public List<String> getUserGroupMembership() {
        return Arrays.asList(GROUPID_CONTENT_AUTHORS, "workflow-users");
    }

    public String createPagePolicy(Map<String, String> policyProperties) throws ClientException {
        return Commons.createPagePolicy(adminClient, defaultPageTemplate, label, policyProperties);
    }

    public String createComponentPolicy(String componentPath, Map<String, String> properties) throws ClientException {
        return Commons.createComponentPolicy(adminClient, defaultPageTemplate, label, componentPath, properties);
    }

    public String createComponentPolicy(String componentPath, List<NameValuePair> properties) throws ClientException {
        return Commons.createComponentPolicy(adminClient, defaultPageTemplate, label, componentPath, properties);
    }

    public void deleteComponentPolicy(String componentPath, String policyPath) throws ClientException {
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content/root/responsivegrid/core-component/components";
        Commons.deletePolicy(adminClient, policyPath, policyAssignmentPath);
        Commons.deletePolicyAssignment(adminClient, componentPath, policyAssignmentPath);
    }


    public void addPathtoComponentPolicy(String componenPathtoUpdate, String pathToAdd) throws ClientException {
        String resourcePath = StringUtils.replaceOnce(componenPathtoUpdate, "structure", "policies");
        JsonNode existingPolicyNodePath = authorClient.doGetJson(resourcePath, 1, 200);
        String existingPolicy = configPath + "/settings/wcm/policies/" + existingPolicyNodePath.get("cq:policy").asText();
        JsonNode policyNode = authorClient.doGetJson(existingPolicy, 1, 200);
        JSONObject policyNodeJson = new JSONObject(policyNode.toString());
        JSONArray jsonArray = policyNodeJson.getJSONArray("components");
        jsonArray.put(pathToAdd);

        adminClient.deletePath(existingPolicy, 200);
        adminClient.importContent(existingPolicy, "json", policyNodeJson.toString(), 201);

    }

}
