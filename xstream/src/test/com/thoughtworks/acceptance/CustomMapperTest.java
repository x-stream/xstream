/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
import com.thoughtworks.xstream.core.ClassLoaderReference;
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
        public FieldPrefixStrippingMapper(final Mapper wrapped) {
            super(wrapped);
        }

        @Override
        public String serializedMember(final Class<?> type, String memberName) {
            if (memberName.startsWith("_")) {
                // _blah -> blah
                memberName = memberName.substring(1); // chop off leading char (the underscore)
            } else if (memberName.startsWith("my")) {
                // myBlah -> blah
                memberName = memberName.substring(2, 3).toLowerCase() + memberName.substring(3);
            }
            return super.serializedMember(type, memberName);
        }

        @Override
        public String realMember(final Class<?> type, final String serialized) {
            final String fieldName = super.realMember(type, serialized);
            // Not very efficient or elegant, but enough to get the point across.
            // Luckily the CachingMapper will ensure this is only ever called once per field per class.
            try {
                type.getDeclaredField("_" + fieldName);
                return "_" + fieldName;
            } catch (final NoSuchFieldException e) {
                try {
                    final String myified = "my" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    type.getDeclaredField(myified);
                    return myified;
                } catch (final NoSuchFieldException e2) {
                    return fieldName;
                }
            }
        }
    }

    public static class ThingWithStupidNamingConventions extends StandardObject {
        private static final long serialVersionUID = 200503L;
        String _firstName;
        String lastName;
        int myAge;

        public ThingWithStupidNamingConventions(final String firstname, final String lastname, final int age) {
            _firstName = firstname;
            lastName = lastname;
            myAge = age;
        }
    }

    public void testUserDefinedMappingCanAlterFieldName() {
        xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(final MapperWrapper next) {
                return new FieldPrefixStrippingMapper(next);
            }
        };
        setupSecurity(xstream);
        xstream.alias("thing", ThingWithStupidNamingConventions.class);

        final ThingWithStupidNamingConventions in = new ThingWithStupidNamingConventions("Joe", "Walnes", 10);
        final String expectedXml = ""
            + "<thing>\n"
            + "  <firstName>Joe</firstName>\n" // look, no underscores!
            + "  <lastName>Walnes</lastName>\n"
            + "  <age>10</age>\n"
            + "</thing>";

        assertBothWays(in, expectedXml);
    }

    private static class PackageStrippingMapper extends MapperWrapper {
        public PackageStrippingMapper(final Mapper wrapped) {
            super(wrapped);
        }

        @Override
        public String serializedClass(final Class<?> type) {
            return type.getName().replaceFirst(".*\\.", "");
        }
    }

    public void testStripsPackagesUponDeserialization() {
        // obviously this isn't deserializable!
        xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(final MapperWrapper next) {
                return new PackageStrippingMapper(next);
            }
        };

        // NOTE: no aliases defined!

        final String expectedXml = ""
            + "<Software>\n"
            + "  <vendor>ms</vendor>\n"
            + "  <name>word</name>\n"
            + "</Software>";
        assertEquals(expectedXml, xstream.toXML(new Software("ms", "word")));
    }

    public void testOwnMapperChainCanBeRegistered() {
        final ClassLoaderReference classLoaderReference = new ClassLoaderReference(getClass().getClassLoader());
        final Mapper mapper = new DefaultMapper(classLoaderReference);
        xstream = new XStream(new PureJavaReflectionProvider(), new DomDriver(), classLoaderReference, mapper);

        final String expected = ""
            + "<com.thoughtworks.acceptance.objects.Software>\n"
            + "  <vendor>ms</vendor>\n"
            + "  <name>word</name>\n"
            + "</com.thoughtworks.acceptance.objects.Software>";
        assertEquals(expected, xstream.toXML(new Software("ms", "word")));
    }

    public void testCanBeUsedToOmitUnexpectedElements() {
        final String expectedXml = ""
            + "<software>\n"
            + "  <version>1.0</version>\n"
            + "  <vendor>Joe</vendor>\n"
            + "  <name>XStream</name>\n"
            + "</software>";

        xstream = new XStream() {

            @Override
            protected MapperWrapper wrapMapper(final MapperWrapper next) {
                return new MapperWrapper(next) {

                    @Override
                    public boolean shouldSerializeMember(final Class<?> definedIn, final String fieldName) {
                        return definedIn != Object.class ? super.shouldSerializeMember(definedIn, fieldName) : false;
                    }

                };
            }

        };
        setupSecurity(xstream);
        xstream.alias("software", Software.class);

        final Software out = xstream.fromXML(expectedXml);
        assertEquals("Joe", out.vendor);
        assertEquals("XStream", out.name);
    }

    public void testInjectingReplacements() {
        final XStream xstream = new XStream() {

            @Override
            protected MapperWrapper wrapMapper(final MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public Class<?> realClass(final String elementName) {
                        try {
                            return super.realClass(elementName);

                        } catch (final CannotResolveClassException e) {
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
        setupSecurity(xstream);
        xstream.alias("wl", WithList.class);
        final WithList<Object> wl = xstream.fromXML(""
            + "<wl>\n"
            + "  <things class='UnknownList'>\n"
            + "    <foo>1</foo>\n"
            + "    <cocoo>2</cocoo>\n"
            + "  </things>\n"
            + "</wl>");
        assertEquals(new ArrayList<>(Arrays.asList(new Integer[]{new Integer(1), new Integer(2)})), wl.things);
        assertTrue(wl.things instanceof LinkedList);
    }
}
