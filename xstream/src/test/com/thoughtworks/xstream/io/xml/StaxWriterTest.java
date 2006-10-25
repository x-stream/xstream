package com.thoughtworks.xstream.io.xml;

import org.apache.oro.text.perl.Perl5Util;

import javax.xml.stream.XMLOutputFactory;

import java.io.StringWriter;

/*
 * @author James Strachan
 */
public class StaxWriterTest extends AbstractXMLWriterTest {

    private StringWriter buffer;
    private Perl5Util perlUtil;

    protected void setUp() throws Exception {
        super.setUp();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        buffer = new StringWriter();
        writer = new StaxWriter(new QNameMap(), outputFactory.createXMLStreamWriter(buffer));
        perlUtil = new Perl5Util();
    }

    protected void assertXmlProducedIs(String expected) {
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
        expected = replaceAll(expected, "&#x0D;", "&#13;");
        expected = "<?xml version='1.0' encoding='utf-8'?>" + expected;
        assertEquals(expected, buffer.toString());
    }
}

