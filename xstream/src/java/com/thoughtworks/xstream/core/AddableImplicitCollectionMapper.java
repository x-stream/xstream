package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ImplicitCollectionMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AddableImplicitCollectionMapper implements ImplicitCollectionMapper {

    private Map itemTypeToFieldName = new HashMap(); // { definedIn (Class) -> { itemType (Class) -> fieldName (String) } }
    private Map fieldToItemType = new HashMap(); // { definedIn.fieldName (String) -> itemType (Class) }

    public String implicitCollectionFieldForType(Class definedIn, Class itemType) {
        Map itemTypeToFieldNameForClass = (Map) itemTypeToFieldName.get(definedIn);
        if (itemTypeToFieldNameForClass == null) {
            return null;
        } else {
            for (Iterator iterator = itemTypeToFieldNameForClass.keySet().iterator(); iterator.hasNext();) {
                Class itemTypeForFieldName = (Class) iterator.next();
                if (itemTypeForFieldName.isAssignableFrom(itemType)) {
                    return (String) itemTypeToFieldNameForClass.get(itemTypeForFieldName);
                }
            }
            return null;
        }
    }

    public boolean isImplicitCollectionField(Class definedIn, String fieldName) {
        return fieldToItemType.containsKey(definedIn.getName() + '.' + fieldName);
    }

    public void add(Class definedIn, String fieldName, Class itemType) {
        fieldToItemType.put(definedIn.getName() + '.' + fieldName, itemType);
        Map itemTypeToFieldNameForClass = (Map) itemTypeToFieldName.get(definedIn);
        if (itemTypeToFieldNameForClass == null) {
            itemTypeToFieldNameForClass = new HashMap();
            itemTypeToFieldName.put(definedIn, itemTypeToFieldNameForClass);
        }
        itemTypeToFieldNameForClass.put(itemType, fieldName);
    }

}
