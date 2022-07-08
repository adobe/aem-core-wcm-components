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
package com.adobe.cq.wcm.core.components.internal.services.seo;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.aem.wcm.seo.localization.SiteRootSelectionStrategy;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.LanguageNavigationImpl;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.xf.ExperienceFragmentsConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class LanguageNavigationSiteRootSelectionStrategyTest {

    public final AemContext aemContext = CoreComponentTestContext.newAemContext();
    private final LanguageNavigationSiteRootSelectionStrategy subject = new LanguageNavigationSiteRootSelectionStrategy();

    @Mock
    private LiveRelationshipManager liveRelationshipManager;

    @BeforeEach
    protected void setUp() {
        internalSetup();
        aemContext.registerService(LiveRelationshipManager.class, liveRelationshipManager);
        // Causing a StackOverflowError because of SLING-10616
        // aemContext.registerInjectActivateService(subject);
    }

    protected void internalSetup() {
        aemContext.load().json("/languagenavigation" + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        aemContext.load().json("/languagenavigation/test-conf.json", "/conf");
    }

    @Test
    void testSiteRootFromPage() throws PersistenceException {
        // given
        Page page = aemContext.pageManager().getPage("/content/languagenavigation/LOCALE-1/LOCALE-5/about");
        deleteSiblings(page.getContentResource().getChild("root/languagenavigation-component-2"));

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNotNull(siteRoot);
        assertEquals("/content/languagenavigation", siteRoot.getPath());
        assertEquals(2, structureDepth);
    }

    @Test
    void testSiteRootFromPageV2() throws PersistenceException {
        // given
        Page page = aemContext.pageManager().getPage("/content/languagenavigation/LOCALE-1/LOCALE-5/about");
        Resource languageNav = page.getContentResource().getChild("root/languagenavigation-component-2");
        deleteSiblings(languageNav);
        ModifiableValueMap properties = languageNav.adaptTo(ModifiableValueMap.class);
        properties.put("sling:resourceType", com.adobe.cq.wcm.core.components.internal.models.v2.LanguageNavigationImpl.RESOURCE_TYPE);
        languageNav.getResourceResolver().commit();

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNotNull(siteRoot);
        assertEquals("/content/languagenavigation", siteRoot.getPath());
        assertEquals(2, structureDepth);
    }

    @Test
    void testSiteRootFromPageWithDefaultStructureDepth() throws PersistenceException {
        // given
        Page page = aemContext.pageManager().getPage("/content/languagenavigation/LOCALE-1/LOCALE-5/about");
        deleteSiblings(page.getContentResource().getChild("root/languagenavigation-component-1"));

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNotNull(siteRoot);
        assertEquals("/content/languagenavigation", siteRoot.getPath());
        assertEquals(1, structureDepth);
    }

    @Test
    void testSiteRootFromContentPolicy() throws PersistenceException {
        // given
        Page page = aemContext.pageManager().getPage("/content/languagenavigation/LOCALE-1/LOCALE-5/about");
        deleteSiblings(page.getContentResource().getChild("root/languagenavigation-component-3"));
        aemContext.contentPolicyMapping(LanguageNavigationImpl.RESOURCE_TYPE,
            "siteRoot", "/content/languagenavigation",
            "structureDepth", 3);

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNotNull(siteRoot);
        assertEquals("/content/languagenavigation", siteRoot.getPath());
        assertEquals(3, structureDepth);
    }

    @Test
    void testNoSiteRoot() throws PersistenceException {
        // given
        Page page = aemContext.pageManager().getPage("/content/languagenavigation/LOCALE-1/LOCALE-5/about");
        deleteSiblings(page.getContentResource().getChild("root/languagenavigation-component-4"));

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNull(siteRoot);
        assertEquals(1, structureDepth);
    }

    @Test
    void testNoSiteRootOnPageWithoutLanguageNav() {
        // given
        Page page = aemContext.create().page("/content/languagenavigation/LOCALE-X/LOCALE-Y");
        aemContext.create().resource(page, "root");
        aemContext.create().resource(page,"anyComponent");

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNull(siteRoot);
        assertEquals(1, structureDepth);
    }

    /**
     * Intermediate pages exist, when a descendant got published but its parent not. They miss the jcr:content node
     */
    @Test
    void testNoSiteRootOnIntermediatePage() {
        // given
        Page page = aemContext.create().resource("/content/languagenavigation/LOCALE-X/LOCALE-Y",
            "jcr:primaryType", "cq:Page").adaptTo(Page.class);

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNull(siteRoot);
        assertEquals(1, structureDepth);
    }

    @Test
    void testNoSiteRootFallbackToDefault() throws PersistenceException, NoSuchFieldException, IllegalAccessException {
        // given
        // replace with service injection after SLING-10616 was released
        DefaultSiteRootSelectionStrategy defaultSiteRootSelectionStrategy = spy(new DefaultSiteRootSelectionStrategy());
        Field defaultSelectionStrategy = subject.getClass().getDeclaredField("defaultSelectionStrategy");
        defaultSelectionStrategy.setAccessible(true);
        defaultSelectionStrategy.set(subject, defaultSiteRootSelectionStrategy);
        Page page = aemContext.pageManager().getPage("/content/languagenavigation/LOCALE-1/LOCALE-5/about");
        deleteSiblings(page.getContentResource().getChild("root/languagenavigation-component-4"));

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertEquals("/content/languagenavigation", siteRoot.getPath());
        assertEquals(2, structureDepth);
        verify(defaultSiteRootSelectionStrategy).getSiteRoot(page);
        verify(defaultSiteRootSelectionStrategy).getStructuralDepth(page);
    }

    @Test
    void testSiteRootFromPageWithExperienceFragment() {
        // given
        Page page = aemContext.create().page("/content/languagenavigation/LOCALE-1/LOCALE-5/about-2");
        Page variant = aemContext.create().page("/content/experience-fragments/fragment/master", "xf",
            ExperienceFragmentsConstants.PN_XF_VARIANT_TYPE, "web");
        String contentPath = page.getContentResource().getPath();
        Resource pageRoot = aemContext.create().resource(contentPath + "/root");
        Resource pageRootXf = aemContext.create().resource(contentPath + "/root/xf",
            "sling:resourceType", ExperienceFragmentImpl.RESOURCE_TYPE_V2,
            ExperienceFragment.PN_FRAGMENT_VARIATION_PATH, variant.getPath());
        String variantContentPath = variant.getContentResource().getPath();
        Resource variantRoot = aemContext.create().resource(variantContentPath + "/root");
        Resource variantRootLanguageNav = aemContext.create().resource(variantContentPath + "/root/languageNavigation",
            "sling:resourceType", LanguageNavigationImpl.RESOURCE_TYPE,
            LanguageNavigation.PN_NAVIGATION_ROOT, "/content/languagenavigation",
            LanguageNavigation.PN_STRUCTURE_DEPTH, 3
        );

        // when
        Page siteRoot = subject.getSiteRoot(page);
        int structureDepth = subject.getStructuralDepth(page);

        // then
        assertNotNull(siteRoot);
        assertEquals("/content/languagenavigation", siteRoot.getPath());
        assertEquals(3, structureDepth);
    }

    static void deleteSiblings(Resource resource) throws PersistenceException {
        Resource parent = resource.getParent();
        ResourceResolver resolver = resource.getResourceResolver();
        for (Iterator<Resource> it = parent.listChildren(); it.hasNext(); ) {
            Resource sibling = it.next();
            if (!sibling.getPath().equals(resource.getPath())) {
                resolver.delete(sibling);
            }
        }
        resolver.commit();
    }

    private static class DefaultSiteRootSelectionStrategy implements SiteRootSelectionStrategy {
        @Override public @Nullable Page getSiteRoot(@NotNull Page page) {
            return page.getAbsoluteParent(1);
        }

        @Override public int getStructuralDepth(@NotNull Page page) {
            return 2;
        }
    }
}
