package com.thoughtworks.xstream.converters.reflection;

import java.util.Map;

/**
 * Does not sort any of the fields.
 *
 * @author Guilherme Silveira
 * @since upcoming
 */
public class DefaultFieldKeySorter implements FieldKeySorter {

	public Map sort(Class definedIn, Map keyedByFieldKey) {
		return keyedByFieldKey;
	}

}
