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

package com.thoughtworks.xstream.core;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByXPathMarshallingStrategyTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("thing", Thing.class);
    }

    public static class Thing extends StandardObject {
        private static final long serialVersionUID = 200405L;
        @SuppressWarnings("unused")
        private String name;

        public Thing() {
        }

        public Thing(final String name) {
            this.name = name;
        }
    }

    public void testStoresReferencesUsingRelativeXPath() {
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        final Thing a = new Thing("a");
        final Thing b = new Thing("b");
        final Thing c = b;

        final List<Thing> list = new ArrayList<Thing>();
        list.add(a);
        list.add(b);
        list.add(c);

        final String expected = ""
            + "<list>\n"
            + "  <thing>\n"
            + "    <name>a</name>\n"
            + "  </thing>\n"
            + "  <thing>\n"
            + "    <name>b</name>\n"
            + "  </thing>\n"
            + "  <thing reference=\"../thing[2]\"/>\n" // xpath
            + "</list>";

        assertBothWays(list, expected);
    }

    public void testStoresReferencesUsingAbsoluteXPath() {
        xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);

        final Thing a = new Thing("a");
        final Thing b = new Thing("b");
        final Thing c = b;

        final List<Thing> list = new ArrayList<Thing>();
        list.add(a);
        list.add(b);
        list.add(c);

        final String expected = ""
            + "<list>\n"
            + "  <thing>\n"
            + "    <name>a</name>\n"
            + "  </thing>\n"
            + "  <thing>\n"
            + "    <name>b</name>\n"
            + "  </thing>\n"
            + "  <thing reference=\"/list/thing[2]\"/>\n" // xpath
            + "</list>";

        assertBothWays(list, expected);
    }

    public class CountingXPathStrategy extends ReferenceByXPathMarshallingStrategy {

        public CountingXPathStrategy() {
            super(ReferenceByXPathMarshallingStrategy.ABSOLUTE);
        }

        public ReferenceByXPathMarshaller requestedMarshaller;
        public ReferenceByXPathUnmarshaller requestedUnmarshaller;

        @Override
        protected ReferenceByXPathUnmarshaller createUnmarshallingContext(final Object root,
                final HierarchicalStreamReader reader, final ConverterLookup converterLookup, final Mapper mapper) {

            assertNull("strategy can only make one unmarshaller", requestedUnmarshaller);
            requestedUnmarshaller = (ReferenceByXPathUnmarshaller)super.createUnmarshallingContext(root, reader,
                converterLookup, mapper);
            return requestedUnmarshaller;
        }

        @Override
        protected ReferenceByXPathMarshaller createMarshallingContext(final HierarchicalStreamWriter writer,
                final ConverterLookup converterLookup, final Mapper mapper) {

            assertNull("strategy can only make one marshaller", requestedMarshaller);
            requestedMarshaller = (ReferenceByXPathMarshaller)super.createMarshallingContext(writer, converterLookup,
                mapper);
            return requestedMarshaller;
        }
    }

    public void testDoNotKeepXPathMapForImmutablesOnMarshall() throws MalformedURLException {
        // configure XStream
        final CountingXPathStrategy marshallingStrategy = new CountingXPathStrategy();
        xstream.setMarshallingStrategy(marshallingStrategy);

        // setup document
        final List<URL> list = new ArrayList<URL>();
        final URL url = new URL("http://jira.codehaus.org/browse");
        list.add(url);
        list.add(url);

        xstream.toXML(list);

        // assert
        final ObjectIdDictionary<?> trackedPathsOnMarshal = getReferences(marshallingStrategy.requestedMarshaller);

        assertTrue(trackedPathsOnMarshal.containsId(list));
        assertEquals(1, trackedPathsOnMarshal.size());
    }

    public void testDoNotKeepXPathMapForImmutablesOnUnmarshall() {
        // configure XStream
        final CountingXPathStrategy marshallingStrategy = new CountingXPathStrategy();
        xstream.setMarshallingStrategy(marshallingStrategy);

        // setup document
        final String document = ""
            + "<list>"
            + "  <url>http://jira.codehaus.org/browse</url>"
            + "  <url>http://jira.codehaus.org/browse</url>"
            + "</list>";

        xstream.fromXML(document);

        // assert
        final Map<Path, Object> trackedPathsOnUnmarshal = getReferences(marshallingStrategy.requestedUnmarshaller);

        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/list")));
        assertEquals(1, trackedPathsOnUnmarshal.size());
    }

    public static class DomainType extends StandardObject {
        private static final long serialVersionUID = 201507L;
        public String value;

        public DomainType(final String value) {
            this.value = value;
        }
    }

    public void testDoesKeepXPathMapForBackwardsCompatibleImmutablesOnUnmarshall() {
        // configure XStream
        final CountingXPathStrategy marshallingStrategy = new CountingXPathStrategy();
        xstream.setMarshallingStrategy(marshallingStrategy);
        xstream.addImmutableType(Thing.class, true);

        // setup document
        final String document = ""
            + "<list>"
            + "  <thing>"
            + "    <name>JUnit</name>"
            + "  </thing>"
            + "  <thing>"
            + "    <name>JUnit</name>"
            + "  </thing>"
            + "</list>";

        xstream.fromXML(document);

        // assert
        final Map<Path, Object> trackedPathsOnUnmarshal = getReferences(marshallingStrategy.requestedUnmarshaller);

        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/list")));
        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/list/thing")));
        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/list/thing[2]")));
        assertEquals(3, trackedPathsOnUnmarshal.size());
    }

    @SuppressWarnings("unchecked")
    private Map<Path, Object> getReferences(final ReferenceByXPathUnmarshaller requestedUnmarshaller) {
        try {
            final Field field = AbstractReferenceUnmarshaller.class.getDeclaredField("values");
            field.setAccessible(true);
            return (Map<Path, Object>)field.get(requestedUnmarshaller);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectIdDictionary<?> getReferences(final ReferenceByXPathMarshaller requestedMarshaller) {
        try {
            final Field field = AbstractReferenceMarshaller.class.getDeclaredField("references");
            field.setAccessible(true);
            return (ObjectIdDictionary<?>)field.get(requestedMarshaller);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
