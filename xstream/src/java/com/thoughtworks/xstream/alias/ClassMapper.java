package com.thoughtworks.xstream.alias;

public interface ClassMapper {
    String lookupName(Class type);

    Class lookupType(String elementName);

    Class lookupDefaultType(Class baseType);

    void alias(String elementName, Class type, Class defaultImplementation);
}
