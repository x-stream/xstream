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

package com.thoughtworks.xstream.testutil;

import java.io.FilePermission;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author J&ouml;rg Schaible
 */
public class DynamicSecurityManager extends SecurityManager {

    private final Map<CodeSource, PermissionCollection> permissions = new HashMap<>();
    private AccessControlContext acc = null;
    private final List<Permission> failedPermissions = new ArrayList<>();

    public void addPermission(final CodeSource codeSource, final Permission permission) {
        PermissionCollection permissionCollection = permissions.get(codeSource);
        if (permissionCollection == null) {
            permissionCollection = new Permissions();
            permissions.put(codeSource, permissionCollection);
        }
        permissionCollection.add(permission);
// updateACC();
    }

    public void setPermissions(final CodeSource codeSource, final PermissionCollection permissionCollection) {
        if (permissionCollection == null) {
            if (permissions.remove(codeSource) != null) {
// updateACC();
            }
        } else {
            if (permissions.put(codeSource, permissionCollection) != null) {
// updateACC();
            }
        }
    }

    private void updateACC() {
        if (permissions.isEmpty()) {
            acc = null;
        } else {
            final ProtectionDomain[] domains = new ProtectionDomain[permissions.size()];
            int i = 0;
            for (final CodeSource codeSource : permissions.keySet()) {
                final PermissionCollection permissionCollection = permissions.get(codeSource);
                domains[i++] = new ProtectionDomain(codeSource, permissionCollection);
            }
            acc = new AccessControlContext(domains);
        }
    }

    public void setReadOnly() {
        updateACC();
    }

    @Override
    public void checkPermission(final Permission perm) {
        if (acc != null) {
            // Ughhh. Eclipse class path leak :-/
            if (perm instanceof FilePermission && "read".equals(perm.getActions())) {
                final String name = perm.getName();
                if (name.indexOf("org.eclipse.osgi") > 0
                    && (name.endsWith("javax.xml.parsers.DocumentBuilderFactory")
                        || name.endsWith("javax.xml.datatype.DatatypeFactory"))) {
                    return;
                }
            }
            try {
                checkPermission(perm, acc);
            } catch (final SecurityException e) {
                failedPermissions.add(perm);
                throw e;
            }
        }
    }

    public List<Permission> getFailedPermissions() {
        return Collections.unmodifiableList(failedPermissions);
    }
}
