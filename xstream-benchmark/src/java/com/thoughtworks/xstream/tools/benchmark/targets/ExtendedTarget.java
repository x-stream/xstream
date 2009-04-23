/*
 * Copyright (C) 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

import java.awt.Color;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Target containing extended types.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class ExtendedTarget implements Target {

    private final static Method EQUALS;
    static {
        Method method;
        try {
            method = Object.class.getMethod("equals", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        } 
        EQUALS = method;
    }
    
    private List list;
    
    public ExtendedTarget() {
        list = new ArrayList();
        list.add(new Color(128, 0, 255));
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Runnable.class}, new RunnableInvocationHandler());
        list.add(proxy);
        list.add(ExtendedTarget.class);
        list.add(EQUALS);
    }
    
    public boolean isEqual(Object other) {
        return list.equals(other);
    }

    public Object target() {
        return list;
    }

    public String toString() {
        return "Extended types";
    }
    
    static class RunnableInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(EQUALS)) {
                return new Boolean(args[0] instanceof Runnable);
            } else if (method.getName().equals("hashCode")) {
                return new Integer(System.identityHashCode(proxy));
            } else if (method.getName().equals("toString")) {
                return "Proxy" + System.identityHashCode(proxy);
            } else if (method.getName().equals("getClass")) {
                return proxy.getClass();
            }
            return null;
        }
        
    }
}
