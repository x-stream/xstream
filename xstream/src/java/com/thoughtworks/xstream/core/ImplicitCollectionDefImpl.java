package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ImplicitCollectionDef;

public class ImplicitCollectionDefImpl implements ImplicitCollectionDef {
    private String fieldName;
    private String itemFieldName;
    private Class itemType;

    ImplicitCollectionDefImpl(String fieldName, Class itemType, String itemFieldName) {
        this.fieldName = fieldName;
        this.itemFieldName = itemFieldName;
        this.itemType = itemType;
    }


	public boolean equals(Object obj) {
        if (obj instanceof ImplicitCollectionDefImpl) {
        	ImplicitCollectionDefImpl b = (ImplicitCollectionDefImpl) obj;
            return fieldName.equals(b.fieldName)
                && isEquals(itemFieldName, b.itemFieldName);
        } else{
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