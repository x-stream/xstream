package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * An utility class to provide annotions from different sources
 *
 * @author Guilherme Silveira
 */
public class AnnotationProvider {

	/**
	 * Returns a field annotation based on an annotation type
	 *
	 * @param field the annotation Field
	 * @param annotationClass the annotation Class
     * @return The Annotation type
	 */
	public <T extends Annotation> T getAnnotation(Field field,
			Class<T> annotationClass) {
		return field.getAnnotation(annotationClass);
	}

}
