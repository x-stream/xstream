package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesConverter implements Converter {

    public boolean canConvert(Class type) {
        return Properties.class.isAssignableFrom(type);
    }

    public void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Properties properties = (Properties) source;
        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startNode("property");
            writer.addAttribute("getNodeName", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.startNode();
        }
    }

    public Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Properties properties = new Properties();
        while (reader.getNextChildNode()) {
            reader.getNextChildNode();
            String name = reader.getAttribute("getNodeName");
            String value = reader.getAttribute("value");;
            properties.setProperty(name, value);
            reader.getParentNode();
        }
        return properties;
    }
}
