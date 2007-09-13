/*
 * Copyright (C) 2007 XStream Committers
 * Created on 06.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.reflection.targets;

import com.thoughtworks.xstream.benchmark.reflection.model.A50StaticInnerClasses;
import com.thoughtworks.xstream.tools.benchmark.Target;

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
