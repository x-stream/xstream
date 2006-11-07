package com.thoughtworks.xstream.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.StreamException;
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
			
			Class<? extends Converter> type = annotation.value();
			context.convertAnother(newObj, newInstance(type));
		} else {
			context.convertAnother(newObj);
		}
	}

	protected Object unmarshallField(final UnmarshallingContext context,
			final Object result, Class type, Field field) {
		XStreamConverter annotation = annotationProvider.getAnnotation(field, XStreamConverter.class);
		if (annotation != null) {
			Class<? extends Converter> converterType = annotation.value();
			Converter converter = newInstance(converterType);
			return context.convertAnother(result, type, converter);
		} else {
			return context.convertAnother(result, type);
		}
	}

	/**
	 * Instantiates a converter using its default constructor.
	 * @param converterType	the converter type to instantiate
	 * @return	the new instance
	 */
	private Converter newInstance(Class<? extends Converter> converterType) {
		Converter converter ;
		try {
			converter = converterType.getConstructor().newInstance();
		} catch (InvocationTargetException e) {
			throw new StreamException(e.getCause());
		} catch (Exception e) {
			throw new StreamException(e);
		}
		return converter;
	}

}
