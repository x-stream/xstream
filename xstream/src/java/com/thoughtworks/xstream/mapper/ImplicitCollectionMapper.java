/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2012, 2013, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 16. February 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.Primitives;


public class ImplicitCollectionMapper extends MapperWrapper {

    private final ReflectionProvider reflectionProvider;

    public ImplicitCollectionMapper(final Mapper wrapped, final ReflectionProvider reflectionProvider) {
        super(wrapped);
        this.reflectionProvider = reflectionProvider;
    }

    private final Map<Class<?>, ImplicitCollectionMapperForClass> classNameToMapper = new HashMap<>();

    private ImplicitCollectionMapperForClass getMapper(final Class<?> declaredFor, final String fieldName) {
        Class<?> definedIn = declaredFor;
        final Field field = fieldName != null ? reflectionProvider.getFieldOrNull(definedIn, fieldName) : null;
        final Class<?> inheritanceStop = field != null ? field.getDeclaringClass() : null;
        while (definedIn != null) {
            final ImplicitCollectionMapperForClass mapper = classNameToMapper.get(definedIn);
            if (mapper != null) {
                return mapper;
            }
            if (definedIn == inheritanceStop) {
                break;
            }
            definedIn = definedIn.getSuperclass();
        }
        return null;
    }

    private ImplicitCollectionMapperForClass getOrCreateMapper(final Class<?> definedIn) {
        ImplicitCollectionMapperForClass mapper = classNameToMapper.get(definedIn);
        if (mapper == null) {
            mapper = new ImplicitCollectionMapperForClass(definedIn);
            classNameToMapper.put(definedIn, mapper);
        }
        return mapper;
    }

    @Override
    public String getFieldNameForItemTypeAndName(final Class<?> definedIn, final Class<?> itemType,
            final String itemFieldName) {
        final ImplicitCollectionMapperForClass mapper = getMapper(definedIn, null);
        if (mapper != null) {
            return mapper.getFieldNameForItemTypeAndName(itemType, itemFieldName);
        } else {
            return null;
        }
    }

    @Override
    public Class<?> getItemTypeForItemFieldName(final Class<?> definedIn, final String itemFieldName) {
        final ImplicitCollectionMapperForClass mapper = getMapper(definedIn, null);
        if (mapper != null) {
            return mapper.getItemTypeForItemFieldName(itemFieldName);
        } else {
            return null;
        }
    }

    @Override
    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(final Class<?> itemType,
            final String fieldName) {
        final ImplicitCollectionMapperForClass mapper = getMapper(itemType, fieldName);
        if (mapper != null) {
            return mapper.getImplicitCollectionDefForFieldName(fieldName);
        } else {
            return null;
        }
    }

    public void add(final Class<?> definedIn, final String fieldName, final Class<?> itemType) {
        add(definedIn, fieldName, null, itemType);
    }

    public void add(final Class<?> definedIn, final String fieldName, final String itemFieldName,
            final Class<?> itemType) {
        add(definedIn, fieldName, itemFieldName, itemType, null);
    }

    public void add(final Class<?> definedIn, final String fieldName, final String itemFieldName, Class<?> itemType,
            final String keyFieldName) {
        Field field = null;
        if (definedIn != null) {
            Class<?> declaredIn = definedIn;
            while (declaredIn != Object.class) {
                try {
                    field = declaredIn.getDeclaredField(fieldName);
                    if (!Modifier.isStatic(field.getModifiers())) {
                        break;
                    }
                    field = null;
                } catch (final SecurityException e) {
                    throw new InitializationException("Access denied for field with implicit collection", e);
                } catch (final NoSuchFieldException e) {
                    declaredIn = declaredIn.getSuperclass();
                }
            }
        }
        if (field == null) {
            throw new InitializationException("No field \"" + fieldName + "\" for implicit collection");
        } else if (Map.class.isAssignableFrom(field.getType())) {
            if (itemFieldName == null && keyFieldName == null) {
                itemType = Map.Entry.class;
            }
        } else if (!Collection.class.isAssignableFrom(field.getType())) {
            final Class<?> fieldType = field.getType();
            if (!fieldType.isArray()) {
                throw new InitializationException("Field \"" + fieldName + "\" declares no collection or array");
            } else {
                Class<?> componentType = fieldType.getComponentType();
                componentType = componentType.isPrimitive() ? Primitives.box(componentType) : componentType;
                if (itemType == null) {
                    itemType = componentType;
                } else {
                    itemType = itemType.isPrimitive() ? Primitives.box(itemType) : itemType;
                    if (!componentType.isAssignableFrom(itemType)) {
                        throw new InitializationException("Field \""
                            + fieldName
                            + "\" declares an array, but the array type is not compatible with "
                            + itemType.getName());

                    }
                }
            }
        }
        final ImplicitCollectionMapperForClass mapper = getOrCreateMapper(definedIn);
        mapper.add(new ImplicitCollectionMappingImpl(fieldName, itemType, itemFieldName, keyFieldName));
    }

