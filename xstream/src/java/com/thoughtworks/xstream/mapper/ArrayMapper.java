package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Arrays;
import java.util.Collection;

/**
 * Mapper that detects arrays and changes the name so it can identified as an array
 * (for example Foo[] gets serialized as foo-array). Supports multi-dimensional arrays.
 *
 * @author Joe Walnes 
 */
public class ArrayMapper extends MapperWrapper {

    private final static Collection BOXED_TYPES = Arrays.asList(
            new Class[] {
                    Boolean.class,
                    Byte.class,
                    Character.class,
                    Short.class,
                    Integer.class,
                    Long.class,
                    Float.class,
                    Double.class
            });

    public ArrayMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #ArrayMapper(Mapper)}
     */
    public ArrayMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public String serializedClass(Class type) {
        StringBuffer arraySuffix = new StringBuffer();
        while (type.isArray()) {
            type = type.getComponentType();
            arraySuffix.append("-array");
        }
        String name = boxedTypeName(type);
        if (name == null) {
            name = super.serializedClass(type);
        }
        if (arraySuffix.length() > 0) {
            return name + arraySuffix;
        } else {
            return name;
        }
    }

    public Class realClass(String elementName) {
        int dimensions = 0;

        // strip off "-array" suffix
        while (elementName.endsWith("-array")) {
            elementName = elementName.substring(0, elementName.length() - 6); // cut off -array
            dimensions++;
        }

        if (dimensions > 0) {
            Class componentType = primitiveClassNamed(elementName);
            if (componentType == null) {
                componentType = super.realClass(elementName);
            }
            try {
                return arrayType(dimensions, componentType);
            } catch (ClassNotFoundException e) {
                throw new CannotResolveClassException(elementName + " : " + e.getMessage());
            }
        } else {
            return super.realClass(elementName);
        }
    }

    private Class arrayType(int dimensions, Class componentType) throws ClassNotFoundException {
        StringBuffer className = new StringBuffer();
        for (int i = 0; i < dimensions; i++) {
            className.append('[');
        }
        if (componentType.isPrimitive()) {
            className.append(charThatJavaUsesToRepresentPrimitiveArrayType(componentType));
            return Class.forName(className.toString());
        } else {
            className.append('L').append(componentType.getName()).append(';');
            return Class.forName(className.toString(), false, componentType.getClassLoader());
        }
    }

    private Class primitiveClassNamed(String name) {
        return
                name.equals("void") ? Void.TYPE :
                name.equals("boolean") ? Boolean.TYPE :
                name.equals("byte") ? Byte.TYPE :
                name.equals("char") ? Character.TYPE :
                name.equals("short") ? Short.TYPE :
                name.equals("int") ? Integer.TYPE :
                name.equals("long") ? Long.TYPE :
                name.equals("float") ? Float.TYPE :
                name.equals("double") ? Double.TYPE :
                null;
    }

    private char charThatJavaUsesToRepresentPrimitiveArrayType(Class primvCls) {
        return
                (primvCls == boolean.class) ? 'Z' :
                (primvCls == byte.class) ? 'B' :
                (primvCls == char.class) ? 'C' :
                (primvCls == short.class) ? 'S' :
                (primvCls == int.class) ? 'I' :
                (primvCls == long.class) ? 'J' :
                (primvCls == float.class) ? 'F' :
                (primvCls == double.class) ? 'D' :
                0;
    }
    
    private String boxedTypeName(Class type) {
        return BOXED_TYPES.contains(type) ? type.getName() : null;
    }
}
