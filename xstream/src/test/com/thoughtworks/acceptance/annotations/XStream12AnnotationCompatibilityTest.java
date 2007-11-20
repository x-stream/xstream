/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 10.11.2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.AnnotationProvider;
import com.thoughtworks.xstream.annotations.AnnotationReflectionConverter;
import com.thoughtworks.xstream.annotations.Annotations;


/**
 * @author J&ouml;rg Schaible
 */
public class XStream12AnnotationCompatibilityTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.registerConverter(
            new AnnotationReflectionConverter(xstream.getMapper(), xstream
                .getReflectionProvider(), new AnnotationProvider()), XStream.PRIORITY_VERY_LOW);
        xstream.alias("annotatedTask", AnnotationFieldConverterTest.TaskWithAnnotations.class);
    }

    public void testDifferentConverterCanBeAnnotatedForFieldsOfSameType() {
        final AnnotationFieldConverterTest.TaskWithAnnotations task = new AnnotationFieldConverterTest.TaskWithAnnotations(
            "Tom", "Dick", "Harry");
        final String xml = ""
            + "<annotatedTask>\n"
            + "  <name1 str=\"Tom\"/>\n"
            + "  <name2>_Dick_</name2>\n"
            + "  <name3>Harry</name3>\n"
            + "</annotatedTask>";
        assertBothWays(task, xml);
    }

    public void testImplicitCollection() {
        String expected = ""
            + "<root>\n"
            + "  <string>one</string>\n"
            + "  <string>two</string>\n"
            + "</root>";
        Annotations.configureAliases(xstream, ImplicitCollectionTest.ImplicitRootOne.class);
        ImplicitCollectionTest.ImplicitRootOne implicitRoot = new ImplicitCollectionTest.ImplicitRootOne();
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

}
