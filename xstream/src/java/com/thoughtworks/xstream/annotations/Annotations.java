package com.thoughtworks.xstream.annotations;

import com.thoughtworks.xstream.XStream;


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
     * This class is not instantiable
     */
    private Annotations() {
    }

    /**
     * Configures aliases on the specified XStream object based on annotations that decorate the
     * specified class. It will recursively invoke itself. If a field is parameterized, a
     * recursive call for each of its parameters type will be made.
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
