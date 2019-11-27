/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.testing;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.xf.social.ExperienceFragmentSocialVariation;
import com.day.cq.wcm.api.Page;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockXFFactory {

    private static final String IMAGE_RT = "wcm/foundation/components/image";

    public static ExperienceFragmentSocialVariation getExperienceFragmentSocialVariation(Page page) {
        ExperienceFragmentSocialVariation socialVariation = mock(ExperienceFragmentSocialVariation.class);
        StringBuilder stringBuilder = new StringBuilder();
        String image = null;
        for (Resource resource : page.getContentResource().getChild("root").getChildren()) {
            if (resource.isResourceType(IMAGE_RT) && StringUtils.isEmpty(image)) {
                image = resource.getValueMap().get("fileReference", String.class);
            }
            String text = resource.getValueMap().get("text", String.class);
            if (StringUtils.isNotEmpty(text)) {
                stringBuilder.append(text);
            }
        }
        when(socialVariation.getText()).thenReturn(stringBuilder.toString());
        when(socialVariation.getImagePath()).thenReturn(image);
        return socialVariation;
    }

}
