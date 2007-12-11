/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. February 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.alias.ClassMapper;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ImplicitCollectionMapper extends MapperWrapper {

    public ImplicitCollectionMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #ImplicitCollectionMapper(Mapper)}
     */
    public ImplicitCollectionMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    // { definedIn (Class) -> (ImplicitCollectionMapperForClass) }
    private final Map classNameToMapper = new HashMap();

    private ImplicitCollectionMapperForClass getMapper(Class definedIn) {
        while (definedIn != null) {
            ImplicitCollectionMapperForClass mapper = (ImplicitCollectionMapperForClass)classNameToMapper
                .get(definedIn);
            if (mapper != null) {
                return mapper;
            }
            definedIn = definedIn.getSuperclass();
        }
        return null;
    }

    private ImplicitCollectionMapperForClass getOrCreateMapper(Class definedIn) {
        ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
        if (mapper == null) {
            mapper = new ImplicitCollectionMapperForClass();
            classNameToMapper.put(definedIn, mapper);
        }
        return mapper;
    }

    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType,
        String itemFieldName) {
        ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
        if (mapper != null) {
            return mapper.getFieldNameForItemTypeAndName(itemType, itemFieldName);
        } else {
            return null;
        }
    }

    public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
        ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
        if (mapper != null) {
            return mapper.getItemTypeForItemFieldName(itemFieldName);
        } else {
            return null;
        }
    }

    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType,
        String fieldName) {
        ImplicitCollectionMapperForClass mapper = getMapper(itemType);
        if (mapper != null) {
            return mapper.getImplicitCollectionDefForFieldName(fieldName);
        } else {
            return null;
        }
    }

    public void add(Class definedIn, String fieldName, Class itemType) {
        add(definedIn, fieldName, null, itemType);
    }

    public void add(Class definedIn, String fieldName, String itemFieldName, Class itemType) {
        Field field = null;
        while (definedIn != Object.class) {
            try {
                field = definedIn.getDeclaredField(fieldName);
                break;
            } catch (SecurityException e) {
                throw new InitializationException(
                    "Access denied for field with implicit collection", e);
            } catch (NoSuchFieldException e) {
                definedIn = definedIn.getSuperclass();
            }
        }
        if (field == null) {
            throw new InitializationException("No field \""
                + fieldName
                + "\" for implicit collection");
        } else if (!Collection.class.isAssignableFrom(field.getType())) {
            throw new InitializationException("Field \""
                + fieldName
                + "\" declares no collection");
        }
        ImplicitCollectionMapperForClass mapper = getOrCreateMapper(definedIn);
        mapper.add(new ImplicitCollectionMappingImpl(fieldName, itemType, itemFieldName));
    }

    private static class ImplicitCollectionMapperForClass {
        // { (NamedItemType) -> (ImplicitCollectionDefImpl) }
        private Map namedItemTypeToDef = new HashMap();
        // { itemFieldName (String) -> (ImplicitCollectionDefImpl) }
        private Map itemFieldNameToDef = new HashMap();
        // { fieldName (String) -> (ImplicitCollectionDefImpl) }
        private Map fieldNameToDef = new HashMap();

        public String getFieldNameForItemTypeAndName(Class itemType, String itemFieldName) {
            ImplicitCollectionMappingImpl unnamed = null;
            for (Iterator iterator = namedItemTypeToDef.keySet().iterator(); iterator.hasNext();) {
                NamedItemType itemTypeForFieldName = (NamedItemType)iterator.next();
                if (itemTypeForFieldName.itemType.isAssignableFrom(itemType)) {
                    ImplicitCollectionMappingImpl def = (ImplicitCollectionMappingImpl)namedItemTypeToDef
                        .get(itemTypeForFieldName);
                    if (def.getItemFieldName() != null) {
                        if (def.getItemFieldName().equals(itemFieldName)) {
                            return def.getFieldName();
                        }
                    } else {
                        unnamed = def;
                        if (itemFieldName == null) {
                            break;
                        }
                    }
                }
            }
            return unnamed != null ? unnamed.getFieldName() : null;
        }

        public Class getItemTypeForItemFieldName(String itemFieldName) {
            ImplicitCollectionMappingImpl def = getImplicitCollectionDefByItemFieldName(itemFieldName);
            if (def != null) {
                return def.getItemType();
            } else {
                return null;
            }
        }

        private ImplicitCollectionMappingImpl getImplicitCollectionDefByItemFieldName(
            String itemFieldName) {
            if (itemFieldName == null) {
                return null;
            } else {
                return (ImplicitCollectionMappingImpl)itemFieldNameToDef.get(itemFieldName);
            }
        }

        public ImplicitCollectionMappingImpl getImplicitCollectionDefByFieldName(
            String fieldName) {
            return (ImplicitCollectionMappingImpl)fieldNameToDef.get(fieldName);
        }

        public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(String fieldName) {
            return (ImplicitCollectionMapping)fieldNameToDef.get(fieldName);
        }

        public void add(ImplicitCollectionMappingImpl def) {
            fieldNameToDef.put(def.getFieldName(), def);
            namedItemTypeToDef.put(def.createNamedItemType(), def);
            if (def.getItemFieldName() != null) {
                itemFieldNameToDef.put(def.getItemFieldName(), def);
            }
        }

    }

    private static class ImplicitCollectionMappingImpl implements ImplicitCollectionMapping {
        private String fieldName;
        private String itemFieldName;
        private Class itemType;

        ImplicitCollectionMappingImpl(String fieldName, Class itemType, String itemFieldName) {
            this.fieldName = fieldName;
            this.itemFieldName = itemFieldName;
            this.itemType = itemType == null ? Object.class : itemType;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ImplicitCollectionMappingImpl) {
                ImplicitCollectionMappingImpl b = (ImplicitCollectionMappingImpl)obj;
                return fieldName.equals(b.fieldName)
                    && isEquals(itemFieldName, b.itemFieldName);
            } else {
                return false;
            }
        }

        public NamedItemType createNamedItemType() {
            return new NamedItemType(itemType, itemFieldName);
        }

        private static boolean isEquals(Object a, Object b) {
            if (a == null) {
                return b == null;
            } else {
                return a.equals(b);
            }
        }

        public int hashCode() {
            int hash = fieldName.hashCode();
            if (itemFieldName != null) {
                hash += itemFieldName.hashCode() << 7;
            }
            return hash;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getItemFieldName() {
            return itemFieldName;
        }

        public Class getItemType() {
            return itemType;
        }
    }

    private static class NamedItemType {
        Class itemType;
        String itemFieldName;

        NamedItemType(Class itemType, String itemFieldName) {
            this.itemType = itemType;
            this.itemFieldName = itemFieldName;
        }

        public boolean equals(Object obj) {
            if (obj instanceof NamedItemType) {
                NamedItemType b = (NamedItemType)obj;
                return itemType.equals(b.itemType) && isEquals(itemFieldName, b.itemFieldName);
            } else {
                return false;
            }
        }

        private static boolean isEquals(Object a, Object b) {
            if (a == null) {
                return b == null;
            } else {
                return a.equals(b);
            }
        }

        public int hashCode() {
            int hash = itemType.hashCode() << 7;
            if (itemFieldName != null) {
                hash += itemFieldName.hashCode();
            }
            return hash;
        }
    }
}
