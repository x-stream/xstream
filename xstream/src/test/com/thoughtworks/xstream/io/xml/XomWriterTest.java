package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;
import nu.xom.Document;
import nu.xom.Element;

public class XomWriterTest extends TestCase {

    private HierarchicalStreamWriter writer;
    private Element root;

    protected void setUp() throws Exception {
        super.setUp();
        root = new Element("my-root");
        writer = new XomWriter(root);
    }

    private void assertXmlProducedIs(String expected) {
        assertEquals(1, root.getChildCount());
        String actual = root.getChild(0).toXML();
        assertEquals(expected, actual);
    }

    public void testProducesXmlElements() {
        writer.startNode("hello");
        writer.setValue("world");
        writer.endNode();

        assertXmlProducedIs("<hello>world</hello>");
    }

    public void testSupportsNestedElements() {

        writer.startNode("a");

        writer.startNode("b");
        writer.setValue("one");
        writer.endNode();

        writer.startNode("b");
        writer.setValue("two");
        writer.endNode();

        writer.startNode("c");
        writer.startNode("d");
        writer.setValue("three");
        writer.endNode();
        writer.endNode();

        writer.endNode();

        assertXmlProducedIs("<a><b>one</b><b>two</b><c><d>three</d></c></a>");
    }

    public void testSupportsEmptyTags() {
        writer.startNode("empty");
        writer.endNode();

        assertXmlProducedIs("<empty />");
    }

    public void testSupportsAttributes() {
        writer.startNode("person");
        writer.addAttribute("firstname", "Joe");
        writer.addAttribute("lastname", "Walnes");
        writer.endNode();

        assertXmlProducedIs("<person firstname=\"Joe\" lastname=\"Walnes\" />");
    }

    public void testEscapesXmlUnfriendlyCharacters() {
        writer.startNode("evil");
        writer.setValue("w0000 $ <xx> &!;");
        writer.endNode();

        assertXmlProducedIs("<evil>w0000 $ &lt;xx&gt; &amp;!;</evil>");
    }

}
