package com.thoughtworks.xstream.persistence;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * 
 * @author Guilherme Silveira
 */
// TODO complications: we need a xml filename for an object, how to generate a
// unique key? uuid algorithms?
// currently can be quite slow as it must check if it is already contained
public class XmlSet extends AbstractSet {

	private final XmlMap map;

	private final StreamStrategy streamStrategy;

	public XmlSet(StreamStrategy streamStrategy) {
		this.map = new XmlMap(streamStrategy);
		this.streamStrategy = streamStrategy;
	}

	public Iterator iterator() {
		return map.values().iterator();
	}

	public int size() {
		return map.size();
	}

	public boolean add(Object o) {
		if (map.containsValue(o)) {
			return false;
		} else {
			// not-synchronized!
			map.put(findEmptyKey(), o);
			return true;
		}
	}

	private String findEmptyKey() {
		// int i = size() + 1; might be faster
		// wow, while true, dangerous toy
		for (int i = 1; true; i++) {
			if (!map.containsKey("" + i)) {
				return "" + i;
			}
		}
	}

}
