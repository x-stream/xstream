package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.StringWriter;

public class DataHolderTest extends AbstractAcceptanceTest {

    class StringWithPrefixConverter implements Converter {

        public boolean canConvert(Class type) {
            return type == String.class;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            String prefix = (String) context.get("prefix");
            if (prefix != null) {
                writer.addAttribute("prefix", prefix);
            }
            writer.setValue(source.toString());
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            assertEquals(context.get("prefix"), reader.getAttribute("prefix"));
            return null;
        }

    }

    public void testCanBePassedInExternallyToXStream() {
        // setup
        xstream.registerConverter(new StringWithPrefixConverter());
        StringWriter writer = new StringWriter();
        DataHolder dataHolder = xstream.newDataHolder();

        // execute
        dataHolder.put("prefix", "additional stuff");
        xstream.marshal("something", new PrettyPrintWriter(writer), dataHolder);

        // verify
        String expected = "<string prefix=\"additional stuff\">something</string>";
        assertEquals(expected, writer.toString());
    }


}
