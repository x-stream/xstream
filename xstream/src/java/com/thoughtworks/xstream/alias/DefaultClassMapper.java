package com.thoughtworks.xstream.alias;

import java.util.HashMap;
import java.util.Map;

public class DefaultClassMapper implements ClassMapper {

    private Map typeToNameMap = new HashMap();
    private Map nameToTypeMap = new HashMap();
    private Map baseTypeToDefaultTypeMap = new HashMap();

    public DefaultClassMapper() {
        // register primitive types
        baseTypeToDefaultTypeMap.put(boolean.class, Boolean.class);
        baseTypeToDefaultTypeMap.put(char.class, Character.class);
        baseTypeToDefaultTypeMap.put(int.class, Integer.class);
        baseTypeToDefaultTypeMap.put(float.class, Float.class);
        baseTypeToDefaultTypeMap.put(double.class, Double.class);
        baseTypeToDefaultTypeMap.put(short.class, Short.class);
        baseTypeToDefaultTypeMap.put(byte.class, Byte.class);
        baseTypeToDefaultTypeMap.put(long.class, Long.class);
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        nameToTypeMap.put(elementName, type);
        typeToNameMap.put(type, elementName);
        if (!type.equals(defaultImplementation)) {
            typeToNameMap.put(defaultImplementation, elementName);
        }
        baseTypeToDefaultTypeMap.put(type, defaultImplementation);
    }

    public String lookupName(Class type) {
        String result = (String) typeToNameMap.get(type);
        if (result == null) {
            // the $ used in inner class names is illegal as an xml element name
            result = type.getName().replaceAll("\\$", "-");
        }
        return result;
    }

    public Class lookupType(String elementName) {
        Class result = (Class) nameToTypeMap.get(elementName);
        if (result == null) {
            // the $ used in inner class names is illegal as an xml element name
            elementName = elementName.replaceAll("\\-", "\\$");
            try {
                result = Class.forName(elementName);
            } catch (ClassNotFoundException e) {
                throw new CannotResolveClassException(elementName);
            }
        }
        return result;
    }

    public Class lookupDefaultType(Class baseType) {
        return (Class) baseTypeToDefaultTypeMap.get(baseType);
    }

}
