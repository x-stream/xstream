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
import com.thoughtworks.xstream.tools.benchmark.model.A50StaticInnerClasses;

import java.util.ArrayList;
import java.util.List;


/**
 * A Target for multiple hierarchy level classes.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class StaticInnerClassesReflection extends AbstractReflectionTarget {

    public StaticInnerClassesReflection() {
        super(new ArrayList());
        List list = (List)target();
        for (int i = 0; i < 10; ++i) {
            StringBuffer name = new StringBuffer(A50StaticInnerClasses.class.getName());
            for (int j = 0; j < 50; ++j) {
                String no = "0" + j;
                try {
                    name.append("$L");
                    name.append(no.substring(no.length() - 2));
                    Class cls = Class.forName(name.toString());
                    Object o = cls.newInstance();
                    fill(o);
                    list.add(o);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String toString() {
        return "StaticInnerClasses Target";
    }

}
