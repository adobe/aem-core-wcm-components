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
package com.adobe.cq.wcm.core.components.it.seljup.tests.search.v1;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.JsonUtils;
import org.apache.sling.testing.clients.util.URLParameterBuilder;
import org.apache.sling.testing.clients.util.poller.Polling;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v1.Search;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Tag("group3")
public class SearchIT extends AuthorBaseUITest {

    private static final String QUERY_BUILDER_URL = "/bin/querybuilder.json";
    protected String clientlibs;
    private String page1Path;
    private String page11Path;
    private String page111Path;
    private String page112Path;
    private String policyPath;
    protected String proxyPath;
    private String compPath;
    protected EditorPage editorPage;
    protected Search search;

    private boolean pollQuery(CQClient client, String path, String searchTerm, String expected) throws ClientException {
        int timeout = 2000;
        int delay = 50;
        class CreatePagePolling extends Polling {
            SlingHttpResponse response;

            @Override
            public Boolean call() throws Exception {
                URLParameterBuilder params = URLParameterBuilder.create().add("fulltext", searchTerm).add("path", path)
                    .add("p.limit", "100").add("type", "cq:Page");
                response = client.doGet(QUERY_BUILDER_URL, params.getList(), HttpStatus.SC_OK);
                return true;
            }
        }
        CreatePagePolling createPolling = new CreatePagePolling();
        try {
            createPolling.poll(timeout, delay);
        } catch (TimeoutException | InterruptedException e) {
            throw new ClientException("Not able to get query result for " + expected + " in" + createPolling.getWaited(), e);
        }

        String content = createPolling.response.getContent();
        final JsonNode results = JsonUtils.getJsonNodeFromString(content);
        final JsonNode hitsNode = results.get("hits");

        boolean match = false;
        for (final JsonNode hit : hitsNode) {
            if(hit.get("path").asText().trim().equals(expected)){
                match = true;
                break;
            }
        }

        return match;
    }

    protected void setupResources() {
        clientlibs = Commons.CLIENTLIBS_SEARCH_V1;;
        proxyPath = Commons.RT_SEARCH_V1;
        search = new Search();
    }

    public void setup() throws ClientException {
        // level 1
        page1Path = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/jcr:title", "Parent Page 1");
        Commons.editNodeProperties(authorClient, page1Path, data);

        // create 20 pages
        for(int i = 0; i < 20; i++) {
            String pagePath = authorClient.createPage("page" + i, "page" + i, page1Path, defaultPageTemplate).getSlingPath();
            data.clear();
            data.put("_charset_", "UTF-8");
            data.put("./jcr:content/jcr:title", "Page " + i);
            Commons.editNodeProperties(authorClient, pagePath, data);
        }

        // level 2
        page11Path = authorClient.createPage("page_1_1", "page_1_1", page1Path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/jcr:title", "Page 1.1");
        Commons.editNodeProperties(authorClient, page11Path, data);

        // level 2 1
        page111Path = authorClient.createPage("page_1_1_1", "page_1_1_1", page11Path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/jcr:title", "Page 1.1.1");
        Commons.editNodeProperties(authorClient, page111Path, data);

        // level 2 2
        page112Path = authorClient.createPage("page_1_1_2", "page_1_1_2", page11Path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/jcr:title", "Page 1.1.2");
        Commons.editNodeProperties(authorClient, page112Path, data);

        // level 2 3
        String page113Path = authorClient.createPage("page_1_1_3", "page_1_1_3", page11Path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/jcr:title", "Page 1.1.3");
        Commons.editNodeProperties(authorClient, page113Path, data);

        // 2.
        createPagePolicy(new HashMap<String, String>() {{
            put("clientlibs", clientlibs);
        }});

        // add the component to test page
        compPath = Commons.addComponentWithRetry(authorClient, proxyPath, page11Path + Commons.relParentCompPath, "search");

        // open test page in page editor
        editorPage = new PageEditorPage(page11Path);
        editorPage.open();
    }

    /**
     * Before Test Case
     **/
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    /**
     * After Test Case
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(page1Path, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Test: Default configuration (search in current page tree)
     */
    @Test
    @DisplayName("Test: Default configuration (search in current page tree)")
    public void testDefaultConfiguration() throws ClientException, InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(pollQuery(authorClient, rootPage, "Page", page111Path), "page_1_1_1 should come on search");
        search.setInput("Page 1.1.1");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.isResultsVisible(), "Results should be displayed");
        assertTrue(search.isPagePresentInSearch(contextPath + page111Path), "page_1_1_1 should be present in search results");
    }

    /**
     * Test: Change search root (start level 4)
     */
    @Test
    @DisplayName("Test: Change search root (start level 4)")
    public void testChangeSearchRoot() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, compPath);
        search.getEditDialog().setSearchRoot(page1Path);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        search.setInput("Page 1");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(!search.isPagePresentInSearch(contextPath + page1Path), "Parent page 1 should not be present in search results");
    }

