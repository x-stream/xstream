/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2018, 2022, 2024 XStream Committers.
 * All rights reserved.
         *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 02. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.util.ArrayIterator;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.MemberDictionary;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.core.util.SerializationMembers;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;


public abstract class AbstractReflectionConverter implements Converter, Caching {

    protected final ReflectionProvider reflectionProvider;
    protected final Mapper mapper;
    /**
     * @deprecated As of 1.4.8, use {@link #serializationMembers}.
     */
    @Deprecated
    protected transient SerializationMethodInvoker serializationMethodInvoker;
    protected transient SerializationMembers serializationMembers;
    private transient ReflectionProvider pureJavaReflectionProvider;

    @SuppressWarnings("deprecation")
    public AbstractReflectionConverter(final Mapper mapper, final ReflectionProvider reflectionProvider) {
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        serializationMethodInvoker = new SerializationMethodInvoker();
        serializationMembers = serializationMethodInvoker.serializationMembers;
    }

    protected boolean canAccess(final Class<?> type) {
        try {
            reflectionProvider.getFieldOrNull(type, "%");
            return true;
        } catch (final NoClassDefFoundError e) {
            // restricted type in GAE
        }
        return false;
    }

    @Override
    public void marshal(final Object original, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final Object source = serializationMembers.callWriteReplace(original);

        if (source != original && context instanceof ReferencingMarshallingContext) {
            ((ReferencingMarshallingContext<?>)context).replace(original, source);
        }
        if (source.getClass() != original.getClass()) {
            final String attributeName = mapper.aliasForSystemAttribute("resolves-to");
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
        final List<FieldInfo> fields = new ArrayList<>();
        final Map<String, Field> defaultFieldDefinition = new HashMap<>();
        final Class<?> sourceType = source.getClass();

        // Attributes might be preferred to child elements ...
        reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            final Set<String> writtenAttributes = new HashSet<>();

            @Override
            public void visit(final String fieldName, final Class<?> type, final Class<?> definedIn,
                    final Object value) {
                if (!mapper.shouldSerializeMember(definedIn, fieldName)) {
                    return;
                }
                if (!defaultFieldDefinition.containsKey(fieldName)) {
                    Class<?> lookupType = sourceType;
                    // See XSTR-457 and OmitFieldsTest
                    if (definedIn != sourceType && !mapper.shouldSerializeMember(lookupType, fieldName)) {
                        lookupType = definedIn;
                    }
                    defaultFieldDefinition.put(fieldName, reflectionProvider.getField(lookupType, fieldName));
                }

                final SingleValueConverter converter = mapper.getConverterFromItemType(fieldName, type, definedIn);
                if (converter != null) {
                    final String attribute = mapper.aliasForAttribute(mapper.serializedMember(definedIn, fieldName));
                    if (value != null) {
                        if (writtenAttributes.contains(fieldName)) {
                            final ConversionException exception = new ConversionException(
                                "Cannot write field as attribute for object, attribute name already in use");
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
            @Override
            public void writeField(final String fieldName, final String aliasName, final Class<?> fieldType,
                    final Class<?> definedIn, final Object newObj) {
                final Class<?> actualType = newObj != null ? newObj.getClass() : fieldType;
                writer.startNode(aliasName != null ? aliasName : mapper.serializedMember(sourceType, fieldName),
                    actualType);

                if (newObj != null) {
                    final Class<?> defaultType = mapper.defaultImplementationOf(fieldType);
                    if (!actualType.equals(defaultType)) {
                        final String serializedClassName = mapper.serializedClass(actualType);
                        if (!serializedClassName.equals(mapper.serializedClass(defaultType))) {
                            final String attributeName = mapper.aliasForSystemAttribute("class");
                            if (attributeName != null) {
                                writer.addAttribute(attributeName, serializedClassName);
                            }
                        }
                    }

                    final Field defaultField = defaultFieldDefinition.get(fieldName);
                    if (defaultField.getDeclaringClass() != definedIn) {
                        final String attributeName = mapper.aliasForSystemAttribute("defined-in");
                        if (attributeName != null) {
                            writer.addAttribute(attributeName, mapper.serializedClass(definedIn));
                        }
                    }

                    final Field field = reflectionProvider.getField(definedIn, fieldName);
                    marshallField(context, newObj, field);
                }
                writer.endNode();
            }

            @Override
            public void writeItem(final Object item) {
                if (item == null) {
                    final String name = mapper.serializedClass(null);
                    writer.startNode(name, Mapper.Null.class);
                    writer.endNode();
                } else {
                    final String name = mapper.serializedClass(item.getClass());
                    writer.startNode(name, item.getClass());
                    context.convertAnother(item);
                    writer.endNode();
                }
            }
        };

        final Map<String, Set<Mapper.ImplicitCollectionMapping>> hiddenMappers = new HashMap<>();
        for (final FieldInfo info : fields) {
            if (info.value != null) {
                final boolean isCollection = info.value instanceof Collection;
                final boolean isMap = info.value instanceof Map;
                final boolean isArray = info.value.getClass().isArray();
                final Field defaultField = defaultFieldDefinition.get(info.fieldName);
                Mapper.ImplicitCollectionMapping mapping = isCollection || isMap || isArray
                    ? mapper.getImplicitCollectionDefForFieldName(defaultField.getDeclaringClass() == info.definedIn
                        ? sourceType
                        : info.definedIn, info.fieldName)
                    : null;
                if (mapping != null) {
                    Set<Mapper.ImplicitCollectionMapping> mappings = hiddenMappers.get(info.fieldName);
                    if (mappings == null) {
                        mappings = new HashSet<>();
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
                            final ReferencingMarshallingContext<?> refContext =
                                    (ReferencingMarshallingContext<?>)context;
                            refContext.registerImplicit(info.value);
                        }
                    }
                    final boolean isEntry = isMap && mapping.getKeyFieldName() == null;
                    for (final Iterator<?> iter = isArray
                        ? new ArrayIterator(info.value)
                        : isCollection
                            ? ((Collection<?>)info.value).iterator()
                            : isEntry
                                ? ((Map<?, ?>)info.value).entrySet().iterator()
                                : ((Map<?, ?>)info.value).values().iterator(); iter.hasNext();) {
                        final Object obj = iter.next();
                        final String itemName;
                        final Class<?> itemType;
                        if (obj == null) {
                            itemType = Object.class;
                            itemName = mapper.serializedClass(null);
                        } else if (isEntry) {
                            final String entryName = mapping.getItemFieldName() != null
                                ? mapping.getItemFieldName()
                                : mapper.serializedClass(Map.Entry.class);
                            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
                            writer.startNode(entryName, entry.getClass());
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
                        fieldMarshaller.writeField(info.fieldName, itemName, itemType, info.definedIn, obj);
                    }
                } else {
                    fieldMarshaller.writeField(info.fieldName, null, info.type, info.definedIn, info.value);
                }
            }
        }
    }

    protected void marshallField(final MarshallingContext context, final Object newObj, final Field field) {
        context.convertAnother(newObj, mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        Object result = instantiateNewInstance(reader, context);
        result = doUnmarshal(result, reader, context);
        return serializationMembers.callReadResolve(result);
    }

    public Object doUnmarshal(final Object result, final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        final Class<?> resultType = result.getClass();
        final MemberDictionary seenFields = new MemberDictionary();

        // process attributes before recursing into child elements.
        final Iterator<String> it = reader.getAttributeNames();
        while (it.hasNext()) {
            final String attrAlias = it.next();
            // TODO: realMember should return FastField
            final String attrName = mapper.realMember(resultType, mapper.attributeForAlias(attrAlias));
            final Field field = reflectionProvider.getFieldOrNull(resultType, attrName);
            if (field != null && shouldUnmarshalField(field)) {
                final Class<?> classDefiningField = field.getDeclaringClass();
                if (!mapper.shouldSerializeMember(classDefiningField, attrName)) {
                    continue;
                }

                // we need a converter that produces a string representation only
                Class<?> type = field.getType();
                final SingleValueConverter converter = mapper.getConverterFromAttribute(classDefiningField, attrName,
                    type);
                if (converter != null) {
                    final Object value = converter.fromString(reader.getAttribute(attrAlias));
                    if (type.isPrimitive()) {
                        type = Primitives.box(type);
                    }
                    if (value != null && !type.isAssignableFrom(value.getClass())) {
                        final ConversionException exception = new ConversionException("Cannot convert type");
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

        Map<FieldLocation, Collection<? super Object>> implicitCollectionsForCurrentObject = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            final String originalNodeName = reader.getNodeName();
            final Class<?> explicitDeclaringClass = readDeclaringClass(reader);
            final Class<?> fieldDeclaringClass = explicitDeclaringClass == null ? resultType : explicitDeclaringClass;
            final String fieldName = mapper.realMember(fieldDeclaringClass, originalNodeName);
            final Mapper.ImplicitCollectionMapping implicitCollectionMapping = mapper
                .getImplicitCollectionDefForFieldName(fieldDeclaringClass, fieldName);
            final Object value;
            String implicitFieldName = null;
            Field field = null;
            Class<?> type = null;
            if (implicitCollectionMapping == null) {
                // no item of an implicit collection for this name ... do we have a field?
                field = reflectionProvider.getFieldOrNull(fieldDeclaringClass, fieldName);
                if (field == null) {
                    // it is not a field ... do we have a field alias?
                    final Class<?> itemType = mapper.getItemTypeForItemFieldName(fieldDeclaringClass, fieldName);
                    if (itemType != null) {
                        final String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
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
                            implicitFieldName = mapper.getFieldNameForItemTypeAndName(fieldDeclaringClass, type,
                                originalNodeName);
                        } catch (final CannotResolveClassException e) {
                            // type stays null ...
                        }
                        if (type == null || type != null && implicitFieldName == null) {
                            // either not a type or element is a type alias, but does not
                            // belong to an implicit field
                            handleUnknownField(explicitDeclaringClass, fieldName, fieldDeclaringClass,
                                originalNodeName);

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
                            final Object key = context.convertAnother(result, HierarchicalStreams.readClassType(reader,
                                mapper));
                            reader.moveUp();
                            reader.moveDown();
                            final Object v = context.convertAnother(result, HierarchicalStreams.readClassType(reader,
                                mapper));
                            reader.moveUp();
                            value = Collections.singletonMap(key, v).entrySet().iterator().next();
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
                                && mapper.shouldSerializeMember(field.getDeclaringClass(), fieldName))) {
                            field = reflectionProvider.getFieldOrNull(field.getDeclaringClass().getSuperclass(),
                                fieldName);
                        }
                    }
                    if (field != null
                        && (fieldAlreadyChecked
                            || shouldUnmarshalField(field)
                                && mapper.shouldSerializeMember(field.getDeclaringClass(), fieldName))) {

                        final String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
                        if (classAttribute != null) {
                            type = mapper.realClass(classAttribute);
                        } else {
                            type = mapper.defaultImplementationOf(field.getType());
                        }
                        // TODO the reflection provider should already return the proper field
                        value = unmarshallField(context, result, type, field);
                        final Class<?> definedType = field.getType();
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
                    final String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
                    type = mapper.realClass(classAttribute != null ? classAttribute : originalNodeName);
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
                    implicitFieldName = mapper.getFieldNameForItemTypeAndName(fieldDeclaringClass, value != null
                        ? value.getClass()
                        : Mapper.Null.class, originalNodeName);
                    implicitFieldName = mapper.getFieldNameForItemTypeAndName(fieldDeclaringClass, value != null
                        ? value.getClass()
                        : Mapper.Null.class, originalNodeName);
                }
                if (implicitCollectionsForCurrentObject == null) {
                    implicitCollectionsForCurrentObject = new HashMap<>();
                }
                writeValueToImplicitCollection(value, implicitCollectionsForCurrentObject, result, new FieldLocation(
                    implicitFieldName, fieldDeclaringClass));
            }

            reader.moveUp();
        }

        if (implicitCollectionsForCurrentObject != null) {
            for (final Map.Entry<FieldLocation, Collection<? super Object>> entry : implicitCollectionsForCurrentObject
                .entrySet()) {
                final Object value = entry.getValue();
                if (value instanceof ArraysList) {
                    final Object array = ((ArraysList)value).toPhysicalArray();
                    final FieldLocation fieldLocation = entry.getKey();
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

    protected Object unmarshallField(final UnmarshallingContext context, final Object result, final Class<?> type,
            final Field field) {
        return context.convertAnother(result, type, mapper.getLocalConverter(field.getDeclaringClass(), field
            .getName()));
    }

    protected boolean shouldUnmarshalTransientFields() {
        return false;
    }

    protected boolean shouldUnmarshalField(final Field field) {
        return !(Modifier.isTransient(field.getModifiers()) && !shouldUnmarshalTransientFields());
    }

    private void handleUnknownField(final Class<?> classDefiningField, final String fieldName,
            final Class<?> resultType, final String originalNodeName) {
        if (classDefiningField == null) {
            for (Class<?> cls = resultType; cls != null; cls = cls.getSuperclass()) {
                if (!mapper.shouldSerializeMember(cls, originalNodeName)) {
                    return;
                }
            }
        }
        throw new UnknownFieldException(resultType.getName(), fieldName);
    }

    private void writeValueToImplicitCollection(final Object value,
            final Map<FieldLocation, Collection<? super Object>> implicitCollections, final Object result,
            final FieldLocation fieldLocation) {
        Collection<? super Object> collection = implicitCollections.get(fieldLocation);
        if (collection == null) {
            final Field field = reflectionProvider.getFieldOrNull(fieldLocation.definedIn, fieldLocation.fieldName);
            final Class<?> physicalFieldType = field != null
                ? field.getType()
                : reflectionProvider.getFieldType(result, fieldLocation.fieldName, null);
            if (physicalFieldType.isArray()) {
                collection = new ArraysList(physicalFieldType);
            } else {
                final Class<?> fieldType = mapper.defaultImplementationOf(physicalFieldType);
                if (!(Collection.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType))) {
                    final ObjectAccessException oaex = new ObjectAccessException(
                        "Field is configured for an implicit Collection or Map, but is of an incompatible type");
                    oaex.add("field", result.getClass().getName() + "." + fieldLocation.fieldName);
                    oaex.add("field-type", fieldType.getName());
                    throw oaex;
                }
                if (pureJavaReflectionProvider == null) {
                    pureJavaReflectionProvider = new PureJavaReflectionProvider();
                }
                final Object instance = pureJavaReflectionProvider.newInstance(fieldType);
                if (instance instanceof Collection) {
                    @SuppressWarnings("unchecked")
                    final Collection<? super Object> uncheckedCollection = (Collection<? super Object>)instance;
                    collection = uncheckedCollection;
                } else {
                    final Mapper.ImplicitCollectionMapping implicitCollectionMapping = mapper
                        .getImplicitCollectionDefForFieldName(fieldLocation.definedIn, fieldLocation.fieldName);
                    @SuppressWarnings("unchecked")
                    final Map<Object, Object> map = (Map<Object, Object>)instance;
                    collection = new MappingList(map, implicitCollectionMapping.getKeyFieldName());
                }
                reflectionProvider.writeField(result, fieldLocation.fieldName, instance, field != null
                    ? field.getDeclaringClass()
                    : null);
            }
            implicitCollections.put(fieldLocation, collection);
        }
        collection.add(value);
    }

    private Class<?> readDeclaringClass(final HierarchicalStreamReader reader) {
        final String attributeName = mapper.aliasForSystemAttribute("defined-in");
        final String definedIn = attributeName == null ? null : reader.getAttribute(attributeName);
        return definedIn == null ? null : mapper.realClass(definedIn);
    }

    protected Object instantiateNewInstance(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final String attributeName = mapper.aliasForSystemAttribute("resolves-to");
        final String readResolveValue = attributeName == null ? null : reader.getAttribute(attributeName);
        final Object currentObject = context.currentObject();
        if (currentObject != null) {
            return currentObject;
        } else if (readResolveValue != null) {
            return reflectionProvider.newInstance(mapper.realClass(readResolveValue));
        } else {
            return reflectionProvider.newInstance(context.getRequiredType());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void flushCache() {
        serializationMethodInvoker.flushCache();
    }

    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        serializationMethodInvoker = new SerializationMethodInvoker();
        serializationMembers = serializationMethodInvoker.serializationMembers;
        return this;
    }

    public static class DuplicateFieldException extends ConversionException {
        private static final long serialVersionUID = 20150926L;

        public DuplicateFieldException(final String msg) {
            super("Duplicate field " + msg);
            add("field", msg);
        }
    }

    public static class UnknownFieldException extends ConversionException {
        private static final long serialVersionUID = 20150926L;

        public UnknownFieldException(final String type, final String field) {
            super("No such field " + type + "." + field);
            add("field", field);
        }
    }

    private static class FieldLocation {
        final String fieldName;
        final Class<?> definedIn;

        FieldLocation(final String fieldName, final Class<?> definedIn) {
            this.fieldName = fieldName;
            this.definedIn = definedIn;
        }

        @Override
        public int hashCode() {
            final int prime = 7;
            int result = 1;
            result = prime * result + (definedIn == null ? 0 : definedIn.getName().hashCode());
            result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (getClass() != obj.getClass())) {
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
        final Class<?> type;
        final Object value;

        FieldInfo(final String fieldName, final Class<?> type, final Class<?> definedIn, final Object value) {
            super(fieldName, definedIn);
            this.type = type;
            this.value = value;
        }
    }

    private interface FieldMarshaller {
        void writeItem(final Object item);

        void writeField(final String fieldName, final String aliasName, final Class<?> fieldType,
                final Class<?> definedIn, final Object newObj);
    }

    private static class ArraysList extends ArrayList<Object> {
        private static final long serialVersionUID = 20150926L;
        final Class<?> physicalFieldType;

        ArraysList(final Class<?> physicalFieldType) {
            this.physicalFieldType = physicalFieldType;
        }

        Object toPhysicalArray() {
            final Object[] objects = toArray();
            final Object array = Array.newInstance(physicalFieldType.getComponentType(), objects.length);
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

    private class MappingList extends AbstractList<Object> {

        private final Map<Object, Object> map;
        private final String keyFieldName;
        private final Map<Class<?>, Field> fieldCache = new HashMap<>();

        public MappingList(final Map<Object, Object> map, final String keyFieldName) {
            this.map = map;
            this.keyFieldName = keyFieldName;
        }

        @Override
        public boolean add(final Object object) {
            if (object == null) {
                final boolean containsNull = !map.containsKey(null);
                map.put(null, null);
                return containsNull;
            }
            final Class<?> itemType = object.getClass();
            if (keyFieldName != null) {
                Field field = fieldCache.get(itemType);
                if (field == null) {
                    field = reflectionProvider.getField(itemType, keyFieldName);
                    fieldCache.put(itemType, field);
                }
                if (field != null) {
                    final Object key = Fields.read(field, object);
                    return map.put(key, object) == null;
                }
            } else if (object instanceof Map.Entry) {
                @SuppressWarnings("unchecked")
                final Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)object;
                return map.put(entry.getKey(), entry.getValue()) == null;
            }

            final ConversionException exception = new ConversionException(
                "Element  is not defined as entry for implicit map");
            exception.add("map-type", map.getClass().getName());
            exception.add("element-type", object.getClass().getName());
            throw exception;
        }

        @Override
        public Object get(final int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return map.size();
        }
    }
}
