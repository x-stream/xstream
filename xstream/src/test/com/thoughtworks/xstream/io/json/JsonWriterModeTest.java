/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. September 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonWriter.Format;

import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Tests the {@link JsonWriter} formats.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class JsonWriterModeTest extends TestCase {

    private XStream xstream;
    private Object target;
    private final int mode;
    private final Format format;

    public JsonWriterModeTest(
        String name, int xstreamMode, int writerMode, JsonWriter.Format format) {
        super(name);
        this.mode = writerMode;
        this.format = format;

        X x = new X(42);
        x.aStr = "Codehaus";
        x.innerObj = new Y();
        x.innerObj.yField = "Y";

        target = new ArrayList(Arrays
            .asList(new Object[]{
                new Object[][]{new Object[0]},
                null,
                new Y(),
                x.innerObj,
                new ArrayList(),
                new CharSequence[]{
                    "JUnit", "XStream", new StringBuffer("JSON"), new StringBuffer("JScript")},
                x,}));

        xstream = new XStream();
        xstream.setMode(xstreamMode);
        xstream.alias("X", X.class);
        xstream.alias("Y", Y.class);
        xstream.alias("CharSequence", CharSequence.class);
    }

    protected void runTest() throws Throwable {
        // toConsole(mode, format);
        String json = toJSON(mode, format);
        assertValidJSON(json);
    }

    private static void assertValidJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        assertEquals(jsonObject.toString(), new JSONObject(jsonObject.toString()).toString());
    }

    private String toJSON(int mode, JsonWriter.Format format) {
        final StringWriter writer = new StringWriter(1024);
        writeJSON(writer, mode, format);
        return writer.toString();
    }

    private void toConsole(int mode, JsonWriter.Format format) {
        System.out.println(xstream.toXML(target));
        try {
            writeJSON(new OutputStreamWriter(System.err, "UTF-8"), mode, format);
            System.err.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJSON(Writer writer, int mode, JsonWriter.Format format) {
        JsonWriter jsonWriter = new JsonWriter(writer, mode, format, 0);
        xstream.marshal(target, jsonWriter);
        jsonWriter.flush();
    }

    public static Test suite() {
        JsonWriter.Format compactFormat = new JsonWriter.Format(
            new char[0], new char[0], JsonWriter.Format.COMPACT_EMPTY_ELEMENT);
        JsonWriter.Format prettyFormat = new JsonWriter.Format("  ".toCharArray(), "\n"
            .toCharArray(), JsonWriter.Format.SPACE_AFTER_LABEL);

        TestSuite suite = new TestSuite(JsonWriterModeTest.class.getName());
        suite.addTest(new JsonWriterModeTest(
            "optimizedCompact", XStream.NO_REFERENCES, 0, compactFormat));
        suite.addTest(new JsonWriterModeTest(
            "optimizedPretty", XStream.NO_REFERENCES, 0, prettyFormat));
        suite.addTest(new JsonWriterModeTest(
            "explicitCompact", XStream.NO_REFERENCES, AbstractJsonWriter.EXPLICIT_MODE,
            compactFormat));
        suite.addTest(new JsonWriterModeTest(
            "explicitPretty", XStream.NO_REFERENCES, AbstractJsonWriter.EXPLICIT_MODE,
            prettyFormat));
        suite.addTest(new JsonWriterModeTest(
            "optimizedCompactWithIds", XStream.ID_REFERENCES, 0, compactFormat));
        suite.addTest(new JsonWriterModeTest(
            "optimizedPrettyWithIds", XStream.ID_REFERENCES, 0, prettyFormat));
        suite.addTest(new JsonWriterModeTest(
            "explicitCompactWithIds", XStream.ID_REFERENCES, AbstractJsonWriter.EXPLICIT_MODE,
            compactFormat));
        suite.addTest(new JsonWriterModeTest(
            "explicitPrettyWithIds", XStream.ID_REFERENCES, AbstractJsonWriter.EXPLICIT_MODE,
            prettyFormat));

        return suite;
    }
}
