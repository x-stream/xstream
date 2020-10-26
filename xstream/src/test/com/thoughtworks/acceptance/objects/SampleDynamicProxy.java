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

package com.thoughtworks.acceptance.objects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class SampleDynamicProxy implements InvocationHandler {

    private final Object aField;
    private transient boolean recursion;

    private SampleDynamicProxy(final Object value) {
        aField = value;
    }

    public static interface InterfaceOne {
        Object doSomething();
    }

    public static interface InterfaceTwo {
        Object doSomething();
    }

    public static Object newInstance() {
        return newInstance("hello");
    }

    public static Object newInstance(final Object value) {
        return Proxy.newProxyInstance(InterfaceOne.class.getClassLoader(), new Class[]{
            InterfaceOne.class, InterfaceTwo.class}, new SampleDynamicProxy(value));
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.getName().equals("equals")) {
            return recursion || equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
        } else if (method.getName().equals("hashCode")) {
            return new Integer(System.identityHashCode(proxy));
        } else {
            return aField;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        try {
            recursion = true;
            return equalsInterfaceOne(obj) && equalsInterfaceTwo(obj);
        } finally {
            recursion = false;
        }
    }

    private boolean equalsInterfaceOne(final Object o) {
        if (o instanceof InterfaceOne) {
            final InterfaceOne interfaceOne = (InterfaceOne)o;
            return aField.equals(interfaceOne.doSomething());
        } else {
            return false;
        }
    }

    private boolean equalsInterfaceTwo(final Object o) {
        if (o instanceof InterfaceTwo) {
            final InterfaceTwo interfaceTwo = (InterfaceTwo)o;
            return aField.equals(interfaceTwo.doSomething());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return aField.hashCode();
    }
}
