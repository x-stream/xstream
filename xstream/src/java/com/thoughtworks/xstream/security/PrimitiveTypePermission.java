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

import com.thoughtworks.xstream.core.util.Primitives;


/**
 * Permission for any primitive type and its boxed counterpart (excl. void).
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class PrimitiveTypePermission implements TypePermission {
    /**
     * @since 1.4.7
     */
    public static final TypePermission PRIMITIVES = new PrimitiveTypePermission();

    @Override
    public boolean allows(Class<?> type) {
        return type != null && type != void.class && type != Void.class && type.isPrimitive()
            || Primitives.isBoxed(type);
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == PrimitiveTypePermission.class;
    }
}
