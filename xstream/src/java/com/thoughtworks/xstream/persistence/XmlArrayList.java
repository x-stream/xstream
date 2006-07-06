package com.thoughtworks.xstream.persistence;

import java.util.AbstractList;

/**
 * A persistent list implementation backed on a XmlMap.
 * 
 * @author Guilherme Silveira
 */
public class XmlArrayList extends AbstractList {

	private final XmlMap map;

	public XmlArrayList(StreamStrategy streamStrategy) {
		this.map = new XmlMap(streamStrategy);
	}

	public int size() {
		return map.size();
	}

	public Object set(int index, Object element) {
		rangeCheck(index);
		Object value = get(index);
		map.put(String.valueOf(index), element);
		return value;
	}

	public void add(int index, Object element) {
		int size = size();
		if (index >= (size + 1) || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ size);
		}
		int to = index != size ? index - 1 : index;
		for (int i = size; i > to; i--) {
			map.put(String.valueOf(i + 1), map.get(String.valueOf(i)));
		}
		map.put(String.valueOf(index), element);
	}

	private void rangeCheck(int index) {
		int size = size();
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ size);
		}
	}

	public Object get(int index) {
		rangeCheck(index);
		return map.get(String.valueOf(index));
	}

	public Object remove(int index) {
		int size = size();
		rangeCheck(index);
		Object value = map.get(String.valueOf(index));
		for (int i = index; i < size - 1; i++) {
			map.put(String.valueOf(i), map.get(String.valueOf(i + 1)));
		}
		map.remove(String.valueOf(size - 1));
		return value;
	}

}
