package com.thoughtworks.xstream.mapper;


/**
 * Default mapper implementation with 'vanilla' functionality. To build up the functionality required, wrap this mapper
 * with other mapper implementations.
 *
 * @author Joe Walnes
 */
public class DefaultMapper extends MapperWrapper {

    private final ClassLoader classLoader;
    private final String classAttributeIdentifier;

    public DefaultMapper(ClassLoader classLoader) {
        this(classLoader, "class");
    }

    public DefaultMapper(ClassLoader classLoader, String classAttributeIdentifier) {
        super((Mapper)null);
        this.classLoader = classLoader;
        this.classAttributeIdentifier = classAttributeIdentifier == null ? "class" : classAttributeIdentifier;
    }

    public String serializedClass(Class type) {
        return type.getName();
    }

    public Class realClass(String elementName) {
        try {
            return classLoader.loadClass(elementName);
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elementName + " : " + e.getMessage());
        }
    }

    public Class defaultImplementationOf(Class type) {
        return type;
    }

    public String attributeForClassDefiningField() {
        return "defined-in";
    }

    public String attributeForReadResolveField() {
        return "resolves-to";
    }

    public String attributeForEnumType() {
        return "enum-type";
    }

    public String attributeForImplementationClass() {
        return classAttributeIdentifier;
    }

    public boolean isImmutableValueType(Class type) {
        return false;
    }

    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
        return null;
    }

    public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
        return null;
    }

    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
        return null;
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        return true;
    }

    public String lookupName(Class type) {
        return serializedClass(type);
    }

    public Class lookupType(String elementName) {
        return realClass(elementName);
    }

    public String serializedMember(Class type, String memberName) {
        return memberName;
    }

    public String realMember(Class type, String serialized) {
        return serialized;
    }
}
