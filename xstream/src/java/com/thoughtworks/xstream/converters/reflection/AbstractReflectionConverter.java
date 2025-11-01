/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2018, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 02. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.SecurityUtils;
import com.thoughtworks.xstream.core.util.ArrayIterator;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.MemberDictionary;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.core.util.SerializationMembers;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class AbstractReflectionConverter implements Converter, Caching {

    protected final ReflectionProvider reflectionProvider;
    protected final Mapper mapper;
    /**
     * @deprecated As of 1.4.8, use {@link #serializationMembers}.
     */
    protected transient SerializationMethodInvoker serializationMethodInvoker;
    protected transient SerializationMembers serializationMembers;
    private transient ReflectionProvider pureJavaReflectionProvider;

    public AbstractReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        serializationMethodInvoker = new SerializationMethodInvoker();
        serializationMembers = serializationMethodInvoker.serializationMembers;
    }

    protected boolean canAccess(Class type) {
        try {
            reflectionProvider.getFieldOrNull(type, "%");
            return true;
        } catch (NoClassDefFoundError e) {
            // restricted type in GAE
        }
        return false;
    }

    public void marshal(Object original, final HierarchicalStreamWriter writer,
        final MarshallingContext context) {
        final Object source = serializationMembers.callWriteReplace(original);

        if (source != original && context instanceof ReferencingMarshallingContext) {
            ((ReferencingMarshallingContext)context).replace(original, source);
        }
        if (source.getClass() != original.getClass()) {
            String attributeName = mapper.aliasForSystemAttribute("resolves-to");
            if (attributeName != null) {
                writer.addAttribute(attributeName, mapper.serializedClass(source.getClass()));
            }
            context.convertAnother(source);
        } else {
            doMarshal(source, writer, context);
        }
    }

    protected void doMarshal(final Object source, final HierarchicalStreamWriter writer,
        final MarshallingContext context) {
        final List fields = new ArrayList();
        final Map defaultFieldDefinition = new HashMap();
        final Class sourceType = source.getClass();

        // Attributes might be preferred to child elements ...
        reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            final Set writtenAttributes = new HashSet();

            public void visit(String fieldName, Class type, Class definedIn, Object value) {
                if (!mapper.shouldSerializeMember(definedIn, fieldName)) {
                    return;
                }
                if (!defaultFieldDefinition.containsKey(fieldName)) {
                    Class lookupType = source.getClass();
                    // See XSTR-457 and OmitFieldsTest
                    if (definedIn != sourceType
                        && !mapper.shouldSerializeMember(lookupType, fieldName)) {
                        lookupType = definedIn;
                    }
                    defaultFieldDefinition.put(
                        fieldName, reflectionProvider.getField(lookupType, fieldName));
                }

                SingleValueConverter converter = mapper.getConverterFromItemType(
                    fieldName, type, definedIn);
                if (converter != null) {
                    final String attribute = mapper.aliasForAttribute(mapper.serializedMember(
                        definedIn, fieldName));
                    if (value != null) {
                        if (writtenAttributes.contains(fieldName)) {
                            ConversionException exception =
                                    new ConversionException("Cannot write field as attribute for object, attribute name already in use");
                            exception.add("field-name", fieldName);
                            exception.add("object-type", sourceType.getName());
                            throw exception;
                        }
                        final String str = converter.toString(value);
                        if (str != null) {
                            writer.addAttribute(attribute, str);
                        }
                    }
                    writtenAttributes.add(fieldName);
                } else {
                    fields.add(new FieldInfo(fieldName, type, definedIn, value));
                }
            }
        });

        final FieldMarshaller fieldMarshaller = new FieldMarshaller() {
            public void writeField(String fieldName, String aliasName, Class fieldType,
                Class definedIn, Object newObj) {
                Class actualType = newObj != null ? newObj.getClass() : fieldType;
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, aliasName != null
                    ? aliasName
                    : mapper.serializedMember(sourceType, fieldName), actualType);

                if (newObj != null) {
                    Class defaultType = mapper.defaultImplementationOf(fieldType);
                    if (!actualType.equals(defaultType)) {
                        String serializedClassName = mapper.serializedClass(actualType);
                        if (!serializedClassName.equals(mapper.serializedClass(defaultType))) {
                            String attributeName = mapper.aliasForSystemAttribute("class");
                            if (attributeName != null) {
                                writer.addAttribute(attributeName, serializedClassName);
                            }
                        }
                    }

                    final Field defaultField = (Field)defaultFieldDefinition.get(fieldName);
                    if (defaultField.getDeclaringClass() != definedIn) {
                        String attributeName = mapper.aliasForSystemAttribute("defined-in");
                        if (attributeName != null) {
                            writer.addAttribute(
                                attributeName, mapper.serializedClass(definedIn));
                        }
                    }

                    Field field = reflectionProvider.getField(definedIn, fieldName);
                    marshallField(context, newObj, field);
                }
                writer.endNode();
            }

            public void writeItem(Object item) {
                if (item == null) {
                    String name = mapper.serializedClass(null);
                    ExtendedHierarchicalStreamWriterHelper.startNode(
                        writer, name, Mapper.Null.class);
                    writer.endNode();
                } else {
                    String name = mapper.serializedClass(item.getClass());
                    ExtendedHierarchicalStreamWriterHelper.startNode(
                        writer, name, item.getClass());
                    context.convertAnother(item);
                    writer.endNode();
                }
            }
        };

        final Map hiddenMappers = new HashMap();
        for (Iterator fieldIter = fields.iterator(); fieldIter.hasNext();) {
            FieldInfo info = (FieldInfo)fieldIter.next();
            if (info.value != null) {
                final Field defaultField = (Field)defaultFieldDefinition.get(info.fieldName);
                Mapper.ImplicitCollectionMapping mapping = mapper
                    .getImplicitCollectionDefForFieldName(
                        defaultField.getDeclaringClass() == info.definedIn ? sourceType : info.definedIn,
                            info.fieldName);
                if (mapping != null) {
                    Set mappings = (Set)hiddenMappers.get(info.fieldName);
                    if (mappings == null) {
                        mappings = new HashSet();
                        mappings.add(mapping);
                        hiddenMappers.put(info.fieldName, mappings);
                    } else {
                        if (!mappings.add(mapping)) {
                            mapping = null;
                        }
                    }
                }
                if (mapping != null) {
                    if (context instanceof ReferencingMarshallingContext) {
                        if (info.value != Collections.EMPTY_LIST
                            && info.value != Collections.EMPTY_SET
                            && info.value != Collections.EMPTY_MAP) {
                            ReferencingMarshallingContext refContext = (ReferencingMarshallingContext)context;
                            refContext.registerImplicit(info.value);
                        }
                    }
                    final boolean isCollection = info.value instanceof Collection;
                    final boolean isMap = info.value instanceof Map;
                    final boolean isEntry = isMap && mapping.getKeyFieldName() == null;
                    final boolean isArray = info.value.getClass().isArray();
                    for (Iterator iter = isArray
                        ? new ArrayIterator(info.value)
                        : isCollection ? ((Collection)info.value).iterator() : isEntry
                            ? ((Map)info.value).entrySet().iterator()
                            : ((Map)info.value).values().iterator(); iter.hasNext();) {
                        Object obj = iter.next();
                        final String itemName;
                        final Class itemType;
                        if (obj == null) {
                            itemType = Object.class;
                            itemName = mapper.serializedClass(null);
                        } else if (isEntry) {
                            final String entryName = mapping.getItemFieldName() != null
                                ? mapping.getItemFieldName()
                                : mapper.serializedClass(Map.Entry.class);
                            Map.Entry entry = (Map.Entry)obj;
                            ExtendedHierarchicalStreamWriterHelper.startNode(
                                writer, entryName, entry.getClass());
                            fieldMarshaller.writeItem(entry.getKey());
                            fieldMarshaller.writeItem(entry.getValue());
                            writer.endNode();
                            continue;
                        } else if (mapping.getItemFieldName() != null) {
                            itemType = mapping.getItemType();
                            itemName = mapping.getItemFieldName();
                        } else {
                            itemType = obj.getClass();
                            itemName = mapper.serializedClass(itemType);
                        }
                        fieldMarshaller.writeField(
                            info.fieldName, itemName, itemType, info.definedIn, obj);
                    }
                } else {
                    fieldMarshaller.writeField(
                        info.fieldName, null, info.type, info.definedIn, info.value);
                }
            }
        }
    }

    protected void marshallField(final MarshallingContext context, Object newObj, Field field) {
        context.convertAnother(
            newObj, mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }

    public Object unmarshal(final HierarchicalStreamReader reader,
        final UnmarshallingContext context) {
        Object result = instantiateNewInstance(reader, context);
        result = doUnmarshal(result, reader, context);
        return serializationMembers.callReadResolve(result);
    }

    public Object doUnmarshal(final Object result, final HierarchicalStreamReader reader,
        final UnmarshallingContext context) {
        final Class resultType = result.getClass();
        final MemberDictionary seenFields = new MemberDictionary();

        SecurityUtils.checkDepthLimit(context, reader);
        int currentFieldCount = 0;

        // process attributes before recursing into child elements.
        Iterator it = reader.getAttributeNames();
        while (it.hasNext()) {
            String attrAlias = (String)it.next();
            // TODO: realMember should return FastField
            String attrName = mapper
                .realMember(resultType, mapper.attributeForAlias(attrAlias));
            Field field = reflectionProvider.getFieldOrNull(resultType, attrName);
            if (field != null && shouldUnmarshalField(field)) {
                Class classDefiningField = field.getDeclaringClass();
                if (!mapper.shouldSerializeMember(classDefiningField, attrName)) {
                    continue;
                }

                // we need a converter that produces a string representation only
                SingleValueConverter converter = mapper.getConverterFromAttribute(
                    classDefiningField, attrName, field.getType());
                Class type = field.getType();
                if (converter != null) {
                    Object value = converter.fromString(reader.getAttribute(attrAlias));
                    if (type.isPrimitive()) {
                        type = Primitives.box(type);
                    }
                    if (value != null && !type.isAssignableFrom(value.getClass())) {
                        ConversionException exception = new ConversionException("Cannot convert type");
                        exception.add("source-type", value.getClass().getName());
                        exception.add("target-type", type.getName());
                        throw exception;
                    }
                    if (!seenFields.add(classDefiningField, attrName)) {
                        throw new DuplicateFieldException(attrName);
                    }
                    reflectionProvider.writeField(result, attrName, value, classDefiningField);
                }
            }
        }

        Map implicitCollectionsForCurrentObject = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String originalNodeName = reader.getNodeName();
            Class explicitDeclaringClass = readDeclaringClass(reader);
            Class fieldDeclaringClass = explicitDeclaringClass == null
                ? resultType
                : explicitDeclaringClass;
            String fieldName = mapper.realMember(fieldDeclaringClass, originalNodeName);
            Mapper.ImplicitCollectionMapping implicitCollectionMapping = mapper
                .getImplicitCollectionDefForFieldName(fieldDeclaringClass, fieldName);
            final Object value;
            String implicitFieldName = null;
            Field field = null;
            Class type = null;
            if (implicitCollectionMapping == null) {
                // no item of an implicit collection for this name ... do we have a field?
                field = reflectionProvider.getFieldOrNull(fieldDeclaringClass, fieldName);
                if (field == null) {
                    // it is not a field ... do we have a field alias?
                    Class itemType = mapper.getItemTypeForItemFieldName(fieldDeclaringClass, fieldName);
                    if (itemType != null) {
                        String classAttribute = HierarchicalStreams.readClassAttribute(
                            reader, mapper);
                        if (classAttribute != null) {
                            type = mapper.realClass(classAttribute);
                        } else {
                            type = itemType;
                        }
                    } else {
                        // it is not an alias ... do we have an element of an implicit
                        // collection based on type only?
                        try {
                            type = mapper.realClass(originalNodeName);
                            implicitFieldName = mapper.getFieldNameForItemTypeAndName(
                                fieldDeclaringClass, type, originalNodeName);
                        } catch (CannotResolveClassException e) {
                            // type stays null ...
                        }
                        if (type == null || (type != null && implicitFieldName == null)) {
                            // either not a type or element is a type alias, but does not
                            // belong to an implicit field
                            handleUnknownField(
                                explicitDeclaringClass, fieldName, fieldDeclaringClass, originalNodeName);

                            // element is unknown in declaring class, ignore it now
                            type = null;
                        }
                    }
                    if (type == null) {
                        // no type, no value
                        value = null;
                    } else {
                        if (Map.Entry.class.equals(type)) {
                            // it is an element of an implicit map with two elements now for
                            // key and value
                            reader.moveDown();
                            final Object key = context.convertAnother(
                                result, HierarchicalStreams.readClassType(reader, mapper));
                            reader.moveUp();
                            reader.moveDown();
                            final Object v = context.convertAnother(
                                result, HierarchicalStreams.readClassType(reader, mapper));
                            reader.moveUp();
                            value = Collections.singletonMap(key, v)
                                .entrySet().iterator().next();
                        } else {
                            // recurse info hierarchy
                            value = context.convertAnother(result, type);
                        }
                    }
                } else {
                    boolean fieldAlreadyChecked = false;

                    // we have a field, but do we have to address a hidden one?
                    if (explicitDeclaringClass == null) {
                        while (field != null
                            && !(fieldAlreadyChecked = shouldUnmarshalField(field)
                                && mapper.shouldSerializeMember(
                                    field.getDeclaringClass(), fieldName))) {
                            field = reflectionProvider.getFieldOrNull(field
                                .getDeclaringClass()
                                .getSuperclass(), fieldName);
                        }
                    }
                    if (field != null
                        && (fieldAlreadyChecked || (shouldUnmarshalField(field) && mapper
                            .shouldSerializeMember(field.getDeclaringClass(), fieldName)))) {
                        String classAttribute = HierarchicalStreams.readClassAttribute(
                            reader, mapper);
                        if (classAttribute != null) {
                            type = mapper.realClass(classAttribute);
                        } else {
                            type = mapper.defaultImplementationOf(field.getType());
                        }
                        // TODO the reflection provider should already return the proper field
                        currentFieldCount += 1;
                        SecurityUtils.checkFieldLimit(context, currentFieldCount);
                        value = unmarshallField(context, result, type, field);
                        Class definedType = field.getType();
                        if (!definedType.isPrimitive()) {
                            type = definedType;
                        }
                    } else {
                        value = null;
                    }
                }
            } else {
                // we have an implicit collection with defined names
                implicitFieldName = implicitCollectionMapping.getFieldName();
                type = implicitCollectionMapping.getItemType();
                if (type == null) {
                    String classAttribute = HierarchicalStreams.readClassAttribute(
                        reader, mapper);
                    type = mapper.realClass(classAttribute != null
                        ? classAttribute
                        : originalNodeName);
                }
                value = context.convertAnother(result, type);
            }

            if (value != null && !type.isAssignableFrom(value.getClass())) {
                throw new ConversionException("Cannot convert type "
                    + value.getClass().getName()
                    + " to type "
                    + type.getName());
            }

            if (field != null) {
                reflectionProvider.writeField(result, fieldName, value, field.getDeclaringClass());
                if (!seenFields.add(field.getDeclaringClass(), fieldName)) {
                    throw new DuplicateFieldException(fieldName);
                }
            } else if (type != null) {
                if (implicitFieldName == null) {
                    // look for implicit field
                    implicitFieldName = mapper.getFieldNameForItemTypeAndName(
                        fieldDeclaringClass,
                        value != null ? value.getClass() : Mapper.Null.class,
                        originalNodeName);
                }
                if (implicitCollectionsForCurrentObject == null) {
                    implicitCollectionsForCurrentObject = new HashMap();
                }
                writeValueToImplicitCollection(
                    value, implicitCollectionsForCurrentObject, result, new FieldLocation(
                        implicitFieldName, fieldDeclaringClass));
            }

            reader.moveUp();
        }

        if (implicitCollectionsForCurrentObject != null) {
            for (Iterator iter = implicitCollectionsForCurrentObject.entrySet().iterator(); iter
                .hasNext();) {
                Map.Entry entry = (Map.Entry)iter.next();
                Object value = entry.getValue();
                if (value instanceof ArraysList) {
                    Object array = ((ArraysList)value).toPhysicalArray();
                    final FieldLocation fieldLocation = (FieldLocation)entry.getKey();
                    final Field field = reflectionProvider.getFieldOrNull(fieldLocation.definedIn,
                        fieldLocation.fieldName);
                    reflectionProvider.writeField(result, fieldLocation.fieldName, array, field != null
                            ? field.getDeclaringClass()
                                : null);
                }
            }
        }

        return result;
    }

    protected Object unmarshallField(final UnmarshallingContext context, final Object result,
        Class type, Field field) {
        return context.convertAnother(
            result, type, mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }

    protected boolean shouldUnmarshalTransientFields() {
        return false;
    }

    protected boolean shouldUnmarshalField(Field field) {
        return !(Modifier.isTransient(field.getModifiers()) && !shouldUnmarshalTransientFields());
    }

    private void handleUnknownField(Class classDefiningField, String fieldName,
        Class resultType, String originalNodeName) {
        if (classDefiningField == null) {
            for (Class cls = resultType; cls != null; cls = cls.getSuperclass()) {
                if (!mapper.shouldSerializeMember(cls, originalNodeName)) {
                    return;
                }
            }
        }
        throw new UnknownFieldException(resultType.getName(), fieldName);
    }

    private void writeValueToImplicitCollection(Object value, Map implicitCollections, Object result, final FieldLocation fieldLocation) {
        Collection collection = (Collection)implicitCollections.get(fieldLocation);
        if (collection == null) {
            final Field field = reflectionProvider.getFieldOrNull(fieldLocation.definedIn, fieldLocation.fieldName);
            Class physicalFieldType = field != null
                    ? field.getType()
                        : reflectionProvider.getFieldType(result, fieldLocation.fieldName, null);
            if (physicalFieldType.isArray()) {
                collection = new ArraysList(physicalFieldType);
            } else {
                Class fieldType = mapper.defaultImplementationOf(physicalFieldType);
                if (!(Collection.class.isAssignableFrom(fieldType) || Map.class
                    .isAssignableFrom(fieldType))) {
                    ObjectAccessException oaex = new ObjectAccessException(
                        "Field is configured for an implicit Collection or Map, but is of an incompatible type");
                    oaex.add("field", result.getClass().getName() + "."+fieldLocation.fieldName);
                    oaex.add("field-type", fieldType.getName());
                    throw oaex;
                }
                if (pureJavaReflectionProvider == null) {
                    pureJavaReflectionProvider = new PureJavaReflectionProvider();
                }
                Object instance = pureJavaReflectionProvider.newInstance(fieldType);
                if (instance instanceof Collection) {
                    collection = (Collection)instance;
                } else {
                    Mapper.ImplicitCollectionMapping implicitCollectionMapping = mapper
                        .getImplicitCollectionDefForFieldName(fieldLocation.definedIn, fieldLocation.fieldName);
                    collection = new MappingList(
                        (Map)instance, implicitCollectionMapping.getKeyFieldName());
                }
                reflectionProvider.writeField(result, fieldLocation.fieldName, instance, field != null
                    ? field.getDeclaringClass()
                    : null);
            }
            implicitCollections.put(fieldLocation, collection);
        }
        collection.add(value);
    }

    private Class readDeclaringClass(HierarchicalStreamReader reader) {
        String attributeName = mapper.aliasForSystemAttribute("defined-in");
        String definedIn = attributeName == null ? null : reader.getAttribute(attributeName);
        return definedIn == null ? null : mapper.realClass(definedIn);
    }

    protected Object instantiateNewInstance(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
        String attributeName = mapper.aliasForSystemAttribute("resolves-to");
        String readResolveValue = attributeName == null ? null : reader
            .getAttribute(attributeName);
        Object currentObject = context.currentObject();
        if (currentObject != null) {
            return currentObject;
        } else if (readResolveValue != null) {
            return reflectionProvider.newInstance(mapper.realClass(readResolveValue));
        } else {
            return reflectionProvider.newInstance(context.getRequiredType());
        }
    }

    public void flushCache() {
        serializationMethodInvoker.flushCache();
    }

    protected Object readResolve() {
        serializationMethodInvoker = new SerializationMethodInvoker();
        serializationMembers = serializationMethodInvoker.serializationMembers;
        return this;
    }

    public static class DuplicateFieldException extends ConversionException {
        public DuplicateFieldException(String msg) {
            super("Duplicate field " + msg);
            add("field", msg);
        }
    }

    public static class UnknownFieldException extends ConversionException {
        public UnknownFieldException(String type, String field) {
            super("No such field " + type + "." + field);
            add("field", field);
        }
    }

    private static class FieldLocation {
        final String fieldName;
        final Class definedIn;

        FieldLocation(final String fieldName, final Class definedIn) {
            this.fieldName = fieldName;
            this.definedIn = definedIn;
        }

        public int hashCode() {
            final int prime = 7;
            int result = 1;
            result = prime * result + (definedIn == null ? 0 : definedIn.getName().hashCode());
            result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
            return result;
        }

        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FieldLocation other = (FieldLocation)obj;
            if (definedIn != other.definedIn) {
                return false;
            }
            if (fieldName == null) {
                if (other.fieldName != null) {
                    return false;
                }
            } else if (!fieldName.equals(other.fieldName)) {
                return false;
            }
            return true;
        }
    }

    private static class FieldInfo extends FieldLocation {
        final Class type;
        final Object value;

        FieldInfo(final String fieldName, final Class type, final Class definedIn, final Object value) {
            super(fieldName, definedIn);
            this.type = type;
            this.value = value;
        }
    }

    private interface FieldMarshaller {
        void writeItem(final Object item);
        void writeField(final String fieldName, final String aliasName, final Class fieldType,
                final Class definedIn, final Object newObj);
    }

    private static class ArraysList extends ArrayList {
        final Class physicalFieldType;

        ArraysList(Class physicalFieldType) {
            this.physicalFieldType = physicalFieldType;
        }

        Object toPhysicalArray() {
            Object[] objects = toArray();
            Object array = Array.newInstance(
                physicalFieldType.getComponentType(), objects.length);
            if (physicalFieldType.getComponentType().isPrimitive()) {
                for (int i = 0; i < objects.length; ++i) {
                    Array.set(array, i, Array.get(objects, i));
                }
            } else {
                System.arraycopy(objects, 0, array, 0, objects.length);
            }
            return array;
        }
    }

    private class MappingList extends AbstractList {

        private final Map map;
        private final String keyFieldName;
        private final Map fieldCache = new HashMap();

        public MappingList(Map map, String keyFieldName) {
            this.map = map;
            this.keyFieldName = keyFieldName;
        }

        public boolean add(Object object) {
            if (object == null) {
                boolean containsNull = !map.containsKey(null);
                map.put(null, null);
                return containsNull;
            }
            Class itemType = object.getClass();

            if (keyFieldName != null) {
                Field field = (Field)fieldCache.get(itemType);
                if (field == null) {
                    field = reflectionProvider.getField(itemType, keyFieldName);
                    fieldCache.put(itemType, field);
                }
                if (field != null) {
                    Object key = Fields.read(field, object);
                    return map.put(key, object) == null;
                }
            } else if (object instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)object;
                return map.put(entry.getKey(), entry.getValue()) == null;
            }

            ConversionException exception =
                    new ConversionException("Element  is not defined as entry for implicit map");
            exception.add("map-type", map.getClass().getName());
            exception.add("element-type", object.getClass().getName());
            throw exception;
        }

        public Object get(int index) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return map.size();
        }
    }
}
