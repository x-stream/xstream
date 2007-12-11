/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import junit.framework.TestCase;

import java.util.Iterator;

public class PropertyDictionaryTest extends TestCase {

    private PropertyDictionary propertyDictionary;

    protected void setUp() throws Exception {
        super.setUp();
        propertyDictionary = new PropertyDictionary();
    }

    /**
     * Test class: three serializable properties, one with a all capital name,
     * two others non serializable, one readable, one writable, and another and
     * a lonely field
     */
    class SomeClass {
        private String a;

        private String URL;

        private String c;

        private String d;

        private String e;

        private String f;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(String url) {
            this.URL = url;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setE(String e) {
            this.e = e;
        }
    }

    public void testListsFieldsInClassInDefinitionOrder() {
        Iterator properties = propertyDictionary.serializablePropertiesFor(SomeClass.class);
        assertEquals("URL", ((BeanProperty) properties.next()).getName());
        assertEquals("a", ((BeanProperty) properties.next()).getName());
        assertEquals("c", ((BeanProperty) properties.next()).getName());
        assertFalse("No more fields should be present", properties.hasNext());
    }

    /**
     * Test subclassing and private properties
     */
    class SpecialClass extends SomeClass {
        private String brilliant;

        public String getBrilliant() {
            return brilliant;
        }

        public void setBrilliant(String brilliant) {
            this.brilliant = brilliant;
        }

        public String getPrivate() {
            return null;
        }

        private void setPrivate(String string) {

        }
    }

    public void testIncludesFieldsInSuperClasses() {
        Iterator properties = propertyDictionary.serializablePropertiesFor(SpecialClass.class);
        assertEquals("URL", ((BeanProperty) properties.next()).getName());
        assertEquals("a", ((BeanProperty) properties.next()).getName());
        assertEquals("brilliant", ((BeanProperty) properties.next()).getName());
        assertEquals("c", ((BeanProperty) properties.next()).getName());
        assertFalse("No more fields should be present", properties.hasNext());
    }
}