package com.thoughtworks.xstream.xml.xpp3;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Xpp3Dom {
    private String name;

    private String value;

    private Map attributes;

    private List childList;

    private Map childMap;

    private Xpp3Dom parent;

    public Xpp3Dom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getAttribute(String name) {
        return (null != attributes) ? (String) attributes.get(name) : null;
    }

    public Xpp3Dom getChild(int i) {
        return (Xpp3Dom) childList.get(i);
    }

    public Xpp3Dom getChild(String name) {
        return (Xpp3Dom) childMap.get(name);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAttribute(String name, String value) {
        if (null == attributes) {
            attributes = new HashMap();
        }

        attributes.put(name, value);
    }

    public void addChild(Xpp3Dom xpp3Dom) {
        if (null == childList) {
            childList = new ArrayList();

            childMap = new HashMap();
        }

        xpp3Dom.setParent(this);

        childList.add(xpp3Dom);

        childMap.put(xpp3Dom.getName(), xpp3Dom);
    }

    public int getChildCount() {
        if (null == childList) {
            return 0;
        }

        return childList.size();
    }

    public Xpp3Dom getParent() {
        return parent;
    }

    public void setParent(Xpp3Dom parent) {
        this.parent = parent;
    }
}
