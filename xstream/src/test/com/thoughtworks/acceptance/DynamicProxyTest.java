package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.SampleDynamicProxy;

public class DynamicProxyTest extends AbstractAcceptanceTest {

    public void testDynamicProxy() {
        assertBothWays(SampleDynamicProxy.newInstance(),
                "<dynamic-proxy>\n" +
                "  <interface>com.thoughtworks.acceptance.objects.SampleDynamicProxy$InterfaceOne</interface>\n" +
                "  <interface>com.thoughtworks.acceptance.objects.SampleDynamicProxy$InterfaceTwo</interface>\n" +
                "  <handler class=\"com.thoughtworks.acceptance.objects.SampleDynamicProxy\">\n" +
                "    <aField>hello</aField>\n" +
                "  </handler>\n" +
                "</dynamic-proxy>");
    }

}
