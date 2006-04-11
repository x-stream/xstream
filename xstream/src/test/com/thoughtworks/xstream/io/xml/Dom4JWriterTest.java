package com.thoughtworks.xstream.io.xml;

import org.dom4j.io.OutputFormat;

import java.io.StringWriter;

public class Dom4JWriterTest extends AbstractXMLWriterTest {

    private StringWriter out;

    protected void setUp() throws Exception {
        super.setUp();

        Dom4JDriver driver = new Dom4JDriver();

        OutputFormat format = OutputFormat.createCompactFormat();
        format.setTrimText(false);
        format.setSuppressDeclaration(true);
        driver.setOutputFormat(format);

        out = new StringWriter();
        writer = driver.createWriter(out);
    }

    protected void assertXmlProducedIs(String expected) {
        writer.close();
        String actual = out.toString().trim();
        assertEquals(expected, actual);
    }

    // inherits tests from superclass

    public void testEscapesWhitespaceCharacters() {
        // This method overrides a test in the superclass to prevent it from being run, since the 
        // OutputFormat will not encode \r.
        writer.startNode("evil");
        writer.setValue("one\ntwo\rthree\r\nfour\n\rfive\tsix");
        writer.endNode();

        assertXmlProducedIs("<evil>one\n"
                + "two\rthree\r\n"
                + "four\n"
                + "\rfive\tsix</evil>");
    }
}
