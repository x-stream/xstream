package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ImplicitCollectionDef;
import com.thoughtworks.xstream.alias.ImplicitCollectionMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AddableImplicitCollectionMapper implements ImplicitCollectionMapper {

    // { definedIn (Class) -> (ImplicitCollectionMapperForClass) }
    private Map classNameToMapper = Collections.synchronizedMap(new HashMap()); 

    private ImplicitCollectionMapperForClass getMapper(Class definedIn) {
        return (ImplicitCollectionMapperForClass) classNameToMapper.get(definedIn);
    }

    private ImplicitCollectionMapperForClass getOrCreateMapper(Class definedIn) {
        ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
        if (mapper == null) {
            mapper = new ImplicitCollectionMapperForClass(definedIn);
            classNameToMapper.put(definedIn, mapper);
        }
        return mapper;
    }
    
    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
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

    public ImplicitCollectionDef getImplicitCollectionDefForFieldName(Class definedIn, String fieldName) {
        ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
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
        ImplicitCollectionMapperForClass mapper = getOrCreateMapper(definedIn);
        mapper.add(new ImplicitCollectionDefImpl(fieldName, itemType, itemFieldName));
    }

    private static class ImplicitCollectionMapperForClass {
        //private Class definedIn;
        private Map namedItemTypeToDef = new HashMap(); // { (NamedItemType) -> (ImplicitCollectionDefImpl) }
        private Map itemFieldNameToDef = new HashMap(); // { itemFieldName (String) -> (ImplicitCollectionDefImpl) }
        private Map fieldNameToDef = new HashMap(); // { fieldName (String) -> (ImplicitCollectionDefImpl) }

        public ImplicitCollectionMapperForClass(Class definedIn) {
            //this.definedIn = definedIn;
        }

        public String getFieldNameForItemTypeAndName(Class itemType, String itemFieldName) {
            ImplicitCollectionDefImpl unnamed = null;
            for (Iterator iterator = namedItemTypeToDef.keySet().iterator(); iterator.hasNext();) {
                NamedItemType itemTypeForFieldName = (NamedItemType) iterator.next();
                if (itemTypeForFieldName.itemType.isAssignableFrom(itemType)) {
                    ImplicitCollectionDefImpl def = (ImplicitCollectionDefImpl) namedItemTypeToDef.get(itemTypeForFieldName);
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
            ImplicitCollectionDefImpl def = getImplicitCollectionDefByItemFieldName(itemFieldName);
            if (def != null) {
                return def.getItemType();
            } else {
                return null;
            }
        }

        private ImplicitCollectionDefImpl getImplicitCollectionDefByItemFieldName(String itemFieldName) {
            if (itemFieldName == null) {
                return null;
            } else {
                return (ImplicitCollectionDefImpl) itemFieldNameToDef.get(itemFieldName);
            }
        }

        public ImplicitCollectionDefImpl getImplicitCollectionDefByFieldName(String fieldName) {
        	return (ImplicitCollectionDefImpl) fieldNameToDef.get(fieldName);
        }

        public ImplicitCollectionDef getImplicitCollectionDefForFieldName(String fieldName) {
            return (ImplicitCollectionDef) fieldNameToDef.get(fieldName);
        }

        public void add(ImplicitCollectionDefImpl def) {
            fieldNameToDef.put(def.getFieldName(), def);
            namedItemTypeToDef.put(def.createNamedItemType(), def);
            if (def.getItemFieldName() != null) {
                itemFieldNameToDef.put(def.getItemFieldName(), def);
            }
        }

    }
}
