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
 * Definition of a type permission. 
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.7
 */
public interface TypePermission {
    /**
     * Check permission for a provided type.
     * 
     * @param type the type to check
     * @return <code>true</code> if provided type is allowed, <code>false</code> if permission does not handle the type
     * @throws ForbiddenClassException if provided type is explicitly forbidden
     * @since 1.4.7
     */
    boolean allows(Class<?> type);
}
