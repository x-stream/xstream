package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.CannotResolveClassException;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.NameMapper;

import java.util.HashMap;
import java.util.Map;

public class DefaultClassMapper implements ClassMapper {

    protected Map typeToNameMap = new HashMap();
    protected Map nameToTypeMap = new HashMap();
    protected Map baseTypeToDefaultTypeMap = new HashMap();
    private Map lookupTypeCache = new HashMap();

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

    public String mapNameToXML( String javaName ) {
        return javaName;
    }

    public String mapNameFromXML( String xmlName ) {
        return xmlName;
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
            // the $ used in inner class names is illegal as an xml element getNodeName
            result = type.getName().replace('$', '-');
        }
        if (isArray) {
            result += "-array";
        }
        return result;
    }

    /** Lookup table for primitive types. */
    private static Class primitiveClassNamed(String name) {
        return
            name.equals("void")    ?      Void.TYPE :
            name.equals("boolean") ?   Boolean.TYPE :
            name.equals("byte")    ?      Byte.TYPE :
            name.equals("char")    ? Character.TYPE :
            name.equals("short")   ?     Short.TYPE :
            name.equals("int")     ?   Integer.TYPE :
            name.equals("long")    ?      Long.TYPE :
            name.equals("float")   ?     Float.TYPE :
            name.equals("double")  ?    Double.TYPE :
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

        boolean isArray = elementName.endsWith("-array");
        
        Class primvCls = null;
        if (isArray) {
            elementName = elementName.substring(0, elementName.length() - 6); // cut off -array

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

        Class result;

        try {
            if (isArray) {
                
                // if a primitive array type exists, return its array   
                if (primvCls != null) {
                    result =
                        (primvCls == boolean.class)  ?   boolean[].class :
                        (primvCls == byte.class)     ?      byte[].class :
                        (primvCls == char.class)     ?      char[].class :
                        (primvCls == short.class)    ?     short[].class :
                        (primvCls == int.class)      ?       int[].class :
                        (primvCls == long.class)     ?      long[].class :
                        (primvCls == float.class)    ?     float[].class :
                        (primvCls == double.class)   ?    double[].class :
                        null;
                        
                // otherwise look it up like normal        
                } else {
                     result = Class.forName("[L" + elementName + ";");
                } 
            } else {
                result = Class.forName(elementName);
            }
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elementName);
        }
        lookupTypeCache.put(key, result);
        return result;
    }

    public Class lookupDefaultType(Class baseType) {
        return (Class) baseTypeToDefaultTypeMap.get(baseType);
    }

}
