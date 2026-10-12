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

package com.adobe.cq.wcm.core.components.it.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.exceptions.TestingValidationException;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.apache.sling.testing.clients.util.HttpUtils;
import org.apache.sling.testing.clients.util.JsonUtils;
import org.apache.sling.testing.clients.util.poller.Polling;

import com.adobe.cq.testing.client.CQClient;
import com.fasterxml.jackson.databind.JsonNode;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HTTP-level helpers shared by the browser ITs to create content and policies on the AEM instance.
 */
public class Commons {

    private static final Duration DEFAULT_RETRY_DELAY_DURATION = Duration.ofSeconds(1);
    private static final Duration DEFAULT_TIMEOUT_DURATION = Duration.ofSeconds(30);
    private static final long DEFAULT_RETRY_DELAY = DEFAULT_RETRY_DELAY_DURATION.toMillis();
    private static final long DEFAULT_TIMEOUT = DEFAULT_TIMEOUT_DURATION.toMillis();
    private static final int DEFAULT_SMALL_SIZE = 8;

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
    public static final String RT_ACCORDION_V1 = "core-component/components/accordion-v1";
    // embed component
    public static final String RT_EMBED_V1 = "core-component/components/embed-v1";
    public static final String RT_EMBED_V2 = "core-component/components/embed-v2";
    public static final String CLIENTLIBS_EMBED_V1 = "core.wcm.components.embed.v1";
    // text component
    public static final String RT_TEXT_V1 = "core-component/components/text-v1";
    public static final String RT_TEXT_V2 = "core-component/components/text-v2";
    // title component
    public static final String RT_TITLE_V1 = "core-component/components/title-v1";
    public static final String CLIENTLIBS_TITLE_V1 = "core.wcm.components.title.v1";
    public static final String RT_TITLE_V2 = "core-component/components/title-v2";
    public static final String CLIENTLIBS_TITLE_V2 = "core.wcm.components.title.v2";
    public static final String RT_TITLE_V3 = "core-component/components/title-v3";
    public static final String CLIENTLIBS_TITLE_V3 = "core.wcm.components.title.v3";
    // list component
    public static final String RT_LIST_V1 = "core-component/components/list-v1";
    public static final String RT_LIST_V2 = "core-component/components/list-v2";
    public static final String RT_LIST_V3 = "core-component/components/list-v3";

    public static final String RT_LIST_V4 = "core-component/components/list-v4";
    // image component
    public static final String RT_IMAGE_V1 = "core-component/components/image-v1";
    public static final String CLIENTLIBS_IMAGE_V1 = "core.wcm.components.image.v1";
    public static final String RT_IMAGE_V2 = "core-component/components/image-v2";
    public static final String CLIENTLIBS_IMAGE_V2 = "core.wcm.components.image.v2";
    public static final String RT_IMAGE_V3 = "core-component/components/image-v3";
    public static final String CLIENTLIBS_IMAGE_V3 = "core.wcm.components.image.v3";
    // breadcrumb component
    public static final String RT_BREADCRUMB_V1 = "core-component/components/breadcrumb-v1";
    public static final String RT_BREADCRUMB_V2 = "core-component/components/breadcrumb-v2";
    public static final String RT_BREADCRUMB_V3 = "core-component/components/breadcrumb-v3";
    // button component
    public static final String RT_BUTTON_V1 = "core-component/components/button-v1";
    public static final String RT_BUTTON_V2 = "core-component/components/button-v2";
    // navigation component
    public static final String RT_NAVIGATION_V1 = "core-component/components/navigation-v1";
    public static final String RT_NAVIGATION_V2 = "core-component/components/navigation-v2";
    // language navigation component
    public static final String RT_LANGUAGE_NAVIGATION_V1 = "core-component/components/languagenavigation-v1";
    public static final String RT_LANGUAGE_NAVIGATION_V2 = "core-component/components/languagenavigation-v2";
    // search component
    public static final String RT_SEARCH_V1 = "core-component/components/search-v1";
    public static final String CLIENTLIBS_SEARCH_V1 = "core.wcm.components.search.v1";
    public static final String RT_SEARCH_V2 = "core-component/components/search-v2";
    public static final String CLIENTLIBS_SEARCH_V2 = "core.wcm.components.search.v2";
    public static final String RT_SEARCH_V3 = "core-component/components/search-v3";
    public static final String CLIENTLIBS_SEARCH_V3 = "core.wcm.components.search.v3";
    public static String rtSearch_v2 = "core/wcm/components/search/v2/search";
    // teaser component
    public static final String RT_TEASER_V1 = "core-component/components/teaser-v1";
    public static final String CLIENTLIBS_TEASER_V1 = "core.wcm.components.teaser.v1";
    public static final String RT_TEASER_V2 = "core-component/components/teaser-v2";
    public static final String CLIENTLIBS_TEASER_V2 = "core.wcm.components.teaser.v2";
    // carousel component
    public static final String RT_CAROUSEL_V1 = "core-component/components/carousel-v1";
    public static final String CLIENTLIBS_CAROUSEL_V1 = "core.wcm.components.carousel.v1";
    // tabs component
    public static final String RT_TABS_V1 = "core-component/components/tabs-v1";
    public static final String CLIENTLIBS_TABS_V1 = "core.wcm.components.tabs.v1";
    // content fragment component
    public static final String RT_CONTENTFRAGMENT_V1 = "core-component/components/contentfragment-v1";
    // pdfviewer component
    public static final String RT_PDFVIEWER_V1 = "core-component/components/pdfviewer-v1";
    public static final String CLIENTLIBS_PDFVIEWER_V1 = "core.wcm.components.pdfviewer.v1";
    // separator component
    public static final String RT_SEPARATOR_V1 = "core-component/components/separator-v1";
    // content fragment list component
    public static final String RT_CONTENTFRAGMENTLIST_V1 = "core-component/components/contentfragmentlist-v1";
    public static final String RT_CONTENTFRAGMENTLIST_V2 = "core-component/components/contentfragmentlist-v2";
    // table of contents component
    public static String RT_TABLEOFCONTENTS_V1 = "core-component/components/tableofcontents-v1";
    // core form container
    public static String RT_FORMCONTAINER_V1 = "core-component/components/formcontainer-v1";
    public static String RT_FORMCONTAINER_V2 = "core-component/components/formcontainer-v2";
    // form button
    public static String RT_FORMBUTTON_V1 = "core-component/components/formbutton-v1";
    public static String RT_FORMBUTTON_V2 = "core-component/components/formbutton-v2";
    // form button
    public static String RT_FORMTEXT_V1 = "core-component/components/formtext-v1";
    public static String RT_FORMTEXT_V2 = "core-component/components/formtext-v2";
    // form option
    public static String RT_FORMOPTIONS_V1 = "core-component/components/formoptions-v1";
    public static String RT_FORMOPTIONS_V2 = "core-component/components/formoptions-v2";
    // hidden field
    public static String RT_FORMHIDDEN_V1 = "core-component/components/formhidden-v1";
    public static String RT_FORMHIDDEN_V2 = "core-component/components/formhidden-v2";
    // download component
    public static final String RT_DOWNLOAD_V1 = "core-component/components/download-v1";
    public static final String RT_DOWNLOAD_V2 = "core-component/components/download-v2";

