package com.thoughtworks.xstream.annotations;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamContainedType;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Contains utility methods that enable to configure an XStream instance with class and field aliases, based on
 * a class decorated with annotations defined in this package.
 *
 * @author Emil Kirschner
 */
public class XStreamConfig {
    private static final Set configuredTypes = new HashSet();

    /**
     * This class is not instantiable
     */
    private XStreamConfig() {
    }

    /**
     * Configures aliases on the specified XStream object based on annotations that decorate the specified class.
     *
     * @param topLevelClass the class for which the XStream object is configured. 
     * This class is expected to be decorated with annotations defined in this package.
     * @param xstream the XStream object that will be configured
     */
    public static synchronized void configureAliases(Class topLevelClass, XStream xstream) {
        configuredTypes.clear();
        configureClass(topLevelClass, xstream);
    }

    private static synchronized void configureClass(Class configurableClass, XStream xstream) {
        if (configuredTypes.contains(configurableClass))
            return;

        XStreamAlias XStreamAlias = (XStreamAlias) configurableClass.getAnnotation(XStreamAlias.class);
        if (XStreamAlias != null)
            xstream.alias(XStreamAlias.value(), configurableClass);

        configuredTypes.add(configurableClass);

        Field[] fields = configurableClass.getDeclaredFields();
        for (Field field : fields) {
            XStreamAlias fieldXStreamAlias = (XStreamAlias) field.getAnnotation(XStreamAlias.class);
            if (fieldXStreamAlias != null)
                xstream.aliasField(fieldXStreamAlias.value(), configurableClass, field.getName());
            Class fieldType = field.getType();
            if (Collection.class.isAssignableFrom(fieldType)) {
                XStreamContainedType XStreamContainedType = (XStreamContainedType) field.getAnnotation(XStreamContainedType.class);
                if (XStreamContainedType != null) {
                    Class containedClass = XStreamContainedType.value();
                    configureClass(containedClass, xstream);
                }
            } else if (!field.getType().isPrimitive()) {
                configureClass(field.getType(), xstream);
            }
        }

        Class superClass = configurableClass.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass))
            configureClass(superClass, xstream);
    }
}
