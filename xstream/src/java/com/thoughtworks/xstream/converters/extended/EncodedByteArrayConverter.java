package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.extended.base64.Base64Encoder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converts a byte array to a single Base64 encoding string.
 * Because this uses Sun specific classes it is not registered in XStream by default.
 *
 * @author Joe Walnes
 */
public class EncodedByteArrayConverter implements Converter {

    private Base64Encoder base64 = new Base64Encoder();

    public boolean canConvert(Class type) {
        return type.isArray() && type.getComponentType().equals(byte.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(base64.encode((byte[]) source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return base64.decode(reader.getValue());
    }

}
