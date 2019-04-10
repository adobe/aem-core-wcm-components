/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Modal;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;

import io.wcm.testing.mock.aem.junit.AemContext;

public class ModalImplTest {

	private static final String TEST_BASE = "/modal";
	private static final String CONTENT_ROOT = "/content";
	private static final String CONTEXT_PATH = "/core";
	private static final String TEST_ROOT_PAGE = "/content/modal";
	private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
	private static final String MODAL_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/modal-1";
	private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

	@Rule
	public final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

	@Before
	public void init() {
		AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
		AEM_CONTEXT.registerService(SlingModelFilter.class, new MockSlingModelFilter());
	}

	@Test
	public void testModalProperties() {
		Modal modal = getModalUnderTest(MODAL_1);
		assertNotNull("Modal Id should not be null", modal.getModalId());
		assertEquals("Modal Description is not as expected", new String("Terms & Conditions Modal"),
				modal.getDescription());
		assertNotNull("Modal Description should not be null", modal.getShowModalByDefault());
		assertEquals("Modal showModalByDefault value is not as expected", new Boolean(false),
				modal.getShowModalByDefault());
		assertEquals("Modal fragmentType is not as expected", new String("xf"), modal.getFragmentType());
		assertEquals("Modal contentFragmentPath is not as expected", new String("/content/dam/modalcontentfragment"),
				modal.getContentFragmentPath());
		assertEquals("The experienceFragmentPath is not as expected",
				new String("/content/experience-fragments/mid_markets/mmfxtest/master").concat(".html"),
				modal.getExperienceFragmentPath());

	}

	private Modal getModalUnderTest(String resourcePath) {
		Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
		if (resource == null) {
			throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
		}
		AEM_CONTEXT.currentResource(resource);
		AEM_CONTEXT.request().setContextPath(CONTEXT_PATH);
		return AEM_CONTEXT.request().adaptTo(Modal.class);
	}

}
