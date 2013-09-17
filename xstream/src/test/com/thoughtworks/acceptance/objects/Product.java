/*
 * Copyright (C) 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. April 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.objects;

import java.util.ArrayList;


public class Product {

    String name;
    String id;
    double price;
    ArrayList tags;

    public Product(String name, String id, double price) {
        super();
        this.name = name;
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList getTags() {
        return tags;
    }

    public void setTags(ArrayList tags) {
        this.tags = tags;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.price);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        result = prime * result + ((this.tags == null) ? 0 : this.tags.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Product other = (Product)obj;
        if (this.id == null) {
            if (other.id != null) return false;
        } else if (!this.id.equals(other.id)) return false;
        if (this.name == null) {
            if (other.name != null) return false;
        } else if (!this.name.equals(other.name)) return false;
        if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price))
            return false;
        if (this.tags == null) {
            if (other.tags != null) return false;
        } else if (!this.tags.equals(other.tags)) return false;
        return true;
    }

    public String toString() {
        String ret = "[" + name + ", " + id + ", " + price;
        if (tags != null) {
            ret += "\n{";
            for (java.util.Iterator it = tags.iterator(); it.hasNext();) {
                String tag = (String)it.next();
                ret += tag + "\n";
            }
            ret += "}";
        }
        ret += "]";
        return ret;
    }

}
