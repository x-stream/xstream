package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.NameMapper;

public class DefaultNameMapper
    implements NameMapper {
    public String toXML(String elementName) {
        return elementName;
    }

    public String fromXML(String fieldName) {
        return fieldName;
    }
}
