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
package com.adobe.cq.wcm.core.components.internal.link;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(AemContextExtension.class)
class LinkHandlerImplTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private Page page;

    @BeforeEach
    void setUp() {
        page = context.create().page("/content/links/site1/en/");
        context.currentPage(page);
    }

    /**
     * Get link for the given resource.
     *
     * @param linkResource The link resource.
     * @return The value of {@link LinkHandler#getLink(Resource)}.
     */
    private Optional<Link<Page>> getLinkUnderTest(@NotNull final Resource linkResource) {
        this.context.currentResource(linkResource);
        return Objects.requireNonNull(context.request().adaptTo(LinkHandler.class)).getLink(linkResource);
    }

    @Test
    void testResourceEmpty() {
        Resource linkResource = context.create().resource(page, "link");
        assertEquals(Optional.empty(), getLinkUnderTest(linkResource));
    }

    @Test
    void testResourceExternalLink() {
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, "http://myhost");
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), "http://myhost");
        assertNull(link.map(Link::getReference).orElse(null));
        assertEquals("http://myhost", link.get().getMappedURL());
    }

    @ParameterizedTest
    @ValueSource(strings = {"_blank", "_parent", "_top"})
    void testResourceExternalLinkWithAllowedTargetsAndAllAttributes(String target) {
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, "http://myhost",
            Link.PN_LINK_TARGET, target,
            Link.PN_LINK_ACCESSIBILITY_LABEL, "My Host Label",
            Link.PN_LINK_TITLE_ATTRIBUTE, "My Host Title");
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), "http://myhost", "My Host Label", "My Host Title", target);
        assertNull(link.map(Link::getReference).orElse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"_self", "_invalid"})
    void testResourceExternalLinkWithInvalidTargets(String target) {
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, "http://myhost",
            Link.PN_LINK_TARGET, target);
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        // invalid target or _self target should be stripped away
        assertTrue(link.isPresent());
        assertValidLink(link.get(), "http://myhost");
        assertNull(link.map(Link::getReference).orElse(null));
    }

    @Test
    void testResourcePageLink() {
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, page.getPath());
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), page.getPath() + ".html");
        assertEquals(page, link.map(Link::getReference).orElse(null));
        assertEquals((page.getPath() + ".html").replaceAll("^\\/content\\/links\\/site1\\/(.+)", "/content/site1/$1"),
            link.get().getMappedURL());
    }

    @Test
    void testMalformedURLLink() {
        String malformedURL = "https://a:80:b/c";
        Optional<Link<Page>> link = Objects.requireNonNull(context.request().adaptTo(LinkHandler.class)).getLink("https://a:80:b/c", null);

        assertTrue(link.isPresent());
        assertEquals(malformedURL, link.get().getURL());
    }

    @Test
    void testResourceInvalidPageLink() {
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, "/content/non-existing");
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        // TODO: this link should be handled as invalid. but we keep this behavior for now to keep backwards compatibility
        assertTrue(link.isPresent());
        assertValidLink(link.get(), "/content/non-existing");
        assertNull(link.get().getReference());
    }

    @Test
    void testPageLink() {
        Optional<Link<Page>> link = Objects.requireNonNull(context.request().adaptTo(LinkHandler.class)).getLink(page);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), page.getPath() + ".html");
        assertEquals("https://example.org" + page.getPath() + ".html", link.map(Link::getExternalizedURL).orElse(null));
        assertEquals(page, link.map(Link::getReference).orElse(null));
    }

    @Test
    void testPageLink_Null() {
        Optional<Link<Page>> link = Objects.requireNonNull(context.request().adaptTo(LinkHandler.class)).getLink((Page) null);

        assertFalse(link.isPresent());
    }

    @Test
    void testEmptyLink() {
        Optional<Link<Page>> link = Objects.requireNonNull(context.request().adaptTo(LinkHandler.class)).getLink("", "");
        if (link.isPresent()) {
            assertNull(link.get().getURL());
            assertNull(link.get().getMappedURL());
            assertNull(link.get().getExternalizedURL());
            assertFalse(link.get().isValid());
        } else {
            fail("noLink");
        }
    }

    @Test
    void testLinkURLPageLinkWithTarget() {
        Optional<Link<Page>> link = Objects.requireNonNull(context.request().adaptTo(LinkHandler.class)).getLink(page.getPath(), "_blank", null, null);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), page.getPath() + ".html", "_blank");
        assertEquals(page, link.map(Link::getReference).orElse(null));
    }

    /**
     * Tests a link whose target is a series of redirect pages.
     * This test confirms that link shadowing resolution functions properly.
     */
    @Test
    void testLinkWithRedirect() {
        // set up target pages
        Page targetPage1 = context.create().page(page.getPath() + "/target1");
        Page targetPage2 = context.create().page(page.getPath() + "/target2");

        // set up redirects
        Objects.requireNonNull(targetPage1.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, targetPage2.getPath());

        // create a link to the first target page
        Resource linkResource = context.create().resource(page, "link", Link.PN_LINK_URL, targetPage1.getPath());
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), targetPage2.getPath() + ".html");
        assertEquals("https://example.org" + targetPage2.getPath() + ".html", link.map(Link::getExternalizedURL).orElse(null));
        assertEquals(targetPage2, link.map(Link::getReference).orElse(null));
    }

    /**
     * Tests a link whose target is a series of redirect pages - but shadowing is disabled.
     * This test confirms the ability to disable shadowing by property on the link component.
     */
    @Test
    void testLinkWithRedirect_shadowingDisabledByProperty() {
        // set up target pages
        Page targetPage1 = context.create().page(page.getPath() + "/target1");
        Page targetPage2 = context.create().page(page.getPath() + "/target2");

        // set up redirects
        Objects.requireNonNull(targetPage1.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, targetPage2.getPath());

        // create a link to the first target page
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, targetPage1.getPath(),
            LinkHandler.PN_DISABLE_SHADOWING, Boolean.TRUE
        );
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), targetPage1.getPath() + ".html");
        assertEquals("https://example.org" + targetPage1.getPath() + ".html", link.map(Link::getExternalizedURL).orElse(null));
        assertEquals(targetPage1, link.map(Link::getReference).orElse(null));
    }

    /**
     * Tests a link whose target is a series of redirect pages - but shadowing is disabled.
     * This test confirms the ability to disable shadowing by the style policy.
     */
    @Test
    void testLinkWithRedirect_shadowingDisabledByStyle() {
        // set up target pages
        Page targetPage1 = context.create().page(page.getPath() + "/target1");
        Page targetPage2 = context.create().page(page.getPath() + "/target2");

        // set up redirects
        Objects.requireNonNull(targetPage1.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, targetPage2.getPath());

        // create a link to the first target page
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, targetPage1.getPath(),
            ResourceResolver.PROPERTY_RESOURCE_TYPE, "/placeholder"
        );
        this.context.contentPolicyMapping("/placeholder", ImmutableMap.of(
            LinkHandler.PN_DISABLE_SHADOWING, Boolean.TRUE
        ));
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), targetPage1.getPath() + ".html");
        assertEquals("https://example.org" + targetPage1.getPath() + ".html", link.map(Link::getExternalizedURL).orElse(null));
        assertEquals(targetPage1, link.map(Link::getReference).orElse(null));
    }

    /**
     * Tests the ability to resolve a link when the link points to a redirect page that subsequently redirects to
     * an external site. This external link is discovered during link shadowing resolution, and is thus a different
     * test than when the link its self points to an external site.
     */
    @Test
    void testLinkWithRedirectToExternal() {
        // set up target pages
        Page targetPage1 = context.create().page(page.getPath() + "/target1");

        // set up redirects
        Objects.requireNonNull(targetPage1.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, "http://myhost");

        // create a link to the first target page
        Resource linkResource = context.create().resource(page, "link",
            Link.PN_LINK_URL, targetPage1.getPath()
        );

        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertValidLink(link.get(), "http://myhost");
        assertEquals(targetPage1, link.map(Link::getReference).orElse(null));
    }

    /**
     * Tests that link shadowing does not get stuck when the link target page is a redirect loop.
     */
    @Test
    void testLinkWithRedirectLoop() {
        // create three pages
        Page targetPage1 = context.create().page(page.getPath() + "/target1");
        Page targetPage2 = context.create().page(page.getPath() + "/target2");
        Page targetPage3 = context.create().page(page.getPath() + "/target3");

        // set up a redirect loop. The cycle between two and three is intentional to prevent false
        // positive test if shadowing becomes disabled.
        Objects.requireNonNull(targetPage1.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, targetPage2.getPath());
        Objects.requireNonNull(targetPage2.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, targetPage3.getPath());
        Objects.requireNonNull(targetPage3.getContentResource().adaptTo(ModifiableValueMap.class)).put(PageImpl.PN_REDIRECT_TARGET, targetPage2.getPath());

        // create a link to the first target page
        Resource linkResource = context.create().resource(page, "link", Link.PN_LINK_URL, targetPage1.getPath());
        Optional<Link<Page>> link = getLinkUnderTest(linkResource);

        assertTrue(link.isPresent());
        assertEquals(targetPage2, link.get().getReference());
    }

}
