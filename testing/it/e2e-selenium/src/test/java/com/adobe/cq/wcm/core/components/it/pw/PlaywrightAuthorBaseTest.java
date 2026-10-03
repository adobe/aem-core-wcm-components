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
        // The editor and properties pages are server-rendered; tolerate short AEM stalls (GC, async jobs)
        context.setDefaultNavigationTimeout(60_000);
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
        // tests may already have navigated to the rendered page, where the editor globals never appear
        if (page.url().contains("/editor.html")) {
            waitEditorReady();
        }
    }

    private void waitEditorReady() {
        page.waitForFunction("() => !!(window.Granite && window.Granite.author && window.Granite.author.pageInfo)");
    }

    protected FrameLocator contentFrame() {
        return page.frameLocator("#ContentFrame");
    }

    /** Selects the editable's overlay and opens its configure dialog. */
    protected void openEditDialog(String componentPath) {
        clickToolbarAction(componentPath, "CONFIGURE");
        assertThat(dialog()).isVisible();
    }

    /** Selects the editable's overlay and clicks the given action in the editable toolbar. */
    protected void clickToolbarAction(String componentPath, String action) {
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + componentPath + "']");
        Locator configure = page.locator("#EditableToolbar button[data-action='" + action + "'][data-path='"
            + componentPath + "']");
        // Nested editables overlap their parent overlay; try points on the parent's perimeter and verify the toolbar
        // selected the requested editable before opening its dialog.
        double[][] points = {{0.5, 0.01}, {0.1, 0.01}, {0.9, 0.01}, {0.1, 0.9}, {0.9, 0.9},
            {0.1, 0.5}, {0.9, 0.5}, {0.5, 0.95}, {0.5, 0.5}};
        RuntimeException lastError = null;
        for (double[] point : points) {
            try {
                overlay.scrollIntoViewIfNeeded();
                overlay.evaluate("element => Promise.all(element.getAnimations().map(animation => "
                    + "animation.finished.catch(() => {})))");
                BoundingBox bounds = overlay.boundingBox();
                if (bounds == null) {
                    lastError = new IllegalStateException("Editable overlay is not visible: " + componentPath);
                    continue;
                }
                double x = Math.min(Math.max(1, bounds.width * point[0]), Math.max(1, bounds.width - 1));
                double y = Math.min(Math.max(1, bounds.height * point[1]), Math.max(1, bounds.height - 1));
                overlay.click(new Locator.ClickOptions().setPosition(x, y).setTimeout(2_000));
                configure.waitFor(new Locator.WaitForOptions().setTimeout(10_000));
                configure.click();
                return;
            } catch (com.microsoft.playwright.TimeoutError e) {
                lastError = e;
            } catch (com.microsoft.playwright.PlaywrightException e) {
                if (!e.getMessage().contains("Element is not attached to the DOM")) {
                    throw e;
                }
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

    /**
     * Component offered by the Insert Component picker. AEM as a Cloud Service renders component browser cards, while
     * 6.5 and LTS render a Coral 3 select list.
     *
     * @param pathMatch a CSS attribute operator and quoted value, e.g. {@code ="/libs/..."} or {@code $="/teaser"}
     */
    protected Locator insertableComponent(String pathMatch) {
        return page.locator(".editor-ComponentBrowser-component[data-path" + pathMatch + "], "
            + ".InsertComponentDialog-list coral-selectlist-item[value" + pathMatch + "]");
    }

    /**
     * Adds a layout-container item through the panel container's children editor and titles it. The new item is
     * appended asynchronously after the component is picked; filling before it exists would title the previous item
     * and leave the new, required title empty, so Done would be blocked by validation.
     */
    protected void addChildrenEditorItem(String title) {
        Locator titles = dialog().locator("[data-cmp-hook-childreneditor='itemTitle']");
        int before = titles.count();
        dialog().locator("[data-cmp-hook-childreneditor='add']").click();
        insertableComponent("='/libs/wcm/foundation/components/responsivegrid'").click();
        assertThat(titles).hasCount(before + 1);
        titles.last().fill(title);
        assertThat(titles.last()).hasValue(title);
    }

    /** Clicks Done without waiting for the dialog to close (e.g. when validation keeps it open). */
    protected void clickDone() {
        dialog().locator(DONE_BUTTON).click();
    }

    /** Clicks Done and waits for the dialog to close (the editable then refreshes asynchronously). */
    protected void saveDialog() {
        clickDone();
        try {
            dialog().waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN).setTimeout(20_000));
        } catch (com.microsoft.playwright.TimeoutError e) {
            // the first click can be swallowed while the form is still initializing
            clickDone();
            assertThat(dialog()).isHidden();
        }
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

    /**
     * Opens the Coral select carrying the given attribute selector and returns its (detached) item list.
     * Several selects can be open at once, so the list must be resolved via the trigger's aria-controls.
     */
    protected Locator openSelectList(String selectSelector) {
        Locator button = dialog().locator(selectSelector).locator("button[handle='button']").first();
        String listId = button.getAttribute("aria-controls");
        Locator list = page.locator("[id='" + (listId == null ? "" : listId) + "']");
        // Clicking an already open select would close it again.
        if (listId == null || !list.isVisible()) {
            button.click();
            list = page.locator("[id='" + button.getAttribute("aria-controls") + "']");
        }
        assertThat(list).isVisible();
        return list;
    }

    /** Closes open Coral popovers so that they cannot swallow the following click on Done (Escape would close the
     * whole dialog). */
    protected void closeOverlays() {
        page.evaluate("() => document.querySelectorAll('coral-popover[open], coral-overlay[open]')"
            + ".forEach(overlay => { if (!overlay.closest('.cq-dialog')) { overlay.open = false; } })");
        page.waitForTimeout(200);
    }

    /** Coral drag handles do not react to Playwright's atomic dragTo, so the gesture is replayed with the mouse. */
    /** Drops the source one row below the target's center, which Coral tables require to move an item down. */
    protected void dragBelow(Locator source, Locator target) {
        BoundingBox to = target.boundingBox();
        dragAndDrop(source, to.x + to.width / 2, to.y + to.height * 2.5);
    }

    protected void dragAndDrop(Locator source, Locator target) {
        BoundingBox to = target.boundingBox();
        dragAndDrop(source, to.x + to.width / 2, to.y + to.height / 2);
    }

    /**
     * Moves panel selector row {@code from} to position {@code to} with a native mouse drag on the row order handle.
     * Coral ignores synthetic mouse events and needs a settle delay before the drag starts.
     */
    protected void reorderPanelSelectorRow(int from, int to) {
        Locator rows = page.locator(".cmp-panelselector__table [is='coral-table-row']");
        assertThat(rows.nth(Math.max(from, to))).isVisible();
        Locator handle = rows.nth(from).locator("button[coral-table-roworder='true']");
        handle.scrollIntoViewIfNeeded();
        // The panel selector popover is still positioning itself after opening (notably on 6.5/LTS), so the
        // coordinates are only taken once the target row stopped moving.
        com.microsoft.playwright.options.BoundingBox tb = rows.nth(to).boundingBox();
        for (int i = 0; i < 20; i++) {
            page.waitForTimeout(250);
            com.microsoft.playwright.options.BoundingBox current = rows.nth(to).boundingBox();
            boolean stable = current.y == tb.y && current.x == tb.x;
            tb = current;
            if (stable) {
                break;
            }
        }
        com.microsoft.playwright.options.BoundingBox hb = handle.boundingBox();
        double x = hb.x + hb.width / 2, y = hb.y + hb.height / 2;
        page.mouse().move(x, y);
        page.mouse().down();
        page.waitForTimeout(300);
        double ty = tb.y + (to > from ? tb.height + 2 : -2);
        page.mouse().move(x, y + 5, new com.microsoft.playwright.Mouse.MoveOptions().setSteps(5));
        page.mouse().move(x, ty, new com.microsoft.playwright.Mouse.MoveOptions().setSteps(20));
        page.waitForTimeout(300);
        page.mouse().up();
        page.waitForTimeout(500);
    }

    protected void dragAndDrop(Locator source, double endX, double endY) {
        BoundingBox from = source.boundingBox();
        double startX = from.x + from.width / 2;
        double startY = from.y + from.height / 2;
        page.mouse().move(startX, startY);
        page.mouse().down();
        // the drag has to start with a small movement, otherwise Coral does not enter its drag mode
        page.mouse().move(startX, startY + (endY > startY ? 5 : -5));
        page.waitForTimeout(100);
        for (int step = 1; step <= 20; step++) {
            page.mouse().move(startX + (endX - startX) * step / 20.0, startY + (endY - startY) * step / 20.0);
            page.waitForTimeout(30);
        }
        page.waitForTimeout(200);
        page.mouse().up();
        page.waitForTimeout(200);
    }

    protected void checkCoralCheckbox(String name) {        dialog().locator("coral-checkbox[name='" + name + "'] input[type='checkbox']").check();
    }

    protected void selectAutocomplete(String selector, String value) {
        Locator autocomplete = dialog().locator("foundation-autocomplete:has(" + selector + ")");
        autocomplete.locator("input[role='combobox']").fill(value);
        Locator suggestion = autocomplete.locator(
            "coral-overlay coral-buttonlist button[is='coral-buttonlist-item'][value='" + value + "']");
        assertThat(suggestion).isVisible();
        suggestion.click();
    }

    /**
     * Types a path in a foundation-autocomplete and commits it, preferring the matching suggestion. Pressing Enter
     * is avoided since it submits the surrounding form.
     */
    protected void fillPathAutocomplete(Locator scope, String name, String value) {
        Locator autocomplete = scope.locator("foundation-autocomplete[name='" + name + "']").first();
        Locator input = autocomplete.locator("input[is='coral-textfield']").first();
        input.fill(value);
        Locator suggestion = autocomplete.locator("coral-overlay button[value='" + value + "']").first();
        try {
            suggestion.click(new Locator.ClickOptions().setTimeout(5000));
        } catch (com.microsoft.playwright.TimeoutError e) {
            input.press("Tab");
        }
    }

    protected void selectInPicker(String prefix, String selector, String value) {
        String relativePath = value.startsWith(prefix + "/") ? value.substring(prefix.length() + 1)
            : value.startsWith("/") ? value.substring(1) : value;
        String[] segments = relativePath.split("/");
        String currentPath = prefix;
        Locator autocomplete = dialog().locator("foundation-autocomplete" + selector);
        autocomplete.locator("button[title='Open Selection Dialog']").click();

        Locator picker = page.locator("coral-dialog:visible").last();
        assertThat(picker).isVisible();
        for (int i = 0; i < segments.length - 1; i++) {
            currentPath += "/" + segments[i];
            Locator folder = picker.locator("[data-foundation-collection-item-id='" + currentPath + "']");
            assertThat(folder).isVisible();
            folder.click();
        }

        currentPath += "/" + segments[segments.length - 1];
        Locator asset = picker.locator("[data-foundation-collection-item-id='" + currentPath + "']");
        assertThat(asset).isVisible();
        Locator checkbox = asset.locator("coral-checkbox");
        if (checkbox.count() > 0 && checkbox.isVisible()) {
            checkbox.click();
        } else {
            asset.locator("coral-columnview-item-thumbnail").click();
        }
        picker.locator("button.granite-pickerdialog-submit[is='coral-button']").click();
    }
}
