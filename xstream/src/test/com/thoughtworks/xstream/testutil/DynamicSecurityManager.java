/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.testutil;

import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author J&ouml;rg Schaible
 */
public class DynamicSecurityManager extends SecurityManager {

    private Map permissions = new HashMap();
    private AccessControlContext acc = null;
    private List failedPermissions = new ArrayList();

    public void addPermission(final CodeSource codeSource, final Permission permission) {
        PermissionCollection permissionCollection = (PermissionCollection)permissions
                .get(codeSource);
        if (permissionCollection == null) {
            permissionCollection = new Permissions();
            permissions.put(codeSource, permissionCollection);
        }
        permissionCollection.add(permission);
//        updateACC();
    }

    public void setPermissions(
            final CodeSource codeSource, final PermissionCollection permissionCollection) {
        if (permissionCollection == null) {
            if (permissions.remove(codeSource) != null) {
//                updateACC();
            }
        } else {
            if (permissions.put(codeSource, permissionCollection) != null) {
//                updateACC();
            }
        }
    }

    private void updateACC() {
        if (permissions.size() == 0) {
            acc = null;
        } else {
            final ProtectionDomain[] domains = new ProtectionDomain[permissions.size()];
            int i = 0;
            for (final Iterator iter = permissions.keySet().iterator(); iter.hasNext();) {
                final CodeSource codeSource = (CodeSource)iter.next();
                final PermissionCollection permissionCollection = (PermissionCollection)permissions
                        .get(codeSource);
                domains[i++] = new ProtectionDomain(codeSource, permissionCollection);
            }
            acc = new AccessControlContext(domains);
        }
    }
    
    public void setReadOnly() {
        updateACC();
    }

    public void checkPermission(Permission perm) {
        if (acc != null) {
            try {
                checkPermission(perm, acc);
            } catch (final SecurityException e) {
                failedPermissions.add(perm);
                throw e;
            }
        }
    }

    public List getFailedPermissions() {
        return Collections.unmodifiableList(failedPermissions);
    }
}
