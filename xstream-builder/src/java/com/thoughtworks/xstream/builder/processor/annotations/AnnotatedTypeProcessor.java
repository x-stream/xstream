package com.thoughtworks.xstream.builder.processor.annotations;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.builder.processor.TypeConfigProcessor;

public class AnnotatedTypeProcessor implements TypeConfigProcessor {

	public void process(XStream instance, Class type) {
		Annotations.configureAliases(instance, new Class[] { type });
	}

}
