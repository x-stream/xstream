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
