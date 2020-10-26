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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.ForbiddenClassException;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.TypePermission;


/**
 * A Mapper implementation injecting a security layer based on permission rules for any type required in the
 * unmarshalling process.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public class SecurityMapper extends MapperWrapper {

    private final List<TypePermission> permissions;

    /**
     * Construct a SecurityMapper.
     * 
     * @param wrapped the mapper chain
     * @since 1.4.7
     */
    public SecurityMapper(final Mapper wrapped) {
        this(wrapped, (TypePermission[])null);
    }

    /**
     * Construct a SecurityMapper.
     * 
     * @param wrapped the mapper chain
     * @param permissions the predefined permissions
     * @since 1.4.7
     */
    public SecurityMapper(final Mapper wrapped, final TypePermission... permissions) {
        super(wrapped);
        this.permissions = permissions == null //
            ? new ArrayList<TypePermission>()
            : new ArrayList<TypePermission>(Arrays.asList(permissions));
    }

    /**
     * Add a new permission.
     * <p>
     * Permissions are evaluated in the added sequence. An instance of {@link NoTypePermission} or
     * {@link AnyTypePermission} will implicitly wipe any existing permission.
     * </p>
     * 
     * @param permission the permission to add.
     * @since 1.4.7
     */
    public void addPermission(final TypePermission permission) {
        if (permission.equals(NoTypePermission.NONE) || permission.equals(AnyTypePermission.ANY)) {
            permissions.clear();
        }
        permissions.add(0, permission);
    }

    @Override
    public Class<?> realClass(final String elementName) {
        final Class<?> type = super.realClass(elementName);
        for (final TypePermission permission : permissions) {
            if (permission.allows(type)) {
                return type;
            }
        }
        throw new ForbiddenClassException(type);
    }
}
