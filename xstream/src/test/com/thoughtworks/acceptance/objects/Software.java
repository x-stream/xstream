package com.thoughtworks.acceptance.objects;

public class Software {

    public String vendor;
    public String name;

    public Software(String vendor, String name) {
        this.vendor = vendor;
        this.name = name;
    }

    public int hashCode() {
        return vendor.hashCode() + name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Software) {
            Software software = (Software) obj;
            return vendor.equals(software.vendor)
                    && name.equals(software.name);
        }
        return false;
    }

    public String toString() {
        return "software:" + vendor + "/" + name;
    }

}
