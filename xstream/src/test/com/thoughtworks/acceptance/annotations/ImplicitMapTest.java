/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. August 2011 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Test for annotations mapping implicit maps.
 * 
 * @author J&ouml;rg Schaible
 */
public class ImplicitMapTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        xstream.addDefaultImplementation(LinkedHashMap.class, Map.class);
        return xstream;
    }

    public void testAnnotation() {
        String expected = ""
            + "<root>\n"
            + "  <software>\n"
            + "    <vendor>Microsoft</vendor>\n"
            + "    <name>Windows</name>\n"
            + "  </software>\n"
            + "  <software>\n"
            + "    <vendor>Red Hat</vendor>\n"
            + "    <name>Linux</name>\n"
            + "  </software>\n"
            + "</root>";
        ImplicitRootOne implicitRoot = new ImplicitRootOne();
        implicitRoot.getValues().put("Windows", new Software("Microsoft", "Windows"));
        implicitRoot.getValues().put("Linux", new Software("Red Hat", "Linux"));
        assertBothWays(implicitRoot, expected);
    }

    public void testAnnotationWithItemFieldName() {
        String expected = ""
            + "<root>\n"
            + "  <value>\n"
            + "    <vendor>Microsoft</vendor>\n"
            + "    <name>Windows</name>\n"
            + "  </value>\n"
            + "  <value>\n"
            + "    <vendor>Red Hat</vendor>\n"
            + "    <name>Linux</name>\n"
            + "  </value>\n"
            + "</root>";
        ImplicitRootTwo implicitRoot = new ImplicitRootTwo();
        implicitRoot.getValues().put("Windows", new Software("Microsoft", "Windows"));
        implicitRoot.getValues().put("Linux", new Software("Red Hat", "Linux"));
        assertBothWays(implicitRoot, expected);
    }

    @XStreamAlias("root")
    public static class ImplicitRootOne {
        @XStreamImplicit(keyFieldName = "name")
        private Map<String, Software> values = new LinkedHashMap<String, Software>();

        public Map<String, Software> getValues() {
            return values;
        }

        public void setValues(Map<String, Software> values) {
            this.values = values;
        }
    }

    @XStreamAlias("root")
    public static class ImplicitRootTwo {
        @XStreamImplicit(keyFieldName = "name", itemFieldName = "value")
        private Map<String, Software> values = new LinkedHashMap<String, Software>();

        public Map<String, Software> getValues() {
            return values;
        }

        public void setValues(Map<String, Software> values) {
            this.values = values;
        }
    }

    @XStreamAlias("implicit")
    public static class ImplicitParameterizedType<T> {
        @XStreamImplicit(itemFieldName = "line", keyFieldName="id")
        private LinkedHashMap<T,Point<T>> signatureLines;
    }

    @XStreamAlias("point")
    public static class Point<T> {
        @XStreamAsAttribute
        private int x;
        @XStreamAsAttribute
        private int y;
        private final T id;

        public Point(T id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    public void testAnnotationHandlesParameterizedTypes() {
        String xml = ""
            + "<implicit>\n"
            + "  <line x=\"33\" y=\"11\">\n"
            + "    <id class=\"long\">42</id>\n"
            + "  </line>\n"
            + "</implicit>";
        ImplicitParameterizedType<Long> root = new ImplicitParameterizedType<Long>();
        root.signatureLines = new LinkedHashMap<Long, Point<Long>>();
        root.signatureLines.put(42L, new Point<Long>(42L, 33, 11));
        assertBothWays(root, xml);
    }

    @XStreamAlias("type")
    public static class ParametrizedTypeIsInterface {
        @XStreamImplicit(keyFieldName="name")
        private Map<String, Code> map = new LinkedHashMap<String, Code>();
    }

    public void testWorksForTypesThatAreInterfaces() {
        ParametrizedTypeIsInterface type = new ParametrizedTypeIsInterface();
        type.map.put("Windows", new Software("Microsoft", "Windows"));
        String xml = "" //
            + "<type>\n" //
            + "  <software>\n"
            + "    <vendor>Microsoft</vendor>\n"
            + "    <name>Windows</name>\n"
            + "  </software>\n"
            + "</type>";
        assertBothWays(type, xml);
    }

    @XStreamAlias("untyped")
    private static class Untyped {
        @XStreamImplicit(keyFieldName="name")
        private Map map = new HashMap();

        public Untyped() {
            map.put("Windows", new Software("Microsoft", "Windows"));
        }
    }

    public void testCanHandleUntypedCollections() {
        Untyped untyped = new Untyped();
        String xml = "" //
            + "<untyped>\n" //
            + "  <software>\n"
            + "    <vendor>Microsoft</vendor>\n"
            + "    <name>Windows</name>\n"
            + "  </software>\n"
            + "</untyped>";
        assertBothWays(untyped, xml);
    }
    
    public interface Code {}

    @XStreamAlias("software")
    public static class Software extends StandardObject implements Code {

        public String vendor;
        public String name;

        public Software() {
        }

        public Software(String vendor, String name) {
            this.vendor = vendor;
            this.name = name;
        }
    }
}
