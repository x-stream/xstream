/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

import javax.swing.*;

/**
 * A Swing JTree instance, which is a suitably complex object graph.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 * @see JTree
 */
public class JTreeTarget implements Target {

    private JTree jTree = new JTree();

    public String toString() {
        return "JTree";
    }

    public Object target() {
        return jTree;
    }

    public boolean isEqual(Object other) {
        // TODO: Check if JTrees are equal. -Joe 
        return true;
    }

}
