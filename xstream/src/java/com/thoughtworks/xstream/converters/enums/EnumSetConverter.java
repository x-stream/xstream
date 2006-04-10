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
 */
public class EnumSetConverter implements Converter {

    private final Field typeField;
    private final Mapper mapper;

    public EnumSetConverter(Mapper mapper) {
        this.mapper = mapper;
        typeField = Fields.find(EnumSet.class, "elementType");
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
