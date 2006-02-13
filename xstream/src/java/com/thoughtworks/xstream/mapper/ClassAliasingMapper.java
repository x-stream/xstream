package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper that allows a fully qualified class name to be replaced with a shorter alias.
 *
 * @author Joe Walnes
 */
public class ClassAliasingMapper extends MapperWrapper {

    protected final Map typeToNameMap = Collections.synchronizedMap(new HashMap());
    protected final Map nameToTypeMap = Collections.synchronizedMap(new HashMap());

    public ClassAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #ClassAliasingMapper(Mapper)}
     */
    public ClassAliasingMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public void addClassAlias(String name, Class type) {
        nameToTypeMap.put(name, type.getName());
        typeToNameMap.put(type.getName(), name);
    }

    public String serializedClass(Class type) {
        String alias = (String) typeToNameMap.get(type.getName());
        if (alias != null) {
            return alias;
        } else {
            return super.serializedClass(type);
        }
    }

    public Class realClass(String elementName) {
        String mappedName = (String) nameToTypeMap.get(elementName);

        if (mappedName != null) {
            elementName = mappedName;
        }

        return super.realClass(elementName);
    }

}
