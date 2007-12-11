/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Mapper that allows aliasing of attribute names.
 * 
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 * @since 1.2
 */
public class AttributeAliasingMapper extends MapperWrapper {

    private final Map aliasToName = new HashMap();
    private transient Map nameToAlias = new HashMap();

    public AttributeAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addAliasFor(final String attributeName, final String alias) {
        aliasToName.put(alias, attributeName);
        nameToAlias.put(attributeName, alias);
    }

    public String aliasForAttribute(String attribute) {
        String alias = (String)nameToAlias.get(attribute);
        return alias == null ? super.aliasForAttribute(attribute) : alias;
    }

    public String attributeForAlias(String alias) {
        String name = (String)aliasToName.get(alias);
        return name == null ? super.attributeForAlias(alias) : name;
    }

    private String getAliasForName(String name) {
        String alias = (String)nameToAlias.get(name);
        return alias == null ? name : alias;
    }

    private Object readResolve() {
        nameToAlias = new HashMap();
        for (final Iterator iter = aliasToName.keySet().iterator(); iter.hasNext();) {
            final Object alias = iter.next();
            nameToAlias.put(aliasToName.get(alias), alias);
        }
        return this;
    }
}
