/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define an XStream class or field alias.
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 * @see com.thoughtworks.xstream.XStream#alias(String, Class)
 * @see com.thoughtworks.xstream.XStream#alias(String, Class, Class)
 * @see com.thoughtworks.xstream.XStream#addDefaultImplementation(Class, Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface XStreamAlias {
    /**
     * The name of the class or field alias.
     */
    public String value();
    /**
     * A possible default implementation if the annotated type is an interface.
     */
    public Class<?> impl() default Void.class; //Use Void to denote as Null
}
