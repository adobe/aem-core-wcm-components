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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveCopy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ExperienceFragmentImplTest {

    private static final String TEST_BASE = "/experiencefragment";
    private static final String CONTEXT_PATH = "/core";
    private static final String APPS_ROOT = "/apps";
    private static final String CONTENT_ROOT = "/content";
    private static final String SITE_ROOT = CONTENT_ROOT + "/mysite";
    private static final String CONF_ROOT = "/conf/coretest/settings";
    private static final String PRODUCT_PAGE_TEMPLATE = CONF_ROOT + "/wcm/templates/product-page";
    private static final String NO_LOC_PAGE = SITE_ROOT + "/page";
    private static final String EN_PAGE = SITE_ROOT + "/en/page";
    private static final String US_EN_PAGE = SITE_ROOT + "/us/en/page";
    private static final String CH_MYSITE_FR_PAGE = SITE_ROOT + "/ch/mysite/fr/page";
    private static final String CH_FR_PAGE = SITE_ROOT + "/ch_fr/page";
    private static final String BLUEPRINT_ROOT = "/content/mysite/blueprint";
    private static final String BLUEPRINT_PAGE = BLUEPRINT_ROOT + "/page";
    private static final String LIVECOPY_ROOT = "/content/mysite/livecopy";
    private static final String LIVECOPY_PAGE = LIVECOPY_ROOT + "/page";
    private static final String XF_NAME = "footer";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() throws WCMException {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONF_JSON, CONF_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);
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
                    final Iterator<LiveRelationship> iterator = relationships.iterator();
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
        context.registerService(LiveRelationshipManager.class, relationshipManager);
    }


    /* ------------------------------- Tests for a site with no localization structure -----------------------------  */


    /**
     * No localization structure
     * XF component is defined in the page
     * fragmentVariationPath is valid
     */
    @Test
    void testValidXFInPageWithoutLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-1");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1"));
    }

    /**
     * Tests that methods that cache results return the same object on subsequent calls.
     */
    @Test
    void testCaching() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-1");
        assertNotNull(experienceFragment.getName());
        assertSame(experienceFragment.getName(), experienceFragment.getName());

        assertNotNull(experienceFragment.getCssClassNames());
        assertSame(experienceFragment.getCssClassNames(), experienceFragment.getCssClassNames());

        assertNotNull(experienceFragment.getExportedItems());
        assertSame(experienceFragment.getExportedItems(), experienceFragment.getExportedItems());

        assertNotNull(experienceFragment.getLocalizedFragmentVariationPath());
        assertSame(experienceFragment.getLocalizedFragmentVariationPath(), experienceFragment.getLocalizedFragmentVariationPath());
    }

    /**
     * No localization structure
     * XF component is defined in the template
     * fragmentVariationPath is valid
     */
    @Test
    void testValidXFInTemplateWithoutLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-1a", NO_LOC_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf1a"));
    }

    /**
     * No localization structure
     * XF component is defined in the page
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInPageWithoutLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-2");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf2"));
    }

    /**
     * No localization structure
     * XF component is defined in the template
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInTemplateWithoutLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-2a", NO_LOC_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf2a"));
    }

    /**
     * No localization structure
     * XF component is defined in the page
     * fragmentVariationPath is empty
     */
    @Test
    void testEmptyXFInPageWithoutLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(NO_LOC_PAGE
            + "/jcr:content/root/xf-component-3");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf3"));
    }

    /**
     * No localization structure
     * XF component is defined in the template
     * fragmentVariationPath is empty
     */
    @Test
    void testEmptyXFInTemplateWithoutLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-3a", NO_LOC_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf3a"));
    }


    /* ------------------------------- Tests for a site with language localization (/en) --------------------------  */


    /**
     * Site with language localization
     * XF component is defined in the page
     * XF component points to the same language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithSameLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-10");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf10"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * XF component points to the same language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithSameLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-10a", EN_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf10a"));
    }

    /**
     * Site with language localization
     * XF component is defined in the page
     * XF component points to a different language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithDifferentLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-11");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf11"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * XF component points to a different language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithDifferentLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-11a", EN_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf11a"));
    }

    /**
     * Site with language localization
     * XF component is defined in the page
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInPageWithLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-12");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf12"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInTemplateWithLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-12a", EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf12a"));
    }

    /**
     * Site with language localization
     * XF component is defined in the page
     * fragmentVariationPath is empty
     */
    @Test
    void testEmptyXFInPageWithLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(EN_PAGE
            + "/jcr:content/root/xf-component-13");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf13"));
    }

    /**
     * Site with language localization
     * XF component is defined in the template
     * fragmentVariationPath is empty
     */
    @Test
    void testEmptyXFInTemplateWithLocalization() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-13a", EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf13a"));
    }


    /* ------------------------------- Tests for a site with country/language localization (us/en) -----------------  */


    /**
     * Site with country-language localization
     * XF component is defined in the page
     * XF component points to the same country-language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithSameCountryLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(US_EN_PAGE
            + "/jcr:content/root/xf-component-20");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the template
     * XF component points to the same country-language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithSameCountryLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-20a", US_EN_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf20a"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the page
     * XF component points to a different country-language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithDifferentCountryLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(US_EN_PAGE
            + "/jcr:content/root/xf-component-21");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf21"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the template
     * XF component points to a different country-language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithDifferentCountryLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-21a", US_EN_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf21a"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the page
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInPageWithLocalizationWithDifferentCountryLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(US_EN_PAGE
            + "/jcr:content/root/xf-component-22");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf22"));
    }

    /**
     * Site with country-language localization
     * XF component is defined in the template
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInTemplateWithLocalizationWithDifferentCountryLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-22a", US_EN_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf22a"));
    }


    /* ----------------- Tests for a site with country-language localization (eu/mysite/en) -----------------------  */


    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the page
     * XF component points to the same country-language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithSameCountrySiteLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_MYSITE_FR_PAGE
            + "/jcr:content/root/xf-component-30");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the template
     * XF component points to the same country-language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithSameCountrySiteLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-30a", CH_MYSITE_FR_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf30a"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the page
     * XF component points to a different country-language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithDifferentCountrySiteLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_MYSITE_FR_PAGE
            + "/jcr:content/root/xf-component-31");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf31"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the template
     * XF component points to a different country-language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithDifferentCountrySiteLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-31a", CH_MYSITE_FR_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf31a"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the page
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInPageWithLocalizationWithDifferentCountrySiteLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_MYSITE_FR_PAGE
            + "/jcr:content/root/xf-component-32");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf32"));
    }

    /**
     * Site with country-language localization (optional): eu/mysite/en
     * XF component is defined in the template
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInTemplateWithLocalizationWithDifferentCountrySiteLanguage() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-32a", CH_MYSITE_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf32a"));
    }


    /* ------------------------------- Tests for a site with country_language localization (ch_fr) -----------------  */


    /**
     * Site with country_language localization
     * XF component is defined in the page
     * XF component points to the same country_language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithSameCountry_Language() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_FR_PAGE
            + "/jcr:content/root/xf-component-40");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf40"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the template
     * XF component points to the same country_language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithSameCountry_Language() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-40a", CH_FR_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf40a"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the page
     * XF component points to a different country_language branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithDifferentCountry_Language() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_FR_PAGE
            + "/jcr:content/root/xf-component-41");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf41"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the template
     * XF component points to a different country_language branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithDifferentCountry_Language() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-41a", CH_FR_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf41a"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the page
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInPageWithLocalizationWithDifferentCountry_Language() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(CH_FR_PAGE
            + "/jcr:content/root/xf-component-42");
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf42"));
    }

    /**
     * Site with country_language localization
     * XF component is defined in the template
     * fragmentVariationPath is undefined
     */
    @Test
    void testUndefinedXFInTemplateWithLocalizationWithDifferentCountry_Language() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-42a", CH_FR_PAGE);
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf42a"));
    }


    /* ------------------------------- Tests for a site with blueprint / live copy -----------------  */


    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the page
     * XF component points to the same region branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithSameBlueprint() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(BLUEPRINT_PAGE
            + "/jcr:content/root/xf-component-50");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf50"));
    }

    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the template
     * XF component points to the same region branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithSameBlueprint() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-50a", BLUEPRINT_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf50a"));
    }

    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the page
     * XF component points to a different region branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithDifferentBlueprint() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(BLUEPRINT_PAGE
            + "/jcr:content/root/xf-component-51");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf51"));
    }

    /**
     * Site with region localization (current page is a blueprint)
     * XF component is defined in the template
     * XF component points to a different region branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithDifferentBlueprint() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-51a", BLUEPRINT_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf51a"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the page
     * XF component points to the same region branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithSameLivecopy() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(LIVECOPY_PAGE
            + "/jcr:content/root/xf-component-60");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf60"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the template
     * XF component points to the same region branch as the page
     */
    @Test
    void testValidXFInTemplateLocalizationWithSameLivecopy() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-60a", LIVECOPY_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf60a"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the page
     * XF component points to a different region branch as the page
     */
    @Test
    void testValidXFInPageWithLocalizationWithDifferentLivecopy() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(LIVECOPY_PAGE
            + "/jcr:content/root/xf-component-61");
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf61"));
    }

    /**
     * Site with region localization (current page is a livecopy)
     * XF component is defined in the template
     * XF component points to a different region branch as the page
     */
    @Test
    void testValidXFInTemplateWithLocalizationWithDifferentLivecopy() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
            PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-61a", LIVECOPY_PAGE);
        assertEquals(XF_NAME, experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf61a"));
    }

    /**
     * XF with content
     */
    @Test
    void testXFWithItems() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
                PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-70", LIVECOPY_PAGE);
        assertEquals("header", experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf70"));
    }

    /**
     * Nested XFs
     */
    @Test
    void testNestedXFs() {
        ExperienceFragment experienceFragment = getExperienceFragmentUnderTest(
                PRODUCT_PAGE_TEMPLATE + "/structure/jcr:content/xf-component-72", LIVECOPY_PAGE);
        assertEquals("parent", experienceFragment.getName());
        Utils.testJSONExport(experienceFragment, Utils.getTestExporterJSONPath(TEST_BASE, "xf72"));
    }


    /* ------------------------------- private stuff -----------------------------------------  */


    private ExperienceFragment getExperienceFragmentUnderTest(String resourcePath) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        String currentPagePath = context.pageManager().getContainingPage(resource).getPath();
        return getExperienceFragmentUnderTest(resourcePath, currentPagePath);
    }

    private ExperienceFragment getExperienceFragmentUnderTest(String resourcePath, String currentPagePath) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        context.currentPage(currentPagePath);
        context.currentResource(resource);
        context.request().setContextPath(CONTEXT_PATH);
        return context.request().adaptTo(ExperienceFragment.class);
    }
}
