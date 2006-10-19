package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractDocumentWriterTest extends TestCase {

    private final HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
    protected DocumentWriter writer;

    protected abstract DocumentReader createDocumentReaderFor(Object node);

    protected void assertDocumentProducedIs(final Xpp3Dom expected) {
        assertDocumentProducedIs(new Xpp3Dom[]{expected});
    }

    protected boolean equals(final Xpp3Dom node1, final Xpp3Dom node2) {
        if (node1.getName().equals(node2.getName())) {
            final String value1 = node1.getValue();
            final String value2 = node2.getValue();
            if ((value1 == null && value2 == null) || value1.equals(value2)) {
                final Set set1 = new HashSet(Arrays.asList(node1.getAttributeNames()));
                final Set set2 = new HashSet(Arrays.asList(node2.getAttributeNames()));
                if (set1.equals(set2)) {
                    final Xpp3Dom[] children1 = node1.getChildren();
                    final Xpp3Dom[] children2 = node2.getChildren();
                    if (children1.length == children2.length) {
                        for (int i = 0; i < children1.length; i++) {
                            if (!equals(children1[i], children2[i])) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void assertDocumentProducedIs(final Xpp3Dom[] expected) {
        for (int i = 0; i < expected.length; i++) {
            copier.copy(new XppDomReader(expected[i]), writer);
        }
        final Object[] nodes = writer.getTopLevelNodes().toArray(new Object[0]);
        assertEquals(expected.length, nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            final XppDomWriter xpp3 = new XppDomWriter();
            copier.copy(createDocumentReaderFor(nodes[i]), xpp3);
            assertTrue(equals(expected[i], xpp3.getConfiguration()));
        }
    }

    public void testProducesDomElements() {
        final Xpp3Dom root = new Xpp3Dom("hello");
        root.setValue("world");
        assertDocumentProducedIs(root);
    }

    public void testSupportsNestedElements() {
        final Xpp3Dom a = new Xpp3Dom("a");

        Xpp3Dom b = new Xpp3Dom("b");
        b.setValue("one");
        a.addChild(b);

        b = new Xpp3Dom("b");
        b.setValue("two");
        a.addChild(b);

        final Xpp3Dom c = new Xpp3Dom("c");
        a.addChild(c);
        final Xpp3Dom d = new Xpp3Dom("d");
        d.setValue("three");
        c.addChild(d);

        assertDocumentProducedIs(a);
    }

    public void testSupportsAttributes() {
        final Xpp3Dom person = new Xpp3Dom("person");
        person.setAttribute("firstname", "Joe");
        person.setAttribute("lastname", "Walnes");
        assertDocumentProducedIs(person);
    }

    public void testAttributesAreResettedForNewNode() {
        final Xpp3Dom[] roots = new Xpp3Dom[2];
        final Xpp3Dom person = roots[0] = new Xpp3Dom("person");
        person.setAttribute("firstname", "Joe");
        person.setAttribute("lastname", "Walnes");
        final Xpp3Dom project = roots[1] = new Xpp3Dom("project");
        project.setAttribute("XStream", "Codehaus");

        assertDocumentProducedIs(roots);
    }

    public void testSupportsEmptyNestedTags() {
        final Xpp3Dom parent = new Xpp3Dom("parent");
        parent.addChild(new Xpp3Dom("child"));

        assertDocumentProducedIs(parent);
    }
}
