package com.thoughtworks.xstream.io.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

/**
 * A driver using the StAX API
 *
 * @author James Strachan
 * @version $Revision$
 */
public class StaxDriver extends AbstractXmlFriendlyDriver {

    private static boolean libraryPresent;

    private QNameMap qnameMap;
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private boolean repairingNamespace = false;

    public StaxDriver() {
        this.qnameMap = new QNameMap();
    }

    public StaxDriver(QNameMap qnameMap) {
        this(qnameMap, false);
    }

    public StaxDriver(QNameMap qnameMap, boolean repairingNamespace) {
        this(qnameMap, repairingNamespace, new XmlFriendlyReplacer());
    }

    public StaxDriver(QNameMap qnameMap, boolean repairingNamespace, XmlFriendlyReplacer replacer) {
        super(replacer);
        this.qnameMap = qnameMap;
        this.repairingNamespace = repairingNamespace;
    }
    
    public StaxDriver(XmlFriendlyReplacer replacer) {
        this(new QNameMap(), false, replacer);
    }

    public HierarchicalStreamReader createReader(Reader xml) {
        loadLibrary();
        try {
            return decorate(new StaxReader(qnameMap, createParser(xml)));
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        loadLibrary();
        try {
            return decorate(new StaxReader(qnameMap, createParser(in)));
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    private void loadLibrary() {
        if (!libraryPresent) {
            try {
                Class.forName("javax.xml.stream.XMLStreamReader");
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("StAX API is not present. Specify another driver." +
                        " For example: new XStream(new DomDriver())");
            }
            libraryPresent = true;
        }
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        try {
            return decorate(new StaxWriter(qnameMap, getOutputFactory().createXMLStreamWriter(out), true, isRepairingNamespace()));
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        try {
            return decorate(new StaxWriter(qnameMap, getOutputFactory().createXMLStreamWriter(out), true, isRepairingNamespace()));
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public AbstractPullReader createStaxReader(XMLStreamReader in) {
        return new StaxReader(qnameMap, in);
    }

    public StaxWriter createStaxWriter(XMLStreamWriter out, boolean writeStartEndDocument) throws XMLStreamException {
        return new StaxWriter(qnameMap, out, writeStartEndDocument, repairingNamespace);
    }

    public StaxWriter createStaxWriter(XMLStreamWriter out) throws XMLStreamException {
        return createStaxWriter(out, true);
    }


    // Properties
    //-------------------------------------------------------------------------
    public QNameMap getQnameMap() {
        return qnameMap;
    }

    public void setQnameMap(QNameMap qnameMap) {
        this.qnameMap = qnameMap;
    }

    public XMLInputFactory getInputFactory() {
        if (inputFactory == null) {
            inputFactory = XMLInputFactory.newInstance();
        }
        return inputFactory;
    }

    public XMLOutputFactory getOutputFactory() {
        if (outputFactory == null) {
            outputFactory = XMLOutputFactory.newInstance();
            outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", isRepairingNamespace() ? Boolean.TRUE : Boolean.FALSE);
        }
        return outputFactory;
    }

    public boolean isRepairingNamespace() {
        return repairingNamespace;
    }


    // Implementation methods
    //-------------------------------------------------------------------------
    protected XMLStreamReader createParser(Reader xml) throws XMLStreamException {
        return getInputFactory().createXMLStreamReader(xml);
    }

    protected XMLStreamReader createParser(InputStream xml) throws XMLStreamException {
        return getInputFactory().createXMLStreamReader(xml);
    }
}
