package com.thoughtworks.xstream.converters;

public interface ConverterLookup {
    void registerConverter(Converter converter);

    Converter lookupConverterForType(Class type);
}
