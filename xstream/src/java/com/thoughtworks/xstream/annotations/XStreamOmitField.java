package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Declares a field to be omitted. The result is the same as invoking the method
 * omitField in a XStream instance.
 * 
 * @author Chung-Onn Cheong
 * @author Guilherme Silveira
 * @since 1.2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @ interface XStreamOmitField {
}
