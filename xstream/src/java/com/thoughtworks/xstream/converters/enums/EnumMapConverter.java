// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream should work fine without it.

package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.core.util.Fields;

import java.util.EnumMap;
import java.lang.reflect.Field;

/**
 * Serializes an Java 5 EnumMap, including the type of Enum it's for.
 *
 * @author Joe Walnes
 */
public class EnumMapConverter extends MapConverter {

    private final static Field typeField;
    static {
        // field name is "keyType" in Sun JDK, but different in IKVM 
        Field assumedTypeField = null;
        Field[] fields = EnumMap.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == Class.class) {
                // take the fist member of type "Class"
                assumedTypeField = fields[i];
                assumedTypeField.setAccessible(true);
                break;
            }
        }
        if (assumedTypeField == null) {
            throw new InternalError("Cannot detect element type of EnumMap");
        }
        typeField = assumedTypeField;
    }

    public EnumMapConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type == EnumMap.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Class type = (Class) Fields.read(typeField, source);
        writer.addAttribute(mapper().aliasForAttribute("enum-type"), mapper().serializedClass(type));
        super.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Class type = mapper().realClass(reader.getAttribute(mapper().aliasForAttribute("enum-type")));
        EnumMap map = new EnumMap(type);
        populateMap(reader, context, map);
        return map;
    }
}
