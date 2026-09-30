/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.pw;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.utils.DisableTour;
import com.adobe.cq.testing.selenium.utils.TestContentBuilder;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import com.microsoft.playwright.options.WaitUntilState;

import static com.adobe.cq.testing.selenium.Constants.GROUPID_CONTENT_AUTHORS;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright counterpart of {@code AuthorBaseUITest}: identical server-side fixtures (TestContentBuilder user, content
 * root, template, disabled tours), but the browser is driven by Playwright. One browser per test class, a fresh
 * context (cookies/storage) per test, and login via HTTP instead of the login form.
 */
public abstract class PlaywrightAuthorBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(PlaywrightAuthorBaseTest.class);

    protected static final String CONFIG_DIALOG = ".cq-dialog.foundation-form.foundation-layout-form";
    protected static final String DONE_BUTTON = "button[is='coral-button'][title='Done']";
    private static final boolean TRACE = Boolean.parseBoolean(System.getProperty("pw.trace", "true"));

    private static Playwright playwright;
    private static Browser browser;
    protected static String baseUrl;

    protected final String randomPassword = RandomStringUtils.randomAlphabetic(8);
    protected CQClient adminClient;
    protected CQClient authorClient;
    protected String rootPage;
    protected String defaultPageTemplate;
    protected String responsiveGridPath;
    protected String configPath;
    protected String label;

    protected BrowserContext context;
    protected Page page;
    private TestContentBuilder testContentBuilder;
    private String testName;

    @RegisterExtension
    final TestWatcher closeContext = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext ctx, Throwable cause) {
            closeContext(true);
        }

        @Override
        public void testSuccessful(ExtensionContext ctx) {
            closeContext(false);
        }
    };

    @BeforeAll
    static void launchBrowser() {
        baseUrl = System.getProperty("sling.it.instance.url.1", "http://localhost:4502").replaceAll("/$", "");
        PlaywrightAssertions.setDefaultAssertionTimeout(20_000);
        playwright = Playwright.create();
        // Same Chrome binary as the Selenium suite; headed by default (like Selenium under Xvfb).
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setChannel(System.getProperty("pw.channel", "chrome"))
            .setHeadless(Boolean.getBoolean("pw.headless")));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setupContentAndLogin(TestInfo testInfo) throws Exception {
        testName = testInfo.getTestMethod().map(m -> m.getName()).orElse("test");
        adminClient = new CQClient(URI.create(baseUrl + "/"),
            System.getProperty("sling.it.instance.adminUser.1", "admin"),
            System.getProperty("sling.it.instance.adminPassword.1", "admin"));

        testContentBuilder = new TestContentBuilder(adminClient, testName);
        testContentBuilder.withUser(randomPassword, getUserGroupMembership());
        testContentBuilder.build();

        authorClient = testContentBuilder.getDefaultUserClient();
        rootPage = testContentBuilder.getContentRootPath();
        defaultPageTemplate = testContentBuilder.getDefaultPageTemplatePath();
        responsiveGridPath = testContentBuilder.getTopLevelComponentPath();
        configPath = testContentBuilder.getConfigPath();
        label = testContentBuilder.getLabel();
        new DisableTour(authorClient).disableDefaultTours();

        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setLocale("en-US"));
        context.setDefaultTimeout(30_000);
        if (TRACE) {
            context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        }
        // The context's request API shares its cookie jar with its pages: log in once over HTTP.
        int status = context.request().post(baseUrl + "/libs/granite/core/content/login.html/j_security_check",
            RequestOptions.create()
                .setHeader("Referer", baseUrl + "/libs/granite/core/content/login.html")
                .setForm(FormData.create()
                .set("j_username", authorClient.getUser())
                .set("j_password", authorClient.getPassword())
                .set("j_validate", "true")
                .set("_charset_", "utf-8"))).status();
        if (status != 200) {
            throw new IllegalStateException("Login failed with HTTP " + status);
        }
        page = context.newPage();
    }

    @AfterEach
    void disposeContent() throws Exception {
        if (testContentBuilder != null) {
            testContentBuilder.dispose();
        }
    }

    private void closeContext(boolean failed) {
        if (context == null) {
            return;
        }
        try {
            if (TRACE && failed) {
                Path dir = Paths.get("target", "playwright-traces");
                Files.createDirectories(dir);
                context.tracing().stop(new Tracing.StopOptions()
                    .setPath(dir.resolve(getClass().getName() + "-" + testName + ".zip")));
            } else if (TRACE) {
                context.tracing().stop();
            }
        } catch (Exception e) {
            LOG.warn("Could not save Playwright trace", e);
        } finally {
            context.close();
            context = null;
        }
    }

    protected List<String> getUserGroupMembership() {
        return Arrays.asList(GROUPID_CONTENT_AUTHORS, "workflow-users");
    }

    protected String createPagePolicy(Map<String, String> policyProperties) throws ClientException {
        return Commons.createPagePolicy(adminClient, defaultPageTemplate, label, policyProperties);
    }

    protected String createComponentPolicy(String componentPath, Map<String, String> properties) throws ClientException {
        return Commons.createComponentPolicy(adminClient, defaultPageTemplate, label, componentPath, properties);
    }

    protected void addComponentToAllowedPolicy(String resourceType) throws ClientException {
        String policyResourcePath = responsiveGridPath.replaceFirst("structure", "policies");
        JsonNode policyAssignment = authorClient.doGetJson(policyResourcePath, 1, 200);
        String policyPath = configPath + "/settings/wcm/policies/" + policyAssignment.get("cq:policy").asText();
        JsonNode policy = authorClient.doGetJson(policyPath, 1, 200);
        JSONObject policyJson = new JSONObject(policy.toString());
        JSONArray components = policyJson.getJSONArray("components");
        components.put(resourceType);
        adminClient.deletePath(policyPath, 200);
        adminClient.importContent(policyPath, "json", policyJson.toString(), 201);
    }

    // ---------------------------------------------------------------- editor helpers

    protected void openEditor(String pagePath) {
        page.navigate(baseUrl + "/editor.html" + pagePath + ".html",
            new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
        waitEditorReady();
    }

    protected void reloadEditor() {
        page.reload();
        waitEditorReady();
    }

    private void waitEditorReady() {
        page.waitForFunction("() => !!(window.Granite && window.Granite.author && window.Granite.author.pageInfo)");
    }

    protected FrameLocator contentFrame() {
        return page.frameLocator("#ContentFrame");
    }

    /** Selects the editable's overlay and opens its configure dialog. */
    protected void openEditDialog(String componentPath) {
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + componentPath + "']");
        Locator configure = page.locator("#EditableToolbar button[data-action='CONFIGURE'][data-path='"
            + componentPath + "']");
        // Nested editables overlap their parent overlay; try points on the parent's perimeter and verify the toolbar
        // selected the requested editable before opening its dialog.
        double[][] points = {{1, 1}, {1, 20}, {20, 1}, {1, 60}, {20, 20}};
        com.microsoft.playwright.TimeoutError lastError = null;
        for (int i = 0; i < points.length; i++) {
            overlay.scrollIntoViewIfNeeded();
            BoundingBox bounds = overlay.boundingBox();
            double x = Math.min(points[i][0], Math.max(1, bounds.width - 1));
            double y = Math.min(points[i][1], Math.max(1, bounds.height - 1));
            overlay.click(new Locator.ClickOptions().setPosition(x, y));
            try {
                configure.waitFor(new Locator.WaitForOptions().setTimeout(5_000));
                configure.click();
                assertThat(dialog()).isVisible();
                return;
            } catch (com.microsoft.playwright.TimeoutError e) {
                lastError = e;
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new IllegalStateException("Could not select editable in the author toolbar: " + componentPath);
    }

    protected Locator dialog() {
        return page.locator(CONFIG_DIALOG);
    }

    /** Clicks Done without waiting for the dialog to close (e.g. when validation keeps it open). */
    protected void clickDone() {
        dialog().locator(DONE_BUTTON).click();
    }

    /** Clicks Done and waits for the dialog to close (the editable then refreshes asynchronously). */
    protected void saveDialog() {
        clickDone();
        assertThat(dialog()).isHidden();
    }

    /** Opens a Coral select; the popover is moved out of the select once opened, so it is resolved via aria-controls. */
    protected Locator openCoralSelect(String selectSelector) {
        Locator button = dialog().locator("coral-select" + selectSelector + " > button");
        button.click();
        Locator list = page.locator("[id='" + button.getAttribute("aria-controls") + "']");
        assertThat(list).isVisible();
        return list;
    }

    protected void selectInCoralSelect(String selectSelector, String value) {
        openCoralSelect(selectSelector).locator("coral-selectlist-item[value='" + value + "']").click();
        page.waitForFunction("([s, v]) => { const e = document.querySelector(s); return !!e && e.value === v; }",
            Arrays.asList(CONFIG_DIALOG + " coral-select" + selectSelector, value));
    }

    protected void checkCoralCheckbox(String name) {
        dialog().locator("coral-checkbox[name='" + name + "'] input[type='checkbox']").check();
    }

    protected void selectAutocomplete(String selector, String value) {
        Locator autocomplete = dialog().locator("foundation-autocomplete:has(" + selector + ")");
        autocomplete.locator("input[role='combobox']").fill(value);
        Locator suggestion = autocomplete.locator(
            "coral-overlay coral-buttonlist button[is='coral-buttonlist-item'][value='" + value + "']");
        assertThat(suggestion).isVisible();
        suggestion.click();
    }
}
