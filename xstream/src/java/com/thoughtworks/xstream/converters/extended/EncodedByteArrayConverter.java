package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterDecoder;
import sun.misc.CharacterEncoder;

import java.io.IOException;

/**
 * Converts a byte array to a single encoding string (such as base64).
 * Because this uses Sun specific classes it is not registered in XStream by default.
 *
 * The following CharacterEncoder/CharacterDecoders pairs are available.
 * <ul>
 *  <li> sun.misc.BASE64Encoder, sun.mis.BASE64Decoder (default) </li>
 *  <li> sun.misc.UCEncoder, sun.misc.UCDecoder </li>
 *  <li> sun.misc.UUEncoder, sun.misc.UUDecoder </li>
 * </ul>
 */
public class EncodedByteArrayConverter implements Converter {

    private CharacterEncoder encoder;
    private CharacterDecoder decoder;

    /**
     * Default converter uses BASE64 encoding.
     */
    public EncodedByteArrayConverter() {
        this.encoder = new BASE64Encoder();
        this.decoder = new BASE64Decoder();
    }

    public EncodedByteArrayConverter(CharacterEncoder encoder, CharacterDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public boolean canConvert(Class type) {
        return type.isArray() && type.getComponentType().equals(byte.class);
    }

    public void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        byte[] data = (byte[]) source;
        writer.writeText(encoder.encode(data));
    }

    public Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
        try {
            return decoder.decodeBuffer(reader.text());
        } catch (IOException e) {
            throw new ConversionException("Cannot decode binary data", e);
        }
    }

}
