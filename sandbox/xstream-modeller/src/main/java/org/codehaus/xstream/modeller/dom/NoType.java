package org.codehaus.xstream.modeller.dom;

import org.codehaus.xstream.modeller.Quantity;

public class NoType implements NodeType {

	public Value changeToValue(String name) {
		return new Value(name);
	}

	public Element changeToElement(String name) {
		return new Element(name);
	}

	public Attribute changeToAttribute(String name) {
		return new Attribute(name);
	}

	public String getCodeAsMember(Quantity quantity) {
		return "";
	}

	public Marker changeToMarker(String name) {
		return new Marker(name);
	}

	public String getName() {
		return "";
	}
}
