package com.thoughtworks.xstream.converters;

public interface Converter {

    boolean canConvert(Class type);

    void toXML(MarshallingContext context);

    Object fromXML(UnmarshallingContext context);
}
