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

	public FieldConfig with(FieldConfigProcessor ... processors) {
		for (FieldConfigProcessor processor : processors) {
			this.processors.add(processor);
		}
		return this;
	}

}
