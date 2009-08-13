/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. March 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class CustomMapperTest extends AbstractAcceptanceTest {

    /**
     * A sample mapper strips the underscore prefix of field names in the XML
     */
    private static class FieldPrefixStrippingMapper extends MapperWrapper {
        public FieldPrefixStrippingMapper(Mapper wrapped) {
            super(wrapped);
        }

        public String serializedMember(Class type, String memberName) {
            if (memberName.startsWith("_")) {
                // _blah -> blah
                memberName = memberName.substring(1); // chop off leading char (the underscore)
            } else if (memberName.startsWith("my")) {
                // myBlah -> blah
                memberName = memberName.substring(2, 3).toLowerCase() + memberName.substring(3);
            }
            return super.serializedMember(type, memberName);
        }

        public String realMember(Class type, String serialized) {
            String fieldName = super.realMember(type, serialized);
            // Not very efficient or elegant, but enough to get the point across.
            // Luckily the CachingMapper will ensure this is only ever called once per field per class.
            try {
                type.getDeclaredField("_" + fieldName);
                return "_" + fieldName;
            } catch (NoSuchFieldException e) {
                try {
                    String myified = "my" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    type.getDeclaredField(myified);
                    return myified;
                } catch (NoSuchFieldException e2) {
                    return fieldName;
                }
            }
        }
    }

    public static class ThingWithStupidNamingConventions extends StandardObject {
        String _firstName;
        String lastName;
        int myAge;

        public ThingWithStupidNamingConventions(String firstname, String lastname, int age) {
            _firstName = firstname;
            this.lastName = lastname;
            myAge = age;
        }
    }

    public void testUserDefinedMappingCanAlterFieldName() {
        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new FieldPrefixStrippingMapper(next);
            }
        };
        xstream.alias("thing", ThingWithStupidNamingConventions.class);

        ThingWithStupidNamingConventions in = new ThingWithStupidNamingConventions("Joe", "Walnes", 10);
        String expectedXml = ""
                + "<thing>\n"
                + "  <firstName>Joe</firstName>\n" // look, no underscores!
                + "  <lastName>Walnes</lastName>\n"
                + "  <age>10</age>\n"
                + "</thing>";

        assertBothWays(in, expectedXml);
    }

    private static class PackageStrippingMapper extends MapperWrapper {
        public PackageStrippingMapper(Mapper wrapped) {
            super(wrapped);
        }

        public String serializedClass(Class type) {
            return type.getName().replaceFirst(".*\\.", "");
        }
    }
    
    public void testStripsPackagesUponDeserialization() {
        // obviously this isn't deserializable!
        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new PackageStrippingMapper(next);
            }
        };

        // NOTE: no aliases defined!

        String expectedXml = "" +
                "<Software>\n" +
                "  <vendor>ms</vendor>\n" +
                "  <name>word</name>\n" +
                "</Software>";
        assertEquals(expectedXml, xstream.toXML(new Software("ms", "word")));
    }
    
    public void testOwnMapperChainCanBeRegistered() {
        Mapper mapper = new DefaultMapper(getClass().getClassLoader());
        xstream = new XStream(new PureJavaReflectionProvider(), new DomDriver(), getClass().getClassLoader(), mapper);
        
        String expected = "" +
                "<com.thoughtworks.acceptance.objects.Software>\n" +
                "  <vendor>ms</vendor>\n" +
                "  <name>word</name>\n" +
                "</com.thoughtworks.acceptance.objects.Software>";
        assertEquals(expected, xstream.toXML(new Software("ms", "word")));
    }
    
    public void testCanBeUsedToOmitUnexpectedElements() {
        String expectedXml = "" +
                "<software>\n" +
                "  <version>1.0</version>\n" +
                "  <vendor>Joe</vendor>\n" +
                "  <name>XStream</name>\n" +
                "</software>";

        xstream = new XStream() {

            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {

                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        return definedIn != Object.class ? super.shouldSerializeMember(definedIn, fieldName) : false;
                    }
                    
                };
            }
            
        };
        xstream.alias("software", Software.class);

        Software out = (Software) xstream.fromXML(expectedXml);
        assertEquals("Joe", out.vendor);
        assertEquals("XStream", out.name);
    }

    public void testInjectingReplacements() {
        XStream xstream = new XStream() {

            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    public Class realClass(String elementName) {
                        try {
                            return super.realClass(elementName);

                        } catch (CannotResolveClassException e) {
                            if (elementName.endsWith("oo")) {
                                return Integer.class;
                            }
                            if (elementName.equals("UnknownList")) {
                                return LinkedList.class;
                            }
                            throw e;
                        }
                    }
                    
                };
            }

        };
        xstream.alias("wl", WithList.class);
        WithList wl = (WithList)xstream.fromXML("" 
                + "<wl>\n" 
                + "  <things class='UnknownList'>\n" 
                + "    <foo>1</foo>\n" 
                + "    <cocoo>2</cocoo>\n" 
                + "  </things>\n" 
                + "</wl>");
        assertEquals(new ArrayList(Arrays.asList(new Integer[]{new Integer(1), new Integer(2)})), wl.things);
        assertTrue(wl.things instanceof LinkedList);
    }
}
