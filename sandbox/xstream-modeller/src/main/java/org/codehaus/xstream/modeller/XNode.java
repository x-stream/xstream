package org.codehaus.xstream.modeller;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xstream.modeller.dom.Attribute;
import org.codehaus.xstream.modeller.dom.Element;
import org.codehaus.xstream.modeller.dom.Marker;
import org.codehaus.xstream.modeller.dom.NoType;
import org.codehaus.xstream.modeller.dom.NodeType;
import org.codehaus.xstream.modeller.dom.Value;

public class XNode {

	private NodeType type = new NoType();

	private final Graph graph;

	public XNode(final Graph graph) {
		this.graph = graph;
	}

	public void loadFrom(DomNode node) {
		if (node.isMarker()) {
			readMarker(node.getName());
		} else if (node.isValue()) {
			readValue(node);
		} else if (node.isAttribute()) {
			readAttribute(node);
		} else if (node.isParentElement()) {
			readElement(node);
		}
	}

	private void readElement(DomNode node) {
		Element t = type.changeToElement(node.getName());
		List<XNode> children = new ArrayList<XNode>();
		for (DomNode child : node.getChildren()) {
			// TODO ignore attributes from cache? is it ok to have an attribute with
			// the same name as an element?
			if(child.isSimpleText()) {
				// ignores text space in element
				continue;
			}
			XNode c = graph.find(child);
			children.add(c);
		}
		t.checkChildren(children);
		for (DomNode child : node.getChildren()) {
			XNode c = graph.find(child);
			c.loadFrom(child);
		}
		type = t;
	}

	private void readValue(DomNode node) {
		Value t = type.changeToValue(node.getName());
		t.checkType(node.getValue());
		type = t;
	}

	private void readAttribute(DomNode node) {
		Attribute t = type.changeToAttribute(node.getName());
		t.checkType(node.getValue());
		type = t;
	}

	private void readMarker(String name) {
		Marker t = type.changeToMarker(name);
		type = t;
	}

	public NodeType getType() {
		return type;
	}

}
