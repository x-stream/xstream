package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class NullConverter implements Converter {

    public boolean canConvert(Class type) {
        return type == null;
    }

    public void toXML(MarshallingContext context) {
        context.getXMLWriter().startElement("null");
        context.getXMLWriter().endElement();
    }

    public Object fromXML(UnmarshallingContext context) {
        return null;
    }
}
