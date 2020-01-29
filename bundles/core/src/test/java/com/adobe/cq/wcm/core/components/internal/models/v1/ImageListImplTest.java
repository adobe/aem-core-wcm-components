/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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


import java.util.Collection;
import java.util.HashMap;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ImageListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ImageListImplTest {

    private static final String TEST_BASE = "/imagelist";
    private static final String CONTENT_ROOT = "/content";
    private static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    private static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/imagelist";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String LINK = "https://www.adobe.com";
    private static final String IMAGELIST_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/imagelist-1";
    private static final String IMAGELIST_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/imagelist-2";
   
    private static final Object[][] EXPECTED_IMAGE_LIST_DEPTH_2 = {
        {"/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", "http://www.adobe.com", "Adobe"},
        {"/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", "https://www.google.com/", "ImageList"},
};
    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content/dam/core/images");
        AEM_CONTEXT.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
    }

    @Test
    public void testFullyConfiguredImageList() {
        ImageListImpl imageList = getImageListUnderTest(IMAGELIST_1);
        Collection<ImageListItem> listItems = imageList.getListItems();
        int index =0;
        for(ImageListItem imageItem: listItems){
        	verifyImageListItems(EXPECTED_IMAGE_LIST_DEPTH_2, imageItem, index);
        	index ++;
        }
        Utils.testJSONExport(imageList, Utils.getTestExporterJSONPath(TEST_BASE, "imagelist1"));
    }
    
    @Test
    public void testNullImageListItem() {
        ImageListImpl imageList = getImageListUnderTest(IMAGELIST_2);
        Collection<ImageListItem> listItems = imageList.getListItems();
        for(ImageListItem imageItem: listItems){
        	verifyNullImageListItems(imageItem);
        }
    }
    
    private void verifyImageListItems(Object[][] expectedimageList, ImageListItem item, int index) {
            assertEquals("The image item's path is not what was expected: " + item.getImagePath(), expectedimageList[index][0], item.getImagePath());
            assertEquals("The image item's url is not what was expected: " + item.getLinkURL(), expectedimageList[index][1], item.getLinkURL());
            assertEquals("The image item's text is not what was expected: " + item.getLinkText(), expectedimageList[index][2], item.getLinkText());
            item.setImagePath(expectedimageList[index][0].toString());
            item.setLinkURL(expectedimageList[index][1].toString());
            item.setLinkText(expectedimageList[index][2].toString());
    }
    
    private void verifyNullImageListItems(ImageListItem item) {
        assertNull("The image item's path is not what was expected: " + item.getImagePath(), item.getImagePath());
        assertNull("The image item's url is not what was expected: " + item.getLinkURL(), item.getLinkURL());
        assertNull("The image item's text is not what was expected: " + item.getLinkText(), item.getLinkText());
}
    private ImageListImpl getImageListUnderTest(String resourcePath) {
        return getImageListUnderTest(resourcePath, null);
    }

    private ImageListImpl getImageListUnderTest(String resourcePath, Style currentStyle) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        if (currentStyle != null) {
            slingBindings.put(WCMBindings.CURRENT_STYLE, currentStyle);
        }
        Component component = mock(Component.class);
        when(component.getProperties()).thenReturn(new ValueMapDecorator(new HashMap<String, Object>() {{
            put(AbstractImageDelegatingModel.IMAGE_DELEGATE, "core/wcm/components/image/v2/image");
        }}));
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(ImageListImpl.class);
    }

    private void testImageResourceValueMap(ValueMap valueMap) {
        assertFalse(valueMap.containsKey(JcrConstants.JCR_TITLE));
        assertFalse(valueMap.containsKey(JcrConstants.JCR_DESCRIPTION));
    }
}
