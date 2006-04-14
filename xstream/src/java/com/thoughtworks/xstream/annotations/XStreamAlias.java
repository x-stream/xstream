package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define an XStream class or field value.
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface XStreamAlias {
    /**
     * The value of the class or field value
     */
    public String value();
    public Class<?> impl() default Void.class; //Use Void to denote as Null
}