    private static String randomSmallText() {
        return RandomStringUtils.randomAlphabetic(DEFAULT_SMALL_SIZE);
    }

    /**
     * Creates form entity builder
     *
     * @param data map containing data for creating FormEntityBuilder
     */
    private static FormEntityBuilder createFEB(Map<String, String> data) {
        FormEntityBuilder feb = FormEntityBuilder.create();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            feb.addParameter(entry.getKey(), entry.getValue());
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

    public static void checkTemplateExists(CQClient adminClient, String templatePath) throws ClientException {
        Polling templatePolling = new Polling() {
            @Override
            public Boolean call() throws Exception {
                return adminClient.doGet(templatePath + ".json").getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            }
        };
        try {
            templatePolling.poll(DEFAULT_TIMEOUT, DEFAULT_RETRY_DELAY);
        } catch (InterruptedException | TimeoutException ex) {
            throw new ClientException("Page template " + templatePath + " does not exist after " + DEFAULT_TIMEOUT + "ms", ex);
        }
    }

    public static String createPagePolicy(CQClient adminClient, String defaultPageTemplate, String siteName, Map<String, String> policyProperties) throws ClientException {
        checkTemplateExists(adminClient, defaultPageTemplate);
        String policySuffix = "/structure/page/new_policy";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:title", "New Policy");
        data.put("sling:resourceType", "wcm/core/components/policy/policy");
        data.putAll(policyProperties);
        String policyRootPath = "/conf/"+ siteName + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, data , policyRootPath);

        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content";
        data.clear();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient,"", data, policyAssignmentPath);

