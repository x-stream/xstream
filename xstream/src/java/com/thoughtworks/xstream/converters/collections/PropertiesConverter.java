package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Special converter for java.util.Properties that stores
 * properties in a more compact form than java.util.Map.
 * <p/>
 * <p>Because all entries of a Properties instance
 * are Strings, a single element is used for each property
 * with two attributes; one for key and one for value.</p>
 *
 * @author Joe Walnes
 */
public class PropertiesConverter implements Converter {

    public boolean canConvert(Class type) {
        return Properties.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Properties properties = (Properties) source;
        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startNode("property");
            writer.addAttribute("name", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Properties properties = new Properties();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String name = reader.getAttribute("name");
            String value = reader.getAttribute("value");
            ;
            properties.setProperty(name, value);
            reader.moveUp();
        }
        return properties;
    }
}
