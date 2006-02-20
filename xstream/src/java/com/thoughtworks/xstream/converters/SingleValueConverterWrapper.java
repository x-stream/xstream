package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Wrapper to convert a  {@link com.thoughtworks.xstream.converters.SingleValueConverter} into a
 * {@link com.thoughtworks.xstream.converters.Converter}.
 *
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 */
public class SingleValueConverterWrapper implements Converter, SingleValueConverter {

    private final SingleValueConverter wrapped;

    public SingleValueConverterWrapper(SingleValueConverter wrapped) {
        this.wrapped = wrapped;
    }

    public boolean canConvert(Class type) {
        return wrapped.canConvert(type);
    }

    public String toString(Object obj) {
        return wrapped.toString(obj);
    }

    public Object fromString(String str) {
        return wrapped.fromString(str);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(toString(source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return fromString(reader.getValue());
    }

}
