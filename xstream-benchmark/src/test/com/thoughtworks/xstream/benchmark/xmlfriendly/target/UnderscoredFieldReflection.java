/*
 * Copyright (C) 2007 XStream Committers
 * Created on 25.06.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.xmlfriendly.target;

import com.thoughtworks.xstream.benchmark.reflection.targets.AbstractReflectionTarget;
import com.thoughtworks.xstream.benchmark.xmlfriendly.model.A100Fields;
import com.thoughtworks.xstream.tools.benchmark.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * A Target for a 100 fields class.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class UnderscoredFieldReflection extends AbstractReflectionTarget {

    public UnderscoredFieldReflection() {
        super(new ArrayList());
        List list = (List)target();
        for(int i = 0; i < 100; ++i) {
            Object o = new A100Fields();
            fill(o);
            list.add(o);
        }
    }

    public String toString() {
        return "Underscored Field Target";
    }

}
