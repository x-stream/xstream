/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. April 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReferenceByXPathMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("thing", Thing.class);
    }

    public static class Thing extends StandardObject {
        private String name;

        public Thing() {
        }

        public Thing(String name) {
            this.name = name;
        }
    }

    public void testStoresReferencesUsingRelativeXPath() {
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        Thing a = new Thing("a");
        Thing b = new Thing("b");
        Thing c = b;

        List list = new ArrayList();
        list.add(a);
        list.add(b);
        list.add(c);

        String expected = "" +
                "<list>\n" +
                "  <thing>\n" +
                "    <name>a</name>\n" +
                "  </thing>\n" +
                "  <thing>\n" +
                "    <name>b</name>\n" +
                "  </thing>\n" +
                "  <thing reference=\"../thing[2]\"/>\n" + // xpath
                "</list>";

        assertBothWays(list, expected);
    }

    public void testStoresReferencesUsingAbsoluteXPath() {
        xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);

        Thing a = new Thing("a");
        Thing b = new Thing("b");
        Thing c = b;

        List list = new ArrayList();
        list.add(a);
        list.add(b);
        list.add(c);

        String expected = "" +
                "<list>\n" +
                "  <thing>\n" +
                "    <name>a</name>\n" +
                "  </thing>\n" +
                "  <thing>\n" +
                "    <name>b</name>\n" +
                "  </thing>\n" +
                "  <thing reference=\"/list/thing[2]\"/>\n" + // xpath
                "</list>";

        assertBothWays(list, expected);
    }

    public class CountingXPathStrategy extends ReferenceByXPathMarshallingStrategy{

        public CountingXPathStrategy() {
            super(ReferenceByXPathMarshallingStrategy.ABSOLUTE);
        }

        public ReferenceByXPathMarshaller requestedMarshaller;
        public ReferenceByXPathUnmarshaller requestedUnmarshaller;

        @Override
        protected ReferenceByXPathUnmarshaller createUnmarshallingContext(Object root,
                                                                          HierarchicalStreamReader reader,
                                                                          ConverterLookup converterLookup,
                                                                          Mapper mapper) {

            assertNull("strategy can only make one unmarshaller", requestedUnmarshaller);
            requestedUnmarshaller = (ReferenceByXPathUnmarshaller) super.createUnmarshallingContext(root, reader, converterLookup, mapper);
            return requestedUnmarshaller;
        }

        @Override
        protected ReferenceByXPathMarshaller createMarshallingContext(HierarchicalStreamWriter writer,
                                                                      ConverterLookup converterLookup,
                                                                      Mapper mapper) {

            assertNull("strategy can only make one marshaller", requestedMarshaller);
            requestedMarshaller = (ReferenceByXPathMarshaller) super.createMarshallingContext(writer, converterLookup, mapper);
            return requestedMarshaller;
        }
    }

    @SuppressWarnings("unchecked") //use of document.things : Untyped list, only contains URLs
    public void testDoNotKeepXPathMapForImmutablesOnMarshall() throws MalformedURLException {
        //configure XStream
        CountingXPathStrategy marshallingStrategy = new CountingXPathStrategy();
        xstream.alias("ThingsDocument", WithNamedList.class);
        xstream.setMarshallingStrategy(marshallingStrategy);

        //setup document
        WithNamedList document = new WithNamedList("immutables!");
        URL sharedURL = new URL("http://jira.codehaus.org/browse");
        document.things.add(sharedURL);
        document.things.add(sharedURL);

        //act
        String serialized = xstream.toXML(document);

        //assert
        ObjectIdDictionary trackedPathsOnMarshal = getReferences(marshallingStrategy.requestedMarshaller);

        assertTrue(trackedPathsOnMarshal.containsId(document));
        assertTrue(trackedPathsOnMarshal.containsId(document.things));
        assertEquals(2, trackedPathsOnMarshal.size());
    }

    @SuppressWarnings("unchecked") //use of document.things : Untyped list, only contains URLs
    public void testDoNotKeepXPathMapForImmutablesOnUnmarshall() throws MalformedURLException{
        //configure XStream
        CountingXPathStrategy marshallingStrategy = new CountingXPathStrategy();
        xstream.alias("ThingsDocument", WithNamedList.class);
        xstream.setMarshallingStrategy(marshallingStrategy);

        //setup document
        String document = "" +
                "<ThingsDocument>" +
                "  <things>" +
                "    <url>http://jira.codehaus.org/browse</url>" +
                "    <url>http://jira.codehaus.org/browse</url>" +
                "  </things>" +
                "  <name>immutables!</name>" +
                "</ThingsDocument>";

        //act
        Object result = xstream.fromXML(document);

        //assert
        Map trackedPathsOnUnmarshal = getReferences(marshallingStrategy.requestedUnmarshaller);

        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/ThingsDocument/things")));
        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/ThingsDocument")));
        assertEquals(2, trackedPathsOnUnmarshal.size());
    }

    public static class DomainType extends StandardObject{
        public String value;

        public DomainType(String value){
            this.value = value;
        }
    }

    @SuppressWarnings("unchecked") //use of document.things : Untyped list, only contains URLs
    public void testDoesKeepXPathMapForBackwardsCompatibleImmutablesOnUnmarshall() throws MalformedURLException{
        //configure XStream
        CountingXPathStrategy marshallingStrategy = new CountingXPathStrategy();
        xstream.alias("ThingsDocument", WithNamedList.class);
        xstream.alias("DomainType", DomainType.class);
        xstream.setMarshallingStrategy(marshallingStrategy);
        xstream.addImmutableType(DomainType.class);

        //setup document
        String document = "" +
                "<ThingsDocument>" +
                "  <things>" +
                "    <DomainType>" +
                "      <value>http://jira.codehaus.org/browse</value>" +
                "    </DomainType>" +
                "    <DomainType>" +
                "      <value>http://jira.codehaus.org/browse</value>" +
                "    </DomainType>" +
                "  </things>" +
                "  <name>immutables!</name>" +
                "</ThingsDocument>";

        //act
        Object result = xstream.fromXML(document);

        //assert
        Map trackedPathsOnUnmarshal = getReferences(marshallingStrategy.requestedUnmarshaller);

        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/ThingsDocument/things")));
        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/ThingsDocument")));
        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/ThingsDocument/things/DomainType")));
        assertTrue(trackedPathsOnUnmarshal.containsKey(new Path("/ThingsDocument/things/DomainType[2]")));
        assertEquals(4, trackedPathsOnUnmarshal.size());
    }

    private Map<Path, Object> getReferences(ReferenceByXPathUnmarshaller requestedUnmarshaller) {
        try {
            Field field = AbstractReferenceUnmarshaller.class.getDeclaredField("values");
            field.setAccessible(true);
            return (Map) field.get(requestedUnmarshaller);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectIdDictionary getReferences(ReferenceByXPathMarshaller requestedMarshaller) {
        try {
            Field field = AbstractReferenceMarshaller.class.getDeclaredField("references");
            field.setAccessible(true);
            return (ObjectIdDictionary) field.get(requestedMarshaller);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
