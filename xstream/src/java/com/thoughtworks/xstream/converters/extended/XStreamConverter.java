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

    private final XStream self;
    private final Converter defaultConverter;

    /**
     * @todo Auto-generated JavaDoc
     * 
     * @since 1.2
     */
    public XStreamConverter(XStream xstream, Converter defaultConverter) {
        this.self = xstream;
        this.defaultConverter = defaultConverter;
    }

    public boolean canConvert(Class type) {
        return type == XStream.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == self) {
            throw new ConversionException("Cannot marshal the XStream instance in action");
        }
        defaultConverter.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return defaultConverter.unmarshal(reader, context);
    }

}
