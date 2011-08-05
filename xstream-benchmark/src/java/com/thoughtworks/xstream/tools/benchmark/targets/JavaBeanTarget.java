/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. May 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;
import com.thoughtworks.xstream.tools.benchmark.model.FiveBean;
import com.thoughtworks.xstream.tools.benchmark.model.OneBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Target containing basic types using the JavaBeanConverter.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class JavaBeanTarget implements Target {

    private List list;
    
    public JavaBeanTarget() {
        list = new ArrayList();
        for (int i = 0; i < 5; ++i) {
            OneBean one = new OneBean();
            one.setOne(Integer.toString(i));
            list.add(one);
        }
        FiveBean five = new FiveBean();
        five.setOne("1");
        five.setTwo(2);
        five.setThree(true);
        five.setFour('4');
        five.setFive(new StringBuffer("5"));
        list.add(five);
    }
    
    public boolean isEqual(Object other) {
        return list.equals(other);
    }

    public Object target() {
        return list;
    }

    public String toString() {
        return "JavaBean Converter";
    }
}
