package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A converter for an XStream instance.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class XStreamConverter implements Converter {

    private final XStream xstream;

    /**
     * @todo Auto-generated JavaDoc
     * 
     * @since 1.2
     */
    public XStreamConverter(XStream xstream) {
        this.xstream = xstream;
    }

    public boolean canConvert(Class type) {
        return type == XStream.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == xstream) {
            throw new ConversionException("Cannot marshal the XStream instance in action");
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

}
