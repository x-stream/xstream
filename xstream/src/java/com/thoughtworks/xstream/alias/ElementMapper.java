package com.thoughtworks.xstream.alias;

public interface ElementMapper {
    String fromXml(String elementName);

    String toXml(String fieldName);
}
