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

package com.thoughtworks.xstream.core.util;

/**
 * ClassLoader that refers to another ClassLoader, allowing a single instance to be passed around the codebase that can
 * later have its destination changed.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.1.1
 * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.ClassLoaderReference} instead
 */
@Deprecated
public class ClassLoaderReference extends ClassLoader {

    private transient ClassLoader reference;

    /**
     * @deprecated As of 1.4.5 use
     *             {@link com.thoughtworks.xstream.core.ClassLoaderReference#ClassLoaderReference(ClassLoader)} instead
     */
    @Deprecated
    public ClassLoaderReference(final ClassLoader reference) {
        this.reference = reference;
    }

    /**
     * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.ClassLoaderReference#getReference()}
     *             .loadClass(String) instead
     */
    @Deprecated
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return reference.loadClass(name);
    }

    /**
     * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.ClassLoaderReference#getReference()} instead
     */
    @Deprecated
    public ClassLoader getReference() {
        return reference;
    }

    /**
     * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.ClassLoaderReference#setReference(ClassLoader)}
     *             instead
     */
    @Deprecated
    public void setReference(final ClassLoader reference) {
        this.reference = reference;
    }

    private Object writeReplace() {
        return new Replacement();
    }

    static class Replacement {

        private Object readResolve() {
            return new ClassLoaderReference(new CompositeClassLoader());
        }

    };
}
