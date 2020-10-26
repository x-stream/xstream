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

package com.thoughtworks.acceptance.annotations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;


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
        final XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    @XStreamAlias("param")
    public static class ParameterizedContainer {

        final ParameterizedType<InternalType> type;

        public ParameterizedContainer() {
            type = new ParameterizedType<InternalType>(new InternalType());
        }

    }

    @XStreamAlias("param")
    public static class DoubleParameterizedContainer {

        private final ArrayList<ArrayList<InternalType>> list;

        public DoubleParameterizedContainer() {
            list = new ArrayList<ArrayList<InternalType>>();
            list.add(new ArrayList<InternalType>());
            list.get(0).add(new InternalType());
        }

    }

    @XStreamAlias("second")
    public static class InternalType {
        @XStreamAlias("aliased")
        private final String original = "value";

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof InternalType ? original.equals(((InternalType)obj).original) : false;
        }

        @Override
        public int hashCode() {
            return original.hashCode();
        }

    }

    @XStreamAlias("typeAlias")
    public static class ParameterizedType<T> {
        @XStreamAlias("fieldAlias")
        private final T object;

        public ParameterizedType(final T object) {
            this.object = object;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof ParameterizedType ? object.equals(((ParameterizedType<?>)obj).object) : false;
        }

        @Override
        public int hashCode() {
            return object.hashCode();
        }
    }

    public void testAreDetectedInParameterizedTypes() {
        final String xml = ""
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
        final String xml = ""
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
        final InternalType[] internalTypes = new InternalType[]{new InternalType(), new InternalType()};
        final String xml = ""
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
        @SuppressWarnings("unchecked")
        final ParameterizedType<String>[] types = new ParameterizedType[]{
            new ParameterizedType<String>("foo"), new ParameterizedType<String>("bar")};
        final String xml = ""
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
        final List<InternalType> list = new ArrayList<InternalType>();
        list.add(new InternalType());
        final String xml = ""
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
        final InternalType internalType = new InternalType();
        final String xml = "" //
            + "<second>\n" //
            + "  <aliased>value</aliased>\n" //
            + "</second>";
        assertEquals(internalType, xstream.fromXML(xml));
    }

    public void testForClassInObjectStreamIsDetectedAtDeserialization() throws IOException, ClassNotFoundException {
        // must preprocess annotations here
        xstream.processAnnotations(InternalType.class);
        xstream.ignoreUnknownElements();
        final InternalType internalType = new InternalType();
        final String xml = ""
            + "<root>\n"
            + "  <second>\n"
            + "    <aliased>value</aliased>\n"
            + "    <none>1</none>\n"
            + "  </second>\n"
            + "</root>";
        final ObjectInputStream in = xstream.createObjectInputStream(new StringReader(xml));
        assertEquals(internalType, in.readObject());
        in.close();
    }

    @XStreamInclude({InternalType.class})
    interface Include {}

    public void testCanBeIncluded() {
        // must preprocess annotations from marker interface with inclusion
        xstream.processAnnotations(Include.class);
        final InternalType internalType = new InternalType();
        final String xml = "" //
            + "<second>\n" //
            + "  <aliased>value</aliased>\n" //
            + "</second>";
        assertEquals(internalType, xstream.fromXML(xml));
    }
}
