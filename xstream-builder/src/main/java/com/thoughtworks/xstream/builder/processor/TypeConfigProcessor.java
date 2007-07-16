package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

/**
 * A type based config processor.
 *
 * @author Guilherme Silveira
 */
public interface TypeConfigProcessor {

	void process(XStream instance, Class type);

}
