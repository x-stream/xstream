package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * <code>BackupHierarchicalStreamReader</code> is a <code>HierarchicalStreamReader</code>
 * with one level of look behind.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
class BackupHierarchicalStreamReader
        implements HierarchicalStreamReader {
    private final HierarchicalStreamReader reader;
    private boolean backedUp;

    public BackupHierarchicalStreamReader(final HierarchicalStreamReader reader) {
        this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasMoreChildren() {
        return reader.hasMoreChildren();
    }

    /**
     * {@inheritDoc}
     */
    public void moveDown() {
        if (backedUp) {
            backedUp = false;

        } else {
            reader.moveDown();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void moveUp() {
        backedUp = false;

        reader.moveUp();
    }

    /**
     * Backup one node so that the current node is skipped without interferring
     * with the next call to {@link #moveDown()}.  The pattern is: <pre>
     *     reader.moveDown();
     * <p/>
     *     if (someCondition()) {
     *         doSomething(reader.getValue());
     *         reader.moveUp();
     *     } else {
     *         reader.backUp();
     *     }
     * </pre>
     */
    public void backUp() {
        backedUp = true;
    }

    /**
     * {@inheritDoc}
     */
    public String getNodeName() {
        return reader.getNodeName();
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return reader.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public String getAttribute(final String name) {
        return reader.getAttribute(name);
    }

    /**
     * {@inheritDoc}
     */
    public Object peekUnderlyingNode() {
        return reader.peekUnderlyingNode();
    }

    /**
     * {@inheritDoc}
     */
    public void appendErrors(final ErrorWriter errorWriter) {
        reader.appendErrors(errorWriter);
    }
}
