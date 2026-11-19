/*
 * Copyright 2021 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adobe.cq.wcm.core.components.it.util;

import com.adobe.cq.testing.client.SecurityClient;
import com.adobe.cq.testing.client.security.CQAuthorizableManager;
import com.adobe.cq.testing.client.security.CQPreferences;
import com.adobe.cq.testing.client.security.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.util.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.adobe.cq.testing.client.security.CQPreferences.PREFERENCES_NODE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public final class DisableTour {

    static final String[] DEFAULT_TOURS = new String[]{
            "cq.authoring.editor.page.showTour62",
            "cq.authoring.editor.page.showOnboarding62",
            "cq.authoring.editor.template.showTour",
            "cq.authoring.editor.template.showOnboarding",
            "granite.shell.showonboarding620"
    };

    private final SlingClient client;
    private final String homePath;

    private JsonNode originalPreferences;

    public DisableTour(final SlingClient c) throws ClientException {
        client = c;
        SecurityClient securityClient = client.adaptTo(SecurityClient.class);
        String user = securityClient.getUser();
        CQAuthorizableManager authorizableManager = new CQAuthorizableManager(securityClient);
        User adminUser = authorizableManager.getUser(user);
        homePath = adminUser.getHomePath();
        originalPreferences = new CQPreferences(adminUser).getJson(HttpStatus.SC_OK);
    }

    /**
     * Uses the known home path of the client's user instead of looking the user up, as the authorizable query can lag
     * behind the creation of a new user (e.g. on AEM as a Cloud Service).
     */
    public DisableTour(final SlingClient c, final String userHomePath) throws ClientException {
        client = c;
        homePath = userHomePath;
        originalPreferences = JsonUtils.getJsonNodeFromString(
            client.doGet(homePath + "." + PREFERENCES_NODE + ".json", HttpStatus.SC_OK).getContent());
    }

    public void disableTours(final String... tourProperties) throws ClientException, UnsupportedEncodingException {
        ArrayList<NameValuePair> parameters = new ArrayList();
        Arrays.stream(tourProperties).forEach(tourProperty -> parameters.add(new BasicNameValuePair(tourProperty, "false")));
        HttpEntity entity = new UrlEncodedFormEntity(parameters);
        client.doPost(homePath + "/" + PREFERENCES_NODE, entity, HttpStatus.SC_OK, HttpStatus.SC_CREATED);
    }

    public void disableDefaultTours() throws UnsupportedEncodingException, ClientException {
        disableTours(DEFAULT_TOURS);
    }

    public void restoreDefaults() throws ClientException {
        StringEntity jsonEntity = new StringEntity(originalPreferences.toString(), APPLICATION_JSON);
        client.doPost(homePath + "/" + PREFERENCES_NODE, jsonEntity, HttpStatus.SC_OK, HttpStatus.SC_CREATED);
    }
}
