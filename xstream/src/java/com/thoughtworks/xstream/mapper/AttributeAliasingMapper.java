/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
