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

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.models.spi.ImplementationPicker;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Picks latest model version of a core component model
 */
@Component(property=Constants.SERVICE_RANKING+":Integer=1") // this must come after ResourceTypeBasedResourcePicker
public class LatestVersionImplementationPicker implements ImplementationPicker {

    private static final Pattern pattern = Pattern.compile("\\.v(\\d+)\\.");
    private static final Logger LOG = LoggerFactory.getLogger(LatestVersionImplementationPicker.class);

    @Override
    public Class<?> pick(Class<?> adapterType, Class<?>[] implementationsTypes, Object adaptable) {
        // make sure picker is only responsible for core component models
        if (adapterType.getPackage().getName().equals("com.adobe.cq.wcm.core.components.models")) {
            Arrays.sort(implementationsTypes, new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> o1, Class<?> o2) {
                    Matcher m1 = pattern.matcher(o1.getName());
                    Matcher m2 = pattern.matcher(o2.getName());
                    if (m1.find() && m2.find()) {
                        try {
                            return Integer.parseInt(m2.group(1)) - Integer.parseInt(m1.group(1));
                        } catch (NumberFormatException e) {
                            LOG.error("Couldn't parse integer", e);
                        }
                    }
                    return 0;
                }
            });
            return implementationsTypes[0];
        }
        return null;
    }
}