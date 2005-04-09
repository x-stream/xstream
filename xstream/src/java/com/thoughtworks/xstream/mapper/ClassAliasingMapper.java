package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Mapper that allows a fully qualified class name to be replaced with a shorter alias.
 *
 * @author Joe Walnes
 */
public class ClassAliasingMapper extends MapperWrapper {

    protected final Map typeToNameMap = Collections.synchronizedMap(new HashMap());
    protected final Map nameToTypeMap = Collections.synchronizedMap(new HashMap());

    public ClassAliasingMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public void addClassAlias(String name, Class type) {
        nameToTypeMap.put(name, type.getName());
        typeToNameMap.put(type.getName(), name);
    }

    public String serializedClass(Class type) {
        String name = super.serializedClass(type);
        String alias = (String) typeToNameMap.get(type.getName());
        if (alias != null) {
            return alias;
        } else {
            return name;
        }
    }

    public Class realClass(String elementName) {
        if (elementName.equals("null")) { // TODO: This is probably the wrong place for this.
            return null;
        }

        String mappedName = (String) nameToTypeMap.get(mapNameFromXML(elementName));

        if (mappedName != null) {
            elementName = mappedName;
        }

        return super.realClass(elementName);
    }

}
