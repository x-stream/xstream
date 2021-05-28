package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking {@link String}, all {@link java.util.Collection} and {@link java.util.Map} types
 * as excluded if the object value is null or the object is empty.
 * For emptiness check corresponded method from each of supported types is used
 * 
 * @author Ruslan Sibgatullin
 * @since 1.5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XStreamExcludeEmpty {
}
