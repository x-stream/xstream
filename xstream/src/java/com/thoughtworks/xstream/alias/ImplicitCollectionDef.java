package com.thoughtworks.xstream.alias;

/**
 * ImplicitCollectionDef holds information about an implicit collection.
 */
public interface ImplicitCollectionDef {
    public String getFieldName();

    public String getItemFieldName();

    public Class getItemType();
}
