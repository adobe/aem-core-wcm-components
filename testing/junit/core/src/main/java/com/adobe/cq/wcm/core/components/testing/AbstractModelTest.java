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
package com.adobe.cq.wcm.core.components.testing;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class AbstractModelTest {

    @SuppressWarnings("squid:S1181")
    public void testDefaultBehaviour(String[] packages) throws IllegalAccessException {
        List<Class> models = new ArrayList<>();
        for (String p : packages) {
            models.addAll(getClasses(p));
        }

        StringBuilder errors = new StringBuilder();
        for (Class clazz : models) {
            if (clazz.isInterface() && !clazz.getName().contains("package-info")) {
                Object instance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                        (Object proxy, Method method, Object[] arguments) -> {
                            if (method.isDefault()) {
                                final Class<?> declaringClass = method.getDeclaringClass();

                                MethodHandle mh;
                                try {
                                    // should work for <= Java 8
                                    // ensure allowed mode will not check visibility
                                    MethodHandles.Lookup lookup = MethodHandles.lookup().in(declaringClass);
                                    final Field f = MethodHandles.Lookup.class.getDeclaredField("allowedModes");
                                    final int modifiers = f.getModifiers();
                                    if (Modifier.isFinal(modifiers)) { // should be done a single time
                                        final Field modifiersField = Field.class.getDeclaredField("modifiers");
                                        modifiersField.setAccessible(true);
                                        modifiersField.setInt(f, modifiers & ~Modifier.FINAL);
                                        f.setAccessible(true);
                                        f.set(lookup, MethodHandles.Lookup.PRIVATE);
                                    }

                                    mh = lookup.unreflectSpecial(method, declaringClass);
                                } catch (Throwable t) {
                                    // should work for > Java 8
                                    mh = MethodHandles.lookup()
                                            .findSpecial(
                                                    declaringClass,
                                                    method.getName(),
                                                    MethodType.methodType(
                                                            method.getReturnType(),
                                                            method.getParameterTypes()),
                                                    declaringClass);
                                }
                                return mh.bindTo(proxy)
                                        .invokeWithArguments(arguments);
                            }

                            // proxy impl of not defaults methods
                            return null;
                        });
                Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (!m.isDefault()) {
                        errors.append("Method ")
                                .append(m.toString())
                                .append(" was not marked as default in class ")
                                .append(clazz.getName())
                                .append(" .\n");
                    }
                    Throwable t = null;
                    Object ret = null;
                    try {
                        if (m.getParameterCount() > 0) {
                            ret = m.invoke(instance, new Object[m.getParameterCount()]);
                        } else {
                            ret = m.invoke(instance);
                        }
                    } catch (InvocationTargetException e) {
                        t = e.getCause();
                    }
                    if (t != null) {
                        errors.append("Expected method ")
                                .append(m.toString())
                                .append(" in class ")
                                .append(clazz.getName())
                                .append(" to not throw an exception.\n");
                    }
                }
            }
        }
        if (errors.length() > 0) {
            errors.insert(0, "\n");
            throw new AssertionError(errors.toString());
        }
    }

    private static List<Class> getClasses(String packageName) {
        Reflections reflections = new Reflections(packageName,  new SubTypesScanner(false));
        return new ArrayList<>(reflections.getSubTypesOf(Object.class));
    }
}
