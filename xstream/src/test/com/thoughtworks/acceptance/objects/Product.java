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
