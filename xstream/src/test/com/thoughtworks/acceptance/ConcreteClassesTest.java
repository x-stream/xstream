/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.xstream.converters.ConversionException;

import java.util.ArrayList;
import java.util.LinkedList;

public class ConcreteClassesTest extends AbstractAcceptanceTest {

    public void testDefaultImplementationOfInterface() {

        xstream.alias("with-list", WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                "<with-list>\n" +
                "  <things/>\n" +
                "</with-list>";

        assertBothWays(withList, expected);

    }

    public void testAlternativeImplementationOfInterface() {

        xstream.alias("with-list", WithList.class);
        xstream.alias("linked-list", LinkedList.class);

        WithList withList = new WithList();
        withList.things = new LinkedList();

        String expected =
                "<with-list>\n" +
                "  <things class=\"linked-list\"/>\n" +
                "</with-list>";

        assertBothWays(withList, expected);

    }

    interface MyInterface {
    }

    public static class MyImp1 extends StandardObject implements MyInterface {
        int x = 1;
    }

    public static class MyImp2 extends StandardObject implements MyInterface {
        int y = 2;
    }

    public static class MyHolder extends StandardObject {
        MyInterface field1;
        MyInterface field2;
    }

    public void testCustomInterfaceCanHaveMultipleImplementations() {
        xstream.alias("intf", MyInterface.class);
        xstream.alias("imp1", MyImp1.class);
        xstream.alias("imp2", MyImp2.class);
        xstream.alias("h", MyHolder.class);

        MyHolder in = new MyHolder();
        in.field1 = new MyImp1();
        in.field2 = new MyImp2();

        String expected = "" +
                "<h>\n" +
                "  <field1 class=\"imp1\">\n" +
                "    <x>1</x>\n" +
                "  </field1>\n" +
                "  <field2 class=\"imp2\">\n" +
                "    <y>2</y>\n" +
                "  </field2>\n" +
                "</h>";

        String xml = xstream.toXML(in);
        assertEquals(expected, xml);

        MyHolder out = (MyHolder) xstream.fromXML(xml);
        assertEquals(MyImp1.class, out.field1.getClass());
        assertEquals(MyImp2.class, out.field2.getClass());
        assertEquals(2, ((MyImp2) out.field2).y);
    }

    public void testUnknownChildMatchingATypeThrowsConversionException() {
        xstream.alias("h", MyHolder.class);

        String xml = ""
            + "<h>\n"
            + "  <int>100</int>\n"
            + "</h>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            // ok
        }
    }
}
