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

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.Path;


/**
 * A {@link MarshallingContext} that manages references.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface ReferencingMarshallingContext<K> extends MarshallingContext {

    /**
     * Retrieve the current path.
     * 
     * @return the current path
     * @since 1.4
     * @deprecated As of 1.4.2
     */
    @Deprecated
    Path currentPath();

    /**
     * Request the reference key for the given item
     * 
     * @param item the item to lookup
     * @return the reference key or <code>null</code>
     * @since 1.4
     */
    K lookupReference(Object item);

    /**
     * Replace the currently marshalled item.
     * <p>
     * <strong>Use this method only, if you know exactly what you do!</strong> It is a special solution for Serializable
     * types that make usage of the writeReplace method where the replacing object itself is referenced.
     * </p>
     * 
     * @param original the original item to convert
     * @param replacement the replacement item that is converted instead
     * @since 1.4
     */
    void replace(Object original, Object replacement);

    /**
     * Register an implicit element. This is typically some kind of collection. Note, that this object may not be
     * referenced anywhere else in the object stream.
     * 
     * @param item the object that is implicit
     * @since 1.4
     */
    void registerImplicit(Object item);
}
