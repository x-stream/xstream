package com.thoughtworks.xstream.alias;

public interface ClassMapper {
    String lookupName(Class type);

    Class lookupType(String elementName);

    Class lookupDefaultType(Class baseType);
}
