/*
 * Copyright (C) 2008, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Mapper that allows a package name to be replaced with an alias.
 * 
 * @author J&ouml;rg Schaible
 */
public class PackageAliasingMapper extends MapperWrapper implements Serializable {

    private static final long serialVersionUID = 20151010L;
    private static final Comparator<String> REVERSE = new Comparator<String>() {

        @Override
        public int compare(final String o1, final String o2) {
            return o2.compareTo(o1);
        }
    };

    private Map<String, String> packageToName = new TreeMap<>(REVERSE);
    protected transient Map<String, String> nameToPackage = new HashMap<>();

    public PackageAliasingMapper(final Mapper wrapped) {
        super(wrapped);
    }

    public void addPackageAlias(String name, String pkg) {
        if (name.length() > 0 && name.charAt(name.length() - 1) != '.') {
            name += '.';
        }
        if (pkg.length() > 0 && pkg.charAt(pkg.length() - 1) != '.') {
            pkg += '.';
        }
        nameToPackage.put(name, pkg);
        packageToName.put(pkg, name);
    }

    @Override
    public String serializedClass(final Class<?> type) {
        final String className = type.getName();
        int length = className.length();
        int dot = -1;
        do {
            dot = className.lastIndexOf('.', length);
            final String pkg = dot < 0 ? "" : className.substring(0, dot + 1);
            final String alias = packageToName.get(pkg);
            if (alias != null) {
                return alias + (dot < 0 ? className : className.substring(dot + 1));
            }
            length = dot - 1;
        } while (dot >= 0);
        return super.serializedClass(type);
    }

    @Override
    public Class<?> realClass(String elementName) {
        int length = elementName.length();
        int dot = -1;
        do {
            dot = elementName.lastIndexOf('.', length);
            final String name = dot < 0 ? "" : elementName.substring(0, dot) + '.';
            final String packageName = nameToPackage.get(name);

            if (packageName != null) {
                elementName = packageName + (dot < 0 ? elementName : elementName.substring(dot + 1));
                break;
            }
            length = dot - 1;
        } while (dot >= 0);

        return super.realClass(elementName);
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(new HashMap<>(packageToName));
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        packageToName = new TreeMap<>(REVERSE);
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>)in.readObject();
        packageToName.putAll(map);
        nameToPackage = new HashMap<>();
        for (final Map.Entry<String, String> entry : packageToName.entrySet()) {
            nameToPackage.put(entry.getValue(), entry.getKey());
        }
    }
}
