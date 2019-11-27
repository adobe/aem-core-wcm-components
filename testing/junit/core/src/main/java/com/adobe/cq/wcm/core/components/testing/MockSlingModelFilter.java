/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.export.json.SlingModelFilter;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.msm.api.MSMNameConstants;

public class MockSlingModelFilter implements SlingModelFilter {
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
}
