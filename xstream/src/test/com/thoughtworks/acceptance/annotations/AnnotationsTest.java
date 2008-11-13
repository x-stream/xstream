/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. September 2005 by Mauro Talevi
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

import java.util.ArrayList;
import java.util.List;


/**
 * Tests for annotation detection.
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class AnnotationsTest extends AbstractAcceptanceTest {
    
    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    @XStreamAlias("param")
    public static class ParameterizedContainer {

        private ParameterizedType<InternalType> type;

        public ParameterizedContainer() {
            type = new ParameterizedType<InternalType>(new InternalType());
        }

    }

    @XStreamAlias("param")
    public static class DoubleParameterizedContainer {

        private ArrayList<ArrayList<InternalType>> list;

        public DoubleParameterizedContainer() {
            list = new ArrayList<ArrayList<InternalType>>();
            list.add(new ArrayList<InternalType>());
            list.get(0).add(new InternalType());
        }

    }
    
    @XStreamAlias("second")
    public static class InternalType {
        @XStreamAlias("aliased")
        private String original = "value";

        @Override
        public boolean equals(Object obj) {
            return obj instanceof InternalType
                ? original.equals(((InternalType)obj).original)
                : false;
        }

    }

    @XStreamAlias("typeAlias")
    public static class ParameterizedType<T> {
        @XStreamAlias("fieldAlias")
        private T object;

        public ParameterizedType(T object) {
            this.object = object;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ParameterizedType ? object
                .equals(((ParameterizedType)obj).object) : false;
        }
    }

    public void testAreDetectedInParameterizedTypes() {
        String xml = ""
            + "<param>\n"
            + "  <type>\n"
            + "    <fieldAlias class=\"second\">\n"
            + "      <aliased>value</aliased>\n"
            + "    </fieldAlias>\n"
            + "  </type>\n"
            + "</param>";
        assertBothWays(new ParameterizedContainer(), xml);
    }

    public void testAreDetectedInNestedParameterizedTypes() {
        String xml = ""
            + "<param>\n"
            + "  <list>\n"
            + "    <list>\n"
            + "      <second>\n"
            + "        <aliased>value</aliased>\n"
            + "      </second>\n"
            + "    </list>\n"
            + "  </list>\n"
            + "</param>";
        assertBothWays(new DoubleParameterizedContainer(), xml);
    }

    public void testAreDetectedInArrays() {
        InternalType[] internalTypes = new InternalType[]{
            new InternalType(), new InternalType()};
        String xml = ""
            + "<second-array>\n"
            + "  <second>\n"
            + "    <aliased>value</aliased>\n"
            + "  </second>\n"
            + "  <second>\n"
            + "    <aliased>value</aliased>\n"
            + "  </second>\n"
            + "</second-array>";
        assertBothWays(internalTypes, xml);
    }

    public void testAreDetectedInParametrizedArrays() {
        ParameterizedType<String>[] types = new ParameterizedType[]{
            new ParameterizedType<String>("foo"), new ParameterizedType<String>("bar")};
        String xml = ""
            + "<typeAlias-array>\n"
            + "  <typeAlias>\n"
            + "    <fieldAlias class=\"string\">foo</fieldAlias>\n"
            + "  </typeAlias>\n"
            + "  <typeAlias>\n"
            + "    <fieldAlias class=\"string\">bar</fieldAlias>\n"
            + "  </typeAlias>\n"
            + "</typeAlias-array>";
        assertBothWays(types, xml);
    }
    
    public void testAreDetectedInJDKCollection() {
        List<InternalType> list = new ArrayList<InternalType>();
        list.add(new InternalType());
        String xml = ""
            + "<list>\n"
            + "  <second>\n"
            + "    <aliased>value</aliased>\n"
            + "  </second>\n"
            + "</list>";
        assertBothWays(list, xml);
    }

    public void testForClassIsDetectedAtDeserialization() {
        // must preprocess annotations here
        xstream.processAnnotations(InternalType.class);
        InternalType internalType = new InternalType();
        String xml = "" // 
            + "<second>\n" // 
            + "  <aliased>value</aliased>\n" // 
            + "</second>";
        assertEquals(internalType, xstream.fromXML(xml));
    }

    @XStreamInclude({InternalType.class})
    interface Include {
    }

    public void testCanBeIncluded() {
        // must preprocess annotations from marker interface with inclusion
        xstream.processAnnotations(Include.class);
        InternalType internalType = new InternalType();
        String xml = "" // 
            + "<second>\n" // 
            + "  <aliased>value</aliased>\n" // 
            + "</second>";
        assertEquals(internalType, xstream.fromXML(xml));
    }
}
