package com.thoughtworks.xstream.alias;

public interface NameMapper {
    String fromXML(String elementName);

    String toXML(String fieldName);
}
