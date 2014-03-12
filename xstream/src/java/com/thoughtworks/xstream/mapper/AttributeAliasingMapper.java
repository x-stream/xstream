/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

/**
 * Mapper that allows aliasing of attribute names.
 * 
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 * @since 1.2
 */
public class AttributeAliasingMapper extends AbstractAttributeAliasingMapper {

    public AttributeAliasingMapper(final Mapper wrapped) {
        super(wrapped);
    }

    @Override
    public String aliasForAttribute(final String attribute) {
        final String alias = nameToAlias.get(attribute);
        return alias == null ? super.aliasForAttribute(attribute) : alias;
    }

    @Override
    public String attributeForAlias(final String alias) {
        final String name = aliasToName.get(alias);
        return name == null ? super.attributeForAlias(alias) : name;
    }
}
