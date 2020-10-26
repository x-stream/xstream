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

/**
 * Wrapper to negate another type permission.
 * <p>
 * If the wrapped {@link TypePermission} allows the type, this instance will throw a {@link ForbiddenClassException}
 * instead. An instance of this permission cannot be used to allow a type.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class NoPermission implements TypePermission {

    private final TypePermission permission;

    /**
     * Construct a NoPermission.
     * 
     * @param permission the permission to negate or <code>null</code> to forbid any type
     * @since 1.4.7
     */
    public NoPermission(final TypePermission permission) {
        this.permission = permission;
    }

    @Override
    public boolean allows(final Class<?> type) {
        if (permission == null || permission.allows(type)) {
            throw new ForbiddenClassException(type);
        }
        return false;
    }
}
