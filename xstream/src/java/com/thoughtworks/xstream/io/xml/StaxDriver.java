package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;

/**
 * A driver using the StAX API
 *
 * @author James Strachan
 * @version $Revision$
 */
public class StaxDriver implements HierarchicalStreamDriver {

    private static boolean libraryPresent;

    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private boolean repairingNamespace = false;

    public HierarchicalStreamReader createReader(Reader xml) {
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
        try {
            return new StaxReader(createParser(xml));
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }


    // Properties
    //-------------------------------------------------------------------------
    public XMLInputFactory getInputFactory() {
        if (inputFactory == null) {
            inputFactory = XMLInputFactory.newInstance();
        }
        return inputFactory;
    }

    public XMLOutputFactory getOutputFactory() {
        if (outputFactory == null) {
            outputFactory = XMLOutputFactory.newInstance();
            if (isRepairingNamespace()) {
                outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
            }
        }
        return outputFactory;
    }

    public boolean isRepairingNamespace() {
        return repairingNamespace;
    }

    public void setRepairingNamespace(boolean repairingNamespace) {
        this.repairingNamespace = repairingNamespace;
    }


    // Implementation methods
    //-------------------------------------------------------------------------
    protected XMLStreamReader createParser(Reader xml) throws XMLStreamException {
        return getInputFactory().createXMLStreamReader(xml);
    }
}
