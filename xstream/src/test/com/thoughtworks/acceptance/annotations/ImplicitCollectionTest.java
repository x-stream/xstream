/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. December 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test for annotations mapping implicit collections.
 * 
 * @author Lucio Benfante
 * @author J&ouml;rg Schaible
 */
public class ImplicitCollectionTest extends AbstractAcceptanceTest {

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
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

    public void testAnnotationWithItemFieldName() {
        String expected = ""
            + "<root>\n"
            + "  <value>one</value>\n"
            + "  <value>two</value>\n"
            + "</root>";
        ImplicitRootTwo implicitRoot = new ImplicitRootTwo();
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

    public void testAnnotationFailsForInvalidFieldType() {
        try {
            xstream.processAnnotations(InvalidImplicitRoot.class);
            fail("Thrown " + InitializationException.class.getName() + " expected");
        } catch (final InitializationException e) {
            assertTrue(e.getMessage().indexOf("\"value\"") > 0);
        }
    }

    @XStreamAlias("root")
    public static class ImplicitRootOne {
        @XStreamImplicit()
        private List<String> values = new ArrayList<String>();

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    @XStreamAlias("root")
    public static class ImplicitRootTwo {
        @XStreamImplicit(itemFieldName = "value")
        private List<String> values = new ArrayList<String>();

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    @XStreamAlias("root")
    public static class InvalidImplicitRoot {
        @XStreamImplicit(itemFieldName = "outch")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XStreamAlias("implicit")
    public static class ImplicitParameterizedType {
        @XStreamImplicit(itemFieldName = "line")
        private ArrayList<ArrayList<Point>> signatureLines;
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

    public void testAnnotationHandlesParameterizedTypes() {
        String xml = ""
            + "<implicit>\n"
            + "  <line>\n"
            + "    <point x=\"33\" y=\"11\"/>\n"
            + "  </line>\n"
            + "</implicit>";
        ImplicitParameterizedType root = new ImplicitParameterizedType();
        root.signatureLines = new ArrayList<ArrayList<Point>>();
        root.signatureLines.add(new ArrayList<Point>());
        root.signatureLines.get(0).add(new Point(33, 11));
        assertBothWays(root, xml);
    }

    @XStreamAlias("type")
    public static class ParametrizedTypeIsInterface {
        @XStreamImplicit()
        private ArrayList<Map> list = new ArrayList<Map>();
    }

    public void testWorksForTypesThatAreInterfaces() {
        ParametrizedTypeIsInterface type = new ParametrizedTypeIsInterface();
        type.list = new ArrayList<Map>();
        type.list.add(new HashMap());
        String xml = "" //
            + "<type>\n" // 
            + "  <map/>\n" //
            + "</type>";
        assertBothWays(type, xml);
    }

    @XStreamAlias("untyped")
    private static class Untyped {
        @XStreamImplicit
        private List list = new ArrayList();

        public Untyped() {
            list.add("1");
        }
    }

    public void testCanHandleUntypedCollections() {
        Untyped untyped = new Untyped();
        String xml = "" //
            + "<untyped>\n" //
            + "  <string>1</string>\n" //
            + "</untyped>";
        assertBothWays(untyped, xml);
    }
}
