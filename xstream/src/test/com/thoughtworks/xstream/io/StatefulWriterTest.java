package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.io.xml.CompactWriter;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;


/**
 * @author J&ouml;rg Schaible
 */
public class StatefulWriterTest extends TestCase {

    private StatefulWriter writer;
    private StringWriter stringWriter;

    protected void setUp() throws Exception {
        super.setUp();
        stringWriter = new StringWriter();
        writer = new StatefulWriter(new CompactWriter(stringWriter));
    }

    public void testDelegatesAllCalls() {
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        writer.setValue("foo");
        writer.endNode();
        writer.close();
        assertEquals("<junit test=\"true\">foo</junit>", stringWriter.toString());
    }

    public void testKeepsBlance() {
        writer.startNode("junit");
        writer.endNode();
        try {
            writer.endNode();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }
    
    public void testCanOnlyWriteAttributesToOpenNode() {
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.startNode("junit");
        writer.setValue("text");
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.endNode();
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }

    public void testCanWriteAttributesOnlyOnce() {
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.endNode();
    }
    
    public void testCanWriteValueOnlyToOpenNode() {
        try {
            writer.setValue("test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.startNode("junit");
        writer.endNode();
        try {
            writer.setValue("test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }
    
    public void testCannotOpenNodeInValue() {
        writer.startNode("junit");
        writer.setValue("test");
        try {
            writer.startNode("junit");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }
    
    public void testCanCloseInFinally() {
        try {
            writer.endNode();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            writer.close();
        }
    }
    
    public void testCannotWriteAfterClose() {
        writer.close();
        try {
            writer.startNode("junit");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.addAttribute("junit", "test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.setValue("test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.endNode();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.flush();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }
    
    public void testCanCloseTwice() {
        writer.close();
        writer.close();
    }
    
    public void testCaresAboutNestingLevelWritingAttributes() {
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        writer.endNode();
        writer.endNode();
    }
}
