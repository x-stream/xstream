package com.thoughtworks.xstream.converters.reflection;

import junit.framework.TestCase;

import java.util.Iterator;
import java.lang.reflect.Field;

public class FieldDictionaryTest extends TestCase {

    private FieldDictionary fieldDictionary;

    protected void setUp() throws Exception {
        super.setUp();
        fieldDictionary = new FieldDictionary();
    }

    class SomeClass {
        private String a;
        private String c;
        private String b;
    }

    public void testListsFieldsInClassInDefinitionOrder() {
        Iterator fields = fieldDictionary.serializableFieldsFor(SomeClass.class);
        assertEquals("a", ((Field)fields.next()).getName());
        assertEquals("c", ((Field)fields.next()).getName());
        assertEquals("b", ((Field)fields.next()).getName());
        assertFalse("No more fields should be present", fields.hasNext());
    }

    class SpecialClass extends SomeClass {
        private String brilliant;
    }

    public void testIncludesFieldsInSuperClasses() {
        Iterator fields = fieldDictionary.serializableFieldsFor(SpecialClass.class);
        assertEquals("brilliant", ((Field)fields.next()).getName());
        assertEquals("a", ((Field)fields.next()).getName());
        assertEquals("c", ((Field)fields.next()).getName());
        assertEquals("b", ((Field)fields.next()).getName());
        assertFalse("No more fields should be present", fields.hasNext());
    }
}
