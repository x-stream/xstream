package org.codehaus.xstream.modeller.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.xstream.modeller.InvalidXmlException;
import org.codehaus.xstream.modeller.Quantity;
import org.codehaus.xstream.modeller.XNode;

public class Element implements NodeType {

	private final String name;

	private final List<XNode> children = new ArrayList<XNode>();

	private final Map<XNode, Quantity> quantity = new HashMap<XNode, Quantity>();

	public Element(String name) {
		this.name = name;
	}

	public Element changeToElement(String name) {
		return this;
	}

	public Value changeToValue(String name) {
		throw new InvalidXmlException(
				"There was already an element with this name: " + name
						+ " so it cannot be a value element.");
	}

	public void checkChildren(List<XNode> newChildren) {
		for (XNode child : newChildren) {
			if (children.contains(child)) {
				quantity.put(child, Quantity.MANY);
			} else {
				quantity.put(child, Quantity.ONE);
				children.add(child);
			}
		}
	}

	public List<XNode> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "[Element node " + name + "]";
	}

	public Attribute changeToAttribute(String name) {
		throw new InvalidXmlException("There was already an element node for "
				+ name + " so it cannot be an attribute.");
	}

	public String getCode(String pkg) {
		// TODO stringbuffer
		String code = "";
		if (!pkg.equals("")) {
			code += "package " + pkg + ";\n\n";
		}
		code += "public class " + tipify(name) + " {\n";
		for (XNode child : children) {
			// int qtd = ((Integer) membersQtd.get(key)).intValue();
			// if (qtd == MORE) {
			// code += "\tList " + t.getName() + ";\n";
			// } else if (qtd == ONE) {
			code += child.getType().getCodeAsMember(quantity.get(child));
			// code += "\t" + t.getTypeName() + " " + t.getName() + ";\n";
			// }
		}
		code += "}\n";
		return code;
	}

	private String tipify(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public String getCodeAsMember(Quantity qtd) {
		if(isImplicitCollection()) {
			return "\tprivate List<" + children.get(0).getType().getName() + "> " + name + ";\n";
		}
		return "\tprivate " + tipify(name) + " " + name + ";\n";
	}

	public boolean isImplicitCollection() {
		if (children.size() != 1) {
			return false;
		}
		XNode node = children.get(0);
		if (!quantity.get(node).equals(Quantity.MANY)) {
			return false;
		}
		if (!(node.getType().getClass().equals(Element.class) || node.getType()
				.getClass().equals(Marker.class))) {
			return false;
		}
		return true;
	}

	public Quantity getQuantity(XNode node) {
		return quantity.get(node);
	}

	public Marker changeToMarker(String name) {
		throw new InvalidXmlException("There was already an element node for "
				+ name + " so it cannot be a marker.");
	}
	
	public String getName() {
		return name;
	}

}
