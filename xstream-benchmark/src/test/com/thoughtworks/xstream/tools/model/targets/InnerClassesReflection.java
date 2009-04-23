/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.model.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;
import com.thoughtworks.xstream.tools.benchmark.model.A50InnerClasses;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * A Target for multiple hierarchy level classes.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class InnerClassesReflection extends AbstractReflectionTarget {

    public InnerClassesReflection() {
        super(new ArrayList());
        for (int i = 0; i < 10; ++i) {
            List list = new ArrayList();
            list.add(new A50InnerClasses());
            StringBuffer name = new StringBuffer(A50InnerClasses.class.getName());
            for (int j = 0; j < 50; ++j) {
                String no = "0" + j;
                Object parent = list.get(j);
                name.append("$L");
                name.append(no.substring(no.length() - 2));
                try {
                    Class cls = Class.forName(name.toString());
                    Constructor ctor = cls
                        .getDeclaredConstructor(new Class[]{parent.getClass()});
                    Object o = ctor.newInstance(new Object[]{parent});
                    fill(o);
                    list.add(o);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            list.remove(0);
            ((List)target()).addAll(list);
        }
    }

    public String toString() {
        return "InnerClasses Target";
    }

}
