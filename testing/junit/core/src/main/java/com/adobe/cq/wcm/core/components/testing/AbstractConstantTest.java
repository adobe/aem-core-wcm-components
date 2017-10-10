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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractConstantTest {

    protected void testConsistentStructure(Class constantsClass) throws Exception {
        assertTrue("Class should be final", Modifier.isFinal(constantsClass.getModifiers()));
        Constructor[] declaredConstructors = constantsClass.getDeclaredConstructors();
        assertEquals("Only one constructor should be present", 1, declaredConstructors.length);
        assertTrue("Constructor should be private", Modifier.isPrivate(declaredConstructors[0].getModifiers()));

        Field fields[] = constantsClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isSynthetic()) {
                //ignore the jacoco generated field
                continue;
            }
            int modifiers = field.getModifiers();
            assertTrue("Field should be public", Modifier.isPublic(modifiers));
            assertTrue("Field should be static", Modifier.isStatic(modifiers));
            assertTrue("Field should be final", Modifier.isFinal(modifiers));
        }

        //the number of methods declared in the class source
        int originalMethodCount = 0;
        for (Method method : constantsClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                //ignore the jacoco generated method
                continue;
            }
            originalMethodCount++;
        }
        assertTrue("There should be no methods declared", originalMethodCount == 0);

        Constructor constructor = declaredConstructors[0];
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
