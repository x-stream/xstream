/*
 * Copyright (C) 2011, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. July 2011 by Joerg Schaible
 */

package com.thoughtworks.xstream.converters.extended;

import java.io.StringReader;
import java.io.StringWriter;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;

import junit.framework.TestCase;


/**
 * Tests {@link ToAttributedValueConverter}.
 * 
 * @author jos / last modified by $Author$
 */
public class ToAttributedValueConverterTest extends TestCase {
    private HierarchicalStreamDriver driver;
    private DefaultConverterLookup converterLookup;
    private ReflectionProvider reflectionProvider;
    private Mapper mapper;

    protected void setUp() throws Exception {
        super.setUp();

        final ClassAliasingMapper classAliasingMapper = new ClassAliasingMapper(
            new DefaultMapper(new ClassLoaderReference(getClass().getClassLoader())));
        classAliasingMapper.addClassAlias("x", X.class);
        classAliasingMapper.addClassAlias("software", Software.class);
        classAliasingMapper.addClassAlias("open-source", OpenSourceSoftware.class);
        mapper = new DefaultImplementationsMapper(new ArrayMapper(classAliasingMapper));

        reflectionProvider = new SunUnsafeReflectionProvider();
        driver = new XppDriver();

        converterLookup = new DefaultConverterLookup();
        converterLookup.registerConverter(
            new SingleValueConverterWrapper(new StringConverter()), 0);
        converterLookup.registerConverter(
            new SingleValueConverterWrapper(new IntConverter()), 0);
        converterLookup.registerConverter(new ArrayConverter(mapper), 0);
        converterLookup.registerConverter(
            new ReflectionConverter(mapper, reflectionProvider), -1);
    }

    /**
     * Tests conversion with field defined in converted class.
     */
    public void testWithValueInConvertedClass() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            Software.class, mapper, reflectionProvider, converterLookup, "name"), 0);

        final Software name = new Software(null, "XStream");
        final StringWriter writer = new StringWriter();
        final CompactWriter compactWriter = new CompactWriter(writer);
        new TreeMarshaller(compactWriter, converterLookup, mapper).start(name, null);
        compactWriter.flush();
        assertEquals("<software>XStream</software>", writer.toString());

        final HierarchicalStreamReader reader = driver.createReader(new StringReader(
            writer.toString()));
        assertEquals(
            name, new TreeUnmarshaller(null, reader, converterLookup, mapper).start(null));
    }

    /**
     * Tests conversion with field defined in superclass.
     */
    public void testWithValueInSuperclass() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            OpenSourceSoftware.class, mapper, reflectionProvider, converterLookup, "name",
            Software.class), 0);

        final Software software = new OpenSourceSoftware("Codehaus", "XStream", "BSD");
        final StringWriter writer = new StringWriter();
        final CompactWriter compactWriter = new CompactWriter(writer);
        new TreeMarshaller(compactWriter, converterLookup, mapper).start(software, null);
        compactWriter.flush();
        assertEquals(
            "<open-source vendor=\"Codehaus\" license=\"BSD\">XStream</open-source>",
            writer.toString());

        final HierarchicalStreamReader reader = driver.createReader(new StringReader(
            writer.toString()));
        assertEquals(
            software, new TreeUnmarshaller(null, reader, converterLookup, mapper).start(null));
    }

    /**
     * Tests conversion distinguishes between different types.
     */
    public void testWillDistinguishBetweenDifferentTypes() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            Software.class, mapper, reflectionProvider, converterLookup, "name"), 0);
        converterLookup.registerConverter(
            new ToAttributedValueConverter(
                OpenSourceSoftware.class, mapper, reflectionProvider, converterLookup,
                "license"), 0);

        final Software[] software = new Software[]{
            new Software("Microsoft", "Windows"),
            new OpenSourceSoftware("Codehaus", "XStream", "BSD")};
        final StringWriter writer = new StringWriter();
        final PrettyPrintWriter prettyPrintWriter = new PrettyPrintWriter(writer);
        new TreeMarshaller(prettyPrintWriter, converterLookup, mapper).start(software, null);
        prettyPrintWriter.flush();
        assertEquals(""
            + "<software-array>\n"
            + "  <software vendor=\"Microsoft\">Windows</software>\n"
            + "  <open-source vendor=\"Codehaus\" name=\"XStream\">BSD</open-source>\n"
            + "</software-array>", writer.toString());

        final HierarchicalStreamReader reader = driver.createReader(new StringReader(
            writer.toString()));
        Software[] array = (Software[])new TreeUnmarshaller(
            null, reader, converterLookup, mapper).start(null);
        assertEquals(software[0], array[0]);
        assertEquals(software[1], array[1]);
    }

    /**
     * Tests conversion with null in field value.
     */
    public void testWithNullValueDeserializedAsEmptyString() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            Software.class, mapper, reflectionProvider, converterLookup, "name"), 0);

        final Software software = new Software(null, null);
        final StringWriter writer = new StringWriter();
        final CompactWriter compactWriter = new CompactWriter(writer);
        new TreeMarshaller(compactWriter, converterLookup, mapper).start(software, null);
        compactWriter.flush();
        assertEquals("<software/>", writer.toString());

        final HierarchicalStreamReader reader = driver.createReader(new StringReader(
            writer.toString()));
        assertEquals(
            "",
            ((Software)new TreeUnmarshaller(null, reader, converterLookup, mapper).start(null)).name);
    }

    /**
     * Tests conversion with null in field value.
     */
    public void testWithoutValueField() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            Software.class, mapper, reflectionProvider, converterLookup, null), 0);

        final Software software = new Software("Codehaus", "XStream");
        final StringWriter writer = new StringWriter();
        final CompactWriter compactWriter = new CompactWriter(writer);
        new TreeMarshaller(compactWriter, converterLookup, mapper).start(software, null);
        compactWriter.flush();
        assertEquals("<software vendor=\"Codehaus\" name=\"XStream\"/>", writer.toString());

        final HierarchicalStreamReader reader = driver.createReader(new StringReader(
            writer.toString()));
        assertEquals(
            software, new TreeUnmarshaller(null, reader, converterLookup, mapper).start(null));
    }

    /**
     * Tests conversion with complex value field.
     */
    public void testWithComplexValueField() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            X.class, mapper, reflectionProvider, converterLookup, "innerObj"), 0);

        final X x = new X(42);
        x.aStr = "xXx";
        x.innerObj = new Y();
        x.innerObj.yField = "inner";
        final StringWriter writer = new StringWriter();
        final CompactWriter compactWriter = new CompactWriter(writer);
        new TreeMarshaller(compactWriter, converterLookup, mapper).start(x, null);
        compactWriter.flush();
        assertEquals(
            "<x aStr=\"xXx\" anInt=\"42\"><yField>inner</yField></x>", writer.toString());

        final HierarchicalStreamReader reader = driver.createReader(new StringReader(
            writer.toString()));
        assertEquals(x, new TreeUnmarshaller(null, reader, converterLookup, mapper).start(null));
    }
    
    public void testFailsWhenFieldCannotBeWrittenAsAttribute() {
        converterLookup.registerConverter(new ToAttributedValueConverter(
            X.class, mapper, reflectionProvider, converterLookup, "aStr"), 0);

        final X x = new X(42);
        x.aStr = "xXx";
        x.innerObj = new Y();
        x.innerObj.yField = "inner";
        final StringWriter writer = new StringWriter();
        final CompactWriter compactWriter = new CompactWriter(writer);
        try {
            new TreeMarshaller(compactWriter, converterLookup, mapper).start(x, null);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().indexOf("innerObj") >= 0);
        }
    }
}