        return policyPath;
    }

    public static String createComponentPolicy(CQClient adminClient, String defaultPageTemplate, String siteName, String componentPath, List<NameValuePair> properties) throws ClientException {
        checkTemplateExists(adminClient, defaultPageTemplate);
        String randomText = randomSmallText();
        String policySuffix = componentPath + "/" + randomText;
        List<NameValuePair> policyProperties = new ArrayList<>();
        policyProperties.add(new BasicNameValuePair("jcr:title", randomText + " Policy"));
        policyProperties.add(new BasicNameValuePair("sling:resourceType", "wcm/core/components/policy/policy"));
        policyProperties.addAll(properties);
        String policyRootPath = "/conf/"+ siteName + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, policyProperties, policyRootPath);

        // add a policy for component
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content/root/responsivegrid/core-component/components";
        HashMap<String, String> mappingProperties = new HashMap<>();
        mappingProperties.put("cq:policy", policyLocation + policySuffix);
        mappingProperties.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient, componentPath, mappingProperties, policyAssignmentPath, 200, 201);

        return policyPath;
    }

    public static String createComponentPolicy(CQClient adminClient, String defaultPageTemplate, String siteName, String componentPath, Map<String, String> properties) throws ClientException {
        checkTemplateExists(adminClient, defaultPageTemplate);
        String randomText = randomSmallText();
        String policySuffix = componentPath + "/" + randomText;
        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put("jcr:title", randomText + " Policy");
        policyProperties.put("sling:resourceType", "wcm/core/components/policy/policy");
        policyProperties.putAll(properties);
        String policyRootPath = "/conf/"+ siteName + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, policyProperties, policyRootPath);

        // add a policy for component
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content/root/responsivegrid/core-component/components";
        HashMap<String, String> mappingProperties = new HashMap<>();
        mappingProperties.put("cq:policy", policyLocation + policySuffix);
        mappingProperties.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient, componentPath, mappingProperties, policyAssignmentPath, 200, 201);

        return policyPath;
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
     * Sets properties of a repository node.
     *
     * @param client            CQClient
     * @param path     Mandatory. absolute path to the node
     * @param data              Mandatory. object with properties to be set on the node.
     * @throws ClientException
     */
    public static void editNodeProperties(CQClient client, String path, HashMap<String, String> data, int... expectedStatus) throws ClientException {
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
        List<NameValuePair> props = new ArrayList<>();
        for (String tag : tags) {
            props.add(new BasicNameValuePair("./cq:tags", tag));
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
     * Adds a component to a page with default retry options.
     */
    public static String addComponentWithRetry(final CQClient client, final String component, final String parentCompPath, final String nameHint)
            throws ClientException {
        return addComponentWithRetry(client, component, parentCompPath, nameHint, null,
                RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK, HttpStatus.SC_CREATED);
    }

    /**
     * Adds a component to a page with retires
     *
     * @param client            CQClient
     * @param component          mandatory components resource type
     * @param parentCompPath     mandatory absolute path to the parent component
     * @param nameHint           optional hint for the component nodes name, if empty component name is taken
     * @param order              optional where to place component e.g. 'before product_grid', if empty, 'last' is used
     * @param timeout            try retries for this time duration
     * @param delay              interval between successive retries
     * @throws ClientException
     */
    public static String addComponentWithRetry(final CQClient client, final String component, final String parentCompPath, final String nameHint,
                                               final String order, long timeout, long delay, final int... expectedStatus) throws ClientException {

        class AddComponentPolling extends Polling {
            String componentPath;

            public Boolean call() throws Exception {
                this.componentPath = addComponent(client, component, parentCompPath, nameHint, order, expectedStatus);
                return true;
            }
        }

        AddComponentPolling addComponentPolling = new AddComponentPolling();

        try {
            addComponentPolling.poll(timeout, delay);
        } catch (TimeoutException | InterruptedException e) {
            throw new ClientException("Failed to add component " + component + " in " + addComponentPolling.getWaited(), e);
        }

        return addComponentPolling.componentPath;
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
     * check if proxy client library present for the component
     *
     * @param client CQClient
     * @param libraryPath component's library path
     * @param expectedStatus
     * @throws ClientException
     */
    public static void checkProxiedClientLibrary(CQClient client,String libraryPath, int... expectedStatus) throws ClientException {
        client.doGet("/etc.clientlibs" + libraryPath, HttpUtils.getExpectedStatus(200, expectedStatus));
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
     * Polls a JSON resource until it exists and satisfies the condition. Used for content written asynchronously
     * with respect to the test (e.g. by a form submitted from the browser).
     */
    public static JsonNode waitForJson(CQClient client, String path, int depth, Predicate<JsonNode> condition)
        throws ClientException, InterruptedException {
        final JsonNode[] result = new JsonNode[1];
        Polling polling = new Polling(() -> {
            SlingHttpResponse response = client.doGet(path + "." + depth + ".json");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return false;
            }
            result[0] = JsonUtils.getJsonNodeFromString(response.getContent());
            return condition.test(result[0]);
        });
        try {
            polling.poll(DEFAULT_TIMEOUT, DEFAULT_RETRY_DELAY);
        } catch (TimeoutException e) {
            throw new TestingValidationException("Condition not met for " + path + " after " + polling.getWaited() + "ms", e);
        }
        return result[0];
    }

    public static void setNGDMImage(CQClient client, String componentPath) throws ClientException {
        client.setPropertyString(componentPath, "fileReference", "/urn:aaid:aem:18a66ed0-a9ae-4cfd-b7b9-78175bd3336d/test.jpg", 200);
    }

}
