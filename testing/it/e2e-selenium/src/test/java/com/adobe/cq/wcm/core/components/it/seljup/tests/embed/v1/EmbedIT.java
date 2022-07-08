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

package com.adobe.cq.wcm.core.components.it.seljup.tests.embed.v1;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.embed.UrlProcessors;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.embed.v1.Embed;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.embed.UrlProcessors.OEmbed;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.embed.EmbedEditDialog.EditDialogProperties;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.CLIENTLIBS_EMBED_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_EMBED_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class EmbedIT extends AuthorBaseUITest {

    private static String componentName = "embed";
    private static String youtubeEmbedField = "core/wcm/components/embed/v1/embed/embeddable/youtube";

    protected EditorPage editorPage;
    protected Embed embed;
    protected UrlProcessors urlProcessors;
    protected String testPage;
    protected String cmpPath;
    protected String embedRT;
    protected String clientlibs;

    private void setupResources() {
        clientlibs = CLIENTLIBS_EMBED_V1;
        embedRT = RT_EMBED_V1;
    }

    /**
     * Setup Before Test Case
     *
     * 1. create test page
     * 2. create the policy
     * 3. add the component to the page
     * 4. open the new page in the editor
     */
    protected void setup() throws ClientException {
        //1.
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // 2.
        createComponentPolicy(embedRT.substring(embedRT.lastIndexOf("/")),new HashMap<String,String>() {{
            put("clientlibs", clientlibs);
            put("allowedEmbeddables", youtubeEmbedField);
        }});

        // 3.
        cmpPath = Commons.addComponentWithRetry(authorClient, embedRT,testPage + Commons.relParentCompPath, componentName);

        // 4.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        embed = new Embed();
        urlProcessors = new UrlProcessors();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    /**
     * After Test Case
     *
     * delete the test page
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Verify URL : verify a given URL can be processed
     *
     * 1. open the edit dialog
     * 2. enter the URL in the URL field
     * 3. verify the status message
     * 4. save the edit dialog
     * 5. verify the embed object on the page
     */
    private void verifyUrl(String url, OEmbed urlProcessor) throws InterruptedException, TimeoutException {
        //1.
        Commons.openEditDialog(editorPage, cmpPath);

        //2.
        EditDialogProperties editDialogProperties = embed.getEmbedEditDialog().getProperties();
        editDialogProperties.setUrlField(url);
        editDialogProperties.waitForUrlFieldToBeValid();

        //3.
        assertTrue(editDialogProperties.isUrlStatusSet(urlProcessor.getName()), "URL status should be set");

        //4.
        Commons.saveConfigureDialog();

        //5.
        Commons.switchContext("ContentFrame");
        assertTrue(urlProcessor.urlProcessorExits(),"URL processor should be set");
        Commons.switchToDefaultContext();
    }

     /**
     * URL Validation
     *
     * 1. open the edit dialog
     * 2. verify no URL status is currently showing
     * 3. enter a valid url
     * 4. verify the status message
     * 5. save the edit dialog
     * 6. open the edit dialog
     * 7. verify the status message
     * 8. enter an invalid URL
     * 9. verify field is marked invalid and the status message is not shown
     * 10. enter a malformed URL
     * 11. verify field is marked invalid and the status message is not shown
     * 12. enter an empty URL
     * 13. verify field is marked invalid and the status message is not shown
     */
    @Test
    @DisplayName("URL Validation")
    public void testUrlValidation() throws InterruptedException, TimeoutException {
        //1.
        Commons.openEditDialog(editorPage, cmpPath);

        //2.
        EditDialogProperties editDialogProperties = embed.getEmbedEditDialog().getProperties();
        assertTrue(editDialogProperties.isUrlStatusEmpty(),"URL status should be empty");

        //3.
        editDialogProperties.setUrlField(editDialogProperties.getValidUrl());
        editDialogProperties.waitForUrlFieldToBeValid();

        //4.
        assertTrue(editDialogProperties.isUrlStatusSet("YouTube"), "URL status should be set");

        //5.
        Commons.saveConfigureDialog();

        //6.
        Commons.openEditDialog(editorPage, cmpPath);

        //7.
        assertTrue(editDialogProperties.isUrlStatusSet("YouTube"), "URL status should be set");

        //8.
        editDialogProperties.setUrlField(editDialogProperties.getInvalidUrl());
        //wait for validation to finish
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS * 5);

        //9.
        assertTrue(!editDialogProperties.isUrlStatusVisible(), "URL status should not be visible");
        assertTrue(editDialogProperties.isUrlFieldInvalid(), "URL field should be invalid");

        //10.
        editDialogProperties.setUrlField(editDialogProperties.getMalformedUrl());
        //wait for validation to finish
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS * 5);

        //11.
        assertTrue(!editDialogProperties.isUrlStatusVisible(), "URL status should not be visible");
        assertTrue(editDialogProperties.isUrlFieldInvalid(), "URL field should be invalid");

        //12.
        editDialogProperties.setUrlField("");
        //wait for validation to finish
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS * 5);

        //13.
        assertTrue(!editDialogProperties.isUrlStatusVisible(), "URL status should not be visible");
        assertTrue(editDialogProperties.isUrlFieldInvalid(), "URL field should be invalid");


    }

    /**
     * URL : oEmbed : Flickr
     *
     * 1. verify all test URLs
     */
    @Test
    @DisplayName("URL : oEmbed : Flickr")
    public void testUrlOEmbedFlickr() throws InterruptedException, TimeoutException {
        OEmbed flickr = urlProcessors.getFlickr();
        String[] urls = flickr.getUrls();
        for(int i = 0; i < urls.length; i++) {
            verifyUrl(urls[i],flickr);
        }
    }

    /**
     * URL : oEmbed : SoundCloud
     *
     * 1. verify all test URLs
     */
    @Test
    @DisplayName("URL : oEmbed : SoundCloud")
    public void testUrlOEmbedSoundCloud() throws InterruptedException, TimeoutException {
        OEmbed soundCloud = urlProcessors.getSoundCloud();
        String[] urls = soundCloud.getUrls();
        for(int i = 0; i < urls.length; i++) {
            verifyUrl(urls[i],soundCloud);
        }
    }

    /**
     * URL : oEmbed : Twitter
     *
     * 1. verify all test URLs
     */
    @Test
    @DisplayName("URL : oEmbed : Twitter")
    public void testUrlOEmbedTwitter() throws InterruptedException, TimeoutException {
        OEmbed twitter = urlProcessors.getTwitter();
        String[] urls = twitter.getUrls();
        for(int i = 0; i < urls.length; i++) {
            verifyUrl(urls[i],twitter);
        }
    }

    /**
     * URL : oEmbed : YouTube
     *
     * 1. verify all test URLs
     */
    @Test
    @DisplayName("URL : oEmbed : YouTube")
    public void testUrlOEmbedYouTube() throws InterruptedException, TimeoutException {
        OEmbed youTube = urlProcessors.getYouTube();
        String[] urls = youTube.getUrls();
        for(int i = 0; i < urls.length; i++) {
            verifyUrl(urls[i],youTube);
        }
    }

    /**
     * Embeddable : YouTube
     *
     * 1. open the edit dialog
     * 2. switch type to embeddable
     * 3. save the edit dialog, verifying it's possible to submit when no embeddable is selected
     * 4. open the edit dialog
     * 5. switch type to embeddable
     * 6. select the YouTube embeddable
     * 7. save the edit dialog
     * 8. verify the dialog did not submit, as the Video ID field is required
     * 9. add a Video ID
     * 10. save the edit dialog
     * 11. verify the YouTube video on the page
     */
    @Test
    @DisplayName("Embeddable : YouTube")
    public void testEmbeddableYoutube() throws InterruptedException, TimeoutException {
        //1.
        Commons.openEditDialog(editorPage, cmpPath);

        //2.
        EditDialogProperties editDialogProperties = embed.getEmbedEditDialog().getProperties();
        editDialogProperties.setTypeRadio("embeddable");

        //3.
        Commons.saveConfigureDialog();

        //4.
        Commons.openEditDialog(editorPage, cmpPath);

        //5.
        editDialogProperties.setTypeRadio("embeddable");

        //6.
        editDialogProperties.setEmbeddableField(youtubeEmbedField);

        //7.
        Commons.saveConfigureDialog();

        //8.
        assertTrue(embed.getEmbedEditDialog().isVisible(),"Edit dialog should not be submit as the Video ID field is required");

        //9.
        editDialogProperties.setEmbeddableYoutubeVideoId("5vOOa3-fifY");

        //10.
        Commons.saveConfigureDialog();

        //11.
        Commons.switchContext("ContentFrame");
        assertTrue(embed.isYoutubeEmbedVisible(),"Embed Youtube visible should be visible");
        Commons.switchToDefaultContext();
    }

    /**
     * HTML
     *
     * 1. open the edit dialog
     * 2. switch type to html
     * 3. save the edit dialog
     * 4. verify the dialog did not submit, as the HTML field is required
     * 5. add an HTML embed code
     * 6. save the edit dialog
     * 7. verify the HTML embed code is present on the page
     */
    @Test
    @DisplayName("HTML")
    public void testHtmlEmbed() throws InterruptedException, TimeoutException {
        //1.
        Commons.openEditDialog(editorPage, cmpPath);

        //2.
        EditDialogProperties editDialogProperties = embed.getEmbedEditDialog().getProperties();
        editDialogProperties.setTypeRadio("html");

        //3.
        Commons.saveConfigureDialog();

        //4.
        assertTrue(embed.getEmbedEditDialog().isVisible(),"Edit dialog should not be submit as the Video ID field is required");

        //5.
        editDialogProperties.setHtmlField("<div id='CmpEmbedHtml'>HTML</div>");

        //6.
        Commons.saveConfigureDialog();

        //7.
        Commons.switchContext("ContentFrame");
        embed.htmlElementExists("#CmpEmbedHtml");
        Commons.switchToDefaultContext();
    }

}
