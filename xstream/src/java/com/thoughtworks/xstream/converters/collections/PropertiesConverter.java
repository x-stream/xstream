package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
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
            writer.startElement("property");
            writer.addAttribute("name", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.endElement();
        }
    }

    public Object fromXML(UnmarshallingContext context) {
        Properties properties = new Properties();
        while (context.xmlNextChild()) {
            context.xmlNextChild();
            String name = context.xmlAttribute("name");
            String value = context.xmlAttribute("value");;
            properties.setProperty(name, value);
            context.xmlPop();
        }
        return properties;
    }
}
