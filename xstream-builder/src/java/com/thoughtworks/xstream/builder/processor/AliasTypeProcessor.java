package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

public class AliasTypeProcessor implements TypeConfigProcessor {

	private final String alias;

	public AliasTypeProcessor(String alias) {
		this.alias = alias;
	}

	public void process(XStream instance, Class type) {
		instance.alias(alias, type);
	}

}
