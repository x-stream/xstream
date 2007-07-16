package com.thoughtworks.xstream.builder.processor;

import com.thoughtworks.xstream.XStream;

/**
 * A field based config processor.
 *
 * @author Guilherme Silveira
 */
public interface FieldConfigProcessor {

	void process(XStream instance, Class type, String fieldName);

}
