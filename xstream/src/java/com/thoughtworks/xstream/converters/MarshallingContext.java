package com.thoughtworks.xstream.converters;

public interface MarshallingContext {

    Object currentObject();
    void convert(Object nextItem);

    void xmlWriteText(String text);
    void xmlStartElement(String fieldName);
    void xmlEndElement();
    void xmlAddAttribute(String name, String value);

}
