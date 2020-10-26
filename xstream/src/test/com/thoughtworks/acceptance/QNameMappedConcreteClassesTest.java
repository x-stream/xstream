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

package com.thoughtworks.acceptance;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import com.thoughtworks.acceptance.someobjects.Handler;
import com.thoughtworks.acceptance.someobjects.Protocol;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.WstxDriver;


public class QNameMappedConcreteClassesTest extends AbstractAcceptanceTest {

    public static final String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>";

    protected QNameMap qnameMap;
    protected String namespace = getDefaultNS(WithList.class);

    public void testUsingNamespace() {
        // lets register some qnames
        final QName qname = new QName(namespace, "withList", "w");
        qnameMap.registerMapping(qname, WithList.class);

        final WithList<?> withList = new WithList<>();
        withList.things = new ArrayList<>();

        final String expected = ""
            + XML_HEADER
            + "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">"
            + /**/ "<things/>"
            + "</w:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDefaultNamespace() {
        qnameMap.setDefaultNamespace(namespace);
        xstream.alias("withList", WithList.class);

        final WithList<?> withList = new WithList<>();
        withList.things = new ArrayList<>();

        final String expected = XML_HEADER
            + "<withList xmlns=\"java://com.thoughtworks.acceptance.someobjects\">"
            + /**/ "<things/>"
            + "</withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDefaultNamespaceAndPrefix() {
        qnameMap.setDefaultNamespace(namespace);
        qnameMap.setDefaultPrefix("x");
        final QName qname = new QName(namespace, "withList", "x");
        qnameMap.registerMapping(qname, WithList.class);

        final WithList<?> withList = new WithList<>();
        withList.things = new ArrayList<>();

        final String expected = XML_HEADER
            + "<x:withList xmlns:x=\"java://com.thoughtworks.acceptance.someobjects\">"
            + /**/ "<x:things/>"
            + "</x:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDifferentNamespaces() {
        // lets register some qnames
        qnameMap.registerMapping(new QName(namespace, "withList", "w"), WithList.class);
        qnameMap.registerMapping(new QName("urn:foo", "things", "f"), "things");

        final WithList<?> withList = new WithList<>();
        withList.things = new ArrayList<>();

        final String expected = XML_HEADER
            + "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">"
            + /**/ "<f:things xmlns:f=\"urn:foo\"/>"
            + "</w:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDifferentNamespacesWithAliases() {
        xstream.alias("handler", X.class);
        xstream.alias("protocol", Y.class);

        qnameMap.registerMapping(new QName(getDefaultNS(Handler.class) + 1, "handler", "h"), "handler");
        qnameMap.registerMapping(new QName(getDefaultNS(Protocol.class) + 2, "protocol", "p"), "innerObj");

        final X x = new X();
        x.aStr = "foo";
        x.anInt = 42;
        x.innerObj = new Y();
        x.innerObj.yField = "YField";

        final String expected = XML_HEADER
            + "<h:handler xmlns:h=\"java://com.thoughtworks.acceptance.someobjects1\">"
            + /**/ "<aStr>foo</aStr>"
            + /**/ "<anInt>42</anInt>"
            + /**/ "<p:protocol xmlns:p=\"java://com.thoughtworks.acceptance.someobjects2\">"
            + /**//**/ "<yField>YField</yField>"
            + /**/ "</p:protocol>"
            + "</h:handler>";

        assertBothWays(x, expected);
    }

    public void testUsingDifferentNamespacesSameAliases() {
        xstream.alias("handler", X.class);
        xstream.aliasField("protocol", X.class, "innerObj");

        qnameMap.setDefaultNamespace(getDefaultNS(Handler.class));
        qnameMap.registerMapping(new QName(getDefaultNS(String.class)+1, "item"), "aStr");
        qnameMap.registerMapping(new QName(getDefaultNS(Integer.class)+2, "item"), "anInt");

        final X x = new X();
        x.aStr = "foo";
        x.anInt = 42;
        x.innerObj = new Y();
        x.innerObj.yField = "YField";

        final String expected = XML_HEADER
            + "<handler xmlns=\"java://com.thoughtworks.acceptance.someobjects\">"
            + /**/ "<item xmlns=\"java://java.lang1\">foo</item>"
            + /**/ "<item xmlns=\"java://java.lang2\">42</item>"
            + /**/ "<protocol>"
            + /**//**/ "<yField>YField</yField>"
            + /**/ "</protocol>"
            + "</handler>";

        assertBothWays(x, expected);
    }

    @Override
    protected HierarchicalStreamDriver createDriver() {
        // careful, called from inside base class constructor
        qnameMap = new QNameMap();
        final StaxDriver driver = new WstxDriver(qnameMap); //TODO: Test for all StAX drivers
        driver.setRepairingNamespace(false);
        return driver;
    }

    protected String getDefaultNS(final Class<?> type) {
        return "java://" + type.getPackage().getName();
    }
}
