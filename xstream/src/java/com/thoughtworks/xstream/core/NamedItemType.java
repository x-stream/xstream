package com.thoughtworks.xstream.core;


class NamedItemType {
    Class itemType;
    String itemFieldName;

    NamedItemType(Class itemType, String itemFieldName) {                                       
        this.itemType = itemType;
        this.itemFieldName = itemFieldName;
    }


    public boolean equals(Object obj) {
        if (obj instanceof NamedItemType) {
            NamedItemType b = (NamedItemType) obj;
            return itemType.equals(b.itemType)
                && isEquals(itemFieldName, b.itemFieldName);
        } else{
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