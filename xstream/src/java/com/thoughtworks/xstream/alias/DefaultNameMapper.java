package com.thoughtworks.xstream.alias;

public class DefaultNameMapper
    implements NameMapper {
    public String toXML(String elementName) {
        return elementName;
    }

    public String fromXML(String fieldName) {
        return fieldName;
    }
}
