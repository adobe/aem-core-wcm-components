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
package com.adobe.cq.wcm.core.components.internal.models.v1.embeddable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Locale;

import javax.json.Json;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.embeddable.YouTube;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.scripting.WCMBindingsConstants;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(AemContextExtension.class)
class YouTubeImplTest {

    private static final String BASE = "/embed/embeddable/youtube";
    private static final String CONTENT_ROOT = "/content";
    private static final String ROOT_PAGE = "/content/youtube";
    private static final String GRID = ROOT_PAGE + "/jcr:content/root/responsivegrid";
    private static final String VIDEO_1 = "/video1";
    private static final String VIDEO_2 = "/video2";
    private static final String VIDEO_3 = "/video3";
    private static final String PATH_VIDEO_1 = GRID + VIDEO_1;
    private static final String PATH_VIDEO_2 = GRID + VIDEO_2;
    private static final String PATH_VIDEO_3 = GRID + VIDEO_3;

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @Mock
    private Style style;

    @Mock
    private Page page;

    @Mock
    private ComponentContext componentContext;
    
    @Mock
    private ComponentContext parentComponentContext;

    @Mock
    private EditContext editContext;

    @BeforeEach
    void setUp() {
        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        style = mock(Style.class);
        // by default all parameters configurable and by enabled
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_AUTOPLAY_ENABLED, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_AUTOPLAY_DEFAULT_VALUE, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_MUTE_ENABLED, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_MUTE_DEFAULT_VALUE, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_LOOP_ENABLED, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_LOOP_DEFAULT_VALUE, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_RELATED_VIDEOS_ENABLED, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_RELATED_VIDEOS_DEFAULT_VALUE, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_PLAYS_INLINE_ENABLED, false)).thenReturn(true);
        Mockito.lenient().when(style.get(YouTube.PN_DESIGN_PLAYS_INLINE_DEFAULT_VALUE, false)).thenReturn(true);
        Mockito.lenient().when(componentContext.getParent()).thenReturn(parentComponentContext);
    }

    @Test
    void testWithDefaultValues() throws URISyntaxException {
        Mockito.when(page.getLanguage()).thenReturn(Locale.US);
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_1, page);
        assertEquals(new URI("https://www.youtube.com/embed/2R2gb0MKJlo?origin=http%3A%2F%2Flocalhost&hl=en_US&mute=1&autoplay=1&loop=1&playlist=2R2gb0MKJlo&rel=1&playsinline=1"), youTube.getIFrameSrc(style).get());
        assertEquals("300", youTube.getIFrameWidth());
        assertEquals("200", youTube.getIFrameHeight());
        assertEquals("56.25", youTube.getIFrameAspectRatio());
        assertEquals("responsive", youTube.getLayout());
    }

    @Test
    void testWithDefaultValuesInEditMode() throws URISyntaxException {
        Mockito.when(page.getLanguage()).thenReturn(Locale.US);
        Mockito.when(parentComponentContext.getEditContext()).thenReturn(editContext);
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_1, page);
        // autoplay should not be set in edit mode
        assertEquals(new URI("https://www.youtube.com/embed/2R2gb0MKJlo?origin=http%3A%2F%2Flocalhost&hl=en_US&mute=1&loop=1&playlist=2R2gb0MKJlo&rel=1&playsinline=1"), youTube.getIFrameSrc(style).get());
        assertEquals("300", youTube.getIFrameWidth());
        assertEquals("200", youTube.getIFrameHeight());
        assertEquals("56.25", youTube.getIFrameAspectRatio());
        assertEquals("responsive", youTube.getLayout());
    }

    @Test
    void testWithOverriddenValues() throws URISyntaxException {
        Mockito.when(page.getLanguage()).thenReturn(Locale.GERMANY);
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_2, page);
        assertEquals(new URI("https://www.youtube.com/embed/2R2gb0MKJlo?origin=http%3A%2F%2Flocalhost&hl=de_DE&mute=0&autoplay=0&loop=0&rel=0&playsinline=0"), youTube.getIFrameSrc(style).get());
        assertNull(youTube.getIFrameWidth());
        assertNull(youTube.getIFrameHeight());
        assertNull(youTube.getIFrameAspectRatio());
        assertNull(youTube.getLayout());
    }

    @Test
    void testWithOverriddenValuesWithoutStyle() throws URISyntaxException {
        Mockito.when(page.getLanguage()).thenReturn(Locale.GERMANY);
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_2, page);
        assertEquals("https://www.youtube.com/embed/2R2gb0MKJlo?origin=http%3A%2F%2Flocalhost&hl=de_DE", youTube.getIFrameSrc());
        assertNull(youTube.getIFrameWidth());
        assertNull(youTube.getIFrameHeight());
        assertNull(youTube.getIFrameAspectRatio());
        assertNull(youTube.getLayout());
    }

    @Test
    void testWithAllOptionalFeaturesDisabled() throws URISyntaxException {
        Mockito.when(page.getLanguage()).thenReturn(Locale.US);
        Mockito.when(style.get(YouTube.PN_DESIGN_AUTOPLAY_ENABLED, false)).thenReturn(false);
        Mockito.when(style.get(YouTube.PN_DESIGN_MUTE_ENABLED, false)).thenReturn(false);
        Mockito.when(style.get(YouTube.PN_DESIGN_LOOP_ENABLED, false)).thenReturn(false);
        Mockito.when(style.get(YouTube.PN_DESIGN_RELATED_VIDEOS_ENABLED, false)).thenReturn(false);
        Mockito.when(style.get(YouTube.PN_DESIGN_PLAYS_INLINE_ENABLED, false)).thenReturn(false);
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_2, page);
        assertEquals(new URI("https://www.youtube.com/embed/2R2gb0MKJlo?origin=http%3A%2F%2Flocalhost&hl=en_US"), youTube.getIFrameSrc(style).get());
        assertNull(youTube.getIFrameWidth());
        assertNull(youTube.getIFrameHeight());
        assertNull(youTube.getIFrameAspectRatio());
        assertNull(youTube.getLayout());
    }

    @Test
    void testWithNoId() throws URISyntaxException {
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_3, page);
        assertFalse(youTube.getIFrameSrc(style).isPresent());
        assertNull(youTube.getIFrameWidth());
        assertNull(youTube.getIFrameHeight());
        assertNull(youTube.getIFrameAspectRatio());
        assertNull(youTube.getLayout());
    }

    @Test
    void testDataLayerJson() {
        Utils.enableDataLayer(context, true);
        String expected = "{\"embed-101c9bdb7f\":{\"@type\":\"core/wcm/components/embed/v1/embed\",\"embeddableProperties\":{\"youtubeVideoId\":\"2R2gb0MKJlo\"}}}";
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_1, context.currentPage(ROOT_PAGE));
        assertEquals(Json.createReader(new StringReader(expected)).read(),
                Json.createReader(new StringReader(youTube.getData().getJson())).read());
    }

    private YouTubeImpl getYouTubeUnderTest(String resourcePath, Page page) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(),
                context.bundleContext());
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, page);
        bindings.put(WCMBindingsConstants.NAME_COMPONENT_CONTEXT, componentContext);
        bindings.put(WCMBindingsConstants.NAME_PROPERTIES, resource.getValueMap());
        ((MockRequestPathInfo)request.getRequestPathInfo()).setResourcePath(resourcePath);
        request.setResource(resource);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        YouTubeImpl youTubeImpl = (YouTubeImpl)modelFactory.createModel(request, YouTube.class);
        return youTubeImpl;
    }

    @Test
    public void testGetStyleForWrappedResource() {
        YouTubeImpl youTube = getYouTubeUnderTest(PATH_VIDEO_3, page);
        Resource resource = context.resourceResolver().getResource(PATH_VIDEO_3);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + PATH_VIDEO_3 + "?");
        }
        context.contentPolicyMapping(resource.getResourceType(), Collections.singletonMap("test", "foo"));
        Style style = youTube.getStyleForWrappedResource(new ResourceWrapper(resource) {
            @Override
            public String getResourceType() {
                return "overriddenResourceType";
            }
        });
        assertEquals("foo", style.get("test", "bar"));
    }
}
