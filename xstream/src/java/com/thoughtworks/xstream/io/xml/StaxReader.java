package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.IntQueue;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A reader using the StAX API
 *
 * @author James Strachan
 * @version $Revision$
 */
public class StaxReader implements HierarchicalStreamReader, XMLStreamConstants {

    private final QNameMap qnameMap;
    private final XMLStreamReader in;
    private final FastStack elementStack = new FastStack(16);
    private final IntQueue lookaheadQueue = new IntQueue(4);

    private boolean hasMoreChildrenCached;
    private boolean hasMoreChildrenResult;

    public StaxReader(QNameMap qnameMap, XMLStreamReader in) {
        this.qnameMap = qnameMap;
        this.in = in;
        moveDown();
    }

    public boolean hasMoreChildren() {
        if (hasMoreChildrenCached) {
            return hasMoreChildrenResult;
        }
        while (true) {
            switch (lookahead()) {
                case START_ELEMENT:
                    hasMoreChildrenCached = true;
                    hasMoreChildrenResult = true;
                    return true;
                case END_ELEMENT:
                case END_DOCUMENT:
                    hasMoreChildrenCached = true;
                    hasMoreChildrenResult = false;
                    return false;
                default:
                    continue;
            }
        }
    }

    private int lookahead() {
        try {
            int event = in.next();
            lookaheadQueue.write(event);
            return event;
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    private int next() {
        if (!lookaheadQueue.isEmpty()) {
            return lookaheadQueue.read();
        }
        else {
            try {
                return in.next();
            }
            catch (XMLStreamException e) {
                throw new StreamException(e);
            }
        }
    }

    public void moveDown() {
        hasMoreChildrenCached = false;
        int currentDepth = elementStack.size();
        while (elementStack.size() <= currentDepth) {
            read();
            if (elementStack.size() < currentDepth) {
                throw new RuntimeException(); // sanity check
            }
        }
    }

    public void moveUp() {
        hasMoreChildrenCached = false;
        int currentDepth = elementStack.size();
        while (elementStack.size() >= currentDepth) {
            read();
        }
    }

    public String getNodeName() {
        return (String) elementStack.peek();
    }

    public String getValue() {
        // we should collapse together any text which
        // contains comments

        // lets only use a string buffer when we get 2 strings
        // to avoid copying strings
        String last = null;
        StringBuffer buffer = null;

        int value = lookahead();
        while (true) {
            if (value == CHARACTERS) {
                String text = in.getText();
                if (text != null && text.length() > 0) {
                    if (last == null) {
                        last = text;
                    }
                    else {
                        if (buffer == null) {
                            buffer = new StringBuffer(last);
                        }
                        buffer.append(text);
                    }
                }
            }
            else if (value != COMMENT) {
                break;
            }
            value = lookahead();
        }
        if (buffer != null) {
            return buffer.toString();
        }
        else {
            return (last == null) ? "" : last;
        }
    }

    public String getAttribute(String name) {
        return in.getAttributeValue(null, name);
    }

    public String getAttribute(int index) {
        return in.getAttributeValue(index);
    }

    public int getAttributeCount() {
        return in.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return in.getAttributeLocalName(index);
    }

    public Object peekUnderlyingNode() {
        throw new UnsupportedOperationException();
    }

    private void read() {
        switch (next()) {
            case START_ELEMENT:
                {
                    // let the QNameMap handle any mapping of QNames to Java class names
                    QName qname = in.getName();
                    String jname = qnameMap.getJavaClassName(qname);
                    elementStack.push(jname);
                }
                break;
            case END_ELEMENT:
            case END_DOCUMENT:
                elementStack.pop();
                break;
        }
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("line number", String.valueOf(in.getLocation().getLineNumber()));
    }

    public void close() {
        try {
            in.close();
        } catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader underlyingReader() {
        return this;
    }
}