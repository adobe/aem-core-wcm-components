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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Search;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SearchImplTest {

	private static final String TEST_BASE = "/searchresult";

	@Rule
	public AemContext context = CoreComponentTestContext.createContext(TEST_BASE, "/content");

	private static final String TEST_ROOT = "/content/en/searchresult/page-template";

	private SlingBindings slingBindings;

	@Before
	public void setUp() {
		slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
		slingBindings.put(WCMBindings.CURRENT_STYLE,
				slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName()));
		slingBindings.put(WCMBindings.CURRENT_PAGE, context.currentPage("/content/en/searchresult/page-template"));
		context.load().json("/searchresult/test-etc.json", "/etc/tags/searchresult");
	}

	@Test
	public void testSearchProperties() throws Exception {
		Resource resource = context.currentResource(TEST_ROOT + "/jcr:content/searchfine");
		slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
		slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
		Search search = context.request().adaptTo(Search.class);
		assertEquals(10, search.getResultsSize());
		assertEquals(3, search.getSearchTermMinimumLength());
		assertEquals("/jcr:content/searchfine", search.getRelativePath());
		assertEquals("ASC", search.getAscLabel());
		assertEquals("DESC", search.getDescLabel());
		assertEquals(false, search.isFacetEnabled());
		assertEquals(false, search.isSortEnabled());
		assertEquals("Load More", search.getLoadMoreText());
		assertEquals("No more results", search.getNoResultText());
		assertEquals("Start Search", search.getSortTitle());
		assertEquals("Start Search", search.getTagProperty());
		assertEquals(0, search.getGuessTotal());
		assertEquals(true, search.getShowResultCount());
		assertEquals("Start Search", search.getFacetTitle());
		Utils.testJSONExport(search, Utils.getTestExporterJSONPath(TEST_BASE, "search2"));
	}

}