package org.codehaus.xstream.modeller;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomNode {

	private final Node node;

	public DomNode(final Node node) {
		this.node = node;
	}

	boolean isMarker() {
		return node.getChildNodes().getLength() == 0
				&& node.getNodeType() == Node.ELEMENT_NODE;
	}

	String getName() {
		return node.getNodeName();
	}

	boolean isParentElement() {
		return node.getChildNodes().getLength() > 0
				&& node.getNodeType() == Node.ELEMENT_NODE;
	}

	List<DomNode> getChildren() {
		NodeList childNodes = node.getChildNodes();
		List<DomNode> children = new ArrayList<DomNode>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			children.add(new DomNode(childNodes.item(i)));
		}
		NamedNodeMap atts = node.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			children.add(new DomNode(atts.item(i)));
		}
		return children;
	}

	boolean isAttribute() {
		return node.getNodeType() == Node.ATTRIBUTE_NODE;
	}

	String getValue() {
		if (isAttribute() || isValue()) {
			return node.getChildNodes().item(0).getNodeValue();
		}
		return node.getNodeValue();
	}

	boolean isValue() {
		return !isAttribute() && node.getChildNodes().getLength() == 1
				&& node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE;
	}

	public boolean isSimpleText() {
		return node.getNodeType()==Node.TEXT_NODE;
	}
}
