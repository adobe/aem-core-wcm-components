/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import java.util.ArrayList;
import java.util.Iterator;

import javax.jcr.RangeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.testing.MockContentPolicyStyle;
import com.adobe.cq.wcm.core.components.testing.MockLanguageManager;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.day.cq.wcm.msm.api.LiveCopy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExperienceFragmentImplTest {

    private static final String TEST_BASE = "/experiencefragment";
    private static final String CONTEXT_PATH = "/core";
    private static final String SITE_ROOT = "/content/mysite";
    private static final String PRODUCT_PAGE_TEMPLATE = "/conf/coretest/settings/wcm/templates/product-page";
    private static final String NO_LOC_PAGE = SITE_ROOT + "/page";
    private static final String EN_PAGE = SITE_ROOT + "/en/page";
    private static final String US_EN_PAGE = SITE_ROOT + "/us/en/page";
    private static final String CH_MYSITE_FR_PAGE = SITE_ROOT + "/ch/mysite/fr/page";
    private static final String CH_FR_PAGE = SITE_ROOT + "/ch_fr/page";
    private static final String BLUEPRINT_ROOT = "/content/mysite/blueprint";
    private static final String BLUEPRINT_PAGE = BLUEPRINT_ROOT + "/page";
    private static final String LIVECOPY_ROOT = "/content/mysite/livecopy";
    private static final String LIVECOPY_PAGE = LIVECOPY_ROOT + "/page";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    @BeforeClass
    public static void init() throws WCMException {
        AEM_CONTEXT.load().json(TEST_BASE + "/test-conf.json", "/conf/coretest/settings");
        AEM_CONTEXT.registerService(LanguageManager.class, new MockLanguageManager());
        LiveRelationshipManager relationshipManager = mock(LiveRelationshipManager.class);
        when(relationshipManager.isSource(any(Resource.class))).then(
            invocation -> {
                Object[] arguments = invocation.getArguments();
                Resource resource = (Resource) arguments[0];
                return BLUEPRINT_PAGE.equals(resource.getPath());
            }
        );
        when(relationshipManager.getLiveRelationships(any(Resource.class), isNull(), isNull())).then(
            invocation -> {
                Object[] arguments = invocation.getArguments();
                Resource resource = (Resource) arguments[0];
                if (BLUEPRINT_PAGE.equals(resource.getPath())) {
                    LiveRelationship liveRelationship = mock(LiveRelationship.class);
                    LiveCopy liveCopy = mock(LiveCopy.class);
                    when(liveCopy.getBlueprintPath()).thenReturn(BLUEPRINT_ROOT);
                    when(liveRelationship.getLiveCopy()).thenReturn(liveCopy);
                    final ArrayList<LiveRelationship> relationships = new ArrayList<>();
                    relationships.add(liveRelationship);
                    final Iterator iterator = relationships.iterator();
                    return new RangeIterator() {

                        int index = 0;

                        @Override
                        public void skip(long skipNum) {

                        }

                        @Override
                        public long getSize() {
                            return relationships.size();
                        }

                        @Override
                        public long getPosition() {
                            return index;
                        }

                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Object next() {
                            index++;
                            return iterator.next();
                        }
                    };
                }
                return null;
            }
        );
        when(relationshipManager.hasLiveRelationship(any(Resource.class))).then(
            invocation -> {
                Object[] arguments = invocation.getArguments();
                Resource resource = (Resource) arguments[0];
                return LIVECOPY_PAGE.equals(resource.getPath());
            }
        );
        when(relationshipManager.getLiveRelationship(any(Resource.class), anyBoolean())).then(
            invocation -> {
                Object[] arguments = invocation.getArguments();
                Resource resource = (Resource) arguments[0];
                if (LIVECOPY_PAGE.equals(resource.getPath())) {
                    LiveRelationship liveRelationship = mock(LiveRelationship.class);
                    LiveCopy liveCopy = mock(LiveCopy.class);
                    when(liveCopy.getPath()).thenReturn(LIVECOPY_ROOT);
                    when(liveRelationship.getLiveCopy()).thenReturn(liveCopy);
                    return liveRelationship;
                }
                return null;
            }
        );
        AEM_CONTEXT.registerService(LiveRelationshipManager.class, relationshipManager);
    }


    /* ------------------------------- Tests for a site with no localization structure -----------------------------  */


    /**
     * No localization structure
     * XF component is defined in the page
     * fragmentPath is valid
     */
    @Test
    public void testExperienceFragment1() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-1");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /**
     * No localization structure
     * XF component is defined in the template
     * fragmentPath is valid
     */
    @Test
    public void testExperienceFragment1a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-1a", NO_LOC_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /**
     * No localization structure
     * XF component is defined in the page
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment2() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-2");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf2"));
    }

    /**
     * No localization structure
     * XF component is defined in the template
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment2a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-2a", NO_LOC_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf2"));
    }

    /**
     * No localization structure
     * XF component is defined in the page
     * fragmentPath is empty
     */
    @Test
    public void testExperienceFragment3() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-3");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf3"));
    }

    /**
     * No localization structure
     * XF component is defined in the template
     * fragmentPath is empty
     */
    @Test
    public void testExperienceFragment3a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-3a", NO_LOC_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf3"));
    }


    /* ------------------------------- Tests for a site with language localization (/en) --------------------------  */


    /**
     * Site with language localization
     * XF component is defined in the page
     * XF component points to the same language branch as the page
     */
    @Test
    public void testExperienceFragment10() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-10");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf10"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * XF component points to the same language branch as the page
     */
    @Test
    public void testExperienceFragment10a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-10a", EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf10"));
    }

    /**
     * Site with language localization
     * XF component is defined in the page
     * XF component points to a different language branch as the page
     */
    @Test
    public void testExperienceFragment11() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-11");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf11"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * XF component points to a different language branch as the page
     */
    @Test
    public void testExperienceFragment11a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-11a", EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf11a"));
    }

    /**
     * Site with language localization
     * XF component is defined in the page
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment12() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-12");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf12"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment12a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-12a", EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf12"));
    }

    /**
     * Site with language localization
     * XF component is defined in the page
     * fragmentPath is empty
     */
    @Test
    public void testExperienceFragment13() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-13");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf13"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * fragmentPath is empty
     */
    @Test
    public void testExperienceFragment13a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-13a", EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf13"));
    }


    /* ------------------------------- Tests for a site with country/language localization (us/en) -----------------  */


    /**
     * Site with country-language localization
     * XF component is defined in the page
     * XF component points to the same country-language branch as the page
     */
    @Test
    public void testExperienceFragment20() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(US_EN_PAGE
            + "/jcr:content/root/xf-component-20");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the template
     * XF component points to the same country-language branch as the page
     */
    @Test
    public void testExperienceFragment20a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-20a", US_EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the page
     * XF component points to a different country-language branch as the page
     */
    @Test
    public void testExperienceFragment21() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(US_EN_PAGE
            + "/jcr:content/root/xf-component-21");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf21"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the template
     * XF component points to a different country-language branch as the page
     */
    @Test
    public void testExperienceFragment21a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-21a", US_EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf21a"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the page
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment22() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(US_EN_PAGE
            + "/jcr:content/root/xf-component-22");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf22"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the template
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment22a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-22a", US_EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf22"));
    }


    /* ----------------- Tests for a site with country-language localization (eu/mysite/en) -----------------------  */


    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the page
     * XF component points to the same country-language branch as the page
     */
    @Test
    public void testExperienceFragment30() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_MYSITE_FR_PAGE
            + "/jcr:content/root/xf-component-30");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the template
     * XF component points to the same country-language branch as the page
     */
    @Test
    public void testExperienceFragment30a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-30a", CH_MYSITE_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the page
     * XF component points to a different country-language branch as the page
     */
    @Test
    public void testExperienceFragment31() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_MYSITE_FR_PAGE
            + "/jcr:content/root/xf-component-31");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf31"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the template
     * XF component points to a different country-language branch as the page
     */
    @Test
    public void testExperienceFragment31a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-31a", CH_MYSITE_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf31a"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the page
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment32() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_MYSITE_FR_PAGE
            + "/jcr:content/root/xf-component-32");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf32"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the template
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment32a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-32a", CH_MYSITE_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf32"));
    }


    /* ------------------------------- Tests for a site with country_language localization (ch_fr) -----------------  */


    /**
     * Site with country_language localization
     * XF component is defined in the page
     * XF component points to the same country_language branch as the page
     */
    @Test
    public void testExperienceFragment40() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_FR_PAGE
            + "/jcr:content/root/xf-component-40");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf40"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the template
     * XF component points to the same country_language branch as the page
     */
    @Test
    public void testExperienceFragment40a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-40a", CH_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf40"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the page
     * XF component points to a different country_language branch as the page
     */
    @Test
    public void testExperienceFragment41() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_FR_PAGE
            + "/jcr:content/root/xf-component-41");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf41"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the template
     * XF component points to a different country_language branch as the page
     */
    @Test
    public void testExperienceFragment41a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-41a", CH_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf41a"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the page
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment42() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_FR_PAGE
            + "/jcr:content/root/xf-component-42");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf42"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the template
     * fragmentPath is undefined
     */
    @Test
    public void testExperienceFragment42a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-42a", CH_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf42"));
    }


    /* ------------------------------- Tests for a site with blueprint / live copy -----------------  */


    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the page
     * XF component points to the same region branch as the page
     */
    @Test
    public void testExperienceFragment50() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(BLUEPRINT_PAGE
            + "/jcr:content/root/xf-component-50");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf50"));
    }

    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the template
     * XF component points to the same region branch as the page
     */
    @Test
    public void testExperienceFragment50a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-50a", BLUEPRINT_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf50"));
    }

    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the page
     * XF component points to a different region branch as the page
     */
    @Test
    public void testExperienceFragment51() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(BLUEPRINT_PAGE
            + "/jcr:content/root/xf-component-51");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf51"));
    }

    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the template
     * XF component points to a different region branch as the page
     */
    @Test
    public void testExperienceFragment51a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-51a", BLUEPRINT_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf51a"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the page
     * XF component points to the same region branch as the page
     */
    @Test
    public void testExperienceFragment60() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(LIVECOPY_PAGE
            + "/jcr:content/root/xf-component-60");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf60"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the template
     * XF component points to the same region branch as the page
     */
    @Test
    public void testExperienceFragment60a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-60a", LIVECOPY_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf60"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the page
     * XF component points to a different region branch as the page
     */
    @Test
    public void testExperienceFragment61() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(LIVECOPY_PAGE
            + "/jcr:content/root/xf-component-61");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf61"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the template
     * XF component points to a different region branch as the page
     */
    @Test
    public void testExperienceFragment61a() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-61a", LIVECOPY_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf61a"));
    }


    /* ------------------------------- private stuff -----------------------------------------  */


    private ExperienceFragment getExperienceFragmentUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        String currentPagePath = AEM_CONTEXT.pageManager().getContainingPage(resource).getPath();
        return getExperienceFragmentUnderTest(resourcePath, currentPagePath);
    }

    private ExperienceFragment getExperienceFragmentUnderTest(String resourcePath, String currentPagePath) {
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
        Page currentPage = AEM_CONTEXT.pageManager().getPage(currentPagePath);
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        slingBindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        slingBindings.put(WCMBindings.CURRENT_STYLE, currentStyle);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(ExperienceFragment.class);
    }

}
