package com.thoughtworks.xstream.alias;

/**
 * A default collection is a special field of an object where any child nodes that do not match with fields will be added.
 *
 * @author Joe Walnes
 */
public interface ImplicitCollectionMapper {

    /**
     * Get the name of the field that acts as the default collection for an object, or return null if there is none.
     * @param definedIn owning type
     * @param itemType item type
     * @param itemFieldName optional item element name
     */
    String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName);

    Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName);
    
    ImplicitCollectionDef getImplicitCollectionDefForFieldName(Class definedIn, String fieldName);

}
