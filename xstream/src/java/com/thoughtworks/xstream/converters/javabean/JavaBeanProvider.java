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

package com.thoughtworks.xstream.converters.javabean;

/**
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface JavaBeanProvider {

    Object newInstance(Class<?> type);

    void visitSerializableProperties(Object object, Visitor visitor);

    void writeProperty(Object object, String propertyName, Object value);

    Class<?> getPropertyType(Object object, String name);

    boolean propertyDefinedInClass(String name, Class<?> type);

    /**
     * Returns true if the Bean provider can instantiate the specified class
     */
    boolean canInstantiate(Class<?> type);

    public interface Visitor {
        boolean shouldVisit(String name, Class<?> definedIn);

        void visit(String name, Class<?> type, Class<?> definedIn, Object value);
    }

}
