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
package com.thoughtworks.xstream.builder;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.builder.processor.ConfigProcessor;
import com.thoughtworks.xstream.builder.processor.TypeConfigProcessor;

/**
 * A type configuration.
 *
 * @author Guilherme Silveira
 * @TODO gs: extract public interface, keep implementation hidden from the end user?
 */
public class TypeConfig implements ConfigProcessor {
	private final Class type;

	private final List childrenNodes;

	public TypeConfig(Class type) {
		this.type = type;
		this.childrenNodes = new ArrayList();
	}

	public void process(XStream instance) {
		for (int i = 0; i < childrenNodes.size(); i++) {
			TypeConfigProcessor node = (TypeConfigProcessor) childrenNodes
					.get(i);
			node.process(instance, type);
		}
	}

	public void with(TypeConfigProcessor[] processors) {
        for (int i = 0; i < processors.length; i++) {
            TypeConfigProcessor processor = processors[i];
			this.childrenNodes.add(processor);
		}
	}

    public void with(TypeConfigProcessor processor) {
        this.childrenNodes.add(processor);
    }

}
