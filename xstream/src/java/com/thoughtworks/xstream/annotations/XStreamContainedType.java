package com.thoughtworks.xstream.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the value of objects contained by a collection. 
 * This annotation can only be applied to class attributes, 
 * but is only effective on collections and collection specializations. 
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XStreamContainedType {
    
}
