package pl.mg6.grooid

import android.content.SharedPreferences
import groovy.transform.CompileStatic

import java.lang.reflect.Constructor

class SharedPrefsTestCase extends GroovyTestCase {

    @CompileStatic
    @SharedPrefs
    static class MyStringPrefsActual {

        private final String myField = "initValue"
    }

    @CompileStatic
    static class MyStringPrefsExpected {

        private final String myField = "initValue"
        private final SharedPreferences __sharedPrefs

        MyStringPrefsExpected(SharedPreferences sharedPrefs) {
            __sharedPrefs = sharedPrefs
        }

        String getMyField() {
            return __sharedPrefs.getString("my_field", myField)
        }

        void setMyField(String myField) {
            __sharedPrefs.edit().putString("my_field", myField).apply()
        }

        boolean containsMyField() {
            return __sharedPrefs.contains("my_field")
        }

        void removeMyField() {
            __sharedPrefs.edit().remove("my_field").apply()
        }
    }

    void test() {
        assertClassContentsEquals(MyStringPrefsExpected, MyStringPrefsActual)
    }

    static void assertClassContentsEquals(Class<?> expectedClass, Class<?> actualClass) {
        def expectedFields = expectedClass.declaredFields
        def actualFields = actualClass.declaredFields
        assertEquals(expectedFields.length, actualFields.length)
        for (int i = 0; i < expectedFields.length; i++) {
            def expectedField = expectedFields[i]
            def actualField = actualFields[i]
            assertEquals(expectedField.name, actualField.name)
            assertEquals(expectedField.class, actualField.class)
            assertEquals(expectedField.type, actualField.type)
            assertEquals(expectedField.modifiers, actualField.modifiers)
        }
        def expectedMethods = expectedClass.declaredMethods
        def actualMethods = actualClass.declaredMethods
        assertEquals(expectedMethods.length, actualMethods.length)
        for (int i = 0; i < expectedMethods.length; i++) {
            def expectedMethod = expectedMethods[i]
            def actualMethod = actualMethods[i]
            assertEquals(expectedMethod.name, actualMethod.name)
            assertEquals(expectedMethod.modifiers, actualMethod.modifiers)
            assertEquals(expectedMethod.returnType, actualMethod.returnType)
            assertEquals(expectedMethod.genericReturnType, actualMethod.genericReturnType)
            assertEquals(expectedMethod.parameterCount, actualMethod.parameterCount)
            for (int j = 0; j < expectedMethod.parameterCount; j++) {
                def expectedParameter = expectedMethod.parameters[j]
                def actualParameter = actualMethod.parameters[j]
                assertEquals(expectedParameter.name, actualParameter.name)
                assertEquals(expectedParameter.modifiers, actualParameter.modifiers)
                assertEquals(expectedParameter.type, actualParameter.type)
                assertEquals(expectedParameter.parameterizedType, actualParameter.parameterizedType)
            }
        }
        def expectedConstructors = expectedClass.constructors
        def actualConstructors = actualClass.constructors
        assertEquals(expectedConstructors.length, actualConstructors.length)
        for (int i = 0; i < expectedConstructors.length; i++) {
            def expectedConstructor = expectedConstructors[i]
            def actualConstructor = actualConstructors[i]
            assertEquals(expectedConstructor.modifiers, actualConstructor.modifiers)
            assertEquals(expectedConstructor.parameterCount, actualConstructor.parameterCount)
            for (int j = 0; j < expectedConstructor.parameterCount; j++) {
                def expectedParameter = expectedConstructor.parameters[j]
                def actualParameter = actualConstructor.parameters[j]
                assertEquals(expectedParameter.name, actualParameter.name)
                assertEquals(expectedParameter.modifiers, actualParameter.modifiers)
                assertEquals(expectedParameter.type, actualParameter.type)
                assertEquals(expectedParameter.parameterizedType, actualParameter.parameterizedType)
            }
        }
    }
}
