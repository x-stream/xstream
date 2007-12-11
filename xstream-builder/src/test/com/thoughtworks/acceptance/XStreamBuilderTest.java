/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. May 2007 by Guilherme Silveira
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.thoughtworks.xstream.builder.XStreamBuilder;
import com.thoughtworks.xstream.builder.processor.TypeConfigProcessor;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Guilherme Silveira
 */
public class XStreamBuilderTest extends AbstractBuilderAcceptanceTest {

    public void testSupportsBuildStyleWithAlias() {
        XStreamBuilder builder = new XStreamBuilder() {
            {
                handle(Office.class).with(alias("office"));
            }
        };

        Office office = new Office("Rua Vergueiro");
        String expected = "<office>\n  <address>Rua Vergueiro</address>\n</office>";
        assertBothWays(builder.buildXStream(), office, expected);
    }

    public static class Office {
        private String address;
        public Office(String address) {
            this.address = address;
        }
    }

    public void testHandleCorrectlyFieldAliases() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
                handle(Office.class).with(new TypeConfigProcessor[]{ 
                                                alias("office"),
                                                field("address").with(as("logradouro"))
                                            });
            }
        };

        Office office = new Office("Rua Vergueiro");
        String expected = "<office>\n  <logradouro>Rua Vergueiro</logradouro>\n</office>";
        assertBothWays(builder.buildXStream(), office, expected);

    }


    public void testHandleCorrectlyFieldOmmission() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
                handle(Office.class).with(new TypeConfigProcessor[]{
                                            alias("office"),
                							ignores("address")
                							// TODO "decision: could be" field("address").with(ignored())
                                            });
            }
        };

        Office office = new Office("Rua Vergueiro");
        String expected = "<office/>";
        assertBothWays(builder.buildXStream(), office, expected);

    }

    public static class CollectionContainer {
        Collection collection;
    }

    public void testHandleCorrectlyDefaultImplementations() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
                handle(Collection.class).with(implementedBy(HashSet.class));
                handle(CollectionContainer.class).with(alias("cc"));
            }
        };

        CollectionContainer root = new CollectionContainer();
        root.collection = new HashSet();
        String expected = "<cc>\n  <collection/>\n</cc>";

        assertBothWays(builder.buildXStream(), root, expected);

    }

    public static class DoNothingConverter implements Converter {
        private final Class support;

        public DoNothingConverter(Class aClass) {
            this.support = aClass;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            writer.startNode("wow");
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            try {
                return support.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean canConvert(Class type) {
            return support.equals(type);
        }
    }

    public void testHandleCorrectlyConverterRegistrations() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
                handle(CollectionContainer.class).with(alias("cc"));
                register(converter(new DoNothingConverter(CollectionContainer.class)));
            }
        };

        CollectionContainer root = new CollectionContainer();
        root.collection = new HashSet();
        String expected = "<cc>\n  <wow/>\n</cc>";

        assertBothWays(builder.buildXStream(), root, expected);

    }


    static class Home {
    }
    
    public void testHandleCorrectlyAbsoluteReferences() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
            	with(absoluteReferences());
            	handle(Home.class).with(alias("home"));
            }
        };

        List root = new ArrayList();
        root.add(new Home());
        root.add(root.get(0));
        String expected = "<list>\n  <home/>\n  <home reference=\"/list/home\"/>\n</list>";

        assertBothWays(builder.buildXStream(), root, expected);

    }
    
    public void testHandleCorrectlyIdReferences() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
            	with(idReferences());
            	handle(Home.class).with(alias("home"));
            }
        };

        List root = new ArrayList();
        root.add(new Home());
        root.add(root.get(0));
        String expected = "<list id=\"1\">\n  <home id=\"2\"/>\n  <home reference=\"2\"/>\n</list>";

        assertBothWays(builder.buildXStream(), root, expected);

    }
    
    public void testHandleCorrectlyNoReferences() {

        XStreamBuilder builder = new XStreamBuilder() {
            {
            	with(noReferences());
            	handle(Home.class).with(alias("home"));
            }
        };

        List root = new ArrayList();
        root.add(new Home());
        root.add(root.get(0));
        String expected = "<list>\n  <home/>\n  <home/>\n</list>";

        assertBothWays(builder.buildXStream(), root, expected);

    }
    
}
