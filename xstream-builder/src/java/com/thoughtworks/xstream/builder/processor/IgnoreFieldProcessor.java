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
