package com.thoughtworks.xstream.converters;

/**
 * ConverterMatcher allows to match converters to classes by 
 * determining if a given type can be converted by the converter instance.
 * ConverterMatcher is the base interface of any converter. 
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 * @since 1.2
 */
public interface ConverterMatcher {

    /**
     * Determines whether the converter can marshall a particular type.
     * @param type the Class representing the object type to be converted
     */
    boolean canConvert(Class type);

}