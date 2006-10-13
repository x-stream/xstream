package org.codehaus.xstream.modeller.dom;

import org.codehaus.xstream.modeller.Quantity;

public interface NodeType {

	Value changeToValue(String name);

	Element changeToElement(String name);

	Attribute changeToAttribute(String name);

	String getCodeAsMember(Quantity quantity);

	Marker changeToMarker(String name);

	String getName();

}
