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
package com.adobe.cq.wcm.core.components.it.seljup.util;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.SecurityClient;
import com.adobe.cq.testing.client.security.AuthorizableManager;
import com.adobe.cq.testing.client.security.Group;
import com.adobe.cq.testing.client.security.User;
import com.adobe.cq.wcm.core.components.it.seljup.constant.WCMSanityConstants;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.poller.Polling;
import org.apache.sling.testing.junit.rules.instance.Instance;
import org.junit.Assert;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * Utility for managing user creation and deletion from instance.
 */
public class UserAdminUtil {

    private static final Logger LOG = LoggerFactory.getLogger(UserAdminUtil.class);


    /**
     * Deletes the user created by this module.
     *
     * @param adminClient  {@link CQClient} used for deleting other user
     * @param authorClient {@link CQClient} which need to be deleted
     * @param userID       userID of author client which need to be deleted
     * @throws ClientException      when something bad happens
     * @throws InterruptedException when user could not be deleted within given time
     */
    public static void deleteUser(CQClient adminClient, CQClient authorClient, String userID)
            throws ClientException, InterruptedException {

    }

    public static void createUser(final CQClient cqClient, List<String> groups, TestInfo testInfo)
            throws ClientException, InterruptedException {
       // String username = testInfo.getTestMethod().orElseThrow(Exception::new).getName()

    }
}
