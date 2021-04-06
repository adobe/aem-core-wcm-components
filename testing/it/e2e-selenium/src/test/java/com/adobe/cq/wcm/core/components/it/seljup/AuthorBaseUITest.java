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

package com.adobe.cq.wcm.core.components.it.seljup;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.adobe.qe.selenium.junit.annotations.Author;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.codehaus.jackson.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.qe.selenium.UIAbstractTest;
import com.adobe.qe.selenium.pageobject.granite.LoginPage;
import com.adobe.qe.selenium.utils.DisableTour;
import com.adobe.qe.selenium.junit.extensions.TestContentExtension;
import com.adobe.qe.selenium.utils.TestContentBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.adobe.qe.selenium.Constants.DEFAULT_SMALL_SIZE;
import static com.adobe.qe.selenium.Constants.GROUPID_CONTENT_AUTHORS;


import static com.adobe.qe.selenium.Constants.RUNMODE_AUTHOR;
import static com.adobe.qe.selenium.pagewidgets.Helpers.setAffinityCookie;

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
    public static CQClient adminClient;
    public  String label;

    /**
     * Author username with authoring privileges
     */
    private static final String AUTHOR_USERNAME = "it-sanity-author-" + RandomStringUtils.random(5, true, true);

    /**
     * Author user's password
     */
    private static final String AUTHOR_PASSWORD = RandomStringUtils.random(10, true, true);


    @RegisterExtension
    protected TestContentExtension testContentAuthor = new TestContentExtension(RUNMODE_AUTHOR);

    @BeforeEach
    public void loginBeforeEach(@Author final CQClient adminAuthor, final TestContentBuilder testContentBuilder, final URI baseURI)
            throws ClientException, InterruptedException, UnsupportedEncodingException, URISyntaxException, Exception {
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

        new DisableTour(authorClient).disableDefaultTours();

        LoginPage loginPage = new LoginPage(baseURI);
        loginPage.loginAs(authorClient.getUser(), authorClient.getPassword());

        setAffinityCookie(authorClient);
    }

    public List<String> getUserGroupMembership() {
        return Arrays.asList(GROUPID_CONTENT_AUTHORS, "workflow-users");
    }


    public void addPathtoComponentPolicy(String componenPathtoUpdate, String pathToAdd) throws Exception {
        String resourcePath = StringUtils.replaceOnce(componenPathtoUpdate, "structure", "policies");
        JsonNode existingPolicyNodePath = authorClient.doGetJson(resourcePath, 1, 200);
        String existingPolicy = configPath + "/settings/wcm/policies/" + existingPolicyNodePath.get("cq:policy").getValueAsText();
        JsonNode policyNode = authorClient.doGetJson(existingPolicy, 1, 200);
        JSONObject policyNodeJson = new JSONObject(policyNode.toString());
        JSONArray jsonArray =policyNodeJson.getJSONArray("components");// new JSONArray(componentsList);
        jsonArray.put(pathToAdd);

        adminClient.deletePath(existingPolicy, 200);
        adminClient.importContent(existingPolicy, "json", policyNodeJson.toString(), 201);

    }

    //todo should move to util
    public String creatProxyCompoenet(String corecomponentPath, String proxyCompoentTitle) throws Exception {
        FormEntityBuilder form = FormEntityBuilder.create()
            .addParameter("./sling:resourceSuperType", corecomponentPath)
            .addParameter("./jcr:title", proxyCompoentTitle)
            .addParameter("./componentGroup", "test.site")
            .addParameter("./jcr:primaryType", "cq:Component");

        SlingHttpResponse exec = adminClient.doPost("/apps/testsite" + RandomStringUtils.randomAlphabetic(DEFAULT_SMALL_SIZE) + "/components/button", form.build(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
        return exec.getSlingPath();
    }


}
