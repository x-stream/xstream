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
