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
     * @deprecated As of 1.1.1 you can register Converters with priorities, making the need for a default converter redundant.
     */
    Converter defaultConverter();

}
