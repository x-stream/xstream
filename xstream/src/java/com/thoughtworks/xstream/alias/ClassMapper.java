package com.thoughtworks.xstream.alias;

public interface ClassMapper {
    String lookupName(Class type);

    Class lookupType(String elementName);

    Class lookupDefaultType(Class baseType);

    void alias(String elementName, Class type, Class defaultImplementation);

    String mapNameFromXML( String xmlName );

    String mapNameToXML( String javaName );

    /**
     * Whether this type is a simple immutable value (int, boolean, String, URL, etc.
     */ 
    boolean isImmutableValueType(Class type);

    /**
     * Place holder type used for null values.
     */
    class Null {}

    /**
     * Place holder type used for dynamic proxies.
     */
    class DynamicProxy {}
}
