package org.codehaus.xstream.modeller;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DomNodeTest extends AbstractTestCase {

	public void testChecksThatAMarkerNodeIsAMarkerNode() throws SAXException,
			IOException {
		String xml = "<myNode/>";
		Node read = toDoc(xml).getChildNodes().item(0);
		DomNode node = new DomNode(read);
		assertTrue(node.isMarker());
	}

	public void testChecksThatAnElementNodeIsNotAMarkerNode()
			throws SAXException, IOException {
		String xml = "<el><my/></el>";
		Node read = toDoc(xml).getChildNodes().item(0);
		DomNode node = new DomNode(read);
		assertFalse(node.isMarker());
	}

	public void testChecksThatAMarkerNodeIsNotAParentElementNode()
			throws SAXException, IOException {
		String xml = "<myNode/>";
		Node read = toDoc(xml).getChildNodes().item(0);
		DomNode node = new DomNode(read);
		assertFalse(node.isParentElement());
	}

	public void testChecksThatAParentElementNodeIsAParentElementNode()
			throws SAXException, IOException {
		String xml = "<el><myNode/></el>";
		Node read = toDoc(xml).getChildNodes().item(0);
		DomNode node = new DomNode(read);
		assertTrue(node.isParentElement());
	}

	public void testChecksThatAParentElementNodeContainsItsChildren()
			throws SAXException, IOException {
		String xml = "<el><myNode1/><myNode2/></el>";
		Node read = toDoc(xml).getChildNodes().item(0);
		DomNode node = new DomNode(read);
		List<DomNode> children = node.getChildren();
		for (DomNode child : children) {
			if (!child.getName().equals("myNode1")
					&& !child.getName().equals("myNode2")) {
				fail("Found an invalid child node named " + child.getName());
			}
		}
		assertEquals(2, children.size());
	}

	public void testChecksWhatIsAnAttributeNode() throws SAXException,
			IOException {
		String xml = "<el att=\"1\"></el>";
		Node read = toDoc(xml).getChildNodes().item(0);
		DomNode node = new DomNode(read);
		assertEquals(1, node.getChildren().size());
		DomNode att = node.getChildren().get(0);
		assertEquals("att", att.getName());
	}

}
