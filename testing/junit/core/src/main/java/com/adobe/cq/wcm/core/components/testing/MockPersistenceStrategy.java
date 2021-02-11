/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.util.regex.Pattern;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.impl.def.DefaultConfigurationPersistenceStrategy;
import org.apache.sling.caconfig.spi.ConfigurationPersistenceStrategy2;
import org.osgi.service.component.annotations.Component;

@Component(service = {ConfigurationPersistenceStrategy2.class})
public class MockPersistenceStrategy extends DefaultConfigurationPersistenceStrategy {

    private static final String JCR_CONTENT = "jcr:content";
    private static final Pattern JCR_CONTENT_PATTERN = Pattern.compile("^(.*/)?" + Pattern.quote(JCR_CONTENT) + "(/.*)?$");

    @Override
    public Resource getResource(final Resource resource) {
        if (containsJcrContent(resource.getPath())) {
            return resource;
        }
        Resource result = resource.getChild(JCR_CONTENT);
        if ( result == null ) {
            result = resource;
        }
        return result;
    }

    @Override
    public String getConfigName(String configName, String relatedConfigPath) {
        if (containsJcrContent(configName)) {
            return configName;
        }
        return configName + "/" + JCR_CONTENT;
    }

    private static boolean containsJcrContent(String path) {
        if (path == null) {
            return false;
        }
        return JCR_CONTENT_PATTERN.matcher(path).matches();
    }
}
