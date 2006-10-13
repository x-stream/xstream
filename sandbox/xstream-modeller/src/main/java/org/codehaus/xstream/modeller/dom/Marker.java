package org.codehaus.xstream.modeller.dom;

import org.codehaus.xstream.modeller.InvalidXmlException;
import org.codehaus.xstream.modeller.Quantity;

public class Marker implements NodeType {

	private final String name;

	public Marker(String name) {
		this.name = name;
	}

	public Element changeToElement(String name) {
		return new Element(name);
	}

	public Value changeToValue(String name) {
		return new Value(name);
	}

	@Override
	public String toString() {
		return "[Marker node " + name + "]";
	}

	public Attribute changeToAttribute(String name) {
		throw new InvalidXmlException("There was already a marker for "
				+ name + " so it cannot be an attribute.");
	}

	public String getCode(String pkg) {
		// TODO stringbuffer
		String code = "";
		if (!pkg.equals("")) {
			code += "package " + pkg + ";\n\n";
		}
		code += "public class " + tipify(name) + " {\n";
		code += "}\n";
		return code;
	}

	private String tipify(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public String getCodeAsMember(Quantity qtd) {
		return "\tprivate " + tipify(name) + " " + name + ";\n";
	}

	public Marker changeToMarker(String name) {
		return this;
	}

	public String getName() {
		return name;
	}
}
