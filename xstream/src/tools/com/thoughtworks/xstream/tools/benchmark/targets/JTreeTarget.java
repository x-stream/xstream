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
