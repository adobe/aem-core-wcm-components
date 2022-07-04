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
package com.adobe.cq.wcm.core.components.commons.link;

import java.util.Objects;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_ACCESSIBILITY_LABEL;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TITLE_ATTRIBUTE;
import static com.adobe.cq.wcm.core.components.internal.link.LinkManagerImpl.PN_DISABLE_SHADOWING;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class LinkManagerTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private Page page;
    private Asset asset;

    @BeforeEach
    void setUp() {
        page = context.create().page("/content/links/site1/en/");
        asset = context.create().asset("/content/dam/asset1", 10, 10, "image/png");
        context.currentPage(page);
    }

    @Test
    void testResourceEmpty() {
        Resource linkResource = context.create().resource(page, "link");
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();
        assertFalse(link.isValid());
    }

    @Test
    void testResourceExternalLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost");
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertValidLink(link, "http://myhost");
        assertNull(link.getReference());
        assertEquals("http://myhost", link.getMappedURL());
    }

    @ParameterizedTest
    @ValueSource(strings = {"_blank", "_parent", "_top"})
    void testResourceExternalLinkWithAllowedTargetsAndAllAttributes(String target) {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost",
                PN_LINK_TARGET, target,
                PN_LINK_ACCESSIBILITY_LABEL, "My Host Label",
                PN_LINK_TITLE_ATTRIBUTE, "My Host Title");
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertValidLink(link, "http://myhost", "My Host Label", "My Host Title", target);
        assertNull(link.getReference());
    }

    @ParameterizedTest
    @ValueSource(strings = {"_self","_invalid"})
    void testResourceExternalLinkWithInvalidTargets(String target) {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost",
                PN_LINK_TARGET, target);
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        // invalid target or _self target should be stripped away
        assertValidLink(link, "http://myhost");
        assertNull(link.getReference());
    }

    @Test
    void testResourcePageLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, page.getPath());
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();
        assertValidLink(link, page.getPath() + ".html");
        assertEquals(page, link.getReference());
        assertEquals((page.getPath() + ".html").replaceAll("^\\/content\\/links\\/site1\\/(.+)","/content/site1/$1"),
                link.getMappedURL());
    }

    @Test
    void testMalformedURLLink() {
        String malformedURL = "https://a:80:b/c";
        Link link = getUnderTest().get("https://a:80:b/c").build();
        assertEquals(malformedURL, link.getURL());
    }

    @Test
    void testResourceInvalidPageLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "/content/non-existing");
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        // TODO: this link should be handled as invalid. but we keep this behavior for now to keep backwards compatibility
        assertValidLink(link, "/content/non-existing");
        assertNull(link.getReference());
    }

    @Test
    void testPageLink() {
        Link link = getUnderTest().get(page).build();

        assertValidLink(link, page.getPath() + ".html");
        assertEquals("https://example.org" + page.getPath() + ".html", link.getExternalizedURL());
        assertEquals(page, link.getReference());
    }

    @Test
    void testPageLink_Null() {
        Link link = getUnderTest().get((Page)null).build();

        assertFalse(link.isValid());
    }

    @Test
    void testEmptyLink() {
        Link link = getUnderTest().get("").withLinkTarget("").build();
        assertNull(link.getURL());
        assertNull(link.getMappedURL());
        assertNull(link.getExternalizedURL());
        assertFalse(link.isValid());
    }

    @Test
    void testLinkURLPageLinkWithTarget() {
        Link link = getUnderTest().get(page.getPath()).withLinkTarget("_blank").build();

        assertValidLink(link, page.getPath() + ".html", "_blank");
        assertEquals(page, link.getReference());
    }

    @Test
    void testLinkWithTargetAsset() {
        Link link = getUnderTest().get(asset).build();

        assertValidLink(link, asset.getPath());
        assertEquals(asset, link.getReference());
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
        Resource linkResource = context.create().resource(page, "link", PN_LINK_URL, targetPage1.getPath());
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertTrue(link.isValid());
        assertValidLink(link, targetPage2.getPath() + ".html");
        assertEquals("https://example.org" + targetPage2.getPath() + ".html", link.getExternalizedURL());
        assertEquals(targetPage2, link.getReference());
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
                PN_LINK_URL, targetPage1.getPath(),
                PN_DISABLE_SHADOWING, Boolean.TRUE
        );
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertTrue(link.isValid());
        assertValidLink(link, targetPage1.getPath() + ".html");
        assertEquals("https://example.org" + targetPage1.getPath() + ".html", link.getExternalizedURL());
        assertEquals(targetPage1, link.getReference());
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
                PN_LINK_URL, targetPage1.getPath(),
                ResourceResolver.PROPERTY_RESOURCE_TYPE, "/placeholder"
        );
        context.contentPolicyMapping("/placeholder", ImmutableMap.of(
                PN_DISABLE_SHADOWING, Boolean.TRUE
        ));
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertTrue(link.isValid());
        assertValidLink(link, targetPage1.getPath() + ".html");
        assertEquals("https://example.org" + targetPage1.getPath() + ".html", link.getExternalizedURL());
        assertEquals(targetPage1, link.getReference());
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
                PN_LINK_URL, targetPage1.getPath()
        );

        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertTrue(link.isValid());
        assertValidLink(link, "http://myhost");
        assertEquals(targetPage1, link.getReference());
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
        Resource linkResource = context.create().resource(page, "link", PN_LINK_URL, targetPage1.getPath());
        context.currentResource(linkResource);
        Link link = getUnderTest().get(linkResource).build();

        assertTrue(link.isValid());
        assertEquals(targetPage2, link.getReference());
    }

    private LinkManager getUnderTest() {
        return context.request().adaptTo(LinkManager.class);
    }

}
