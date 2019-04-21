/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import static org.junit.Assert.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.testing.MockLanguageManager;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

import io.wcm.testing.mock.aem.junit.AemContext;

public class ExperienceFragmentImplTest {

    private static final String TEST_BASE = "/experiencefragment";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(
        TEST_BASE, "/content");

    private static final String CONTEXT_PATH = "/core";

    private static final String EXPERIENCE_FRAGMENT_ROOT = "/content/experiencefragment";

    @BeforeClass
    public static void init() throws WCMException {
        AEM_CONTEXT.registerService(LanguageManager.class,
            new MockLanguageManager());
    }

    @Test
    public void testInvalidXfVariationPath() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            EXPERIENCE_FRAGMENT_ROOT
                + "/en/page/jcr:content/root/expfragment-component-1");
        String expFragmentPath = experienceFragment.getExperienceFragmentVariationPath();
        assertEquals(null, expFragmentPath);

        Utils.testJSONExport(experienceFragment,
            Utils.getTestExporterJSONPath(TEST_BASE, "experiencefragment1"));

    }

    @Test
    public void testValidXfVariationPath() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            EXPERIENCE_FRAGMENT_ROOT
                + "/en/page/jcr:content/root/expfragment-component-2");
        String expFragmentPath = experienceFragment.getExperienceFragmentVariationPath();
        assertEquals(
            "/content/experience-fragments/language-masters/en/footer/master/jcr:content",
            expFragmentPath);

        Utils.testJSONExport(experienceFragment,
            Utils.getTestExporterJSONPath(TEST_BASE, "experiencefragment2"));

    }

    @Test
    public void testValidLanguageXfVariationPath() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            EXPERIENCE_FRAGMENT_ROOT
                + "/es/page/jcr:content/root/expfragment-component-1");
        String expFragmentPath = experienceFragment.getExperienceFragmentVariationPath();
        assertEquals(
            "/content/experience-fragments/language-masters/es/footer/master/jcr:content",
            expFragmentPath);

        Utils.testJSONExport(experienceFragment,
            Utils.getTestExporterJSONPath(TEST_BASE, "experiencefragment3"));

    }

    @Test
    public void testInvalidLanguageXfVariationPath() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            EXPERIENCE_FRAGMENT_ROOT
                + "/es/page/jcr:content/root/expfragment-component-2");
        String expFragmentPath = experienceFragment.getExperienceFragmentVariationPath();
        assertEquals("/content/experience-fragments/language-masters/en/footer/variation/jcr:content", expFragmentPath);

        Utils.testJSONExport(experienceFragment,
            Utils.getTestExporterJSONPath(TEST_BASE, "experiencefragment4"));

    }

    @Test
    public void testXfVariationPathWhenPageStructureFollowsLocale() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            EXPERIENCE_FRAGMENT_ROOT
                + "/it-it/page/jcr:content/root/expfragment-component-1");
        String expFragmentPath = experienceFragment.getExperienceFragmentVariationPath();
        assertEquals(
            "/content/experience-fragments/it-it/footer/master/jcr:content",
            expFragmentPath);

        Utils.testJSONExport(experienceFragment,
            Utils.getTestExporterJSONPath(TEST_BASE, "experiencefragment5"));

    }

    private ExperienceFragment getExperienceFragmentUnderTest(
            String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(
            resourcePath);
        if (resource == null) {
            throw new IllegalStateException(
                "Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(
            AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        Page currentPage = AEM_CONTEXT.pageManager().getContainingPage(
            resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        slingBindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(ExperienceFragment.class);
    }

}