    private class ImplicitCollectionMapperForClass {
        private final Class<?> definedIn;
        private final Map<NamedItemType, ImplicitCollectionMappingImpl> namedItemTypeToDef = new HashMap<>();
        private final Map<String, ImplicitCollectionMappingImpl> itemFieldNameToDef = new HashMap<>();
        private final Map<String, ImplicitCollectionMappingImpl> fieldNameToDef = new HashMap<>();

        ImplicitCollectionMapperForClass(final Class<?> definedIn) {
            this.definedIn = definedIn;
        }

        public String getFieldNameForItemTypeAndName(final Class<?> itemType, final String itemFieldName) {
            ImplicitCollectionMappingImpl unnamed = null;
            for (final NamedItemType itemTypeForFieldName : namedItemTypeToDef.keySet()) {
                final ImplicitCollectionMappingImpl def = namedItemTypeToDef.get(itemTypeForFieldName);
                if (itemType == Mapper.Null.class) {
                    unnamed = def;
                    break;
                } else if (itemTypeForFieldName.itemType.isAssignableFrom(itemType)) {
                    if (def.getItemFieldName() != null) {
                        if (def.getItemFieldName().equals(itemFieldName)) {
                            return def.getFieldName();
                        }
                    } else {
                        if (unnamed == null
                            || unnamed.getItemType() == null
                            || def.getItemType() != null && unnamed.getItemType().isAssignableFrom(def.getItemType())) {
                            unnamed = def;
                        }
                    }
                }
            }
            if (unnamed != null) {
                return unnamed.getFieldName();
            } else {
                final ImplicitCollectionMapperForClass mapper = getMapper(definedIn.getSuperclass(), null);
                return mapper != null ? mapper.getFieldNameForItemTypeAndName(itemType, itemFieldName) : null;
            }
        }

        public Class<?> getItemTypeForItemFieldName(final String itemFieldName) {
            final ImplicitCollectionMappingImpl def = getImplicitCollectionDefByItemFieldName(itemFieldName);
            if (def != null) {
                return def.getItemType();
            } else {
                final ImplicitCollectionMapperForClass mapper = getMapper(definedIn.getSuperclass(), null);
                return mapper != null ? mapper.getItemTypeForItemFieldName(itemFieldName) : null;
            }
        }

        private ImplicitCollectionMappingImpl getImplicitCollectionDefByItemFieldName(final String itemFieldName) {
            if (itemFieldName == null) {
                return null;
            } else {
                final ImplicitCollectionMappingImpl mapping = itemFieldNameToDef.get(itemFieldName);
                if (mapping != null) {
                    return mapping;
                } else {
                    final ImplicitCollectionMapperForClass mapper = getMapper(definedIn.getSuperclass(), null);
                    return mapper != null ? mapper.getImplicitCollectionDefByItemFieldName(itemFieldName) : null;
                }
            }
        }

        public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(final String fieldName) {
            final ImplicitCollectionMapping mapping = fieldNameToDef.get(fieldName);
            if (mapping != null) {
                return mapping;
            } else {
                final ImplicitCollectionMapperForClass mapper = getMapper(definedIn.getSuperclass(), null);
                return mapper != null ? mapper.getImplicitCollectionDefForFieldName(fieldName) : null;
            }
        }

        public void add(final ImplicitCollectionMappingImpl def) {
            fieldNameToDef.put(def.getFieldName(), def);
            namedItemTypeToDef.put(def.createNamedItemType(), def);
            if (def.getItemFieldName() != null) {
                itemFieldNameToDef.put(def.getItemFieldName(), def);
            }
        }

    }

    private static class ImplicitCollectionMappingImpl implements ImplicitCollectionMapping {
        private final String fieldName;
        private final String itemFieldName;
        private final Class<?> itemType;
        private final String keyFieldName;

        ImplicitCollectionMappingImpl(
                final String fieldName, final Class<?> itemType, final String itemFieldName,
                final String keyFieldName) {
            this.fieldName = fieldName;
            this.itemFieldName = itemFieldName;
            this.itemType = itemType;
            this.keyFieldName = keyFieldName;
        }

        public NamedItemType createNamedItemType() {
            return new NamedItemType(itemType, itemFieldName);
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String getItemFieldName() {
            return itemFieldName;
        }

        @Override
        public Class<?> getItemType() {
            return itemType;
        }

        @Override
        public String getKeyFieldName() {
            return keyFieldName;
        }
    }

    private static class NamedItemType {
        Class<?> itemType;
        String itemFieldName;

        NamedItemType(final Class<?> itemType, final String itemFieldName) {
            this.itemType = itemType == null ? Object.class : itemType;
            this.itemFieldName = itemFieldName;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof NamedItemType) {
                final NamedItemType b = (NamedItemType)obj;
                return itemType.equals(b.itemType) && isEquals(itemFieldName, b.itemFieldName);
            } else {
                return false;
            }
        }

        private static boolean isEquals(final Object a, final Object b) {
            if (a == null) {
                return b == null;
            } else {
                return a.equals(b);
            }
        }

        @Override
        public int hashCode() {
            int hash = itemType.hashCode() << 7;
            if (itemFieldName != null) {
                hash += itemFieldName.hashCode();
            }
            return hash;
        }
    }
}
