package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext {

    Object convertAnother(Object current, Class type);

    Object currentObject();

    Class getRequiredType();

}
