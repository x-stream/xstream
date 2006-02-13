package com.thoughtworks.xstream.mapper;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.Software;

public class FieldAliasingMapperTest extends AbstractAcceptanceTest {

    public void testAllowsIndividualFieldsToBeAliased() {
        Software in = new Software("ms", "word");
        xstream.alias("software", Software.class);
        xstream.aliasField("CUSTOM-VENDOR", Software.class, "vendor");
        xstream.aliasField("CUSTOM-NAME", Software.class, "name");

        String expectedXml = "" +
                "<software>\n" +
                "  <CUSTOM-VENDOR>ms</CUSTOM-VENDOR>\n" +
                "  <CUSTOM-NAME>word</CUSTOM-NAME>\n" +
                "</software>";

        assertBothWays(in, expectedXml);
    }
}
