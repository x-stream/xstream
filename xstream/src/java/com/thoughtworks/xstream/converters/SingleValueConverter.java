package com.thoughtworks.xstream.converters;

/**
 * SingleValueConverter implementations are marshallable to/from a single value String representation.
 * <p/>
 * <p>{@link com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter}
 * provides a starting point for objects that can store all information in a single value String.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter
 * @since 1.2
 */
public interface SingleValueConverter extends ConverterMatcher {

    /**
     * Marshalls an Object into a single value representation.
     * @param obj the Object to be converted
     * @return a String with the single value of the Object or <code>null</code>
     */
    public String toString(Object obj);

    /**
     * Unmarshalls an Object from its single value representation.
     * @param str the String with the single value of the Object
     * @return the Object
     */
    public Object fromString(String str);

}