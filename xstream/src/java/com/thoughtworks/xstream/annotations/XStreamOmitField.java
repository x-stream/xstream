package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Declares a field as to be ommited. The result is the same as invoking the method
 * omitField in a xstream instance.
 * 
 * @author Chung-Onn Cheong
 * @author Guilherme Silveira
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @ interface XStreamOmitField {
}
