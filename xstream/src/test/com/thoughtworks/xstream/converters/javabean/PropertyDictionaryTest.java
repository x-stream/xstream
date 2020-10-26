/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import java.beans.PropertyDescriptor;
import java.util.Iterator;

import junit.framework.TestCase;


public class PropertyDictionaryTest extends TestCase {

    private PropertyDictionary propertyDictionary;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        propertyDictionary = new PropertyDictionary();
    }

    /**
     * Test class: three serializable properties, one with a all capital name, two others non serializable, one
     * readable, one writable, and another and a lonely field
     */
    class SomeClass {
        private String a;

        private String URL;

        private String c;

        private String d;

        @SuppressWarnings("unused")
        private String e;

        @SuppressWarnings("unused")
        private String f;

        public String getA() {
            return a;
        }

        public void setA(final String a) {
            this.a = a;
        }

        public String getURL() {
            return URL;
        }

        public void setURL(final String url) {
            URL = url;
        }

        public String getC() {
            return c;
        }

        public void setC(final String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setE(final String e) {
            this.e = e;
        }
    }

    public void testListsFieldsInClassInDefinitionOrder() {
        final Iterator<BeanProperty> beanProperties = propertyDictionary.serializablePropertiesFor(SomeClass.class);
        assertEquals("URL", beanProperties.next().getName());
        assertEquals("a", beanProperties.next().getName());
        assertEquals("c", beanProperties.next().getName());
        assertFalse("No more fields should be present", beanProperties.hasNext());

        final Iterator<PropertyDescriptor> propertyDesc = propertyDictionary.propertiesFor(SomeClass.class);
        assertEquals("URL", propertyDesc.next().getName());
        assertEquals("a", propertyDesc.next().getName());
        assertEquals("c", propertyDesc.next().getName());
        assertEquals("d", propertyDesc.next().getName());
        assertEquals("e", propertyDesc.next().getName());
        assertFalse("No more fields should be present", propertyDesc.hasNext());
    }

    /**
     * Test subclassing and private properties
     */
    class SpecialClass extends SomeClass {
        private String brilliant;

        public String getBrilliant() {
            return brilliant;
        }

        public void setBrilliant(final String brilliant) {
            this.brilliant = brilliant;
        }

        public String getPrivate() {
            return null;
        }

        @SuppressWarnings("unused")
        private void setPrivate(final String string) {

        }
    }

    public void testIncludesFieldsInSuperClasses() {
        final Iterator<BeanProperty> beanProperties = propertyDictionary.serializablePropertiesFor(SpecialClass.class);
        assertEquals("URL", beanProperties.next().getName());
        assertEquals("a", beanProperties.next().getName());
        assertEquals("brilliant", beanProperties.next().getName());
        assertEquals("c", beanProperties.next().getName());
        assertFalse("No more fields should be present", beanProperties.hasNext());

        final Iterator<PropertyDescriptor> propertyDesc = propertyDictionary.propertiesFor(SpecialClass.class);
        assertEquals("URL", propertyDesc.next().getName());
        assertEquals("a", propertyDesc.next().getName());
        assertEquals("brilliant", propertyDesc.next().getName());
        assertEquals("c", propertyDesc.next().getName());
        assertEquals("d", propertyDesc.next().getName());
        assertEquals("e", propertyDesc.next().getName());
        assertEquals("private", propertyDesc.next().getName());
        assertFalse("No more fields should be present", propertyDesc.hasNext());
    }
}
