package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

public abstract class MapperWrapper implements ClassMapper {

    private final ClassMapper wrapped;

    public MapperWrapper(ClassMapper wrapped) {
        this.wrapped = wrapped;
    }

    public String serializedClass(Class type) {
        return wrapped.serializedClass(type);
    }

    public Class realClass(String elementName) {
        return wrapped.realClass(elementName);
    }

    public String serializedMember(Class type, String memberName) {
        return wrapped.serializedMember(type, memberName);
    }

    public String realMember(Class type, String serialized) {
        return wrapped.realMember(type, serialized);
    }

    public String mapNameFromXML(String xmlName) {
        return wrapped.mapNameFromXML(xmlName);
    }

    public String mapNameToXML(String javaName) {
        return wrapped.mapNameToXML(javaName);
    }

    public boolean isImmutableValueType(Class type) {
        return wrapped.isImmutableValueType(type);
    }

    public Class defaultImplementationOf(Class type) {
        return wrapped.defaultImplementationOf(type);
    }

    public String attributeForClassDefiningField() {
        return wrapped.attributeForClassDefiningField();
    }

    public String attributeForImplementationClass() {
        return wrapped.attributeForImplementationClass();
    }

    public String attributeForReadResolveField() {
        return wrapped.attributeForReadResolveField();
    }

    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
        return wrapped.getFieldNameForItemTypeAndName(definedIn, itemType, itemFieldName);
    }

    public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
        return wrapped.getItemTypeForItemFieldName(definedIn, itemFieldName);
    }

    public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
        return wrapped.getImplicitCollectionDefForFieldName(itemType, fieldName);
    }

    /**
     * @deprecated As of 1.1.1, use {@link #defaultImplementationOf(Class)}
     */
    public Class lookupDefaultType(Class baseType) {
        return defaultImplementationOf(baseType);
    }

    public String lookupName(Class type) {
        return serializedClass(type);
    }

    public Class lookupType(String elementName) {
        return realClass(elementName);
    }

    /**
     * @deprecated As of 1.1.1, use {@link com.thoughtworks.xstream.mapper.AliasingMapper#addAlias(String, Class)} for creating an alias and
     *             {@link DefaultImplementationsMapper#addDefaultImplementation(Class, Class)} for specifiny a
     *             default implementation.
     */
    public void alias(String elementName, Class type, Class defaultImplementation) {
        AliasingMapper aliasingMapper = (AliasingMapper) findWrapped(AliasingMapper.class);
        if (aliasingMapper == null) {
            throw new UnsupportedOperationException("ClassMapper.alias() longer supported. Use AliasingMapper.alias() instead.");
        } else {
            aliasingMapper.addAlias(elementName, type);
        }
        if (defaultImplementation != null && defaultImplementation != type) {
            DefaultImplementationsMapper defaultImplementationsMapper = (DefaultImplementationsMapper) findWrapped(DefaultImplementationsMapper.class);
            if (defaultImplementationsMapper == null) {
                throw new UnsupportedOperationException("ClassMapper.alias() longer supported. Use DefaultImplementatoinsMapper.add() instead.");
            } else {
                defaultImplementationsMapper.addDefaultImplementation(defaultImplementation, type);
            }
        }
    }

    private ClassMapper findWrapped(Class typeOfMapper) {
        ClassMapper current = this;
        while (true) {
            if (current.getClass().isAssignableFrom(typeOfMapper)) {
                return current;
            } else if (current instanceof MapperWrapper) {
                MapperWrapper wrapper = (MapperWrapper) current;
                current = wrapper.wrapped;
            } else {
                return null;
            }
        }
    }
}
