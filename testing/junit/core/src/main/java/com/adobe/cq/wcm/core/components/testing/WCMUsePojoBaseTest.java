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
package com.adobe.cq.wcm.core.components.testing;


import java.lang.reflect.ParameterizedType;
import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * The {@code WCMUsePojoBaseTest} class provides a set of utility methods for helping in testing Sightly use objects based on
 * {@link WCMUsePojo}.
 *
 * @param <T> the use object class to test
 */
@RunWith(PowerMockRunner.class)
public abstract class WCMUsePojoBaseTest<T extends WCMUsePojo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WCMUsePojoBaseTest.class);

    protected static String TEST_CONTENT_ROOT;
    protected static String TEST_APPS_ROOT;
    protected static String TEST_BASE = StringUtils.EMPTY;

    @Rule
    protected final AemContext context = new AemContext();

    public static final String TEST_CONTENT = "/test-content.json";
    public static final String TEST_APPS = "/test-apps.json";

    @Before
    public void setUp() {
        initTestResources(TEST_CONTENT_ROOT, TEST_CONTENT);
        initTestResources(TEST_APPS_ROOT, TEST_APPS);
        context.registerInjectActivateService(new MockAdapterFactory());
    }

    /**
     * Initialize test resources for the mocked objects
     *
     * @param rootPath path were the resources should be mounted in the JCR tree
     * @param jsonPath path to the json file to mount
     */
    protected void initTestResources(String rootPath, String jsonPath) {
        if (StringUtils.isNotEmpty(rootPath)) {
            try {
                context.load().json(TEST_BASE + jsonPath, rootPath);
            } catch (IllegalArgumentException e) {
                LOGGER.info("Attempted to load {} from classpath but did not find the resource.", jsonPath);
            }
        }
    }


    /**
     * Provides a spied object that can be used for further mocking.
     *
     * @return the spied object
     * @throws IllegalArgumentException if an object of type {@code T} cannot be instantiated
     */
    protected T getSpiedObject() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
        T real;
        try {
            real = type.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instantiate object of class " + type.getName(), e);
        }
        return spy(real);
    }

    /**
     * Provides a spied object that  can be used for further mocking backed-up by the {@link Resource} available at {@code resourcePath}.
     *
     * @param resourcePath the path to the {@link Resource}
     * @return the spied object
     */
    protected T getSpiedObject(String resourcePath) {
        T object = getSpiedObject();
        object.init(getResourceBackedBindings(resourcePath));
        return object;
    }

    /**
     * Retrieves the {@link Bindings} map associated with the {@link org.apache.sling.api.SlingHttpServletRequest} from the {@link
     * AemContext}. This map can be augmented and then passed to spy objects retrieved from {@link #getSpiedObject()} to initialise them.
     *
     * @return the bindings map
     */
    protected Bindings getDefaultSlingBindings() {
        SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        if (slingBindings != null) {
            return new SimpleBindings(slingBindings);
        }
        return new SimpleBindings();
    }

    /**
     * <p>
     * Creates a {@link Bindings} map initialised with the following default bindings available to Sightly use objects based on {@link
     * WCMUsePojo}:
     * </p>
     * <ul>
     * <li>{@link SlingBindings#RESOURCE}</li>
     * <li>{@link SlingBindings#REQUEST}</li>
     * <li>{@link SlingBindings#RESPONSE}</li>
     * <li>{@link WCMBindings#PROPERTIES}</li>
     * <li>{@link WCMBindings#WCM_MODE}</li>
     * <li>{@link WCMBindings#PAGE_MANAGER}</li>
     * <li>{@link WCMBindings#RESOURCE_PAGE}</li>
     * <li>{@link WCMBindings#CURRENT_PAGE}</li>
     * <li>{@link WCMBindings#PAGE_PROPERTIES}</li>
     * </ul>
     *
     * @param resourcePath the path to a resource already loaded in the testing context
     * @return the bindings map
     */
    protected Bindings getResourceBackedBindings(String resourcePath) {
        Bindings bindings = getDefaultSlingBindings();
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource != null) {
            ValueMap properties = resource.adaptTo(ValueMap.class);
            bindings.put(SlingBindings.RESOURCE, resource);
            bindings.put(WCMBindings.PROPERTIES, properties);
            bindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(context.request()));
            PageManager pageManager = context.pageManager();
            bindings.put(WCMBindings.PAGE_MANAGER, pageManager);

            context.request().setResource(resource);
            Page resourcePage = pageManager.getContainingPage(resource);
            if (resourcePage != null) {
                bindings.put(WCMBindings.RESOURCE_PAGE, resourcePage);
                bindings.put(WCMBindings.CURRENT_PAGE, resourcePage);
                bindings.put(WCMBindings.PAGE_PROPERTIES, properties);
            }
        } else {
            throw new IllegalArgumentException("Cannot find a resource at " + resourcePath);
        }
        return bindings;
    }

    /**
     * Sets the {@link WCMMode} for the mocked request.
     *
     * @param wcmMode the WCMMode to set
     */
    protected void setWCMMode(WCMMode wcmMode) {
        context.request().setAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME, wcmMode);
    }



}
