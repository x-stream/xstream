/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 09. October 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;


/**
 * Mapper that allows aliasing of system attribute names.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class SystemAttributeAliasingMapper extends AbstractAttributeAliasingMapper {

    public SystemAttributeAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public String aliasForSystemAttribute(String attribute) {
        String alias = (String)nameToAlias.get(attribute);
        if (alias == null) {
            alias = super.aliasForSystemAttribute(attribute);
            if (alias == attribute) {
                alias = super.aliasForAttribute(attribute);
            }
        }
        return alias;
    }

    public String systemAttributeForAlias(String alias) {
        String name = (String)aliasToName.get(alias);
        if (name == null) {
            name = super.systemAttributeForAlias(alias);
            if (name == alias) {
                name = super.attributeForAlias(alias);
            }
        }
        return name;
    }
}
