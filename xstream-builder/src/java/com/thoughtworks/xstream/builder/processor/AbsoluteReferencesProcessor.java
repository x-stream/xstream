/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. October 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

/**
 * A processor which sets xstream to use absolute references.
 * 
 * @author Guilherme Silveira
 * @since upcoming
 */
public class AbsoluteReferencesProcessor implements ConfigProcessor {

	public void process(XStream instance) {
		instance.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
	}

}
