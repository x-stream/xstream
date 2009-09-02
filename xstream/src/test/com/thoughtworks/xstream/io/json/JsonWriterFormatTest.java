/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 02. September 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;
import com.thoughtworks.xstream.io.json.JsonWriter.Format;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Tests the {@link JsonWriter} formats.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class JsonWriterFormatTest extends TestCase {

    private XStream xstream;
    private Object target;
    private final int mode;
    private final Format format;
    private final String json;

    public JsonWriterFormatTest(
        String name, Object target, String json, int writerMode, JsonWriter.Format format) {
        super(name);
        this.target = target;
        this.json = json;
        this.mode = writerMode;
        this.format = format;

        xstream = new XStream();
        xstream.setMode(XStream.NO_REFERENCES);
    }

    protected void runTest() throws Throwable {
        assertEquals(json, toJSON(mode, format));
    }

    private String toJSON(int mode, JsonWriter.Format format) {
        final StringWriter writer = new StringWriter(1024);
        writeJSON(writer, mode, format);
        return writer.toString();
    }

    private void writeJSON(Writer writer, int mode, JsonWriter.Format format) {
        JsonWriter jsonWriter = new JsonWriter(writer, mode, format, 0);
        xstream.marshal(target, jsonWriter);
        jsonWriter.flush();
    }

    public static Test suite() {
        final Map modes = new OrderRetainingMap();
        modes.put("optimized", new Integer(0));
        modes.put("noRoot", new Integer(AbstractJsonWriter.DROP_ROOT_MODE));
        modes.put("explicit", new Integer(AbstractJsonWriter.EXPLICIT_MODE));

        final Map formats = new OrderRetainingMap();
        formats.put("Minimal", new JsonWriter.Format(
            new char[0], new char[0], JsonWriter.Format.COMPACT_EMPTY_ELEMENT));
        formats.put("Pretty", new JsonWriter.Format(
            "  ".toCharArray(), "\n".toCharArray(), JsonWriter.Format.SPACE_AFTER_LABEL));
        formats.put("Compact", new JsonWriter.Format(
            "  ".toCharArray(), "\n".toCharArray(), JsonWriter.Format.SPACE_AFTER_LABEL
                | JsonWriter.Format.COMPACT_EMPTY_ELEMENT));

        final Properties properties = new Properties();
        properties.put("one", "1");
        final Map targets = new OrderRetainingMap();
        targets.put("String", "text");
        targets.put("StringArray", new String[]{"text", null});
        targets.put("EmptyStringArray", new String[][]{new String[0]});
        targets.put("Properties", properties);

        final Map results = new HashMap();
        results.put("optimizedMinimalString", "{'string':'text'}");
        results.put("optimizedPrettyString", "{'string': 'text'}");
        results.put("optimizedCompactString", "{'string': 'text'}");
        results.put("noRootMinimalString", "'text'");
        results.put("noRootPrettyString", "'text'");
        results.put("noRootCompactString", "'text'");
        results.put("explicitMinimalString", "{'string':'text'}");
        results.put("explicitPrettyString", "{'string': 'text'}");
        results.put("explicitCompactString", "{'string': 'text'}");
        results.put("optimizedMinimalStringArray", "{'string-array':['text',null]}");
        results.put("optimizedPrettyStringArray", "{'string-array': [\n  'text',\n  null\n]}");
        results.put("optimizedCompactStringArray", "{'string-array': [\n  'text',\n  null\n]}");
        results.put("noRootMinimalStringArray", "['text',null]");
        results.put("noRootPrettyStringArray", "[\n  'text',\n  null\n]");
        results.put("noRootCompactStringArray", "[\n  'text',\n  null\n]");
        results.put(
            "explicitMinimalStringArray", "{'string-array':[{'string':'text'},{'null':null}]}");
        results
            .put(
                "explicitPrettyStringArray",
                "{'string-array': [\n  {\n    'string': 'text'\n  },\n  {\n    'null': null\n  }\n]}");
        results
            .put(
                "explicitCompactStringArray",
                "{'string-array': [\n  {\n    'string': 'text'\n  },\n  {\n    'null': null\n  }\n]}");
        results.put("optimizedMinimalEmptyStringArray", "{'string-array-array':[[]]}");
        results
            .put("optimizedPrettyEmptyStringArray", "{'string-array-array': [\n  [\n  ]\n]}");
        results.put("optimizedCompactEmptyStringArray", "{'string-array-array': [\n  []\n]}");
        results.put("noRootMinimalEmptyStringArray", "[[]]");
        results.put("noRootPrettyEmptyStringArray", "[\n  [\n  ]\n]");
        results.put("noRootCompactEmptyStringArray", "[\n  []\n]");
        results.put(
            "explicitMinimalEmptyStringArray", "{'string-array-array':[{'string-array':[]}]}");
        results.put(
            "explicitPrettyEmptyStringArray",
            "{'string-array-array': [\n  {\n    'string-array': [\n    ]\n  }\n]}");
        results.put(
            "explicitCompactEmptyStringArray",
            "{'string-array-array': [\n  {\n    'string-array': []\n  }\n]}");
        results.put(
            "optimizedMinimalProperties", "{'properties':[{'@name':'one','@value':'1'}]}");
        results.put(
            "optimizedPrettyProperties",
            "{'properties': [\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]}");
        results.put(
            "optimizedCompactProperties",
            "{'properties': [\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]}");
        results.put("noRootMinimalProperties", "[{'@name':'one','@value':'1'}]");
        results.put(
            "noRootPrettyProperties", "[\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]");
        results
            .put(
                "noRootCompactProperties",
                "[\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]");
        results.put(
            "explicitMinimalProperties",
            "{'properties':[{'property':{'@name':'one','@value':'1','$':{}}}]}");
        results
            .put(
                "explicitPrettyProperties",
                "{'properties': [\n  {\n    'property': {\n      '@name': 'one',\n      '@value': '1',\n      '$': {\n      }\n    }\n  }\n]}");
        results
            .put(
                "explicitCompactProperties",
                "{'properties': [\n  {\n    'property': {\n      '@name': 'one',\n      '@value': '1',\n      '$': {}\n    }\n  }\n]}");

        TestSuite suite = new TestSuite(JsonWriterFormatTest.class.getName());
        for (final Iterator iterMode = modes.entrySet().iterator(); iterMode.hasNext();) {
            final Map.Entry entryMode = (Map.Entry)iterMode.next();
            final String modeName = (String)entryMode.getKey();
            final int mode = ((Integer)entryMode.getValue()).intValue();
            for (final Iterator iterFormat = formats.entrySet().iterator(); iterFormat
                .hasNext();) {
                final Map.Entry entryFormat = (Map.Entry)iterFormat.next();
                final String formatName = (String)entryFormat.getKey();
                final JsonWriter.Format format = (JsonWriter.Format)entryFormat.getValue();
                for (final Iterator iterTarget = targets.entrySet().iterator(); iterTarget
                    .hasNext();) {
                    final Map.Entry entryTarget = (Map.Entry)iterTarget.next();
                    final String targetName = (String)entryTarget.getKey();
                    final Object target = entryTarget.getValue();
                    final String name = modeName + formatName + targetName;

                    suite.addTest(new JsonWriterFormatTest(name, target, ((String)results
                        .get(name)).replace('\'', '"'), mode, format));
                }
            }
        }

        return suite;
    }
}
