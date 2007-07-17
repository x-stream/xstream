package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

public class AliasFieldProcessor implements FieldConfigProcessor {

	private final String alias;

	public AliasFieldProcessor(String alias) {
		this.alias = alias;
	}

	public void process(XStream instance, Class type, String fieldName) {
		instance.aliasField(alias, type, fieldName);
	}

}
