/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. February 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;


/**
 * Special converter for java.util.Properties that stores properties in a more compact form than
 * java.util.Map.
 * <p>
 * Because all entries of a Properties instance are Strings, a single element is used for each
 * property with two attributes; one for key and one for value.
 * </p>
 * <p>
 * Additionally, default properties are also serialized, if they are present or if a
 * SecurityManager is set, and it has permissions for SecurityManager.checkPackageAccess,
 * SecurityManager.checkMemberAccess(this, EnumSet.MEMBER) and
 * ReflectPermission("suppressAccessChecks").
 * </p>
 * 
 * @author Joe Walnes
 * @author Kevin Ring
 */
public class PropertiesConverter implements Converter {

    private final static Field defaultsField;
    static {
        Field field = null;
        try {
            field = Fields.find(Properties.class, "defaults");
        } catch (SecurityException ex) {
            // ignore, no access possible with current SecurityManager
        } catch (RuntimeException ex) {
            throw new ExceptionInInitializerError("No field 'defaults' in type Properties found");
        }
        defaultsField = field;
    }
    private final boolean sort;

    public PropertiesConverter() {
        this(false);
    }

    public PropertiesConverter(boolean sort) {
        this.sort = sort;
    }

    public boolean canConvert(Class type) {
        return Properties.class == type;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        final Properties properties = (Properties) source;
        Map map = sort ? (Map)new TreeMap(properties) : (Map)properties;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startNode("property");
            writer.addAttribute("name", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.endNode();
        }
        if (defaultsField != null) {
            Properties defaults = (Properties)Fields.read(defaultsField, properties);
            if (defaults != null) {
                writer.startNode("defaults");
                marshal(defaults, writer, context);
                writer.endNode();
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Properties properties = new Properties();
        Properties defaults = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("defaults")) {
                defaults = (Properties) unmarshal(reader, context);
            } else {
                String name = reader.getAttribute("name");
                String value = reader.getAttribute("value");
                properties.setProperty(name, value);
            }
            reader.moveUp();
        }
        if (defaults == null) {
            return properties;
        } else {
            Properties propertiesWithDefaults = new Properties(defaults);
            propertiesWithDefaults.putAll(properties);
            return propertiesWithDefaults;
        }
    }

}
