/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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

package apps.core.wcm.components.list.v1.list;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(PowerMockRunner.class)
@PrepareForTest(List.class)
public class ListTest {

    public static final String ACTIVITIES_CHILDREN_LIST_ROOT = "/content/list/activities-children-list";
    public static final String ACTIVITIES_CHILDREN_LIST = "/content/list/activities-children-list/jcr:content/sidebar/list";

    @Rule
    public final AemContext context = new AemContext();

    @Test
    public void testListFromChildrenPages() {
        context.load().json("/children-list.json", ACTIVITIES_CHILDREN_LIST_ROOT);
        List list = setupListObject(ACTIVITIES_CHILDREN_LIST);
        list.activate();
        assertTrue(list.getItems().size() == 5);
    }

    private List setupListObject(String resourcePath) {
        final Resource resource = context.resourceResolver().getResource(resourcePath);
        ValueMap properties = resource.adaptTo(ValueMap.class);
        List list = new List();
        List spy = PowerMockito.spy(list);
        doReturn(resource).when(spy).getResource();
        doReturn(properties).when(spy).getProperties();
        doReturn(context.request()).when(spy).getRequest();
        doReturn(context.pageManager()).when(spy).getPageManager();
        return spy;
    }


}
