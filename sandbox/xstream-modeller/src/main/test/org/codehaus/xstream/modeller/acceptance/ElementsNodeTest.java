package org.codehaus.xstream.modeller.acceptance;

import java.io.IOException;

import org.codehaus.xstream.modeller.Quantity;
import org.codehaus.xstream.modeller.XNode;
import org.codehaus.xstream.modeller.dom.Element;
import org.xml.sax.SAXException;

public class ElementsNodeTest extends AcceptanceTestCase {

	public void testLoadsAnElementNode() throws SAXException, IOException {
		String xml = "<element><c1/><c2/></element>";
		XNode root = createRootFor(xml);
		assertEquals(Element.class, root.getType().getClass());
	}

	public void testLoadsAnElementNodeWithItsChildren() throws SAXException,
			IOException {
		String xml = ("<element><c1/><c2/></element>");
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		assertEquals(2, type.getChildren().size());
	}

	public void testReadsElementWithTwiceTheSameChildren() throws SAXException,
			IOException {
		String xml = "<element><c1/><c1/></element>";
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		assertEquals(1, type.getChildren().size());
	}

	public void testChecksForQuantityReadingElementWithTwiceTheSameChildren() throws SAXException,
			IOException {
		String xml = "<element><c1/><c1/></element>";
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		assertEquals(Quantity.MANY,type.getQuantity(type.getChildren().get(0)));
	}

	public void testChecksForImplicitCollectionReadingElementWithTwiceTheSameChildren() throws SAXException,
	IOException {
		String xml = "<element><c1/><c1/></element>";
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		assertTrue(type.isImplicitCollection());
	}

	public void testTwiceReadsElementWithOneChildren() throws SAXException,
			IOException {
		String xml = "<main>" +
					"\t<element><c1/></element>" +
					"\t<element><c1/><c1/></element>" +
					"</main>";
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		XNode element = type.getChildren().get(0);
		type = (Element) element.getType();
		assertEquals(1, type.getChildren().size());
		assertTrue(type.isImplicitCollection());
	}

	public void testTwiceReadsElementWithOneToManyChildCheckingItsQuantity() throws SAXException,
	IOException {
		String xml = "<main>" +
					"\t<element><c1/></element>" +
					"\t<element><c1/><c1/></element>" +
					"</main>";
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		XNode element = type.getChildren().get(0);
		type = (Element) element.getType();
		assertEquals(Quantity.MANY, type.getQuantity(type.getChildren().get(0)));
	}

	public void testTwiceReadsElementWithOneChildOnlyCheckingItsQuantity() throws SAXException,
		IOException {
		String xml = "<main>" +
					"\t<element><c1/></element>" +
					"\t<element><c1/></element>" +
					"</main>";
		XNode root = createRootFor(xml);
		Element type = (Element) root.getType();
		XNode element = type.getChildren().get(0);
		type = (Element) element.getType();
		assertEquals(Quantity.ONE, type.getQuantity(type.getChildren().get(0)));
	}

}
