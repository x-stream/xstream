package org.codehaus.xstream.modeller.acceptance;

import java.io.IOException;

import org.codehaus.xstream.modeller.AbstractTestCase;
import org.codehaus.xstream.modeller.DomNode;
import org.codehaus.xstream.modeller.Graph;
import org.codehaus.xstream.modeller.XNode;
import org.xml.sax.SAXException;

public abstract class AcceptanceTestCase extends AbstractTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}

	DomNode readDom(String xml) throws SAXException, IOException {
		return new DomNode(toDoc(xml).getChildNodes().item(0));
	}

	XNode createRootFor(String xml) throws SAXException, IOException {
		Graph graph = new Graph();
		DomNode node = readDom(xml);
		XNode root = graph.find(node);
		root.loadFrom(node);
		return root;
	}

}
