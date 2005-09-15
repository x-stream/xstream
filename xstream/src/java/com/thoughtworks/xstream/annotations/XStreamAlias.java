package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to define an XStream class or field value. This annotation should only be used with classes and fields
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface XStreamAlias {
    /**
     * The value of the class or field value
     */
    public String value();
    public Class<?> impl() default Void.class; //Use Void to denote as Null
}
