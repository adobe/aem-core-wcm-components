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
package com.adobe.cq.wcm.core.components.internal.services;

import org.apache.sling.models.spi.ImplementationPicker;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Picks latest model version of a core component model based on the version information in the package name
 * (e.g. com.adobe.cq.wcm.core.components.internal.models.<b>v1</b>.ImageImpl). External models which implements Core Component
 * interfaces have precedence over internal models.
 */
@Component
@ServiceRanking(value = 1)// this must come after ResourceTypeBasedResourcePicker
public class LatestVersionImplementationPicker implements ImplementationPicker {

    private static final Pattern INTERNAL_MODEL_PATTERN = Pattern.compile("^com\\.adobe\\.cq\\.wcm\\.core\\.components\\.internal\\.models\\.v(\\d+)\\.\\S*$");
    private static final String CORE_COMPONENTS_MODELS = "com.adobe.cq.wcm.core.components.models";

    @Override
    public Class<?> pick(Class<?> adapterType, Class<?>[] implementationsTypes, Object adaptable) {
        // make sure picker is only responsible for core component models
        if (adapterType.getPackage().getName().equals(CORE_COMPONENTS_MODELS)) {
            return Arrays.stream(implementationsTypes)
                // filter adobe internal models which are not part of the core components project
                .filter(aClass -> !aClass.getName().startsWith("com.adobe.cq") ||
                    aClass.getName().startsWith("com.adobe.cq.wcm.core.components.internal.models"))
                .min((Class<?> o1, Class<?> o2) -> {
                    Matcher m1 = INTERNAL_MODEL_PATTERN.matcher(o1.getName());
                    Matcher m2 = INTERNAL_MODEL_PATTERN.matcher(o2.getName());
                    if (m1.matches() && m2.matches()) {
                        return Integer.parseInt(m2.group(1)) - Integer.parseInt(m1.group(1));
                    } else {
                        return (m1.matches() ? 1 : (m2.matches() ? -1 : 0));
                    }
                }).orElse(implementationsTypes[0]);
        }
        return null;
    }
}
