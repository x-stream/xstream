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
        nameToTypeMap.put(elementName, type.getName());
        typeToNameMap.put(type, elementName);
        if (!type.equals(defaultImplementation)) {
            typeToNameMap.put(defaultImplementation, elementName);
        }
        baseTypeToDefaultTypeMap.put(type, defaultImplementation);
    }

    public String lookupName(Class type) {
        boolean isArray = type.isArray();
        if (type.isArray()) {
            type = type.getComponentType();
        }
        String result = (String) typeToNameMap.get(type);
        if (result == null) {
            // the $ used in inner class names is illegal as an xml element name
            result = type.getName().replaceAll("\\$", "-");
        }
        if (isArray) {
            result += "-array";
        }
        return result;
    }

    public Class lookupType(String elementName) {
        if (elementName.equals("null")) {
            return null;
        }
        boolean isArray = elementName.endsWith("-array");
        if (isArray) {
            elementName = elementName.substring(0, elementName.length() - 6); // cut off -array
        }
        String mappedName = (String) nameToTypeMap.get(elementName);
        if (mappedName != null) {
            elementName = mappedName;
        }
        // the $ used in inner class names is illegal as an xml element name
        elementName = elementName.replaceAll("\\-", "\\$");
        try {
            if (isArray) {
                return Class.forName("[L" + elementName + ";");
            } else {
                return Class.forName(elementName);
            }
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elementName);
        }
    }

    public Class lookupDefaultType(Class baseType) {
        return (Class) baseTypeToDefaultTypeMap.get(baseType);
    }

}
