package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking a field as an implicit collection.
 *
 * @author Lucio Benfante
 * since 1.2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XStreamImplicit {
    /**
     * Element name of the implicit collection
     */
    String itemFieldName() default "";
}
