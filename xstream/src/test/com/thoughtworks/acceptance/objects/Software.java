package com.thoughtworks.acceptance.objects;

import com.thoughtworks.acceptance.StandardObject;

public class Software extends StandardObject {

    public String vendor;
    public String name;

    public Software() {
    }

    public Software(String vendor, String name) {
        this.vendor = vendor;
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Software) {
            final Software software = (Software)obj;
            return software.vendor.equals(vendor) && software.name.equals(name);
        }
        return false;
    }
}
