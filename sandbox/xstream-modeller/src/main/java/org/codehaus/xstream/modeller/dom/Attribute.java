package org.codehaus.xstream.modeller.dom;

import org.codehaus.xstream.modeller.InvalidXmlException;
import org.codehaus.xstream.modeller.Quantity;

public class Attribute implements NodeType {

	private final Value value;

	private final String name;

	public Attribute(String name) {
		this.value = new Value(name);
		this.name = name;
	}

	public Attribute changeToAttribute(String name) {
		return this;
	}

	public Element changeToElement(String name) {
		throw new InvalidXmlException(
				"There was already an attribute node for " + name
						+ " so it cannot be a parent element.");
	}

	public Value changeToValue(String name) {
		throw new InvalidXmlException(
				"There was already an attribute node for " + name
						+ " so it cannot be a value element.");
	}

	public void checkType(String value) {
		this.value.checkType(value);
	}

	@Override
	public String toString() {
		return "[Attribute node " + value.getType() + " named " + name + "]";
	}

	public String getCodeAsMember(Quantity qtd) {
		return qtd.getCodeAsMember(value.getType(), name);
	}

	public Marker changeToMarker(String name) {
		throw new InvalidXmlException("There was already an attribute node for "
				+ name + " so it cannot be a marker.");
	}

	public String getName() {
		return name;
	}
}