    /**
     * Test: Clear button
     */
    @Test
    @DisplayName("Test: Clear button")
    public void testClearButton() throws InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(!search.isClearVisible(), "Clear button should not be visible");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.isClearVisible(), "Clear button should be visible");
        search.clickClear();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(!search.isClearVisible(), "Clear button should not be visible");
        assertTrue(!search.isResultsVisible(), "Results should not be displayed after clearing search");
        assertTrue(search.getInputValue().equals(""), "Input should be empty after clearing search");
    }

    /**
     * Test: Key: Enter key in input field doesn't navigate or clear input
     */
    @Test
    @DisplayName("Test: Key: Enter key in input field doesn't navigate or clear input")
    public void testKeyEnterInput() throws InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        search.pressEnter();
        assertTrue(Commons.getCurrentUrl().contains("page_1_1"), "Current page should be page_1_1");
        assertTrue(search.getInputValue().equals("Page"), "Search input should be Page");
    }

    /**
     * Test: Outside Click - dismisses results
     */
    @Test
    @DisplayName("Test: Outside Click - dismisses results")
    public void testOutsideClick() throws InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(!search.isClearVisible(), "Clear button should not be visible");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.isClearVisible(), "Clear button should be visible");
        search.clickOutside();
        assertTrue(!search.isResultsVisible(), "Results should not be displayed");
    }

    /**
     * Test: Mark - search term marked
     */
    @Test
    @DisplayName("Test: Mark - search term marked")
    public void testMark() throws ClientException, InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(!search.isClearVisible(), "Clear button should not be visible");
        assertTrue(pollQuery(authorClient, rootPage, "Page", page111Path), "page_1_1_1 should come on search");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.isMarkItemsPresent("Page"), "Page should be present in mark item");
    }

    /**
     * Test: Input Length - minimum length of the search term
     */
    @Test
    @DisplayName("Test: Input Length - minimum length of the search term")
    public void testMinLength() throws ClientException, InterruptedException {
        createComponentPolicy(proxyPath.substring(proxyPath.lastIndexOf("/")), new HashMap<String, String>() {{
            put("searchTermMinimumLength", "5");
        }});

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.getResultsCount() == 0, "No result should be present since minimum search key size is 5");

        search.setInput("Page ");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.getResultsCount() > 0, "Results should be present");
    }

    /**
     * Test: Results Size - Amount of fetched results
     */
    @Test
    @DisplayName("Test: Results Size - Amount of fetched results")
    public void testResultsSize() throws ClientException, InterruptedException {
        createComponentPolicy(proxyPath.substring(proxyPath.lastIndexOf("/")), new HashMap<String, String>() {{
            put("resultsSize", "2");
        }});

        Commons.switchContext("ContentFrame");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.getResultsCount() == 2, "2 Results should be present");
    }

    /**
     * Test: Scroll Down - Load more results
     */
    @Test
    @DisplayName("Test: Scroll Down - Load more results")
    public void testScrollDown() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, compPath);
        search.getEditDialog().setSearchRoot(page1Path);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.getResultsCount() == 10, "10 Results should be present");
        search.scrollResults(10);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.getResultsCount() == 20, "20 Results should be present after scrolling");
    }

}
