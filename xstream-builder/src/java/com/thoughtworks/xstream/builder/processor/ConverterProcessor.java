/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. July 2007 by Guilherme Silveira
 */
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
