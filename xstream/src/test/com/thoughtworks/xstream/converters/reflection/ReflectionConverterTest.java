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

package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;

import junit.framework.TestCase;


public class ReflectionConverterTest extends TestCase {

    public static class World extends StandardObject {
        private static final long serialVersionUID = 200501L;
        int anInt = 1;
        Integer anInteger = new Integer(2);
        char anChar = 'a';
        Character anCharacter = new Character('w');
        boolean anBool = true;
        Boolean anBoolean = new Boolean(false);
        byte aByte = 4;
        Byte aByteClass = new Byte("5");
        short aShort = 6;
        Short aShortClass = new Short("7");
        float aFloat = 8f;
        Float aFloatClass = new Float("9");
        long aLong = 10;
        Long aLongClass = new Long("11");
        String anString = new String("XStream programming!");
    }

    public void testSerializesAllPrimitiveFieldsInACustomObject() {
        final World world = new World();

        final XStream xstream = new XStream(DefaultDriver.create());
        xstream.alias("world", World.class);

        final String expected = ""
            + "<world>\n"
            + "  <anInt>1</anInt>\n"
            + "  <anInteger>2</anInteger>\n"
            + "  <anChar>a</anChar>\n"
            + "  <anCharacter>w</anCharacter>\n"
            + "  <anBool>true</anBool>\n"
            + "  <anBoolean>false</anBoolean>\n"
            + "  <aByte>4</aByte>\n"
            + "  <aByteClass>5</aByteClass>\n"
            + "  <aShort>6</aShort>\n"
            + "  <aShortClass>7</aShortClass>\n"
            + "  <aFloat>8.0</aFloat>\n"
            + "  <aFloatClass>9.0</aFloatClass>\n"
            + "  <aLong>10</aLong>\n"
            + "  <aLongClass>11</aLongClass>\n"
            + "  <anString>XStream programming!</anString>\n"
            + "</world>";

        assertEquals(expected, xstream.toXML(world));
    }

    public static class TypesOfFields extends StandardObject {
        private static final long serialVersionUID = 200504L;
        String normal = "normal";
        transient String trans = "transient";
        static String stat = "stat";
    }

    public void testDoesNotSerializeTransientOrStaticFields() {
        final TypesOfFields fields = new TypesOfFields();
        final String expected = "" //
            + "<types>\n"
            + "  <normal>normal</normal>\n"
            + "</types>";

        final XStream xstream = new XStream(DefaultDriver.create());
        xstream.alias("types", TypesOfFields.class);

        final String xml = xstream.toXML(fields);
        assertEquals(expected, xml);

    }

    public void testCanBeOverloadedToDeserializeTransientFields() {
        final XStream xstream = new XStream(DefaultDriver.create());
        xstream.allowTypes(TypesOfFields.class);
        xstream.alias("types", TypesOfFields.class);
        xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider()) {

            @Override
            public boolean canConvert(final Class<?> type) {
                return type == TypesOfFields.class;
            }

            @Override
            protected boolean shouldUnmarshalTransientFields() {
                return true;
            }
        });

        final String xml = "" //
            + "<types>\n"
            + "  <normal>normal</normal>\n"
            + "  <trans>foo</trans>\n"
            + "</types>";

        final TypesOfFields fields = xstream.<TypesOfFields>fromXML(xml);
        assertEquals("foo", fields.trans);
    }

    public void testCustomConverterCanBeInstantiatedAndRegisteredWithDesiredPriority() {
        final XStream xstream = new XStream(DefaultDriver.create());
        // using default mapper instead of XStream#buildMapper()
        Mapper mapper = new DefaultMapper(new ClassLoaderReference(new CompositeClassLoader()));
        // AttributeMapper required by ReflectionConverter
        mapper = new AttributeMapper(mapper, xstream.getConverterLookup(), xstream.getReflectionProvider());
        final Converter converter = new CustomReflectionConverter(mapper, new PureJavaReflectionProvider());
        xstream.registerConverter(converter, -20);
        xstream.alias("world", World.class);
        final World world = new World();

        final String expected = ""
            + "<world>\n"
            + "  <anInt class=\"java.lang.Integer\">1</anInt>\n"
            + "  <anInteger>2</anInteger>\n"
            + "  <anChar class=\"java.lang.Character\">a</anChar>\n"
            + "  <anCharacter>w</anCharacter>\n"
            + "  <anBool class=\"java.lang.Boolean\">true</anBool>\n"
            + "  <anBoolean>false</anBoolean>\n"
            + "  <aByte class=\"java.lang.Byte\">4</aByte>\n"
            + "  <aByteClass>5</aByteClass>\n"
            + "  <aShort class=\"java.lang.Short\">6</aShort>\n"
            + "  <aShortClass>7</aShortClass>\n"
            + "  <aFloat class=\"java.lang.Float\">8.0</aFloat>\n"
            + "  <aFloatClass>9.0</aFloatClass>\n"
            + "  <aLong class=\"java.lang.Long\">10</aLong>\n"
            + "  <aLongClass>11</aLongClass>\n"
            + "  <anString>XStream programming!</anString>\n"
            + "</world>";
        assertEquals(expected, xstream.toXML(world));

    }

    static class CustomReflectionConverter extends ReflectionConverter {

        public CustomReflectionConverter(final Mapper mapper, final ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
        }
    }

}
