package com.thoughtworks.xstream.converters;

/**
 * Responsible for looking up the correct Converter implementation for a specific type.
 *
 * @author Joe Walnes
 * @see Converter
 */
public interface ConverterLookup {

    /**
     * Lookup a converter for a specific type.
     * <p/>
     * This type may be any Class, including primitive and array types. It may also be null, signifying
     * the value to be converted is a null type.
     */
    Converter lookupConverterForType(Class type);

    /**
     * Lookup the default converter. This should be a catch all capable of serializng any object using reflection.
     */
    Converter defaultConverter();

}
