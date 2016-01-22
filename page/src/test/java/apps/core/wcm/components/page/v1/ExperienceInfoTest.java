/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.page.v1;

import java.util.Calendar;
import javax.script.Bindings;

import org.apache.jackrabbit.util.ISO8601;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;

import apps.core.wcm.components.page.v1.page.ExperienceInfo;
import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.spy;

@PrepareForTest(ExperienceInfo.class)
public class ExperienceInfoTest extends WCMUsePojoBaseTest<ExperienceInfo> {

    static {
        TEST_ROOT = "/content/mysite";
    }

    public static final String TEMPLATED_PAGE = TEST_ROOT + "/templated-page";
    public static final String TESTED_RESOURCE = TEMPLATED_PAGE + "/" + JcrConstants.JCR_CONTENT;

    @Test
    public void testExperience() throws Exception {
        ExperienceInfo experienceInfo = getSpiedObject();
        Bindings bindings = getResourceBackedBindings(TESTED_RESOURCE);
        /**
         * need to spy the actual value map since the mock doesn't do the same Calendar to String conversion as {@link JcrValueMap
         */
        final ValueMap properties = (ValueMap) bindings.get(WCMBindings.PROPERTIES);
        ValueMap spiedProperties = spy(properties);
        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                Calendar calendar = properties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
                return ISO8601.format(calendar);
            }
        }).when(spiedProperties).get(NameConstants.PN_PAGE_LAST_MOD, String.class);
        bindings.put(WCMBindings.PROPERTIES, spiedProperties);
        experienceInfo.init(bindings);
        assertEquals("2016-01-20T10:33:36.000+01:00", experienceInfo.getLastModifiedDate());
        assertEquals(TEMPLATED_PAGE, experienceInfo.getAnalyzeUrl());
        assertEquals("Templated Page", experienceInfo.getExperienceTitle());
        assertEquals("Description", experienceInfo.getDescription());
        assertEquals("templated-page", experienceInfo.getId());
    }
}
