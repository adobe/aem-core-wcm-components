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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.testing.MockContentPolicyStyle;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExperienceFragmentImplTest {

    private static final String TEST_BASE = "/experiencefragment";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    private static final String CONTEXT_PATH = "/core";

    private static final String EXPERIENCE_FRAGMENT_ROOT = "/content/mysite";


    /* ------------------------------- Tests for a site without localization structure -----------------------------  */


    /***
     * No localization structure
     */
    @Test
    public void testExperienceFragment1() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/page/jcr:content/root/xf-component-1");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /***
     * No localization structure
     * localizationRoot is undefined
     */
    @Test
    public void testExperienceFragment2() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/page/jcr:content/root/xf-component-2");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /***
     * No localization structure
     * localizationDepth is undefined
     */
    @Test
    public void testExperienceFragment3() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/page/jcr:content/root/xf-component-3");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /***
     * No localization structure
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment4() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/page/jcr:content/root/xf-component-4");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf4"));
    }


    /* ------------------------------- Tests for a site with language localization -----------------------------  */


    /***
     * Site with language localization
     * XF component points to the same language branch as the page
     * localizationRoot and localizationDepth are undefined
     */
    @Test
    public void testExperienceFragment10() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-10");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /***
     * Site with language localization
     * XF component points to a different language branch as the page
     * localizationRoot and localizationDepth are undefined
     */
    @Test
    public void testExperienceFragment11() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-11");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf11"));
    }

    /***
     * Site with language localization
     * XF component points to a different language branch as the page
     * localizationRoot and localizationDepth are defined
     */
    @Test
    public void testExperienceFragment12() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-12");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf12"));
    }

    /***
     * Site with language localization
     * localizationRoot is undefined
     */
    @Test
    public void testExperienceFragment13() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-13");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /***
     * Site with language localization
     * localizationDepth is undefined
     */
    @Test
    public void testExperienceFragment14() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-14");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /***
     * Site with language localization
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment15() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-15");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf4"));
    }

    /***
     * Site with language localization
     * localizationDepth is wrong
     */
    @Test
    public void testExperienceFragment16() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-16");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf16"));
    }

    /***
     * Site with language localization
     * localizationRoot is wrong
     */
    @Test
    public void testExperienceFragment17() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-17");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf16"));
    }

    /***
     * Site with language localization
     * localizationRoot is wrong
     */
    @Test
    public void testExperienceFragment18() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/en/page/jcr:content/root/xf-component-18");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf16"));
    }



    /* ------------------------------- Tests for a site with country-language localization (us/en) -----------------  */


    /***
     * Site with country-language localization
     * XF component points to the same country-language branch as the page
     * localizationRoot and localizationDepth are undefined
     */
    @Test
    public void testExperienceFragment20() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/us/en/page/jcr:content/root/xf-component-20");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20"));
    }

    /***
     * Site with country-language localization
     * XF component points to a different country-language branch as the page
     * localizationRoot and localizationDepth are undefined
     */
    @Test
    public void testExperienceFragment21() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/us/en/page/jcr:content/root/xf-component-21");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf21"));
    }

    /***
     * Site with country-language localization
     * XF component points to a different country-language branch as the page
     * localizationRoot and localizationDepth are defined
     */
    @Test
    public void testExperienceFragment22() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/us/en/page/jcr:content/root/xf-component-22");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf22"));
    }

    /***
     * Site with country-language localization
     * localizationRoot is undefined
     */
    @Test
    public void testExperienceFragment23() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/us/en/page/jcr:content/root/xf-component-23");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20"));
    }

    /***
     * Site with country-language localization
     * localizationDepth is undefined
     */
    @Test
    public void testExperienceFragment24() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/us/en/page/jcr:content/root/xf-component-24");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20"));
    }

    /***
     * Site with country-language localization
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment25() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/us/en/page/jcr:content/root/xf-component-25");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf25"));
    }



    /* ----------------- Tests for a site with country-language localization (eu/mysite/en) -----------------------  */



    /***
     * Site with country-language localization (optional): eu/mysite/en
     * XF component points to the same country-language branch as the page
     * localizationRoot and localizationDepth are undefined
     */
    @Test
    public void testExperienceFragment30() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/eu/mysite/en/page/jcr:content/root/xf-component-30");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30"));
    }

    /***
     * Site with country-language localization (optional): eu/mysite/en
     * XF component points to a different country-language branch as the page
     * localizationRoot and localizationDepth are undefined
     *
     */
    @Test
    public void testExperienceFragment31() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/eu/mysite/en/page/jcr:content/root/xf-component-31");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf31"));
    }

    /***
     * Site with country-language localization (optional): eu/mysite/en
     * XF component points to a different country-language branch as the page
     * localizationRoot and localizationDepth are defined
     */
    @Test
    public void testExperienceFragment32() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/eu/mysite/en/page/jcr:content/root/xf-component-32");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf32"));
    }

    /***
     * Site with country-language localization (optional): eu/mysite/en
     * XF component points to the same country-language branch as the page
     * localizationRoot is undefined
     */
    @Test
    public void testExperienceFragment33() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/eu/mysite/en/page/jcr:content/root/xf-component-33");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30"));
    }

    /***
     * Site with country-language localization (optional): eu/mysite/en
     * XF component points to the same country-language branch as the page
     * localizationDepth is undefined
     */
    @Test
    public void testExperienceFragment34() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/eu/mysite/en/page/jcr:content/root/xf-component-34");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30"));
    }

    /***
     * Site with country-language localization (optional): eu/mysite/en
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment35() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EXPERIENCE_FRAGMENT_ROOT
            + "/eu/mysite/en/page/jcr:content/root/xf-component-35");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf35"));
    }



    private ExperienceFragment getExperienceFragmentUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        ContentPolicyMapping mapping = resource.adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = null;
        if (mapping != null) {
            contentPolicy = mapping.getPolicy();
        }
        final MockSlingHttpServletRequest request =
            new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        Page currentPage = AEM_CONTEXT.pageManager().getContainingPage(resource);
        SlingBindings slingBindings = new SlingBindings();
        Style currentStyle;
        if (contentPolicy != null) {
            ContentPolicyManager policyManager = mock(ContentPolicyManager.class);
            when(policyManager.getPolicy(resource)).thenReturn(contentPolicy);
            currentStyle = new MockContentPolicyStyle(contentPolicy);
        } else {
            currentStyle = mock(Style.class);
            when(currentStyle.get(anyString(), (Object) Matchers.anyObject())).thenAnswer(
                invocation -> invocation.getArguments()[1]
            );
        }
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        slingBindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        slingBindings.put(WCMBindings.CURRENT_STYLE, currentStyle);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(ExperienceFragment.class);
    }

}
