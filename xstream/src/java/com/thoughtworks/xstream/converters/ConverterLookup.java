package com.thoughtworks.xstream.converters;

public interface ConverterLookup {
    Converter lookup(Class type);
}
