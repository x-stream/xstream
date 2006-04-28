/*
 * Copyright (c) 2004-2006 Elsag Solutions AG, Germany. All rights reserved.
 * www.elsag-solutions.com
 *
 * $Id$
 */

package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;


/**
 * DOCUMENT_ME
 *
 * @author  jos / last modified by $Author$
 * @version $Revision$
 */
public class JDomAcceptanceTest extends TestCase
{
	//~ Instance fields --------------------------------------------------------

	private XStream xstream;

	//~ Methods ----------------------------------------------------------------

	/**
	 * DOCUMENT_ME
	 *
	 * @throws Exception DOCUMENT_ME
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		xstream = new XStream();
		xstream.alias("x", X.class);
	}

	/**
	 * DOCUMENT_ME
	 *
	 * @throws Exception DOCUMENT_ME
	 */
	public void testUnmarshalsObjectFromJDOM() throws Exception
	{
		String xml =
			"<x>"
			+ "  <aStr>joe</aStr>"
			+ "  <anInt>8</anInt>"
			+ "  <innerObj>"
			+ "    <yField>walnes</yField>"
			+ "  </innerObj>"
			+ "</x>";

		Document doc = new SAXBuilder().build(new StringReader(xml));

		X x = (X) xstream.unmarshal(new JDomReader(doc));

		assertEquals("joe", x.aStr);
		assertEquals(8, x.anInt);
		assertEquals("walnes", x.innerObj.yField);
	}

	/**
	 * DOCUMENT_ME
	 */
	public void testMarshalsObjectToJDOM()
	{
		X x = new X();
		x.anInt = 9;
		x.aStr = "zzz";
		x.innerObj = new Y();
		x.innerObj.yField = "ooo";

		String expected =
			"<x>\n"
			+ "  <aStr>zzz</aStr>\n"
			+ "  <anInt>9</anInt>\n"
			+ "  <innerObj>\n"
			+ "    <yField>ooo</yField>\n"
			+ "  </innerObj>\n"
			+ "</x>";

		JDomWriter writer = new JDomWriter();
		xstream.marshal(x, writer);

		List result = writer.getResult();

		assertEquals("Result list should contain exactly 1 element", 1, result.size());

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n"));

		assertEquals(expected, outputter.outputString(result));
	}
}
