package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

public class ImplementedByProcessor implements TypeConfigProcessor {

	private final Class defaultImplementation;

	public ImplementedByProcessor(Class defaultImplementation) {
		this.defaultImplementation = defaultImplementation;
	}

	public void process(XStream instance, Class type) {
		instance.addDefaultImplementation(defaultImplementation, type);
	}

}
