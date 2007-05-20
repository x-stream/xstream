package com.thoughtworks.xstream.converters.reflection;

import java.util.Map;

/**
 * Does not change the order of the fields.
 *
 * @author Guilherme Silveira
 * since 1.2.2
 */
public class ImmutableFieldKeySorter implements FieldKeySorter {

	public Map sort(Class type, Map keyedByFieldKey) {
		return keyedByFieldKey;
	}

}
