package com.thoughtworks.acceptance.objects;

public class OpenSourceSoftware extends Software {

    private String license;

    public OpenSourceSoftware(String vendor, String name, String license) {
        super(vendor, name);
        this.license = license;
    }

    public boolean equals(Object obj) {
        return super.equals(obj) &&
                (obj instanceof OpenSourceSoftware) &&
                ((OpenSourceSoftware) obj).license.equals(this.license);
    }
}
