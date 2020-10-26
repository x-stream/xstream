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

package com.thoughtworks.xstream.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Explicit permission for a type with a name matching one in the provided list.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class ExplicitTypePermission implements TypePermission {

    final Set<String> names;

    /**
     * @since 1.4.7
     */
    public ExplicitTypePermission(final Class<?>... types) {
        this(new Object() {
            public String[] getNames() {
                if (types == null)
                    return null;
                String[] names = new String[types.length];
                for (int i = 0; i < types.length; ++i)
                    names[i] = types[i].getName();
                return names;
            }
        }.getNames());
    }

    /**
     * @since 1.4.7
     */
    public ExplicitTypePermission(String... names) {
        this.names = names == null ? Collections.<String>emptySet() : new HashSet<>(Arrays.asList(names));
    }

    @Override
    public boolean allows(Class<?> type) {
        if (type == null)
            return false;
        return names.contains(type.getName());
    }

}
