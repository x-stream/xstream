package com.thoughtworks.xstream.io.xml;

import org.dom4j.DocumentFactory;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import java.io.Writer;
import java.io.StringWriter;

public class Dom4JWriterTest extends AbstractXMLWriterTest {

    private StringWriter out;

    protected void setUp() throws Exception {
        super.setUp();

        Dom4JDriver driver = new Dom4JDriver();

        OutputFormat format = OutputFormat.createCompactFormat();
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
        // TODO: Support whitespaces.
        // This method overrides a test in the superclass to prevent it from being run.
    }
}
