package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Mapper that allows a fully qualified class name to be replaced with a shorter alias.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ClassAliasingMapper extends MapperWrapper {

    protected final Map typeToNameMap = new HashMap();
    protected transient Map nameToTypeMap = new HashMap();
    protected final Set knownAttributes = new HashSet();

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

    public void addClassAttributeAlias(String name, Class type) {
        addClassAlias(name, type);
        knownAttributes.add(name);
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

    public boolean itemTypeAsAttribute(Class clazz) {
        return typeToNameMap.containsKey(clazz);
    }

    public boolean aliasIsAttribute(String name) {
        return nameToTypeMap.containsKey(name);
    }
    
    private Object readResolve() {
        nameToTypeMap = new HashMap();
        for (final Iterator iter = typeToNameMap.keySet().iterator(); iter.hasNext();) {
            final Object type = iter.next();
            nameToTypeMap.put(typeToNameMap.get(type), type);
        }
        return this;
    }

}
