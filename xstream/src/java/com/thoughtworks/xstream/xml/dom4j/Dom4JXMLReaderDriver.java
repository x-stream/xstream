package com.thoughtworks.xstream.xml.dom4j;

import com.thoughtworks.xstream.xml.CannotParseXMLException;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLReaderDriver;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class Dom4JXMLReaderDriver implements XMLReaderDriver {
    public XMLReader createReader(String xml) {
        try {
            return new Dom4JXMLReader(DocumentHelper.parseText(xml));
        } catch (DocumentException e) {
            throw new CannotParseXMLException(e);
        }
    }

}
