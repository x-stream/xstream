package com.thoughtworks.xstream.io.xml.xppdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Xpp3Dom {
    protected String name;

    protected String value;

    protected Map attributes;

    protected List childList;

    protected Map childMap;

    protected Xpp3Dom parent;

    public Xpp3Dom(String name) {
        this.name = name;
        childList = new ArrayList();
        childMap = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    public String[] getAttributeNames() {
        if ( null == attributes ) {
            return new String[0];
        }
        else {
            return (String[]) attributes.keySet().toArray( new String[0] );
        }
    }

    public String getAttribute(String name) {
        return (null != attributes) ? (String) attributes.get(name) : null;
    }

    public void setAttribute(String name, String value) {
        if (null == attributes) {
            attributes = new HashMap();
        }

        attributes.put(name, value);
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getChild(int i) {
        return (Xpp3Dom) childList.get(i);
    }

    public Xpp3Dom getChild(String name) {
        return (Xpp3Dom) childMap.get(name);
    }

    public void addChild(Xpp3Dom xpp3Dom) {
        xpp3Dom.setParent(this);
        childList.add(xpp3Dom);
        childMap.put(xpp3Dom.getName(), xpp3Dom);
    }

    public Xpp3Dom[] getChildren() {
        if ( null == childList ) {
            return new Xpp3Dom[0];
        }
        else {
            return (Xpp3Dom[]) childList.toArray( new Xpp3Dom[0] );
        }
    }

    public Xpp3Dom[] getChildren( String name ) {
        if ( null == childList ) {
            return new Xpp3Dom[0];
        }
        else {
            ArrayList children = new ArrayList();
            int size = this.childList.size();

            for ( int i = 0; i < size; i++ ) {
                Xpp3Dom configuration = (Xpp3Dom) this.childList.get( i );
                if ( name.equals( configuration.getName() ) ) {
                    children.add( configuration );
                }
            }

            return (Xpp3Dom[]) children.toArray( new Xpp3Dom[0] );
        }
    }

    public int getChildCount() {
        if (null == childList) {
            return 0;
        }

        return childList.size();
    }

    // ----------------------------------------------------------------------
    // Parent handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getParent() {
        return parent;
    }

    public void setParent(Xpp3Dom parent) {
        this.parent = parent;
    }
}
