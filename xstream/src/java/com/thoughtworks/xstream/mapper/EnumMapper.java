package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.core.JVM;

/**
 * Mapper that handles the special case of polymorphic enums in Java 1.5. This renames MyEnum$1 to MyEnum making it
 * less bloaty in the XML and avoiding the need for an alias per enum value to be specified.
 *
 * @author Joe Walnes
 */
public class EnumMapper extends MapperWrapper {

    private static final Class enumClass = new JVM().loadClass("java.lang.Enum"); // dynamically try to load Enum class.
    // If using pre an version of Java before 1.5, this will return null, causing this Mapper to have no behavior.

    public EnumMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public String serializedClass(Class type) {
        if (enumClass != null && enumClass.isAssignableFrom(type) && type.getSuperclass() != enumClass) {
            type = type.getSuperclass();
        }
        return super.serializedClass(type);
    }

}
