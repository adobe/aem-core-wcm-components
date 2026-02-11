/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

/**
 * V1 Language Navigation Item implementation.
 */
public class LanguageNavigationItemImpl extends NavigationItemImpl implements LanguageNavigationItem {

    /**
     * Navigation item title.
     */
    private final String title;

    /**
     * Locale for this language navigation item.
     */
    private Locale locale;

    /**
     * Country for this language navigation item.
     */
    private String country;

    /**
     * Language for this language navigation item.
     */
    private String language;

    /**
     * Construct a Language Navigation Item.
     *
     * @param page             The page for which to create a navigation item.
     * @param active           Flag indicating if the navigation item is active.
     * @param current          Flag indicating if the navigation item is current page.
     * @param linkManager      Link manager service.
     * @param level            Depth level of the navigation item.
     * @param childrenSupplier The child navigation items supplier.
     * @param title            The item title.
     * @param parentId         ID of the parent navigation component.
     * @param component        The parent navigation {@link Component}.
     */
    public LanguageNavigationItemImpl(@NotNull final Page page,
                                      final boolean active,
                                      final boolean current,
                                      @NotNull final LinkManager linkManager,
                                      final int level,
                                      @NotNull final Supplier<List<NavigationItem>> childrenSupplier,
                                      final String title,
                                      final String parentId,
                                      final Component component) {
        super(page, active, current, linkManager, level, childrenSupplier, parentId, component);
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Locale getLocale() {
        if (locale == null) {
            // looks up jcr:language properties to the root, then considers the page name, falls back to system default
            // we therefore assume the language structure is correctly configured for the site for this to be accurate
            locale = page.getLanguage(false);
        }
        return locale;
    }

    @Override
    public String getCountry() {
        if (country == null) {
            country = page.getLanguage(false).getCountry();
        }
        return country;
    }

    @Override
    public String getLanguage() {
        if (language == null) {
            // uses hyphens to ensure it's hreflang valid
            language = page.getLanguage(false).toString().replace('_', '-');
        }
        return language;
    }

    @Override
    @NotNull
    protected final PageData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asPage()
            .withLanguage(this::getLanguage)
            .build();
    }
}
