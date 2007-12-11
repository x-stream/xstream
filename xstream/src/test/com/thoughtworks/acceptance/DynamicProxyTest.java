/*
 * Copyright (C) 2004, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. March 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.SampleDynamicProxy;


public class DynamicProxyTest extends AbstractAcceptanceTest {
    public static class ClassWithProxyMember {
        SampleDynamicProxy.InterfaceOne one;
        SampleDynamicProxy.InterfaceTwo two;
    };

    public void testDynamicProxy() {
        assertBothWays(SampleDynamicProxy.newInstance(), ""
                + "<dynamic-proxy>\n"
                + "  <interface>com.thoughtworks.acceptance.objects.SampleDynamicProxy$InterfaceOne</interface>\n"
                + "  <interface>com.thoughtworks.acceptance.objects.SampleDynamicProxy$InterfaceTwo</interface>\n"
                + "  <handler class=\"com.thoughtworks.acceptance.objects.SampleDynamicProxy\">\n"
                + "    <aField>hello</aField>\n"
                + "  </handler>\n"
                + "</dynamic-proxy>");
    }

    public void testDynamicProxyAsFieldMember() {
        ClassWithProxyMember expected = new ClassWithProxyMember();
        expected.one = (SampleDynamicProxy.InterfaceOne)SampleDynamicProxy.newInstance();
        expected.two = (SampleDynamicProxy.InterfaceTwo)expected.one;
        xstream.alias("with-proxy", ClassWithProxyMember.class);
        assertBothWays(expected, ""
                + "<with-proxy>\n"
                + "  <one class=\"dynamic-proxy\">\n"
                + "    <interface>com.thoughtworks.acceptance.objects.SampleDynamicProxy$InterfaceOne</interface>\n"
                + "    <interface>com.thoughtworks.acceptance.objects.SampleDynamicProxy$InterfaceTwo</interface>\n"
                + "    <handler class=\"com.thoughtworks.acceptance.objects.SampleDynamicProxy\">\n"
                + "      <aField>hello</aField>\n"
                + "    </handler>\n"
                + "  </one>\n"
                + "  <two class=\"dynamic-proxy\" reference=\"../one\"/>\n"
                + "</with-proxy>");
    }

}
