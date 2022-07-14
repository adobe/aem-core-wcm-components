/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.it.http;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.testing.clients.ClientException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveImageServletIT {
    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    static CQClient adminAuthor;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testAdaptiveImageServletPolicyDelegate() throws ClientException {
        // the requested image does not have a policy: the AIS throws an error
        adminAuthor.doGet("/content/core-components/adaptive-image-servlet/image-without-policy/jcr:content/root/container/image.coreimg.80.500.png", 404);
        // the AIS uses the policy of the delegated resource defined by the contentPolicyDelegatePath request parameter
        List<NameValuePair> parameters = new ArrayList();
        parameters.add(new BasicNameValuePair("contentPolicyDelegatePath", "/content/core-components/adaptive-image-servlet/image-with-policy/jcr:content/root/container/image"));
        adminAuthor.doGet("/content/core-components/adaptive-image-servlet/image-without-policy/jcr:content/root/container/image.coreimg.80.500.png/1657709625634.png", parameters, 200);
    }

}
