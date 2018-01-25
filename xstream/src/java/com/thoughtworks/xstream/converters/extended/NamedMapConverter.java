/*
 * Copyright (C) 2013, 2016, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 20. September 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A map converter that uses predefined names for its elements.
 * <p>
 * To be used as local converter. Note, suppress the usage of the implicit type argument, if
 * registered with annotation. Depending on the constructor arguments it is possible to support
 * various formats:
 * </p>
 * <ul>
 * <li>new NamedMapConverter(xstream.getMapper(), "entry", "key", String.class, "value",
 * Integer.class);
 * 
 * <pre>
 * &lt;map&gt;
 *   &lt;entry&gt;
 *     &lt;key&gt;keyValue&lt;/key&gt;
 *     &lt;value&gt;0&lt;/value&gt;
 *   &lt;/entry&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * </li>
 * <li>new NamedMapConverter(xstream.getMapper(), null, "key", String.class, "value",
 * Integer.class);
 * 
 * <pre>
 * &lt;map&gt;
 *   &lt;key&gt;keyValue&lt;/key&gt;
 *   &lt;value&gt;0&lt;/value&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * </li>
 * <li>new NamedMapConverter(xstream.getMapper(), "entry", "key", String.class, "value",
 * Integer.class, true, true, xstream.getConverterLookup());
 * 
 * <pre>
 * &lt;map&gt;
 *   &lt;entry&gt; key=&quot;keyValue&quot; value=&quot;0&quot;/&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * </li>
 * <li>new NamedMapConverter(xstream.getMapper(), "entry", "key", String.class, "value",
 * Integer.class, true, false, xstream.getConverterLookup());
 * 
 * <pre>
 * &lt;map&gt;
 *   &lt;entry key=&quot;keyValue&quot;&gt;
 *     &lt;value&gt;0&lt;/value&gt;
 *   &lt;/entry&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * </li>
 * <li>new NamedMapConverter(xstream.getMapper(), "entry", "key", String.class, "value",
 * Integer.class, false, true, xstream.getConverterLookup());
 * 
 * <pre>
 * &lt;map&gt;
 *   &lt;entry value=&quot;0&quot;&gt;
 *     &lt;key&gt;keyValue&lt;/key&gt;
 *   &lt;/entry&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * </li>
 * <li>new NamedMapConverter(xstream.getMapper(), "entry", "key", String.class, null,
 * Integer.class, true, false, xstream.getConverterLookup());
 * 
 * <pre>
 * &lt;map&gt;
 *   &lt;entry key=&quot;keyValue&quot;&gt;0&lt;/entry&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public class NamedMapConverter extends MapConverter {

    private final String entryName;
    private final String keyName;
    private final Class keyType;
    private final String valueName;
    private final Class valueType;
    private final boolean keyAsAttribute;
    private final boolean valueAsAttribute;
    private final ConverterLookup lookup;
    private final Mapper enumMapper;

    /**
     * Constructs a NamedMapConverter.
     * 
     * @param mapper the mapper
     * @param entryName the name of the entry elements
     * @param keyName the name of the key elements
     * @param keyType the base type of key elements
     * @param valueName the name of the value elements
     * @param valueType the base type of value elements
     * @since 1.4.5
     */
    public NamedMapConverter(
        Mapper mapper, String entryName, String keyName, Class keyType, String valueName,
        Class valueType) {
        this(mapper, entryName, keyName, keyType, valueName, valueType, false, false, null);
    }

    /**
     * Constructs a NamedMapConverter handling an explicit Map type.
     * 
     * @param type the Map type this instance will handle
     * @param mapper the mapper
     * @param entryName the name of the entry elements
     * @param keyName the name of the key elements
     * @param keyType the base type of key elements
     * @param valueName the name of the value elements
     * @param valueType the base type of value elements
     * @since 1.4.5
     */
    public NamedMapConverter(
        Class type, Mapper mapper, String entryName, String keyName, Class keyType,
        String valueName, Class valueType) {
        this(
            type, mapper, entryName, keyName, keyType, valueName, valueType, false, false, null);
    }

    /**
     * Constructs a NamedMapConverter with attribute support.
     * 
     * @param mapper the mapper
     * @param entryName the name of the entry elements
     * @param keyName the name of the key elements
     * @param keyType the base type of key elements
     * @param valueName the name of the value elements
     * @param valueType the base type of value elements
     * @param keyAsAttribute flag to write key as attribute of entry element
     * @param valueAsAttribute flag to write value as attribute of entry element
     * @param lookup used to lookup SingleValueConverter for attributes
     * @since 1.4.5
     */
    public NamedMapConverter(
        Mapper mapper, String entryName, String keyName, Class keyType, String valueName,
        Class valueType, boolean keyAsAttribute, boolean valueAsAttribute,
        ConverterLookup lookup) {
        this(
            null, mapper, entryName, keyName, keyType, valueName, valueType, keyAsAttribute,
            valueAsAttribute, lookup);
    }

    /**
     * Constructs a NamedMapConverter with attribute support handling an explicit Map type.
     * 
     * @param type the Map type this instance will handle
     * @param mapper the mapper
     * @param entryName the name of the entry elements
     * @param keyName the name of the key elements
     * @param keyType the base type of key elements
     * @param valueName the name of the value elements
     * @param valueType the base type of value elements
     * @param keyAsAttribute flag to write key as attribute of entry element
     * @param valueAsAttribute flag to write value as attribute of entry element
     * @param lookup used to lookup SingleValueConverter for attributes
     * @since 1.4.5
     */
    public NamedMapConverter(
        Class type, Mapper mapper, String entryName, String keyName, Class keyType,
        String valueName, Class valueType, boolean keyAsAttribute, boolean valueAsAttribute,
        ConverterLookup lookup) {
        super(mapper, type);
        this.entryName = entryName != null && entryName.length() == 0 ? null : entryName;
        this.keyName = keyName != null && keyName.length() == 0 ? null : keyName;
        this.keyType = keyType;
        this.valueName = valueName != null && valueName.length() == 0 ? null : valueName;
        this.valueType = valueType;
        this.keyAsAttribute = keyAsAttribute;
        this.valueAsAttribute = valueAsAttribute;
        this.lookup = lookup;
        enumMapper = JVM.isVersion(5) ? UseAttributeForEnumMapper.createEnumMapper(mapper) : null;

        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("Class types of key and value are mandatory");
        }
        if (entryName == null) {
            if (keyAsAttribute || valueAsAttribute) {
                throw new IllegalArgumentException(
                    "Cannot write attributes to map entry, if map entry must be omitted");
            }
            if (valueName == null) {
                throw new IllegalArgumentException(
                    "Cannot write value as text of entry, if entry must be omitted");
            }
        }
        if (keyName == null) {
            throw new IllegalArgumentException("Cannot write key without name");
        }
        if (valueName == null) {
            if (valueAsAttribute) {
                throw new IllegalArgumentException(
                    "Cannot write value as attribute without name");
            } else if (!keyAsAttribute) {
                throw new IllegalArgumentException(
                    "Cannot write value as text of entry, if key is also child element");
            }
        }
        if (keyAsAttribute && valueAsAttribute && keyName.equals(valueName)) {
            throw new IllegalArgumentException(
                "Cannot write key and value with same attribute name");
        }
    }

    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
        Map map = (Map)source;
        SingleValueConverter keyConverter = null;
        SingleValueConverter valueConverter = null;
        if (keyAsAttribute) {
            keyConverter = getSingleValueConverter(keyType, "key");
        }
        if (valueAsAttribute || valueName == null) {
            valueConverter = getSingleValueConverter(valueType, "value");
        }
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (entryName != null) {
                ExtendedHierarchicalStreamWriterHelper.startNode(
                    writer, entryName, entry.getClass());
                if (keyConverter != null && key != null) {
                    writer.addAttribute(keyName, keyConverter.toString(key));
                }
                if (valueName != null && valueConverter != null && value != null) {
                    writer.addAttribute(valueName, valueConverter.toString(value));
                }
            }

            if (keyConverter == null) {
                writeItem(keyName, keyType, key, context, writer);
            }
            if (valueConverter == null) {
                writeItem(valueName, valueType, value, context, writer);
            } else if (valueName == null) {
                writer.setValue(valueConverter.toString(value));
            }

            if (entryName != null) {
                writer.endNode();
            }
        }
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context,
        Map map, Map target) {
        SingleValueConverter keyConverter = null;
        SingleValueConverter valueConverter = null;
        if (keyAsAttribute) {
            keyConverter = getSingleValueConverter(keyType, "key");
        }
        if (valueAsAttribute || valueName == null) {
            valueConverter = getSingleValueConverter(valueType, "value");
        }

        while (reader.hasMoreChildren()) {
            Object key = null;
            Object value = null;

            if (entryName != null) {
                reader.moveDown();

                if (keyConverter != null) {
                    String attribute = reader.getAttribute(keyName);
                    if (attribute != null) {
                        key = keyConverter.fromString(attribute);
                    }
                }

                if (valueAsAttribute && valueConverter != null) {
                    String attribute = reader.getAttribute(valueName);
                    if (attribute != null) {
                        value = valueConverter.fromString(attribute);
                    }
                }
            }

            if (keyConverter == null) {
                reader.moveDown();
                if (valueConverter == null
                    && !keyName.equals(valueName)
                    && reader.getNodeName().equals(valueName)) {
                    value = readItem(valueType, reader, context, map);
                } else {
                    key = readItem(keyType, reader, context, map);
                }
                reader.moveUp();
            }

            if (valueConverter == null) {
                reader.moveDown();
                if (keyConverter == null && key == null && value != null) {
                    key = readItem(keyType, reader, context, map);
                } else {
                    value = readItem(valueType, reader, context, map);
                }
                reader.moveUp();
            } else if (!valueAsAttribute) {
                value = valueConverter.fromString(reader.getValue());
            }

            target.put(key, value);

            if (entryName != null) {
                reader.moveUp();
            }
        }
    }

    private SingleValueConverter getSingleValueConverter(Class type, String part) {
        SingleValueConverter conv = UseAttributeForEnumMapper.isEnum(type) ? enumMapper
            .getConverterFromItemType(null, type, null) : mapper().getConverterFromItemType(
            null, type, null);
        if (conv == null) {
            Converter converter = lookup.lookupConverterForType(type);
            if (converter instanceof SingleValueConverter) {
                conv = (SingleValueConverter)converter;
            } else {
                throw new ConversionException("No SingleValueConverter for " + part +  " available");
            }
        }
        return conv;
    }

    protected void writeItem(String name, Class type, Object item, MarshallingContext context,
        HierarchicalStreamWriter writer) {
        Class itemType = item == null ? Mapper.Null.class : item.getClass();
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, itemType);
        if (!itemType.equals(type)) {
            String attributeName = mapper().aliasForSystemAttribute("class");
            if (attributeName != null) {
                writer.addAttribute(attributeName, mapper().serializedClass(itemType));
            }
        }
        if (item != null) {
            context.convertAnother(item);
        }
        writer.endNode();
    }

    protected Object readItem(Class type, HierarchicalStreamReader reader,
        UnmarshallingContext context, Object current) {
        String className = HierarchicalStreams.readClassAttribute(reader, mapper());
        Class itemType = className == null ? type : mapper().realClass(className);
        if (Mapper.Null.class.equals(itemType)) {
            return null;
        } else {
            return context.convertAnother(current, itemType);
        }
    }
}
