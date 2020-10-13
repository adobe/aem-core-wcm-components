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
package com.adobe.cq.wcm.core.components.testing;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component(
        service = ConfigurationResourceResolver.class
)
public class MockConfigurationResourceResolver implements ConfigurationResourceResolver {

    private ResourceResolver resourceResolver;
    private String configRoot;

    public MockConfigurationResourceResolver(ResourceResolver resourceResolver, String configRoot) {
        this.resourceResolver = resourceResolver;
        this.configRoot = configRoot;
    }

    @CheckForNull
    @Override
    public Resource getResource(@Nonnull Resource resource, @Nonnull String bucketName, @Nonnull String configName) {
        if (configRoot == null && resourceResolver == null) {
            return null;
        }
        Resource configRootResource = resourceResolver.getResource(configRoot);
        if (configRootResource == null) {
            return null;
        }
        return configRootResource.getChild(configName);
    }

    @Nonnull
    @Override
    public Collection<Resource> getResourceCollection(@Nonnull Resource resource, @Nonnull String bucketName, @Nonnull String configName) {
        return null;
    }

    @Override
    public String getContextPath(@Nonnull Resource resource) {
        return null;
    }

    @Nonnull
    @Override
    public Collection<String> getAllContextPaths(@Nonnull Resource resource) {
        return null;
    }
}
