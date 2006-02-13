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

    // Dynamically try to load Enum class. Pre JDK1.5 will silently fail.
    private static JVM jvm = new JVM();
    private static final Class enumClass = jvm.loadClass("java.lang.Enum");

    private static final boolean active = enumClass != null;

    private static final Class enumSetClass = active ? jvm.loadClass("java.util.EnumSet") : null;

    public EnumMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #EnumMapper(Mapper)}
     */
    public EnumMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public String serializedClass(Class type) {
        if (!active) {
            return super.serializedClass(type);
        } else {
            if (enumClass.isAssignableFrom(type) && type.getSuperclass() != enumClass) {
                return super.serializedClass(type.getSuperclass());
            } else if (enumSetClass.isAssignableFrom(type)) {
                return super.serializedClass(enumSetClass);
            } else {
                return super.serializedClass(type);
            }
        }
    }

}
