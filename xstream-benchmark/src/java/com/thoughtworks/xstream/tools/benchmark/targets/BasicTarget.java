/*
 * Copyright (C) 2008, 2009, 2011 XStream Committers.
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Target containing basic types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class BasicTarget implements Target {

    private List list;
    
    public BasicTarget() {
        list = new ArrayList();
        list.add(new Integer(1));
        list.add(new Byte((byte)2));
        list.add(new Short((short)3));
        list.add(new Long(4));
        list.add("Profile");
        list.add(Boolean.TRUE);
        list.add(new Float(1.2f));
        list.add(new Double(1.2f));
        list.add(new File("profile.txt"));
        list.add(Locale.ENGLISH);
    }
    
    public boolean isEqual(Object other) {
        return list.equals(other);
    }

    public Object target() {
        return list;
    }

    public String toString() {
        return "SingleValue Converters";
    }
}
