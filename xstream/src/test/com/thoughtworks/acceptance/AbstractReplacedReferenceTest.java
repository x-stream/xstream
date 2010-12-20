/*
 * Copyright (C) 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. December 2010 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractReplacedReferenceTest extends AbstractAcceptanceTest {

    private TreeData parent;

    protected void setUp() throws Exception {
        super.setUp();

        parent = new TreeData("parent");
        parent.add(new TreeData("child") {});

        xstream.alias("element", TreeData.class);
        xstream.alias("anonymous-element", parent.children.get(0).getClass());
    }
    
    public static class TreeData implements Serializable {
        String data;
        TreeData parent;
        List children;
        
        public TreeData(String data) {
            this.data = data;
            children = new ArrayList();
        }
        
        private TreeData(TreeData clone) {
            data = clone.data;
            parent = clone.parent;
            children = clone.children;
        }
        
        public void add(TreeData child) {
            child.parent = this;
            children.add(child);
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.children == null) ? 0 : this.children.hashCode());
            result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof TreeData)) return false;
            TreeData other = (TreeData)obj;
            if (this.children == null) {
                if (other.children != null) return false;
            } else if (!this.children.equals(other.children)) return false;
            if (this.data == null) {
                if (other.data != null) return false;
            } else if (!this.data.equals(other.data)) return false;
            return true;
        }

        private Object writeReplace() {
            if (getClass() == TreeData.class) {
                return this;
            }
            return new TreeData(this);
        }
    }
    
    public abstract void testReplacedReference();
    
    public void replacedReference(String expectedXml) {
        assertEquals(expectedXml, xstream.toXML(parent));
        TreeData clone = (TreeData)xstream.fromXML(expectedXml);
        assertEquals(parent, clone);
    }
}
