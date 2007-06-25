/*
 * Copyright (C) 2007 XStream Committers
 * Created on 25.06.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.reflection.targets;

import com.thoughtworks.xstream.benchmark.reflection.model.A100Parents;
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
public class HierarchyLevelReflection extends AbstractReflectionTarget {

    public HierarchyLevelReflection() {
        super(new ArrayList());
        List list = (List)target();
        for(int i = 0; i < 100; ++i) {
            String no = "00" + i;
            try {
                Class cls = Class.forName(A100Parents.class.getName() + "$Parent" + no.substring(no.length() - 3));
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

    public String toString() {
        return "HierarchyLevel Target";
    }

}
