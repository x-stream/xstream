package com.thoughtworks.xstream.converters;

public interface ConverterLookup {

    Converter lookupConverterForType(Class type);

}
