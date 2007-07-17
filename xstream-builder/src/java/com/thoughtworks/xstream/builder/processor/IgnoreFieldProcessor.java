package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

/**
 * A processor which omits a field.
 *
 * @author Guilherme Silveira
 */
public class IgnoreFieldProcessor implements TypeConfigProcessor {

	private final String fieldName;

	public IgnoreFieldProcessor(String fieldName) {
		this.fieldName = fieldName;
	}

	public void process(XStream instance, Class type) {
		instance.omitField(type, fieldName);
	}

}
