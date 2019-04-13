/*
 * Copyright (C) 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 05. August 2011 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


/**
 * Test for annotations mapping implicit maps.
 *
 * @author J&ouml;rg Schaible
 */
public class ImplicitMapTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        final XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        xstream.addDefaultImplementation(LinkedHashMap.class, Map.class);
        return xstream;
    }

    public void testAnnotation() {
        final String expected = ""
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
        final ImplicitRootOne implicitRoot = new ImplicitRootOne();
        implicitRoot.getValues().put("Windows", new Software("Microsoft", "Windows"));
        implicitRoot.getValues().put("Linux", new Software("Red Hat", "Linux"));
        assertBothWays(implicitRoot, expected);
    }

    public void testAnnotationWithItemFieldName() {
        final String expected = ""
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
        final ImplicitRootTwo implicitRoot = new ImplicitRootTwo();
        implicitRoot.getValues().put("Windows", new Software("Microsoft", "Windows"));
        implicitRoot.getValues().put("Linux", new Software("Red Hat", "Linux"));
        assertBothWays(implicitRoot, expected);
    }

    @XStreamAlias("root")
    public static class ImplicitRootOne {
        @XStreamImplicit(keyFieldName = "name")
        private Map<String, Software> values = new LinkedHashMap<>();

        public Map<String, Software> getValues() {
            return values;
        }

        public void setValues(final Map<String, Software> values) {
            this.values = values;
        }
    }

    @XStreamAlias("root")
    public static class ImplicitRootTwo {
        @XStreamImplicit(keyFieldName = "name", itemFieldName = "value")
        private Map<String, Software> values = new LinkedHashMap<>();

        public Map<String, Software> getValues() {
            return values;
        }

        public void setValues(final Map<String, Software> values) {
            this.values = values;
        }
    }

    @XStreamAlias("implicit")
    public static class ImplicitParameterizedType<T> {
        @XStreamImplicit(itemFieldName = "line", keyFieldName = "id")
        private LinkedHashMap<T, Point<T>> signatureLines;
    }

    @XStreamAlias("point")
    public static class Point<T> {
        @XStreamAsAttribute
        private final int x;
        @XStreamAsAttribute
        private final int y;
        final T id;

        public Point(final T id, final int x, final int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    public void testAnnotationHandlesParameterizedTypes() {
        final String xml = ""
            + "<implicit>\n"
            + "  <line x=\"33\" y=\"11\">\n"
            + "    <id class=\"long\">42</id>\n"
            + "  </line>\n"
            + "</implicit>";
        final ImplicitParameterizedType<Long> root = new ImplicitParameterizedType<>();
        root.signatureLines = new LinkedHashMap<>();
        root.signatureLines.put(42L, new Point<>(42L, 33, 11));
        assertBothWays(root, xml);
    }

    @XStreamAlias("type")
    public static class ParametrizedTypeIsInterface {
        @XStreamImplicit(keyFieldName = "name")
        private final Map<String, Code> map = new LinkedHashMap<>();
    }

    public void testWorksForTypesThatAreInterfaces() {
        final ParametrizedTypeIsInterface type = new ParametrizedTypeIsInterface();
        type.map.put("Windows", new Software("Microsoft", "Windows"));
        final String xml = "" //
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
        @SuppressWarnings("rawtypes")
        @XStreamImplicit(keyFieldName = "name")
        private final Map map = new HashMap();

        @SuppressWarnings("unchecked")
        public Untyped() {
            map.put("Windows", new Software("Microsoft", "Windows"));
        }
    }

    public void testCanHandleUntypedCollections() {
        final Untyped untyped = new Untyped();
        final String xml = "" //
            + "<untyped>\n"
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
        private static final long serialVersionUID = 201108L;
        public String vendor;
        public String name;

        public Software() {
        }

        public Software(final String vendor, final String name) {
            this.vendor = vendor;
            this.name = name;
        }
    }
}
