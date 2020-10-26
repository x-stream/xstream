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

/**
 * Instantiates a new object on the Sun JVM by bypassing the constructor (meaning code in the constructor will never be
 * executed and parameters do not have to be known). This is the same method used by the internals of standard Java
 * serialization, but relies on internal Sun code that may not be present on all JVMs.
 * 
 * @author Joe Walnes
 * @author Brian Slesinsky
 * @deprecated As of 1.4.7 use {@link SunUnsafeReflectionProvider}
 */
@Deprecated
public class Sun14ReflectionProvider extends SunUnsafeReflectionProvider {
    /**
     * @deprecated As of 1.4.7 use {@link SunUnsafeReflectionProvider#SunUnsafeReflectionProvider()}
     */
    @Deprecated
    public Sun14ReflectionProvider() {
        super();
    }

    /**
     * @deprecated As of 1.4.7 use {@link SunUnsafeReflectionProvider#SunUnsafeReflectionProvider(FieldDictionary)}
     */
    @Deprecated
    public Sun14ReflectionProvider(final FieldDictionary dic) {
        super(dic);
    }

    private Object readResolve() {
        init();
        return this;
    }
}
