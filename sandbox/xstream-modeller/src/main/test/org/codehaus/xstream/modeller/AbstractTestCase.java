package org.codehaus.xstream.modeller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class AbstractTestCase extends TestCase {

	DocumentBuilderFactory factory;

	DocumentBuilder builder;

	@Override
	protected void setUp() throws Exception {
		this.factory = DocumentBuilderFactory.newInstance();
		this.factory.setValidating(false);
		this.builder = factory.newDocumentBuilder();
	}
	
	InputStream toInputStream(String xml) {
		return new ByteArrayInputStream(xml.getBytes());
	}

	public Document toDoc(String xml) throws SAXException, IOException {
		return builder.parse(toInputStream(xml));
	}

}
