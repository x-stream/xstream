/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. June 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.model.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;
import com.thoughtworks.xstream.tools.benchmark.model.A100Parents;

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
