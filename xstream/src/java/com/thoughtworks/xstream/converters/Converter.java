package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter implementations are responsible marshalling Java objects
 * to/from textual data.
 * <p/>
 * <p>If an exception occurs during processing, a {@link ConversionException}
 * should be thrown.</p>
 * <p/>
 * <p>If working with the high level {@link com.thoughtworks.xstream.XStream} facade,
 * you can register new converters using the XStream.registerConverter() method.</p>
 * <p/>
 * <p>If working with the lower level API, the
 * {@link com.thoughtworks.xstream.converters.ConverterLookup} implementation is
 * responsible for looking up the appropriate converter.</p>
 * <p/>
 * <p>{@link com.thoughtworks.xstream.converters.basic.AbstractBasicConverter}
 * provides a starting point for objects that can store all information
 * in a single String.</p>
 * <p/>
 * <p>{@link com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter}
 * provides a starting point for objects that hold a collection of other objects
 * (such as Lists and Maps).</p>
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.XStream
 * @see com.thoughtworks.xstream.converters.ConverterLookup
 * @see com.thoughtworks.xstream.converters.basic.AbstractBasicConverter
 * @see com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter
 */
public interface Converter {

    /**
     * Called by XStream to determine whether to use this converter
     * instance to marshall a particular type.
     */
    boolean canConvert(Class type);

    /**
     * Convert an object to textual data.
     *
     * @param source  The object to be marshalled.
     * @param writer  A stream to write to.
     * @param context A context that allows nested objects to be processed by XStream.
     */
    void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    /**
     * Convert textual data back into an object.
     *
     * @param reader  The stream to read the text from.
     * @param context
     * @return The resulting object.
     */
    Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

}
