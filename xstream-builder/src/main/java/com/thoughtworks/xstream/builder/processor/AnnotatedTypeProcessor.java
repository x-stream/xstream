package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

public class AnnotatedTypeProcessor implements TypeConfigProcessor {

	public void process(XStream instance, Class type) {
		Annotations.configureAliases(instance, type);
	}

}
