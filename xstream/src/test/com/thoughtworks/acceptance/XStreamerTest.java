/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import java.io.ObjectStreamException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamer;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.security.TypePermission;


/**
 * @author J&ouml;rg Schaible
 */
public class XStreamerTest extends AbstractAcceptanceTest {

    private Transformer transformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final URL url = getClass().getResource("XStreamer.xsl");
        transformer = transformerFactory.newTransformer(new StreamSource(url.openStream()));
    }

    final static class ImplicitXStreamContainer {
        @SuppressWarnings("unused")
        private XStream myXStream;
    }

    public void testDetectsSelfMarshalling() {
        final ImplicitXStreamContainer c = new ImplicitXStreamContainer();
        c.myXStream = xstream;
        try {
            xstream.toXML(c);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().contains("XStream instance"));
        }
    }

    public void testCanConvertAnotherInstance() throws TransformerException {
        final XStream x = createXStream();
        final String xml = normalizedXStreamXML(xstream.toXML(x));
        for (final TypePermission permission : XStreamer.getDefaultPermissions()) {
            xstream.addPermission(permission);
        }
        final XStream serialized = (XStream)xstream.fromXML(xml);
        final String xmlSerialized = normalizedXStreamXML(xstream.toXML(serialized));
        assertEquals(xml, xmlSerialized);
    }

    public void testCanBeUsedAfterSerialization() throws TransformerException {
        final String xml = xstream.toXML(createXStream());
        for (final TypePermission permission : XStreamer.getDefaultPermissions()) {
            xstream.addPermission(permission);
        }
        xstream = (XStream)xstream.fromXML(xml);
        testCanConvertAnotherInstance();
    }

    public void testCanSerializeSelfContained() throws ClassNotFoundException, ObjectStreamException {
        final OpenSourceSoftware oos = new OpenSourceSoftware("Walnes", "XStream", "BSD");
        xstream.alias("software", OpenSourceSoftware.class);
        final String xml = new XStreamer().toXML(xstream, oos);
        assertEquals(oos, new XStreamer().fromXML(xml));
    }

    private String normalizedXStreamXML(final String xml) throws TransformerException {
        final StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        return writer.toString();
    }
}
