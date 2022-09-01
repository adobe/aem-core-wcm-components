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
package com.adobe.cq.wcm.core.components.internal.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.apache.sling.models.jacksonexporter.ModuleProvider;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultMethodSkippingModuleProvider implements ModuleProvider {

    private static final String PACKAGE_CORE_COMPONENTS = "com.adobe.cq.wcm.core.components";
    private static final String PACKAGE_IMPL_INTERNAL = "internal.models";
    private static final String PACKAGE_UTIL = "com.adobe.cq.wcm.core.components.util";

    private SimpleModule module;

    public DefaultMethodSkippingModuleProvider() {
        module = new SimpleModule();
        module.setSerializerModifier(new BeanSerializerModifier() {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {

                return super.changeProperties(config, beanDesc, beanProperties.stream().filter(bpw -> {
                    final AnnotatedMember jacksonMember = bpw.getMember();
                    final Member member = jacksonMember.getMember();
                    if (member instanceof Method) {
                        final Method method = (Method) member;
                        if (method.isDefault()) {
                            try {
                                // only exclude default methods if they are defined on interfaces from the core components
                                String className = beanDesc.getBeanClass().getMethod(method.getName()).getDeclaringClass().getName();
                                return !className.startsWith(PACKAGE_CORE_COMPONENTS) ||
                                    className.contains(PACKAGE_IMPL_INTERNAL) ||
                                    className.contains(PACKAGE_UTIL);
                            } catch (NoSuchMethodException e) {
                                return false;
                            }
                        }
                    }
                    return true;
                }).collect(Collectors.toList()));
            }
        });
    }

    @Override
    public Module getModule() {
        return module;
    }
}
