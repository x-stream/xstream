package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext {

    /** @deprecated Use convertAnother(Object current, Class type) instead. */
    Object convertAnother(Class type);

    Object convertAnother(Object current, Class type);
    Object currentObject();
    Class getRequiredType();

}
