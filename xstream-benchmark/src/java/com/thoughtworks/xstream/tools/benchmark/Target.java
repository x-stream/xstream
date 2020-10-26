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

package com.thoughtworks.xstream.tools.benchmark;

/**
 * Provides a target object to use in the metric. This could be a very small object or a large
 * complicated graph.
 *
 * Also used to test if the object is equal to another instance (as some object's don't provide
 * sensible equals() methods.
 *  
 * @author Joe Walnes
 * @see Harness
 * @deprecated As of 1.4.9 use JMH instead
 */
public interface Target {

    /**
     * The target to use in the metric.
     */
    Object target();

    /**
     * Check whether the object for this target is equal to another one.
     */
    boolean isEqual(Object other);

}
