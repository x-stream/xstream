package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;


public class InheritanceTest extends AbstractAcceptanceTest {
    public void testHandlesInheritanceHeirarchies() {
        OpenSourceSoftware openSourceSoftware = new OpenSourceSoftware("apache", "geronimo", "license");
        String xml =
                "<oss>\n" +
                "  <license>license</license>\n" +
                "  <name>geronimo</name>\n" +
                "  <vendor>apache</vendor>\n" +
                "</oss>";

        xstream.alias("oss", OpenSourceSoftware.class);
        assertBothWays(openSourceSoftware, xml);
    }
}
