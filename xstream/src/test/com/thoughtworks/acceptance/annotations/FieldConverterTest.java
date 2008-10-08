/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. March 2006 by Mauro Talevi
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Tests for using annotations to override field converters
 * 
 * @author Guilherme Silveira
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 */
public class FieldConverterTest extends AbstractAcceptanceTest {
    
    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("annotatedTask", TaskWithAnnotations.class);
        xstream.alias("derivedTask", DerivedTask.class);
        xstream.alias("taskContainer", TaskContainer.class);
    }

    public void testAnnotationForFieldsOfSameType() {
        final TaskWithAnnotations task = new TaskWithAnnotations("Tom", "Dick", "Harry");
        final String xml = ""
            + "<annotatedTask name2=\"_Dick_\">\n"
            + "  <name1 str=\"Tom\"/>\n"
            + "  <name3>Harry</name3>\n"
            + "</annotatedTask>";
        assertBothWays(task, xml);
    }

    public void testAnnotationForHiddenFields() {
        final DerivedTask task = new DerivedTask("Tom", "Dick", "Harry");
        final String xml = ""
            + "<derivedTask name2=\"_Dick_\">\n"
            + "  <name1 defined-in=\"annotatedTask\" str=\"Tom\"/>\n"
            + "  <name3 defined-in=\"annotatedTask\">Harry</name3>\n"
            + "  <name1>Harry</name1>\n"
            + "  <name3 str=\"Tom\"/>\n"
            + "</derivedTask>";
        assertBothWays(task, xml);
    }

    public void testIsFoundInReferencedTypes() {
        final TaskContainer taskContainer = new TaskContainer();
        final String xml = ""
            + "<taskContainer>\n"
            + "  <task name2=\"_Dick_\">\n"
            + "    <name1 defined-in=\"annotatedTask\" str=\"Tom\"/>\n"
            + "    <name3 defined-in=\"annotatedTask\">Harry</name3>\n"
            + "    <name1>Harry</name1>\n"
            + "    <name3 str=\"Tom\"/>\n"
            + "  </task>\n"
            + "</taskContainer>";
        assertEquals(taskContainer, xstream.fromXML(xml));
    }

    public static class TaskWithAnnotations {

        @XStreamConverter(FirstConverter.class)
        private final String name1;

        @XStreamConverter(SecondaryConverter.class)
        @XStreamAsAttribute
        private final String name2;
        private final String name3;

        public TaskWithAnnotations(final String name1, final String name2, final String name3) {
            this.name1 = name1;
            this.name2 = name2;
            this.name3 = name3;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj != null
                && TaskWithAnnotations.class.isAssignableFrom(obj.getClass())
                && ((TaskWithAnnotations)obj).name1.equals(name1)
                && ((TaskWithAnnotations)obj).name2.equals(name2)
                && ((TaskWithAnnotations)obj).name3.equals(name3);
        }
    }

    public static class DerivedTask extends TaskWithAnnotations {
        private final String name1;

        @XStreamConverter(FirstConverter.class)
        private final String name3;

        public DerivedTask(final String name1, final String name2, final String name3) {
            super(name1, name2, name3);
            this.name1 = name3;
            this.name3 = name1;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj != null
                && DerivedTask.class.isAssignableFrom(obj.getClass())
                && ((DerivedTask)obj).name1.equals(name1)
                && ((DerivedTask)obj).name3.equals(name3)
                && super.equals(obj);
        }
    }

    public static class TaskContainer {
        private final DerivedTask task = new DerivedTask("Tom", "Dick", "Harry");

        @Override
        public boolean equals(final Object obj) {
            return obj != null
                && TaskContainer.class.equals(obj.getClass())
                && task.equals(((TaskContainer)obj).task);
        }
    }

    public static class FirstConverter implements Converter {

        public void marshal(final Object source, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
            final String str = source.toString();
            writer.addAttribute("str", str);
        }

        public Object unmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
            final String str = reader.getAttribute("str");
            return str;
        }

        public boolean canConvert(final Class type) {
            return type.equals(String.class);
        }
    }

    public static class SecondaryConverter implements SingleValueConverter {

        public boolean canConvert(final Class type) {
            return type.equals(String.class);
        }

        public Object fromString(String value) {
            return value.substring(1, value.length() - 1);
        }

        public String toString(Object source) {
            return "_" + source.toString() + "_";
        }
    }

    public static class CustomConverter implements Converter {

        private static int total = 0;

        public CustomConverter() {
            total++ ;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }

        public boolean canConvert(Class type) {
            return type.equals(Double.class);
        }

    }

    public static class Account {
        @XStreamConverter(CustomConverter.class)
        private Double value;

        public Account() {
            this.value = Math.random();
        }
    }

    public static class Client {
        @XStreamConverter(CustomConverter.class)
        private Double value;

        public Client() {
            this.value = Math.random();
        }
    }

    public void testAreCachedPerField() {
        int before = CustomConverter.total;
        toXML(new Account());
        int after = CustomConverter.total;
        assertEquals(before + 1, after);
    }

    public void testAreCachedPerFieldInDifferentContexts() {
        int before = CustomConverter.total;
        toXML(new Account());
        toXML(new Client());
        int after = CustomConverter.total;
        assertEquals(before + 1, after);
    }

}
