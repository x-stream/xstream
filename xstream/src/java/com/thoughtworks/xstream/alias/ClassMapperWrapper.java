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

    /**
     * @deprecated As of 1.1.1, use {@link #defaultImplementationOf(Class)}
     */
    public Class lookupDefaultType(Class baseType) {
        return defaultImplementationOf(baseType);
    }

    /**
     * @deprecated As of 1.1.1, use {@link AliasingMapper#addAlias(String, Class)} for creating an alias and
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
            } else if (current instanceof ClassMapperWrapper) {
                ClassMapperWrapper wrapper = (ClassMapperWrapper) current;
                current = wrapper.wrapped;
            } else {
                return null;
            }
        }
    }
}
