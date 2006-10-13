package org.codehaus.xstream.modeller.dom;

import org.codehaus.xstream.modeller.InvalidXmlException;
import org.codehaus.xstream.modeller.Quantity;
import org.codehaus.xstream.modeller.TypeChecker;

public class Value implements NodeType {

	private String type;

	private final String name;

	public Value(final String name) {
		this.name = name;
	}

	public Value changeToValue(String name) {
		return this;
	}

	public void checkType(String value) {
		if (TypeChecker.isLong(value)) {
			if (type == null) {
				type = "long";
			}
		} else if (TypeChecker.isDouble(value)) {
			if (type == null || type == "long") {
				type = "double";
			}
			return;
		} else {
			type = "String";
		}
	}

	public String getType() {
		return type;
	}

	public Element changeToElement(String name) {
		throw new InvalidXmlException("There was already a value node for "
				+ name + " so it cannot be a parent element.");
	}

	@Override
	public String toString() {
		return "[Value node " + type + " named " + name + "]";
	}

	public Attribute changeToAttribute(String name) {
		throw new InvalidXmlException("There was already a value node for "
				+ name + " so it cannot be an attribute.");
	}

	public String getCodeAsMember(Quantity qtd) {
		return qtd.getCodeAsMember(type,name);
	}
	
	public Marker changeToMarker(String name) {
		throw new InvalidXmlException("There was already a value for "
				+ name + " so it cannot be a marker.");
	}

	public String getName() {
		return name;
	}

}
