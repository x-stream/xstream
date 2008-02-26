/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. August 2005 by Mauro Talevi
 */
package com.thoughtworks.xstream.annotations;

import com.thoughtworks.xstream.XStream;


/**
 * Contains utility methods that enable to configure an XStream instance with class and field
 * aliases, based on a class decorated with annotations defined in this package.
 * 
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 * @deprecated since 1.3, use {@link XStream#processAnnotations(Class[])}
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
     * @deprecated since 1.3, use {@link XStream#processAnnotations(Class[])}
     */
    @Deprecated
    public static synchronized void configureAliases(XStream xstream,
        Class<?> ... topLevelClasses) {
        xstream.processAnnotations(topLevelClasses);
    }
}
