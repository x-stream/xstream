package com.thoughtworks.acceptance.objects;

public class Hardware {
    public String arch;
    public String name;

    public Hardware(String arch, String name) {
        this.arch = arch;
        this.name = name;
    }

    public int hashCode() {
        return arch.hashCode() + name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Hardware) {
            Hardware hardware = (Hardware) obj;
            return arch.equals(hardware.arch)
                    && name.equals(hardware.name);
        }
        return false;
    }

    public String toString() {
        return "hardware:" + arch + "/" + name;
    }
}
