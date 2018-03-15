/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.testing.MockAdapterFactory;
import com.adobe.cq.wcm.core.components.testing.MockResponsiveGrid;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplateManager;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class PageImplTest {

    private static String TEST_BASE = "/page";
    protected static final String CONTEXT_PATH = "/core";
    protected static final String ROOT = "/content/page";
    protected static final String PAGE = ROOT + "/templated-page";
    protected static final String DESIGN_PATH = "/etc/designs/mysite";
    protected static final String POLICIES_MAPPING_PATH = "policies/jcr:content";

    protected static final String FN_FAVICON_ICO = "favicon.ico";
    protected static final String FN_FAVICON_PNG = "favicon_32.png";
    protected static final String FN_TOUCH_ICON_60 = "touch-icon_60.png";
    protected static final String FN_TOUCH_ICON_76 = "touch-icon_76.png";
    protected static final String FN_TOUCH_ICON_120 = "touch-icon_120.png";
    protected static final String FN_TOUCH_ICON_152 = "touch-icon_152.png";

    protected static final String PN_FAVICON_ICO = "faviconIco";
    protected static final String PN_FAVICON_PNG = "faviconPng";
    protected static final String PN_TOUCH_ICON_60 = "touchIcon60";
    protected static final String PN_TOUCH_ICON_76 = "touchIcon76";
    protected static final String PN_TOUCH_ICON_120 = "touchIcon120";
    protected static final String PN_TOUCH_ICON_152 = "touchIcon152";

    protected static MockSlingHttpServletRequest request;
    protected static ResourceResolver resolver;
    protected static ContentPolicyManager contentPolicyManager;
    protected static TemplateManager templateManager;
    protected static ComponentContext componentContext;

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext();

    @BeforeClass
    public static void setUp() {
        internalSetUp(CONTEXT, TEST_BASE, ROOT);
    }

    @Before
    public void beforeTest() {
        resolver = spy(CONTEXT.resourceResolver());
        request = spy(new MockSlingHttpServletRequest(resolver, CONTEXT.bundleContext()));
    }

    protected static void internalSetUp(AemContext aemContext, String testBase, String contentRoot) {
        aemContext.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, contentRoot);
        contentPolicyManager = mock(ContentPolicyManager.class);
        aemContext.registerInjectActivateService(new MockAdapterFactory());
        aemContext.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
                new Function<ResourceResolver, ContentPolicyManager>() {
                    @Nullable
                    @Override
                    public ContentPolicyManager apply(@Nullable ResourceResolver resolver) {
                        return contentPolicyManager;
                    }
                });
        aemContext.addModelsForClasses(MockResponsiveGrid.class);
        aemContext.load().json(testBase + "/test-conf.json", "/conf/coretest/settings");
        aemContext.load().json(testBase + "/default-tags.json", "/etc/tags/default");
        SlingModelFilter slingModelFilter = mock(SlingModelFilter.class);
        aemContext.registerService(SlingModelFilter.class, slingModelFilter);
        aemContext.registerService(SlingModelFilter.class, new SlingModelFilter() {

            private final Set<String> IGNORED_NODE_NAMES = new HashSet<String>() {{
                add(NameConstants.NN_RESPONSIVE_CONFIG);
                add(MSMNameConstants.NT_LIVE_SYNC_CONFIG);
                add("cq:annotations");
            }};

            @Override
            public Map<String, Object> filterProperties(Map<String, Object> map) {
                return map;
            }

            @Override
            public Iterable<Resource> filterChildResources(Iterable<Resource> childResources) {
                return StreamSupport
                        .stream(childResources.spliterator(), false)
                        .filter(r -> !IGNORED_NODE_NAMES.contains(r.getName()))
                        .collect(Collectors.toList());
            }
        });
    }

    @Test
    public void testPage() throws ParseException {
        CONTEXT.load().binaryFile(TEST_BASE + "/" + FN_FAVICON_ICO, DESIGN_PATH + "/" + FN_FAVICON_ICO);
        CONTEXT.load().binaryFile(TEST_BASE + "/" + FN_FAVICON_PNG, DESIGN_PATH + "/" + FN_FAVICON_PNG);
        CONTEXT.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_60, DESIGN_PATH + "/" + FN_TOUCH_ICON_60);
        CONTEXT.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_76, DESIGN_PATH + "/" + FN_TOUCH_ICON_76);
        CONTEXT.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_120, DESIGN_PATH + "/" + FN_TOUCH_ICON_120);
        CONTEXT.load().binaryFile(TEST_BASE + "/" + FN_TOUCH_ICON_152, DESIGN_PATH + "/" + FN_TOUCH_ICON_152);
        CONTEXT.load().binaryFile(TEST_BASE + "/static.css", DESIGN_PATH + "/static.css");

        Page page = getPageUnderTest(Page.class, PAGE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        calendar.setTime(sdf.parse("2016-01-20T10:33:36.000+0100"));
        assertEquals(page.getLastModifiedDate(), calendar);
        assertEquals("en-GB", page.getLanguage());
        assertEquals("Templated Page", page.getTitle());
        assertEquals(DESIGN_PATH, page.getDesignPath());
        assertEquals(DESIGN_PATH + "/static.css", page.getStaticDesignPath());
        String[] keywordsArray = page.getKeywords();
        assertEquals(3, keywordsArray.length);
        Set<String> keywords = new HashSet<>(keywordsArray.length);
        keywords.addAll(Arrays.asList(keywordsArray));
        assertTrue(keywords.contains("one") && keywords.contains("two") && keywords.contains("three"));
        assertEquals("coretest.product-page", page.getClientLibCategories()[0]);
        assertEquals("product-page", page.getTemplateName());
        Utils.testJSONExport(page, Utils.getTestExporterJSONPath(TEST_BASE, PAGE));
    }

    @Test
    public void testFavicons() {
        Page page = getPageUnderTest(Page.class, PAGE);
        Map favicons = page.getFavicons();
        assertEquals(DESIGN_PATH + "/" + FN_FAVICON_ICO, favicons.get(PN_FAVICON_ICO));
        assertEquals(DESIGN_PATH + "/" + FN_FAVICON_PNG, favicons.get(PN_FAVICON_PNG));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_60, favicons.get(PN_TOUCH_ICON_60));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_76, favicons.get(PN_TOUCH_ICON_76));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_120, favicons.get(PN_TOUCH_ICON_120));
        assertEquals(DESIGN_PATH + "/" + FN_TOUCH_ICON_152, favicons.get(PN_TOUCH_ICON_152));
    }

    @Test
    public void testDefaultDesign() {
        Page page = getPageUnderTest(Page.class, PAGE, null);
        assertNull(page.getDesignPath());
        assertNull(page.getStaticDesignPath());
    }

    protected <T> T getPageUnderTest(Class<T> model, String pagePath) {
        return getPageUnderTest(model, pagePath, DESIGN_PATH);
    }

    protected <T> T getPageUnderTest(Class<T> model, String pagePath, String designPath) {
        return getPageUnderTest(model, pagePath, designPath, true);
    }

    protected <T> T getPageUnderTest(Class<T> model, String pagePath, String designPath, boolean withEditableTemplate) {
        Resource resource = CONTEXT.resourceResolver().getResource(pagePath + "/" + JcrConstants.JCR_CONTENT);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + pagePath + "?");
        }

        Resource resourceSpy = spy(resource);
        when(resourceSpy.listChildren()).thenReturn(resource.listChildren());

        com.day.cq.wcm.api.Page page = CONTEXT.pageManager().getPage(pagePath);
        com.day.cq.wcm.api.Page pageSpy = spy(page);
        componentContext = mock(ComponentContext.class);
        SlingBindings slingBindings = new SlingBindings();
        Design design = mock(Design.class);

        if (designPath != null) {
            when(design.getPath()).thenReturn(designPath);
        } else {
            when(design.getPath()).thenReturn(Designer.DEFAULT_DESIGN_PATH);
        }

        Resource templateResource = CONTEXT.resourceResolver().getResource("/conf/coretest/settings/wcm/templates/product-page");
        Template template = mock(Template.class);
        when(template.adaptTo(Resource.class)).thenReturn(templateResource);
        when(pageSpy.getTemplate()).thenReturn(template);
        when(template.hasStructureSupport()).thenReturn(withEditableTemplate);
        contentPolicyManager = mock(ContentPolicyManager.class);
        templateManager = mock(TemplateManager.class);
        when(resolver.adaptTo(TemplateManager.class)).thenReturn(templateManager);
        ContentPolicyMapping mapping = templateResource.getChild(POLICIES_MAPPING_PATH).adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        Style style;
        slingBindings.put(WCMBindings.CURRENT_DESIGN, design);

        if (contentPolicy != null) {
            Resource contentPolicyResource = CONTEXT.resourceResolver().getResource(contentPolicy.getPath());
            style = new MockStyle(contentPolicyResource, contentPolicyResource.adaptTo(ValueMap.class));
            when(contentPolicyManager.getPolicy(pageSpy.getContentResource())).thenReturn(contentPolicy);
        } else {
            style = mock(Style.class);
            when(style.get(anyString(), Matchers.anyObject())).thenAnswer(
                    invocationOnMock -> invocationOnMock.getArguments()[1]
            );
        }

        Set<String> cssClassNames = new LinkedHashSet<>(Arrays.asList("class1", "class2"));
        when(componentContext.getCssClassNames()).thenReturn(cssClassNames);
        slingBindings.put(WCMBindings.CURRENT_STYLE, style);
        slingBindings.put(SlingBindings.RESOLVER, request.getResourceResolver());
        slingBindings.put(WCMBindings.CURRENT_PAGE, pageSpy);
        slingBindings.put(WCMBindings.PAGE_MANAGER, CONTEXT.pageManager());
        slingBindings.put(SlingBindings.RESOURCE, resourceSpy);
        slingBindings.put(WCMBindings.PAGE_PROPERTIES, pageSpy.getProperties());
        slingBindings.put(WCMBindings.COMPONENT_CONTEXT, componentContext);
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resourceSpy);
        slingBindings.put(SlingBindings.REQUEST, request);
        when(request.getAttribute(eq("com.day.cq.wcm.componentcontext"))).thenReturn(componentContext);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(model);
    }

    private void testGetChildModel(boolean withEditableTemplate) {
        List<Resource> childResources = new ArrayList<>();
        Resource resource1 = mock(Resource.class);
        String resourceName = "test";
        when(resource1.getName()).thenReturn(resourceName);

        childResources.add(resource1);

        Page page = getPageUnderTest(Page.class, PAGE, DESIGN_PATH, withEditableTemplate);

        when(request.getResource().getChildren()).thenReturn(childResources);

        ModelFactory modelFactory = mock(ModelFactory.class);

        ComponentExporter componentExporter = mock(ComponentExporter.class);

        when(modelFactory.getModelFromWrappedRequest(eq(request), eq(resource1), eq(ComponentExporter.class))).thenReturn(componentExporter);

        Whitebox.setInternalState(page, "modelFactory", modelFactory);

        when(templateManager.getStructureResources(eq(componentContext))).thenReturn(childResources);

        Map<String, ? extends ComponentExporter> exportedItems = page.getExportedItems();

        Assert.assertEquals(childResources.size(), exportedItems.size());

        Assert.assertEquals(componentExporter, exportedItems.get(resourceName));
    }

    @Test
    public void testGetChildModelStaticTemplate() {
        testGetChildModel(false);
    }

    @Test
    public void testGetChildModelEditableTemplate() {
        testGetChildModel(true);
    }

    @Test
    public void testGetChildModelStructured() {
        List<Resource> structuredChildResources = new ArrayList<>();
        Resource resource1 = mock(Resource.class);
        String resourceName = "test";
        when(resource1.getName()).thenReturn(resourceName);

        structuredChildResources.add(resource1);

        Page page = getPageUnderTest(Page.class, PAGE);

        ModelFactory modelFactory = mock(ModelFactory.class);

        ComponentExporter componentExporter = mock(ComponentExporter.class);

        when(modelFactory.getModelFromWrappedRequest(eq(request), eq(resource1), eq(ComponentExporter.class))).thenReturn(componentExporter);

        Whitebox.setInternalState(page, "modelFactory", modelFactory);

        when(templateManager.getStructureResources(eq(componentContext))).thenReturn(structuredChildResources);

        Map<String, ? extends ComponentExporter> exportedItems = page.getExportedItems();

        Assert.assertEquals(structuredChildResources.size(), exportedItems.size());

        Assert.assertEquals(componentExporter, exportedItems.get(resourceName));
    }
}
