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

package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.core.util.CompositeClassLoader;

/**
 * Reference to a ClassLoader, allowing a single instance to be passed around the codebase that
 * can later have its destination changed.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public final class ClassLoaderReference {

    private transient ClassLoader reference;

    public ClassLoaderReference(ClassLoader reference) {
        setReference(reference);
    }

    public ClassLoader getReference() {
        return reference;
    }

    public void setReference(ClassLoader reference) {
        this.reference = reference instanceof com.thoughtworks.xstream.core.util.ClassLoaderReference
                ? ((com.thoughtworks.xstream.core.util.ClassLoaderReference)reference)
                    .getReference() : reference;
    }

    private Object readResolve() {
        this.reference = new CompositeClassLoader();
        return this;
    }
}
