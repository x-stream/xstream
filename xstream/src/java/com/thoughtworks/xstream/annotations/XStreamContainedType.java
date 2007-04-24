package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to notify Annotations.configureAliases that it should recursively invoke itself for
 * all parameterized types of this field.
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XStreamContainedType {

}
