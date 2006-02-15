package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Base abstract implementation of  {@link com.thoughtworks.xstream.converters.Converter}
 * and  {@link com.thoughtworks.xstream.converters.SingleValueConverter}.
 * <p/>
 * <p>Subclasses should implement methods canConvert(Class) and fromString(String) for the conversion.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 */
public abstract class AbstractSingleValueConverter implements Converter, SingleValueConverter {

    public abstract boolean canConvert(Class type);

    public String toString(Object obj) {
        return obj.toString();
    }

    public abstract Object fromString(String str);

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(toString(source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return fromString(reader.getValue());
    }

}
