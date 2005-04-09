package com.thoughtworks.xstream.alias;

import com.thoughtworks.xstream.mapper.Mapper;

public interface ClassMapper extends Mapper {

    /**
     * Place holder type used for null values.
     */
    class Null {}

    /**
     * @deprecated As of 1.1.1, use {@link #serializedClass(Class)}
     */
    String lookupName(Class type);

    /**
     * @deprecated As of 1.1.1, use {@link #realClass(String)}
     */
    Class lookupType(String elementName);

    /**
     * @deprecated As of 1.1.1, use {@link #serializedMember(Class, String)}
     */
    String mapNameFromXML( String xmlName );

    /**
     * @deprecated As of 1.1.1, use {@link #realMember(Class, String)}
     */
    String mapNameToXML( String javaName );

    /**
     * @deprecated As of 1.1.1, use {@link #defaultImplementationOf(Class)}
     */
    Class lookupDefaultType(Class baseType);

    /**
     * @deprecated As of 1.1.1, use {@link com.thoughtworks.xstream.mapper.ClassAliasingMapper#addClassAlias(String, Class)} for creating an alias and
     *             {@link com.thoughtworks.xstream.mapper.DefaultImplementationsMapper#addDefaultImplementation(Class, Class)} for specifiny a
     *             default implementation.
     */
    void alias(String elementName, Class type, Class defaultImplementation);

}
