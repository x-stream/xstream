package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext {

    Object convertAnother(Class type);
    Object currentObject();
    Class getRequiredType();

    String xmlText();
    String xmlElementName();
    void xmlPop();
    boolean xmlNextChild();
    String xmlAttribute(String name);
    Object xmlPeek();



}
