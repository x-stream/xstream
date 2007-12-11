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
import com.thoughtworks.xstream.builder.processor.FieldConfigProcessor;
import com.thoughtworks.xstream.builder.processor.TypeConfigProcessor;

/**
 * A field configuration.
 *
 * @author Guilherme Silveira
 * @TODO gs: extract public interface, keep implementation hidden from the end user?
 */
public class FieldConfig implements TypeConfigProcessor {

	private final String fieldName;
    private final List processors;

    public FieldConfig(String fieldName) {
		this.fieldName = fieldName;
        this.processors = new ArrayList();
	}

	public void process(XStream instance, Class type) {
        for(int i=0;i < processors.size();i++) {
            FieldConfigProcessor node = (FieldConfigProcessor) processors.get(i);
            node.process(instance, type, fieldName);
        }
	}

	public FieldConfig with(FieldConfigProcessor []processors) {
        for (int i = 0; i < processors.length; i++) {
            FieldConfigProcessor processor = processors[i];
			this.processors.add(processor);
		}
		return this;
	}

    public FieldConfig with(FieldConfigProcessor processor) {
        this.processors.add(processor);
        return this;
    }

}
