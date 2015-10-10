/*
 * Copyright (C) 2008, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 09. October 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import java.util.HashMap;
import java.util.Map;


/**
 * Abstract base class for AttributeAliassingMapper and its system version.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3.1
 */
public abstract class AbstractAttributeAliasingMapper extends MapperWrapper {

    protected final Map<String, String> aliasToName = new HashMap<>();
    protected transient Map<String, String> nameToAlias = new HashMap<>();

    public AbstractAttributeAliasingMapper(final Mapper wrapped) {
        super(wrapped);
    }

    public void addAliasFor(final String attributeName, final String alias) {
        aliasToName.put(alias, attributeName);
        nameToAlias.put(attributeName, alias);
    }

    Object readResolve() {
        nameToAlias = new HashMap<>();
        for (final Map.Entry<String, String> entry : aliasToName.entrySet()) {
            nameToAlias.put(entry.getValue(), entry.getKey());
        }
        return this;
    }

}
