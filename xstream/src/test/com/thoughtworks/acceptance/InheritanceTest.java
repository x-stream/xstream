package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;


public class InheritanceTest extends AbstractAcceptanceTest {
    public void testHandlesInheritanceHeirarchies() {
        OpenSourceSoftware openSourceSoftware = new OpenSourceSoftware("apache", "geronimo", "license");
        String xml =
                "<oss>\n" +
                "  <license>license</license>\n" +
                "  <vendor>apache</vendor>\n" +
                "  <name>geronimo</name>\n" +
                "</oss>";

        xstream.alias("oss", OpenSourceSoftware.class);
        assertBothWays(openSourceSoftware, xml);
    }
}
