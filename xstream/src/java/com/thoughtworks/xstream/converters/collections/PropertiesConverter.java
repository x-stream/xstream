package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesConverter implements Converter {

    public boolean canConvert(Class type) {
        return Properties.class.isAssignableFrom(type);
    }

    public void toXML(MarshallingContext context) {
        Properties properties = (Properties) context.currentObject();
        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            context.xmlStartElement("property");
            context.xmlAddAttribute("name", entry.getKey().toString());
            context.xmlAddAttribute("value", entry.getValue().toString());
            context.xmlEndElement();
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
