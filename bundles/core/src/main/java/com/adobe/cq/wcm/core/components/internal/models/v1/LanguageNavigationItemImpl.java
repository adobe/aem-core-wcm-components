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

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;

public class LanguageNavigationItemImpl extends NavigationItemImpl implements LanguageNavigationItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageNavigationItemImpl.class);

    protected String title;
    protected Locale locale;
    protected String country;
    protected String language;

    public LanguageNavigationItemImpl(Page page, boolean active, SlingHttpServletRequest request, int level, List<NavigationItem> children, String title, String parentId) {
        super(page, active, request, level, children, parentId);
        this.title = title;
    }

    @Override
    public String getTitle() { return title; }

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

    /*
     * DataLayerProvider implementation of field getters
     */

    @Override
    public String getDataLayerLanguage() {
        return getLanguage();
    }
}
