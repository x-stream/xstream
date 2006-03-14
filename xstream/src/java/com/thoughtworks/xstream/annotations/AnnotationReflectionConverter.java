package com.thoughtworks.xstream.annotations;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * ReflectionConverter which uses an AnnotationProvider to marshall and unmarshall
 * fields based on the annotated converters.
 *
 * @author Guilherme Silveira
 * @author Mauro Talevi
 */
public class AnnotationReflectionConverter extends ReflectionConverter {

	private final AnnotationProvider annotationProvider;

	public AnnotationReflectionConverter(Mapper mapper,
			ReflectionProvider reflectionProvider,
			AnnotationProvider annotationProvider) {
		super(mapper, reflectionProvider);
		this.annotationProvider = annotationProvider;
	}

	protected void marshallField(final MarshallingContext context,
			Object newObj, Field field) {
		XStreamConverter annotation = annotationProvider.getAnnotation(field, XStreamConverter.class);
		if (annotation != null) {
			context.convertAnother(newObj, (Converter) reflectionProvider
					.newInstance(annotation.value()));
		} else {
			context.convertAnother(newObj);
		}
	}

	protected Object unmarshallField(final UnmarshallingContext context,
			final Object result, Class type, Field field) {
		XStreamConverter annotation = annotationProvider.getAnnotation(field, XStreamConverter.class);
		if (annotation != null) {
			return context.convertAnother(result, type,
					(Converter) reflectionProvider.newInstance(annotation
							.value()));
		} else {
			return context.convertAnother(result, type);
		}
	}

}
