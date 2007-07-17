package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;

public class ConverterProcessor implements ConfigProcessor {

	private final Converter converter;

	public ConverterProcessor(Converter converter) {
		this.converter = converter;
	}

	public void process(XStream instance) {
		instance.registerConverter(converter);
	}

}
