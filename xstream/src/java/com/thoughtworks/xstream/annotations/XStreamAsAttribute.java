package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that a field should be serialized as an attribute.
 * 
 * @author Guilherme Silveira
 * since 1.2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface XStreamAsAttribute {
}
