package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext extends DataHolder {

    Object convertAnother(Object current, Class type);

    Object currentObject();

    Class getRequiredType();

}
