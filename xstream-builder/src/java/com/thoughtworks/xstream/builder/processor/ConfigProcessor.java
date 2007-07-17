package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

/**
 * A basic configuration processor.
 * 
 * @author Guilherme Silveira
 */
public interface ConfigProcessor {

	void process(XStream instance);

}
