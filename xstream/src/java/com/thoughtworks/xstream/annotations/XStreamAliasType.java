/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * Created on 12.07.2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation used to define an XStream type alias.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 * @see com.thoughtworks.xstream.XStream#aliasType(String, Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XStreamAliasType {
    /**
     * The name of the type alias.
     */
    public String value();
}
