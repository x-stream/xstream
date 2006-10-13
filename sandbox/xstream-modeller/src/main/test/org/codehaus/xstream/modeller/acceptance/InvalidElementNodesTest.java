package org.codehaus.xstream.modeller.acceptance;

import java.io.IOException;

import org.codehaus.xstream.modeller.DomNode;
import org.codehaus.xstream.modeller.Graph;
import org.codehaus.xstream.modeller.InvalidXmlException;
import org.codehaus.xstream.modeller.XNode;
import org.xml.sax.SAXException;

public class InvalidElementNodesTest extends AcceptanceTestCase {

	public void testTriesToChangeAnElementNodeToValueNode() throws SAXException, IOException {
		String xml = ("<main>\n" + 
						"<element><c1/><c2/></element>\n" +
						"<element>value</element>\n" +
						"</main>");
		try {
			XNode root = createRootFor(xml);
			fail("Should not be able to change from a parent node to value node");
		} catch(InvalidXmlException ex) {
			// ok
		}
	}

	public void testTriesToChangeAValueNodeToAnParentElementNode() throws SAXException, IOException {
		String xml = ("<main>\n" + 
						"<element>value</element>\n" +
						"<element><c1/><c2/></element>\n" +
						"</main>");
		try {
			XNode root = createRootFor(xml);
			fail("Should not be able to change it");
		} catch(InvalidXmlException ex) {
			// ok
		}
	}

	public void testTriesToChangeAMarkerNodeToAParentElementNode() throws SAXException, IOException {
		String xml = ("<main>\n" + 
						"<element/>\n" +
						"<element><c1/><c2/></element>\n" +
						"</main>");
		XNode root = createRootFor(xml);
	}

}
