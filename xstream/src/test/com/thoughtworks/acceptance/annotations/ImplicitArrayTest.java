/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. July 2011 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


/**
 * Test for annotations mapping implicit arrays.
 * 
 * @author J&ouml;rg Schaible
 */
public class ImplicitArrayTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    public void testAnnotation() {
        String expected = ""
            + "<root>\n"
            + "  <string>one</string>\n"
            + "  <string>two</string>\n"
            + "</root>";
        ImplicitRootOne implicitRoot = new ImplicitRootOne();
        implicitRoot.values = new String[]{
            "one", "two"
        };
        assertBothWays(implicitRoot, expected);
    }

    public void testAnnotationWithItemFieldName() {
        String expected = ""
            + "<root>\n"
            + "  <value>one</value>\n"
            + "  <value>two</value>\n"
            + "</root>";
        ImplicitRootTwo implicitRoot = new ImplicitRootTwo();
        implicitRoot.values = new String[]{
            "one", "two"
        };
        assertBothWays(implicitRoot, expected);
    }

    @XStreamAlias("root")
    public static class ImplicitRootOne {
        @XStreamImplicit()
        String[] values;
    }

    @XStreamAlias("root")
    public static class ImplicitRootTwo {
        @XStreamImplicit(itemFieldName = "value")
        String[] values;
    }

    @XStreamAlias("component")
    public static class ParameterizedComponentType {
        @XStreamImplicit(itemFieldName = "line")
        private Point[][] signatureLines;
    }

    @XStreamAlias("point")
    public static class Point {
        @XStreamAsAttribute
        private int x;
        @XStreamAsAttribute
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void testAnnotationHandlesParameterizedComponentTypes() {
        String xml = ""
            + "<component>\n"
            + "  <line>\n"
            + "    <point x=\"33\" y=\"11\"/>\n"
            + "  </line>\n"
            + "</component>";
        ParameterizedComponentType root = new ParameterizedComponentType();
        root.signatureLines = new Point[][] {
            new Point[] { new Point(33, 11) }
        };
        assertBothWays(root, xml);
    }

    @XStreamAlias("primitives")
    public static class PrimitiveArrays {
        @XStreamImplicit()
        private char[] chars;
    }

    public void testWorksForTypesThatArePrimitiveArrays() {
        PrimitiveArrays type = new PrimitiveArrays();
        type.chars = new char[]{'f', 'o', 'o'};
        String xml = "" //
            + "<primitives>\n" // 
            + "  <char>f</char>\n" //
            + "  <char>o</char>\n" //
            + "  <char>o</char>\n" //
            + "</primitives>";
        assertBothWays(type, xml);
    }
}
