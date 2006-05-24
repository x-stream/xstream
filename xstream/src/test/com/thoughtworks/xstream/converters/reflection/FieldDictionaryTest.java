package com.thoughtworks.xstream.converters.reflection;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class FieldDictionaryTest extends TestCase {

    private FieldDictionary fieldDictionary;

    protected void setUp() throws Exception {
        super.setUp();
        fieldDictionary = new FieldDictionary();
    }

    static class SomeClass {
        private String a;
        private String c;
        private transient String b;
        private static String d;
        private String e;
    }

    public void testListsFieldsInClassInDefinitionOrder() {
        Iterator fields = fieldDictionary.serializableFieldsFor(SomeClass.class);
        assertEquals("a", getNonStaticFieldName(fields));
        assertEquals("c", getNonStaticFieldName(fields));
        assertEquals("b", getNonStaticFieldName(fields));
        assertEquals("e", getNonStaticFieldName(fields));
        assertFalse("No more fields should be present", fields.hasNext());
    }

    static class SpecialClass extends SomeClass {
        private String brilliant;
    }

    public void testIncludesFieldsInSuperClasses() {
        Iterator fields = fieldDictionary.serializableFieldsFor(SpecialClass.class);
        assertEquals("brilliant", getNonStaticFieldName(fields));
        assertEquals("a", getNonStaticFieldName(fields));
        assertEquals("c", getNonStaticFieldName(fields));
        assertEquals("b", getNonStaticFieldName(fields));
        assertEquals("e", getNonStaticFieldName(fields));
        assertFalse("No more fields should be present", fields.hasNext());
    }

    class InnerClass { // note: no static makes this an inner class, not nested class.
        private String someThing;
    }

    public void testIncludesOuterClassReferenceForInnerClass() {
        Iterator fields = fieldDictionary.serializableFieldsFor(InnerClass.class);
        assertEquals("someThing", getNonStaticFieldName(fields));
        Field innerField = ((Field)fields.next());
        assertEquals("this$0", innerField.getName());
        assertEquals(FieldDictionaryTest.class, innerField.getType());
        assertFalse("No more fields should be present", fields.hasNext());
    }

    private static String getNonStaticFieldName(Iterator fields) {
        final Field field = (Field)fields.next();
        // JRockit declares static fields first, XStream will ignore them anyway
        if ((field.getModifiers() & Modifier.STATIC) > 0) {
            return getNonStaticFieldName(fields);
        }
        return field.getName();
    }
}
