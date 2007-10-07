package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

/**
 * A processor which sets xstream to use absolute references.
 * 
 * @author Guilherme Silveira
 * @since 1.3
 */
public class NoReferencesProcessor implements ConfigProcessor {

	public void process(XStream instance) {
		instance.setMode(XStream.NO_REFERENCES);
	}

}
