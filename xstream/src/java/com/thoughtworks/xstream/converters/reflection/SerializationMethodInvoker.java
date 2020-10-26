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

package com.thoughtworks.xstream.converters.reflection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.SerializationMembers;


/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @deprecated As of 1.4.8, moved into internal util package.
 */
@Deprecated
public class SerializationMethodInvoker implements Caching {

    SerializationMembers serializationMembers = new SerializationMembers();

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     * 
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public Object callReadResolve(final Object result) {
        return serializationMembers.callReadResolve(result);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public Object callWriteReplace(final Object object) {
        return serializationMembers.callWriteReplace(object);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public boolean supportsReadObject(final Class<?> type, final boolean includeBaseClasses) {
        return serializationMembers.supportsReadObject(type, includeBaseClasses);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public void callReadObject(final Class<?> type, final Object object, final ObjectInputStream stream) {
        serializationMembers.callReadObject(type, object, stream);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public boolean supportsWriteObject(final Class<?> type, final boolean includeBaseClasses) {
        return serializationMembers.supportsWriteObject(type, includeBaseClasses);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public void callWriteObject(final Class<?> type, final Object instance, final ObjectOutputStream stream) {
        serializationMembers.callWriteObject(type, instance, stream);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Override
    @Deprecated
    public void flushCache() {
        serializationMembers.flushCache();
    }
}
