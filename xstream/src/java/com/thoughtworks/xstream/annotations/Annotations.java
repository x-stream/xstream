package com.thoughtworks.xstream.annotations;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Contains utility methods that enable to configure an XStream instance with class and field
 * aliases, based on a class decorated with annotations defined in this package.
 * 
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 * @author J&ouml;rg Schaible
 * @deprecated since upcoming, use {@link XStream#processAnnotations(Class[])}
 */
@Deprecated
public class Annotations {
    /**
     * Collection of visited types.
     */
    private static final Set<Class<?>> visitedTypes = new HashSet<Class<?>>();

    /**
     * This class is not instantiable
     */
    private Annotations() {
    }

    /**
     * Configures aliases on the specified XStream object based on annotations that decorate the
     * specified class. It will recursively invoke itself for each field annotated with
     * XStreamContainedType. If a field containing such annotation is parameterized, a recursive
     * call for each of its parameters type will be made.
     * 
     * @param topLevelClasses the class for which the XStream object is configured. This class
     *                is expected to be decorated with annotations defined in this package.
     * @param xstream the XStream object that will be configured
     * @deprecated since upcoming, use {@link XStream#processAnnotations(Class[])}
     */
    @Deprecated
    public static synchronized void configureAliases(XStream xstream,
        Class<?> ... topLevelClasses) {
        xstream.processAnnotations(topLevelClasses);
    }
}
