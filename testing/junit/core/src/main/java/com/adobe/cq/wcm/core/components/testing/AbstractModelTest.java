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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import static org.junit.Assert.fail;

public class AbstractModelTest {

    public void testDefaultBehaviour(String[] packages) throws Exception {
        List<Class> models = new ArrayList<>();
        for (String p : packages) {
            models.addAll(getClasses(p));
        }

        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        StringBuilder errors = new StringBuilder();
        for (Class clazz : models) {
            if (clazz.isInterface() && !clazz.getName().contains("package-info")) {
                Object instance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                        (Object proxy, Method method, Object[] arguments) -> {
                            if (method.isDefault()) {
                                final Class<?> declaringClass = method.getDeclaringClass();
                                return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                                        .unreflectSpecial(method, declaringClass)
                                        .bindTo(proxy)
                                        .invokeWithArguments(arguments);
                            }

                            // proxy impl of not defaults methods
                            return null;
                        });
                Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (!m.isDefault()) {
                        errors.append("Method ").append(m.toString()).append(" was not marked as default.\n");
                    }
                    Throwable t = null;
                    try {
                        m.invoke(instance);
                    } catch (InvocationTargetException e) {
                        t = e.getCause();
                    }
                    if (t == null || !(t instanceof UnsupportedOperationException)) {
                        errors.append("Expected method ")
                                .append(m.toString())
                                .append("in class ")
                                .append(clazz.getName())
                                .append(" to throw an ")
                                .append(UnsupportedOperationException.class.getName())
                                .append(".\n");
                    }
                }
            }
        }
        if (errors.length() > 0) {
            errors.insert(0, "\n");
            fail(errors.toString());
        }
    }

    private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        List<Class> classes = new ArrayList<>();
        ClassPath classpath = ClassPath.from(AbstractModelTest.class.getClassLoader());
        String packagePrefix = packageName + '.';
        ImmutableSet.Builder<ClassPath.ClassInfo> builder = ImmutableSet.builder();
        classpath.getAllClasses().stream().filter(classInfo -> classInfo.getName().startsWith(packagePrefix)).forEach(builder::add);
        ImmutableSet<ClassPath.ClassInfo> packageClasses = builder.build();
        classes.addAll(packageClasses.stream().map(ClassPath.ClassInfo::load).collect(Collectors.toList()));
        return classes;
    }

}
