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
        super(new ImmutableTypesMapper(new DefaultImplementationsMapper(new XmlFriendlyClassMapper(new ArrayMapper(new DynamicProxyMapper(new AliasingMapper(new OldClassMapper())))))));
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
            // the $ used in inner class names is illegal as an xml element getNodeName
            String result = type.getName().replace('$', '-');
            if (result.charAt(0) == '-') {
                // special case for classes named $Blah with no package; <-Blah> is illegal XML
                result = "default" + result;
            }
            return result;
        }

        public Class lookupType(String elementName) {
            final String key = elementName;
            if (lookupTypeCache.containsKey(key)) {
                return (Class) lookupTypeCache.get(key);
            }

            // the $ used in inner class names is illegal as an xml element getNodeName
            elementName = elementName.replace('-', '$');
            if (elementName.startsWith("default$")) {
                // special case for classes named $Blah with no package; <-Blah> is illegal XML
                elementName = elementName.substring(7);
            }

            Class result;

            try {
                result = classLoader.loadClass(elementName);
            } catch (ClassNotFoundException e) {
                throw new CannotResolveClassException(elementName + " : " + e.getMessage());
            }
            lookupTypeCache.put(key, result);
            return result;
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
