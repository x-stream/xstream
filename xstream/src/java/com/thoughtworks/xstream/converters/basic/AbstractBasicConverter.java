package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Base helper class for converters that can store the
 * state of an object as a single String.
 * <p/>
 * <p>Subclasses should implement the toString(Object) and
 * fromString(String) methods for the conversion.</p>
 *
 * @author Joe Walnes
 */
public abstract class AbstractBasicConverter implements Converter {

    protected abstract Object fromString(String str);

    public abstract boolean canConvert(Class type);

    protected String toString(Object obj) {
        return obj.toString();
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(toString(source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return fromString(reader.getValue());
    }

}
