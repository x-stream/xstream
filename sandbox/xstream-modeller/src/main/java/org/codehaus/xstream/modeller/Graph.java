package org.codehaus.xstream.modeller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xstream.modeller.dom.Element;

public class Graph {

	private final Map<String, XNode> nodes = new HashMap<String, XNode>();

	public XNode find(DomNode node) {
		XNode c = nodes.get(node.getName());
		if (c == null) {
			c = new XNode(this);
			nodes.put(node.getName(), c);
		}
		return c;
	}

	public Collection<XNode> getElements() {
		Collection<XNode> nodes = new ArrayList<XNode>();
		for (XNode node : this.nodes.values()) {
			if (node.getType().getClass().equals(Element.class)) {
				nodes.add(node);
			}
		}
		return nodes;
	}

}
