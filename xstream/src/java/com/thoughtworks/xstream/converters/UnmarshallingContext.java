package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext {

    Object convertAnother(Class type);
    Object currentObject();
    Class getRequiredType();

}
