/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. April 2005 by Joe Walnes
 */

// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream should work fine without it.

package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.core.util.Fields;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * Serializes a Java 5 EnumSet.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EnumSetConverter implements Converter {

    private final static Field typeField;
    static {
        // field name is "elementType" in Sun JDK, but different in Harmony 
        Field assumedTypeField = null;
        Field[] fields = EnumSet.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == Class.class) {
                // take the fist member of type "Class"
                assumedTypeField = fields[i];
                assumedTypeField.setAccessible(true);
                break;
            }
        }
        if (assumedTypeField == null) {
            throw new ExceptionInInitializerError("Cannot detect element type of EnumSet");
        }
        typeField = assumedTypeField;
    }
    
    private final Mapper mapper;

    public EnumSetConverter(Mapper mapper) {
        this.mapper = mapper;
    }

    public boolean canConvert(Class type) {
        return EnumSet.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        EnumSet set = (EnumSet) source;
        Class enumTypeForSet = (Class) Fields.read(typeField, set);
        writer.addAttribute(mapper.aliasForAttribute("enum-type"), mapper.serializedClass(enumTypeForSet));
        writer.setValue(joinEnumValues(set));
    }

    private String joinEnumValues(EnumSet set) {
        boolean seenFirst = false;
        StringBuffer result = new StringBuffer();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Enum value = (Enum) iterator.next();
            if (seenFirst) {
                result.append(',');
            } else {
                seenFirst = true;
            }
            result.append(value.name());
        }
        return result.toString();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Class enumTypeForSet = mapper.realClass(reader.getAttribute(mapper.aliasForAttribute("enum-type")));
        EnumSet set = EnumSet.noneOf(enumTypeForSet);
        String[] enumValues = reader.getValue().split(",");
        for (int i = 0; i < enumValues.length; i++) {
            String enumValue = enumValues[i];
            if(enumValue.length() > 0) {
                set.add(Enum.valueOf(enumTypeForSet, enumValue));
            }
        }
        return set;
    }

}
