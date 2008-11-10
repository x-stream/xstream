/*
 * Copyright (C) 2008 XStream Committers.
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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Mapper that allows a package name to be replaced with an alias.
 * 
 * @author J&ouml;rg Schaible
 */
public class PackageAliasingMapper extends MapperWrapper implements Serializable {

    private static final Comparator REVERSE = new Comparator() {

        public int compare(final Object o1, final Object o2) {
            return ((String)o2).compareTo((String)o1);
        }
    };

    private Map packageToName = new TreeMap(REVERSE);
    protected transient Map nameToPackage = new HashMap();

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

    public String serializedClass(final Class type) {
        final String className = type.getName();
        int length = className.length();
        int dot = -1;
        do {
            dot = className.lastIndexOf('.', length);
            final String pkg = dot < 0 ? "" : className.substring(0, dot + 1);
            final String alias = (String)packageToName.get(pkg);
            if (alias != null) {
                return alias + (dot < 0 ? className : className.substring(dot + 1));
            }
            length = dot - 1;
        } while (dot >= 0);
        return super.serializedClass(type);
    }

    public Class realClass(String elementName) {
        int length = elementName.length();
        int dot = -1;
        do {
            dot = elementName.lastIndexOf('.', length);
            final String name = dot < 0 ? "" : elementName.substring(0, dot) + '.';
            final String packageName = (String)nameToPackage.get(name);

            if (packageName != null) {
                elementName = packageName
                    + (dot < 0 ? elementName : elementName.substring(dot + 1));
                break;
            }
            length = dot - 1;
        } while (dot >= 0);

        return super.realClass(elementName);
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(new HashMap(packageToName));
    }

    private void readObject(final ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        packageToName = new TreeMap(REVERSE);
        packageToName.putAll((Map)in.readObject());
        nameToPackage = new HashMap();
        for (final Iterator iter = packageToName.keySet().iterator(); iter.hasNext();) {
            final Object type = iter.next();
            nameToPackage.put(packageToName.get(type), type);
        }
    }
}
