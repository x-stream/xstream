package com.thoughtworks.xstream.io.squidgey;

import com.thoughtworks.xstream.io.xml.CompactWriter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.StringReader;
import java.io.StringWriter;

public class SquidgeyTranslatorTest extends TestCase {

    private String expectedXml;
    private String squidgeyInput;

    public SquidgeyTranslatorTest(String testName, String expectedXml, String squidgeyInput) {
        super(testName);
        this.expectedXml = expectedXml;
        this.squidgeyInput = squidgeyInput;
    }

    protected void runTest() throws Throwable {
        SquidgeyTranslator translator = new SquidgeyTranslator();
        StringWriter output = new StringWriter();
        translator.transform(new StringReader(squidgeyInput), new CompactWriter(output));
        assertEquals(expectedXml, output.toString());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new SquidgeyTranslatorTest("single element",
                "<hello/>",
                "hello"));
        suite.addTest(new SquidgeyTranslatorTest("nested elements",
                "<a><b><ooh/></b><b><aah/></b></a>",
                "a{b{ooh},b{aah}}"));
        suite.addTest(new SquidgeyTranslatorTest("attributes",
                "<hello one=\"1\" two=\"2\"><child three=\"3\"/></hello>",
                "hello(one[1],two[2]){child(three[3])}"));
        suite.addTest(new SquidgeyTranslatorTest("text",
                "<root><a>value</a><b>more</b></root>",
                "root{a[value],b[more]}"));
        suite.addTest(new SquidgeyTranslatorTest("text with whitespace",
                "<root> hello world </root>",
                "root[ hello world ]"));
        suite.addTest(new SquidgeyTranslatorTest("legal chars",
                "<he:llo.world-there a.b=\"c\">it's great (here)</he:llo.world-there>",
                "he:llo.world-there(a.b[c])[it's great (here)]"));
        // todo: escape ] in text, new lines, escape <> in xml
        return suite;
    }

}
