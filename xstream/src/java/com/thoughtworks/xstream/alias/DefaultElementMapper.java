package com.thoughtworks.xstream.alias;

public class DefaultElementMapper
    implements ElementMapper {
    public String toXml(String elementName) {
        return elementName;
    }

    public String fromXml(String fieldName) {
        return fieldName;
    }
}
