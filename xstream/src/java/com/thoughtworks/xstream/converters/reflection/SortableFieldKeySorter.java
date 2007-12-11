/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. April 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.util.OrderRetainingMap;
import com.thoughtworks.xstream.io.StreamException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The default implementation for sorting fields. Invoke registerFieldOrder in
 * order to set the field order for an specific type.
 *
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public class SortableFieldKeySorter implements FieldKeySorter {

	private final Map map = new WeakHashMap();

	public Map sort(Class type, Map keyedByFieldKey) {
		if (map.containsKey(type)) {
			Map result = new OrderRetainingMap();
			FieldKey[] fieldKeys = (FieldKey[]) keyedByFieldKey.keySet()
					.toArray(new FieldKey[keyedByFieldKey.size()]);
			Arrays.sort(fieldKeys, (Comparator) map.get(type));
			for (int i = 0; i < fieldKeys.length; i++) {
				result.put(fieldKeys[i], keyedByFieldKey.get(fieldKeys[i]));
			}
			return result;
		} else {
			return keyedByFieldKey;
		}
	}

	/**
	 * Registers the field order to use for a specific type. This will not
	 * affect any of the type's super or sub classes. If you skip a field which
	 * will be serialized, XStream will thrown an StreamException during the
	 * serialization process.
	 *
	 * @param type
	 *            the type
	 * @param fields
	 *            the field order
	 */
	public void registerFieldOrder(Class type, String[] fields) {
		map.put(type, new FieldComparator(fields));
	}

	private class FieldComparator implements Comparator {

		private final String[] fieldOrder;

		public FieldComparator(String[] fields) {
			this.fieldOrder = fields;
		}

		public int compare(String first, String second) {
			int firstPosition = -1, secondPosition = -1;
			for (int i = 0; i < fieldOrder.length; i++) {
				if (fieldOrder[i].equals(first)) {
					firstPosition = i;
				}
				if (fieldOrder[i].equals(second)) {
					secondPosition = i;
				}
			}
			if (firstPosition == -1 || secondPosition == -1) {
				// field not defined!!!
				throw new StreamException(
						"You have not given XStream a list of all fields to be serialized.");
			}
			return firstPosition - secondPosition;
		}

		public int compare(Object firstObject, Object secondObject) {
			FieldKey first = (FieldKey) firstObject, second = (FieldKey) secondObject;
			Class definedIn = first.getDeclaringClass();
			return compare(first.getFieldName(), second.getFieldName());
		}

	}

}
