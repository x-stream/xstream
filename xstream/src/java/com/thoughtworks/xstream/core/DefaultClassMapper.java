package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.CannotResolveClassException;
import com.thoughtworks.xstream.alias.ClassMapper;

import java.io.File;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;

public class DefaultClassMapper implements ClassMapper {

    protected Map typeToNameMap = new HashMap();
    protected Map nameToTypeMap = new HashMap();
    protected Map baseTypeToDefaultTypeMap = new HashMap();
    private Map lookupTypeCache = Collections.synchronizedMap(new HashMap());
    private HashSet immutableTypes = new HashSet();

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

        // register immutable primitives
        immutableTypes.add(boolean.class);
        immutableTypes.add(Boolean.class);
        immutableTypes.add(byte.class);
        immutableTypes.add(Byte.class);
        immutableTypes.add(char.class);
        immutableTypes.add(Character.class);
        immutableTypes.add(double.class);
        immutableTypes.add(Double.class);
        immutableTypes.add(float.class);
        immutableTypes.add(Float.class);
        immutableTypes.add(int.class);
        immutableTypes.add(Integer.class);
        immutableTypes.add(long.class);
        immutableTypes.add(Long.class);
        immutableTypes.add(short.class);
        immutableTypes.add(Short.class);

        // register other immutable types
        immutableTypes.add(ClassMapper.Null.class);
        immutableTypes.add(BigDecimal.class);
        immutableTypes.add(BigInteger.class);
        immutableTypes.add(String.class);
        immutableTypes.add(URL.class);
        immutableTypes.add(File.class);
        immutableTypes.add(Class.class);
    }

    public String mapNameToXML(String javaName) {
        StringBuffer result = new StringBuffer();
        int length = javaName.length();
        for(int i = 0; i < length; i++) {
            char c = javaName.charAt(i);
            if (c == '$') {
                result.append("_DOLLAR_");
            } else if (c == '_') {
                result.append("__");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String mapNameFromXML(String xmlName) {
        StringBuffer result = new StringBuffer();
        int length = xmlName.length();
        for(int i = 0; i < length; i++) {
            char c = xmlName.charAt(i);
            if (c == '_') {
                if (xmlName.charAt(i + 1)  == '_') {
                    i++;
                    result.append('_');
                } else if (xmlName.length() >= i + 8 && xmlName.substring(i + 1, i + 8).equals("DOLLAR_")) {
                    i += 7;
                    result.append('$');
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
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
        StringBuffer arraySuffix = new StringBuffer();
        while (type.isArray()) {
            type = type.getComponentType();
            arraySuffix.append("-array");
        }
        String result = (String) typeToNameMap.get(type);
        if (result == null && Proxy.isProxyClass(type)) {
            result = (String) typeToNameMap.get(DynamicProxy.class);
        }
        if (result == null) {
            // the $ used in inner class names is illegal as an xml element getNodeName
            result = type.getName().replace('$', '-');
            if (result.charAt(0) == '-') {
                // special case for classes named $Blah with no package; <-Blah> is illegal XML
                result = "default" + result;
            }
        }
        if (arraySuffix.length() > 0) {
            result += arraySuffix.toString();
        }
        return result;
    }

    /**
     * Lookup table for primitive types.
     */
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

    public Class lookupType(String elementName) {
        final String key = elementName;
        if (elementName.equals("null")) {
            return null;
        }
        if (lookupTypeCache.containsKey(key)) {
            return (Class) lookupTypeCache.get(key);
        }

        int arrayDepth = 0;
        while (elementName.endsWith("-array")) {
            elementName = elementName.substring(0, elementName.length() - 6); // cut off -array
            arrayDepth++;
        }

        Class primvCls = null;
        if (arrayDepth > 0) {
            // try to determine if the array type is a primitive
            primvCls = primitiveClassNamed(elementName);
        }

        String mappedName = null;

        // only look for a mappedName if no primitive array type has been found
        if (primvCls == null) {
            mappedName = (String) nameToTypeMap.get(mapNameFromXML(elementName));
        }

        if (mappedName != null) {
            elementName = mappedName;
        }

        
        // the $ used in inner class names is illegal as an xml element getNodeName
        elementName = elementName.replace('-', '$');
        if (elementName.startsWith("default$")) {
            // special case for classes named $Blah with no package; <-Blah> is illegal XML
            elementName = elementName.substring(7);
        }

        Class result;

        try {
            if (arrayDepth > 0) {
                // if a primitive array type exists, return its array
                if (primvCls != null) {
                    StringBuffer className = new StringBuffer();
                    for (int i = 0; i < arrayDepth; i++) {
                        className.append('[');
                    }
                    className.append(charThatJavaUsesToRepresentPrimitiveType(primvCls));
                    result = loadClass(className.toString());
                    // otherwise look it up like normal
                } else {
                    StringBuffer className = new StringBuffer();
                    for (int i = 0; i < arrayDepth; i++) {
                        className.append('[');
                    }
                    className.append('L').append(elementName).append(';');
                    result = loadClass(className.toString());
                }
            } else {
                result = loadClass(elementName);
            }
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elementName + " : " + e.getMessage());
        }
        lookupTypeCache.put(key, result);
        return result;
    }

    private Class loadClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Servlet engine friendly.
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
    }

    private char charThatJavaUsesToRepresentPrimitiveType(Class primvCls) {
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

    public Class lookupDefaultType(Class baseType) {
        Class result = (Class) baseTypeToDefaultTypeMap.get(baseType);
        return result == null ? baseType : result;
    }

    public boolean isImmutableValueType(Class type) {
        return immutableTypes.contains(type);
    }

    public void addImmutableType(Class type) {
        immutableTypes.add(type);
    }

}
