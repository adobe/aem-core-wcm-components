/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1.custom;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.adobe.cq.wcm.core.components.models.custom.ChildFilter;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ChildFilter.class
)
public class ChildFilterImpl implements ChildFilter {

    @Inject
    @Named(OPTION_RESOURCE_TYPES)
    Collection<String> resourceTypes;

    @Inject
    @Named(OPTION_FILTER_REGEX)
    String filterRegex;

    @Inject
    @Named(OPTION_INHERITED)
    @Default(booleanValues = OPTION_INHERITED_DEFAULT)
    boolean inherited;

    @ScriptVariable
    //TODO: will this resolver work on publish?
    private ResourceResolver resolver;

    private Pattern pattern;
    private List<String> paths;

    @PostConstruct
    public void init() {
        pattern = Pattern.compile(filterRegex);
    }

    @Override
    public List<String> getPaths() {
        if (paths == null) {
            paths = new LinkedList<>();

            for (String resourceType : resourceTypes) {
                Resource componentResource = getResource(resourceType);
                addPaths(componentResource, paths);

                if (inherited && componentResource != null) {
                    addPaths(getResource(componentResource.getResourceSuperType()), paths);
                }
            }
        }
        return paths;
    }

    private void addPaths(Resource resource, Collection<String> paths) {
        if (resource == null) {
            return;
        }
        for (Resource child : resource.getChildren()) {
            if (pattern.matcher(child.getName()).matches()) {
                paths.add(child.getPath());
            }
        }
    }

    private Resource getResource(String path) {
        if (path == null) {
            return null;
        }
        return resolver.getResource(path);
    }
}
