package com.thoughtworks.xstream.alias;

public abstract class ClassMapperWrapper implements ClassMapper {

    private final ClassMapper wrapped;

    public ClassMapperWrapper(ClassMapper wrapped) {
        this.wrapped = wrapped;
    }

    public String lookupName(Class type) {
        return wrapped.lookupName(type);
    }

    public Class lookupType(String elementName) {
        return wrapped.lookupType(elementName);
    }

    public Class lookupDefaultType(Class baseType) {
        return wrapped.lookupDefaultType(baseType);
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        wrapped.alias(elementName, type, defaultImplementation);
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

}
