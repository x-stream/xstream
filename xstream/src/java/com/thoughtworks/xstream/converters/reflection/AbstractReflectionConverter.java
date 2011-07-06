/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 XStream Committers.
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
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReflectionConverter implements Converter {

    protected final ReflectionProvider reflectionProvider;
    protected final Mapper mapper;
    protected transient SerializationMethodInvoker serializationMethodInvoker;
    private transient ReflectionProvider pureJavaReflectionProvider;

    public AbstractReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        serializationMethodInvoker = new SerializationMethodInvoker();
    }

    public void marshal(Object original, final HierarchicalStreamWriter writer,
        final MarshallingContext context) {
        final Object source = serializationMethodInvoker.callWriteReplace(original);

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

    protected void doMarshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final List fields = new ArrayList(); 
        final Map defaultFieldDefinition = new HashMap();

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
                    // if (definedIn != source.getClass() && !mapper.shouldSerializeMember(lookupType, fieldName)) {
                    //    lookupType = definedIn;
                    // }
                    defaultFieldDefinition.put(fieldName, reflectionProvider.getField(lookupType, fieldName));
                }
                
                SingleValueConverter converter = mapper.getConverterFromItemType(fieldName, type, definedIn);
                if (converter != null) {
                    final String attribute = mapper.aliasForAttribute(mapper.serializedMember(definedIn, fieldName));
                    if (value != null) {
                        if (writtenAttributes.contains(fieldName)) { // TODO: use attribute
                            throw new ConversionException("Cannot write field with name '" + fieldName 
                                + "' twice as attribute for object of type " + source.getClass().getName());
                        }
                        final String str = converter.toString(value);
                        if (str != null) {
                            writer.addAttribute(attribute, str);
                        }
                    }
                    writtenAttributes.add(fieldName); // TODO: use attribute
                } else {
                    fields.add(new FieldInfo(fieldName, type, definedIn, value));
                }
            }
        });

        new Object() {
            {
                for (Iterator fieldIter = fields.iterator(); fieldIter.hasNext();) {
                    FieldInfo info = (FieldInfo)fieldIter.next();
                    if (info.value != null) {
                        Mapper.ImplicitCollectionMapping mapping = mapper
                            .getImplicitCollectionDefForFieldName(
                                source.getClass(), info.fieldName);
                        if (mapping != null) {
                            if (context instanceof ReferencingMarshallingContext) {
                                ReferencingMarshallingContext refContext = (ReferencingMarshallingContext)context;
                                refContext.registerImplicit(info.value);
                            }
                            Collection collection = (Collection)info.value;
                            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                                Object obj = iter.next();
                                final String itemName;
                                final Class itemType;
                                if (obj == null) {
                                    itemType = Object.class;
                                    itemName = mapper.serializedClass(null);
                                } else if (mapping.getItemFieldName() != null) {
                                    itemType = mapping.getItemType();
                                    itemName = mapping.getItemFieldName();
                                } else {
                                    itemType = obj.getClass();
                                    itemName = mapper.serializedClass(itemType);
                                }
                                writeField(
                                    info.fieldName, itemName, itemType, info.definedIn, obj);
                            }
                        } else {
                            writeField(
                                info.fieldName, null, info.type, info.definedIn, info.value);
                        }
                    }
                }

            }

            void writeField(String fieldName, String aliasName, Class fieldType,
                Class definedIn, Object newObj) {
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, aliasName != null
                    ? aliasName
                    : mapper.serializedMember(source.getClass(), fieldName), fieldType);

                if (newObj != null) {
                    Class actualType = newObj.getClass();
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

        };
    }

    protected void marshallField(final MarshallingContext context, Object newObj, Field field) {
        context.convertAnother(newObj, mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        Object result = instantiateNewInstance(reader, context);
        result = doUnmarshal(result, reader, context);
        return serializationMethodInvoker.callReadResolve(result);
    }

    public Object doUnmarshal(final Object result, final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Set seenFields = new HashSet() {
            public boolean add(Object e) {
                if (!super.add(e)) {
                    throw new DuplicateFieldException(e.toString());
                }
                return true;
            }
        };
        Iterator it = reader.getAttributeNames();

        // Process attributes before recursing into child elements.
        while (it.hasNext()) {
            String attrAlias = (String) it.next();
            // TODO: realMember should return FastField
            String attrName = mapper.realMember(result.getClass(), mapper.attributeForAlias(attrAlias));
            boolean fieldExistsInClass = reflectionProvider.fieldDefinedInClass(attrName, result.getClass());
            if (fieldExistsInClass) {
                Field field = reflectionProvider.getField(result.getClass(), attrName);
                if (Modifier.isTransient(field.getModifiers()) && !shouldUnmarshalTransientFields()) {
                    continue;
                }
                Class classDefiningField = field.getDeclaringClass();
                if (!mapper.shouldSerializeMember(classDefiningField, attrName)) {
                    continue;
                }
                SingleValueConverter converter = mapper.getConverterFromAttribute(classDefiningField, attrName, field.getType());
                Class type = field.getType();
                if (converter != null) {
                    Object value = converter.fromString(reader.getAttribute(attrAlias));
                    if (type.isPrimitive()) {
                        type = Primitives.box(type);
                    }
                    if (value != null && !type.isAssignableFrom(value.getClass())) {
                        throw new ConversionException("Cannot convert type " + value.getClass().getName() + " to type " + type.getName());
                    }
                    seenFields.add(new FastField(classDefiningField, attrName));
                    reflectionProvider.writeField(result, attrName, value, classDefiningField);
                }
            }
        }

        Map implicitCollectionsForCurrentObject = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String originalNodeName = reader.getNodeName();
            Class classDefiningField = determineWhichClassDefinesField(reader);
            Class fieldDeclaringClass = classDefiningField == null
                ? result.getClass()
                : classDefiningField;
            String fieldName = mapper.realMember(fieldDeclaringClass, originalNodeName);
            Mapper.ImplicitCollectionMapping implicitCollectionMapping = mapper
                .getImplicitCollectionDefForFieldName(fieldDeclaringClass, fieldName);
            boolean fieldExistsInClass = implicitCollectionMapping == null
                && reflectionProvider.fieldDefinedInClass(fieldName, fieldDeclaringClass);
            Class type = implicitCollectionMapping == null || implicitCollectionMapping.getItemType() == null
                ? determineType(
                    reader, fieldExistsInClass, result, fieldName, classDefiningField)
                : implicitCollectionMapping.getItemType();
            final Object value;
            if (fieldExistsInClass) {
                Field field = reflectionProvider.getField(classDefiningField != null ? classDefiningField : result.getClass(), fieldName);
                if ((Modifier.isTransient(field.getModifiers()) && !shouldUnmarshalTransientFields()) 
                        || !mapper.shouldSerializeMember(classDefiningField != null ? classDefiningField : result.getClass(), fieldName)) {
                    reader.moveUp();
                    continue;
                }
                value = unmarshallField(context, result, type, field);
                // TODO the reflection provider should have returned the proper field in first place ....
                Class definedType = reflectionProvider.getFieldType(result, fieldName, classDefiningField);
                if (!definedType.isPrimitive()) {
                    type = definedType;
                }
            } else {
                value = type != null ? context.convertAnother(result, type) : null;
            }
            
            if (value != null && !type.isAssignableFrom(value.getClass())) {
                throw new ConversionException("Cannot convert type " + value.getClass().getName() + " to type " + type.getName());
            }

            if (fieldExistsInClass) {
                reflectionProvider.writeField(result, fieldName, value, classDefiningField);
                seenFields.add(new FastField(classDefiningField, fieldName));
            } else if (type != null) {
                implicitCollectionsForCurrentObject = writeValueToImplicitCollection(context, value, implicitCollectionsForCurrentObject, result, originalNodeName);
            }

            reader.moveUp();
        }

        return result;
    }

    protected Object unmarshallField(final UnmarshallingContext context, final Object result, Class type, Field field) {
        return context.convertAnother(result, type, mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }
    
    protected boolean shouldUnmarshalTransientFields() {
        return false;
    }

    private Map writeValueToImplicitCollection(UnmarshallingContext context, Object value,
        Map implicitCollections, Object result, String itemFieldName) {
        String fieldName = mapper.getFieldNameForItemTypeAndName(
            context.getRequiredType(), value != null ? value.getClass() : Mapper.Null.class,
            itemFieldName);
        if (fieldName != null) {
            if (implicitCollections == null) {
                implicitCollections = new HashMap(); // lazy instantiation
            }
            Collection collection = (Collection)implicitCollections.get(fieldName);
            if (collection == null) {
                Class fieldType = mapper.defaultImplementationOf(reflectionProvider
                    .getFieldType(result, fieldName, null));
                if (!Collection.class.isAssignableFrom(fieldType)) {
                    throw new ObjectAccessException("Field "
                        + fieldName
                        + " of "
                        + result.getClass().getName()
                        + " is configured for an implicit Collection, but field is of type "
                        + fieldType.getName());
                }
                if (pureJavaReflectionProvider == null) {
                    pureJavaReflectionProvider = new PureJavaReflectionProvider();
                }
                collection = (Collection)pureJavaReflectionProvider.newInstance(fieldType);
                reflectionProvider.writeField(result, fieldName, collection, null);
                implicitCollections.put(fieldName, collection);
            }
            collection.add(value);
        } else {
            throw new ConversionException("Element "
                + itemFieldName
                + " of type "
                + value.getClass().getName()
                + " is not defined as field in type "
                + result.getClass().getName());
        }
        return implicitCollections;
    }

    private Class determineWhichClassDefinesField(HierarchicalStreamReader reader) {
        String attributeName = mapper.aliasForSystemAttribute("defined-in");
        String definedIn = attributeName == null ? null : reader.getAttribute(attributeName);
        return definedIn == null ? null : mapper.realClass(definedIn);
    }

    protected Object instantiateNewInstance(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String attributeName = mapper.aliasForSystemAttribute("resolves-to");
        String readResolveValue = attributeName == null ? null : reader.getAttribute(attributeName);
        Object currentObject = context.currentObject();
        if (currentObject != null) {
            return currentObject;
        } else if (readResolveValue != null) {
            return reflectionProvider.newInstance(mapper.realClass(readResolveValue));
        } else {
            return reflectionProvider.newInstance(context.getRequiredType());
        }
    }

    private Class determineType(HierarchicalStreamReader reader, boolean validField, Object result, String fieldName, Class definedInCls) {
        String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
        if (!validField) {
            Class itemType = mapper.getItemTypeForItemFieldName(result.getClass(), fieldName);
            if (itemType != null) {
                return itemType;
            } else {
                String originalNodeName = reader.getNodeName();
                if (definedInCls == null) {
                    for(definedInCls = result.getClass(); definedInCls != null; definedInCls = definedInCls.getSuperclass()) {
                        if (!mapper.shouldSerializeMember(definedInCls, originalNodeName)) {
                            return null;
                        }
                    }
                }
                try {
                    return mapper.realClass(originalNodeName);
                } catch (CannotResolveClassException e) {
                    throw new UnknownFieldException(result.getClass().getName(), fieldName);
                }
            }
        } else {
            if (classAttribute != null) {
                return mapper.realClass(classAttribute);
            } 
            return mapper.defaultImplementationOf(reflectionProvider.getFieldType(result, fieldName, definedInCls));
        }
    }
    
    private Object readResolve() {
        serializationMethodInvoker = new SerializationMethodInvoker();
        return this;
    }

    public static class DuplicateFieldException extends ConversionException {
        public DuplicateFieldException(String msg) {
            super(msg);
            add("duplicate-field", msg);
        }
    }

    public static class UnknownFieldException extends ConversionException {
        public UnknownFieldException(String type, String field) {
            super("No such field " + type + "." + field);
            add("field", field);
        }
    }
    
    private static class FieldInfo {
        final String fieldName;
        final Class type;
        final Class definedIn;
        final Object value;

        FieldInfo(String fieldName, Class type, Class definedIn, Object value) {
            this.fieldName = fieldName;
            this.type = type;
            this.definedIn = definedIn;
            this.value = value;
        }
    }
}
