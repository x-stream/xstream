package com.thoughtworks.acceptance.objects;

public class OpenSourceSoftware extends Software {

    private String license;

    public OpenSourceSoftware() {
    }

    public OpenSourceSoftware(String vendor, String name, String license) {
        super(vendor, name);
        this.license = license;
    }

}
