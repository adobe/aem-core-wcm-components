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
package com.adobe.cq.wcm.core.components.testing;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.commons.Language;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;

public class MockLanguageManager implements LanguageManager {

    @Override
    public String getIsoCountry(Locale locale) {
        return null;
    }

    @Override
    public Map<Locale, Info> getAdjacentInfo(ResourceResolver resourceResolver, String s) {
        return null;
    }

    @Override
    public Map<Language, Info> getAdjacentLanguageInfo(ResourceResolver resourceResolver, String s) {
        return null;
    }

    @Override
    public Locale getLanguage(Resource resource) {
        return null;
    }

    @Override
    public Language getCqLanguage(Resource resource) {
        return null;
    }

    @Override
    public Locale getLanguage(Resource resource, boolean b) {
        return null;
    }

    @Override
    public Language getCqLanguage(Resource resource, boolean b) {
        return null;
    }

    @Override
    public Page getLanguageRoot(Resource resource) {
        String rootPath = LanguageUtil.getLanguageRoot(resource.getPath());
        if (rootPath == null) {
            return null;
        }
        Resource languageRootResource = resource.getResourceResolver().getResource(rootPath);
        return languageRootResource != null ? languageRootResource.adaptTo(Page.class) : null;
    }

    @Override
    public Collection<Locale> getLanguages(ResourceResolver resourceResolver, String s) {
        return null;
    }

    @Override
    public Collection<Language> getCqLanguages(ResourceResolver resourceResolver, String s) {
        return null;
    }

    @Override
    public Collection<Page> getLanguageRoots(ResourceResolver resourceResolver, String s) {
        return null;
    }

    @Override
    public Tree compareLanguageTrees(ResourceResolver resourceResolver, String s) {
        return null;
    }
}
