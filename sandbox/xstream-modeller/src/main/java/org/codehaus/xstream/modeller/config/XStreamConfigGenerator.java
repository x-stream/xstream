package org.codehaus.xstream.modeller.config;

import java.util.Collection;

import org.codehaus.xstream.modeller.Graph;
import org.codehaus.xstream.modeller.XNode;
import org.codehaus.xstream.modeller.dom.Attribute;
import org.codehaus.xstream.modeller.dom.Element;
import org.codehaus.xstream.modeller.dom.NodeType;

public class XStreamConfigGenerator implements ConfigGenerator {

	private final String basePackage;

	public XStreamConfigGenerator(String basePackage) {
		this.basePackage = basePackage;
	}

	public void printConfiguration(Graph graph) {

		String code = "";
		if (!basePackage.equals("")) {
			code += "package " + basePackage + ";\n\n";
		}
		code += "public class CustomXStream extends XStream {\n";
		code += "\tpublic CustomXStream() {\n";

		Collection<XNode> nodes = graph.getElements();
		for (XNode node : nodes) {
			Element type = (Element) node.getType();
			String name = type.getName();
			if (type.isImplicitCollection()) {
				continue;
			}
			code += "\t\talias(\"" + name + "\", " + tipify(name) + ".class);\n";
			for (XNode child : type.getChildren()) {
				NodeType childType = child.getType();
				// TODO nasty... should use some type of enum? factory? anything else
				if (childType.getClass().equals(Element.class)) {
					Element el = (Element) childType;
					if (el.isImplicitCollection()) {
						code += "\t\taddImplicitCollection(\"" + el.getName()
								+ "\", \"" + el.getName() + "\", " + tipify(name)
								+ ".class);\n";
					}
				} else if (childType.getClass().equals(Attribute.class)) {
					code += "\t\tuseAttributeFor(\"" + childType.getName()
							+ "\", \"" + childType.getName() + "\", " + tipify(name)
							+ ".class);\n";
				}
			}
		}

		code += "\t}\n";
		code += "}\n";
		System.out.println(code);
	}

	private String tipify(String name) {
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}

}
