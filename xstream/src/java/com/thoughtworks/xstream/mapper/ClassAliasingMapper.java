/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapper that allows a fully qualified class name to be replaced with a shorter alias.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ClassAliasingMapper extends MapperWrapper {

    protected final Map typeToName = new HashMap();
    protected final Map classToName = new HashMap();
    protected transient Map nameToType = new HashMap();

    public ClassAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated since 1.2, use {@link #ClassAliasingMapper(Mapper)}
     */
    public ClassAliasingMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public void addClassAlias(String name, Class type) {
        nameToType.put(name, type.getName());
        classToName.put(type.getName(), name);
    }

    /**
     * @deprecated since 1.3, method was a leftover of an old implementation
     */
    public void addClassAttributeAlias(String name, Class type) {
        addClassAlias(name, type);
    }

    public void addTypeAlias(String name, Class type) {
        nameToType.put(name, type.getName());
        typeToName.put(type, name);
    }

    public String serializedClass(Class type) {
        String alias = (String) classToName.get(type.getName());
        if (alias != null) {
            return alias;
        } else {
            for (final Iterator iter = typeToName.keySet().iterator(); iter.hasNext();) {
                final Class compatibleType = (Class)iter.next();
                if (compatibleType.isAssignableFrom(type)) {
                    return (String)typeToName.get(compatibleType);
                }
            }
            return super.serializedClass(type);
        }
    }

    public Class realClass(String elementName) {
        String mappedName = (String) nameToType.get(elementName);

        if (mappedName != null) {
            Class type = primitiveClassNamed(mappedName);
            if (type != null) {
                return type;
            }
            elementName = mappedName;
        }

        return super.realClass(elementName);
    }

    public boolean itemTypeAsAttribute(Class clazz) {
        return classToName.containsKey(clazz);
    }

    public boolean aliasIsAttribute(String name) {
        return nameToType.containsKey(name);
    }
    
    private Object readResolve() {
        nameToType = new HashMap();
        for (final Iterator iter = classToName.keySet().iterator(); iter.hasNext();) {
            final Object type = iter.next();
            nameToType.put(classToName.get(type), type);
        }
        for (final Iterator iter = typeToName.keySet().iterator(); iter.hasNext();) {
            final Class type = (Class)iter.next();
            nameToType.put(typeToName.get(type), type.getName());
        }
        return this;
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
}
