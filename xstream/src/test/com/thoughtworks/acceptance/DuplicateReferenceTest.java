package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppReader;
import junit.framework.TestCase;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class DuplicateReferenceTest extends TestCase {

    private StringWriter buffer;
    private ReferenceByIdMarshaller marshaller;
    private ClassMapper classMapper;
    private DefaultConverterLookup converterLookup;
    private PrettyPrintWriter writer;

    protected void setUp() throws Exception {
        super.setUp();
        buffer = new StringWriter();

        writer = new PrettyPrintWriter(buffer);
        classMapper = new DefaultClassMapper();
        converterLookup = new DefaultConverterLookup(
                        new PureJavaReflectionProvider(),
                        classMapper, "class");
        classMapper.alias("y", Y.class, Y.class);
        converterLookup.setupDefaults();

        marshaller = new ReferenceByIdMarshaller(
                        writer, converterLookup, classMapper);

    }

    public void test() {

        Y same = new Y();
        same.yField = "something";
        Y another = new Y();
        another.yField = "something";

        List list = new ArrayList();
        list.add(same);
        list.add(same);
        list.add(another);

        String expected = "" +
                "<list id=\"1\">\n" +
                "  <y id=\"2\">\n" +
                "    <yField>something</yField>\n" +
                "  </y>\n" +
                "  <y reference=\"2\"/>\n" +
                "  <y id=\"3\">\n" +
                "    <yField>something</yField>\n" +
                "  </y>\n" +
                "</list>";

        marshaller.start(list);

        assertEquals(expected, buffer.toString());

        XppReader reader = new XppReader(new StringReader(buffer.toString()));

        ReferenceByIdUnmarshaller unmarshaller = new ReferenceByIdUnmarshaller(
                null, reader, converterLookup, classMapper, "class");

        List result = (List) unmarshaller.start();
        assertEquals(list, result);
        assertSame(result.get(0), result.get(1));
        assertNotSame(result.get(0), result.get(2));

    }


}
