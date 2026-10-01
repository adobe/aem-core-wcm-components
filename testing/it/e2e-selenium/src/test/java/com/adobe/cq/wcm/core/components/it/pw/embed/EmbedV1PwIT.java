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
package com.adobe.cq.wcm.core.components.it.pw.embed;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_EMBED_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class EmbedV1PwIT extends ComponentPwBaseTest {

    protected String embedResourceType() {
        return RT_EMBED_V1;
    }

    protected String embedPath;

    protected String createEmbed() throws Exception {
        createComponentPolicy(embedResourceType().substring(embedResourceType().lastIndexOf("/")),
            Map.of("clientlibs", Commons.CLIENTLIBS_EMBED_V1,
                "allowedEmbeddables", "core/wcm/components/embed/v1/embed/embeddable/youtube"));
        embedPath = addStandaloneComponent(embedResourceType(), "embed");
        return embedPath;
    }

    private void openEmbedDialog() {
        openEditDialog(embedPath);
    }

    private void setType(String type) {
        dialog().locator("[data-cmp-embed-dialog-edit-hook='typeField'] coral-radio[value='" + type + "']").click();
    }

    private void setUrl(String url) {
        dialog().locator("[data-cmp-embed-dialog-edit-hook='urlField']").fill(url);
    }

    private Locator urlStatus() {
        return dialog().locator("[data-cmp-embed-dialog-edit-hook='urlStatus']");
    }

    @Test
    public void testUrlValidation() throws Exception {
        createEmbed();
        openEmbedDialog();
        assertTrue(urlStatus().innerText().trim().isEmpty());
        setUrl("https://www.youtube.com/watch?v=5vOOa3-fifY");
        page.waitForTimeout(1500);
        assertThat(urlStatus()).containsText("YouTube");
        clickDone();
        assertThat(dialog()).isHidden();

        openEmbedDialog();
        assertThat(urlStatus()).containsText("YouTube");
        setUrl("https://www.youtube.com/watch?v=5vOOa3-fifYinvalid");
        page.waitForTimeout(1500);
        assertThat(urlStatus()).isHidden();
        assertThat(dialog().locator("[data-cmp-embed-dialog-edit-hook='urlField'].is-invalid")).isVisible();
        setUrl("malformed");
        page.waitForTimeout(1000);
        assertThat(urlStatus()).isHidden();
        assertThat(dialog().locator("[data-cmp-embed-dialog-edit-hook='urlField'].is-invalid")).isVisible();
        setUrl("");
        page.waitForTimeout(1000);
        assertThat(urlStatus()).isHidden();
        assertThat(dialog().locator("[data-cmp-embed-dialog-edit-hook='urlField'].is-invalid")).isVisible();
    }

    private void testOEmbed(String url, String expectedName, String renderedSelector) throws Exception {
        createEmbed();
        openEmbedDialog();
        setUrl(url);
        page.waitForTimeout(1500);
        assertThat(urlStatus()).containsText(expectedName);
        saveDialog();
        assertThat(contentFrame().locator(renderedSelector)).isVisible();
    }

    @Test
    public void testUrlOEmbedFlickr() throws Exception {
        testOEmbed("https://www.flickr.com/photos/adobe/6951486964/in/album-72157629498635308/", "Flickr",
            ".cmp-embed [src^='https://live.staticflickr.com']");
    }

    @Test
    public void testUrlOEmbedSoundCloud() throws Exception {
        testOEmbed("https://soundcloud.com/adobeexperiencecloud/sets/think-tank-audio-experience", "SoundCloud",
            ".cmp-embed [src^='https://w.soundcloud.com/player']");
    }

    @Test
    public void testUrlOEmbedTwitter() throws Exception {
        testOEmbed("https://twitter.com/Adobe/status/1168253464675307525", "Twitter", ".cmp-embed .twitter-tweet");
    }

    @Test
    public void testUrlOEmbedYouTube() throws Exception {
        createEmbed();
        for (String url : new String[]{"https://www.youtube.com/watch?v=5vOOa3-fifY", "https://youtu.be/5vOOa3-fifY"}) {
            openEmbedDialog();
            setUrl(url);
            page.waitForTimeout(1500);
            assertThat(urlStatus()).containsText("YouTube");
            saveDialog();
            assertThat(contentFrame().locator(".cmp-embed [src^='https://www.youtube.com/embed']")).isVisible();
        }
    }

    @Test
    public void testEmbeddableYoutube() throws Exception {
        createEmbed();
        openEmbedDialog();
        setType("embeddable");
        saveDialog();
        openEmbedDialog();
        setType("embeddable");
        selectInCoralSelect("[data-cmp-embed-dialog-edit-hook='embeddableField']",
            "core/wcm/components/embed/v1/embed/embeddable/youtube");
        clickDone();
        assertThat(dialog()).isVisible();
        dialog().locator("[name='./youtubeVideoId']").fill("5vOOa3-fifY");
        saveDialog();
        assertThat(contentFrame().locator(".cmp-embed iframe[src*='youtube.com/embed']")).isVisible();
    }

    @Test
    public void testHtmlEmbed() throws Exception {
        createEmbed();
        openEmbedDialog();
        setType("html");
        clickDone();
        assertThat(dialog()).isVisible();
        dialog().locator("[data-cmp-embed-dialog-edit-showhidetargetvalue='html'] [name='./html']")
            .fill("<div id='CmpEmbedHtml'>HTML</div>");
        saveDialog();
        assertThat(contentFrame().locator("#CmpEmbedHtml")).containsText("HTML");
    }
}
