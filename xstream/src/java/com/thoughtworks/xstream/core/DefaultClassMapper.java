package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ArrayMapper;
import com.thoughtworks.xstream.alias.CannotResolveClassException;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.ClassMapperWrapper;
import com.thoughtworks.xstream.alias.DefaultImplementationsMapper;
import com.thoughtworks.xstream.alias.DynamicProxyMapper;
import com.thoughtworks.xstream.alias.ImmutableTypesMapper;
import com.thoughtworks.xstream.alias.XmlFriendlyClassMapper;
import com.thoughtworks.xstream.alias.AliasingMapper;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultClassMapper extends ClassMapperWrapper {

    public DefaultClassMapper() {
        super(new ImmutableTypesMapper(new DefaultImplementationsMapper(new ArrayMapper(new DynamicProxyMapper(new AliasingMapper(new XmlFriendlyClassMapper(new OldClassMapper())))))));
    }

    public static class OldClassMapper implements ClassMapper {

        private final ClassLoader classLoader;
        private final Map lookupTypeCache = Collections.synchronizedMap(new HashMap());

        public OldClassMapper() {
            this(new CompositeClassLoader());
        }

        public OldClassMapper(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        public String lookupName(Class type) {
            return type.getName();
        }

        public Class lookupType(String elementName) {
            final String key = elementName;
            if (lookupTypeCache.containsKey(key)) {
                return (Class) lookupTypeCache.get(key);
            }
            try {
                Class result = classLoader.loadClass(elementName);
                lookupTypeCache.put(key, result);
                return result;
            } catch (ClassNotFoundException e) {
                throw new CannotResolveClassException(elementName + " : " + e.getMessage());
            }
        }

        public Class lookupDefaultType(Class baseType) {
            return baseType;
        }

        public boolean isImmutableValueType(Class type) {
            return false;
        }

        public String mapNameFromXML(String xmlName) {
            return xmlName;
        }

        public String mapNameToXML(String javaName) {
            return javaName;
        }

        public void alias(String elementName, Class type, Class defaultImplementation) {
        }

    }

}
