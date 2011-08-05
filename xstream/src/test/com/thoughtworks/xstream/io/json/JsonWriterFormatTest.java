/*
 * Copyright (C) 2009, 2010, 2011 XStream Committers.
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
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;
import com.thoughtworks.xstream.io.json.JsonWriter.Format;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Tests the {@link JsonWriter} formats.
 * 
 * @author J&ouml;rg Schaible
 */
public class JsonWriterFormatTest extends TestCase {

    private XStream xstream;
    private Object target;
    private final int mode;
    private final Format format;
    private final String json;

    public static class YString extends Y {
        public YString(String y) {
            this.yField = y;
        }
        public String toString() {
            return yField;
        }
    }
    
    public JsonWriterFormatTest(
        String name, Object target, String json, int writerMode, JsonWriter.Format format) {
        super(name);
        this.target = target;
        this.json = json;
        this.mode = writerMode;
        this.format = format;

        xstream = new XStream();
        xstream.setMode(name.endsWith("+ID") ? XStream.ID_REFERENCES : XStream.NO_REFERENCES);
        xstream.alias("chseq", CharSequence.class);
        xstream.alias("oss", OpenSourceSoftware.class);
        xstream.alias("collections", SampleLists.class);
        xstream.alias("x", X.class);
        xstream.alias("ys", YString.class);
        xstream.useAttributeFor(OpenSourceSoftware.class, "license");
        try {
            xstream.registerConverter(new ToStringConverter(YString.class));
        } catch (NoSuchMethodException e) {
            throw new AssertionFailedError(e.getMessage());
        }
    }

    protected void runTest() throws Throwable {
        assertEquals(json, toJSON(mode, format));
    }

    private String toJSON(int mode, JsonWriter.Format format) {
        final StringWriter writer = new StringWriter(1024);
        try {
            writeJSON(writer, mode, format);
            return writer.toString();
        } finally {
            //System.out.println(writer.toString() + "  ---> " + getName());
        }
    }

    private void writeJSON(Writer writer, int mode, JsonWriter.Format format) {
        JsonWriter jsonWriter = new JsonWriter(writer, mode, format, 0);
        try {
            xstream.marshal(target, jsonWriter);
        } finally {
            jsonWriter.flush();
        }
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
        final X x = new X();
        x.anInt = 42;
        x.aStr = "X";
        x.innerObj = new YString("Y");
        final X emptyX = new X();
        emptyX.innerObj = new Y();
        final SampleLists lists = new SampleLists();
        lists.good = new LinkedList();
        lists.good.add("XStream");
        lists.bad = new TreeSet();
        lists.bad.add(new X());
        final Map targets = new OrderRetainingMap();
        targets.put("String", "text");
        targets.put("CharSequenceArray", new CharSequence[]{"text", new StringBuffer("buffer"), null});
        targets.put("CharSequenceArray+ID", new CharSequence[]{"text", new StringBuffer("buffer"), null});
        targets.put("EmptyStringArray", new String[][]{new String[0]});
        targets.put("EmptyStringArray+ID", new String[][]{new String[0]});
        targets.put("Properties", properties);
        targets.put("Object", new OpenSourceSoftware("Codehaus", "XStream", "BSD"));
        targets.put("AttributeOnly", new OpenSourceSoftware(null, null, "BSD"));
        targets.put("X", x);
        targets.put("EmptyX", emptyX);
        targets.put("Collections", lists);

        final Map results = new HashMap();
        results.put("optimizedMinimalString", "{'string':'text'}");
        results.put("optimizedPrettyString", "{'string': 'text'}");
        results.put("optimizedCompactString", "{'string': 'text'}");
        results.put("noRootMinimalString", "'text'");
        results.put("noRootPrettyString", "'text'");
        results.put("noRootCompactString", "'text'");
        results.put("explicitMinimalString", "{'string':[[],['text']]}");
        results.put("explicitPrettyString", "{'string': [\n  [\n  ],\n  [\n    'text'\n  ]\n]}");
        results.put("explicitCompactString", "{'string': [\n  [],\n  [\n    'text'\n  ]\n]}");
        results.put("optimizedMinimalCharSequenceArray", "{'chseq-array':['text','buffer',null]}");
        results.put("optimizedPrettyCharSequenceArray", "{'chseq-array': [\n  'text',\n  'buffer',\n  null\n]}");
        results.put("optimizedCompactCharSequenceArray", "{'chseq-array': [\n  'text',\n  'buffer',\n  null\n]}");
        results.put("noRootMinimalCharSequenceArray", "['text','buffer',null]");
        results.put("noRootPrettyCharSequenceArray", "[\n  'text',\n  'buffer',\n  null\n]");
        results.put("noRootCompactCharSequenceArray", "[\n  'text',\n  'buffer',\n  null\n]");
        results.put("explicitMinimalCharSequenceArray", "{'chseq-array':[[],[{'string':[[],['text']]},{'string-buffer':[[],['buffer']]},{'null':[[],[null]]}]]}");
        results.put("explicitPrettyCharSequenceArray", "{'chseq-array': [\n  [\n  ],\n  [\n    {\n      'string': [\n        [\n        ],\n        [\n          'text'\n        ]\n      ]\n    },\n    {\n      'string-buffer': [\n        [\n        ],\n        [\n          'buffer'\n        ]\n      ]\n    },\n    {\n      'null': [\n        [\n        ],\n        [\n          null\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactCharSequenceArray", "{'chseq-array': [\n  [],\n  [\n    {\n      'string': [\n        [],\n        [\n          'text'\n        ]\n      ]\n    },\n    {\n      'string-buffer': [\n        [],\n        [\n          'buffer'\n        ]\n      ]\n    },\n    {\n      'null': [\n        [],\n        [\n          null\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalCharSequenceArray+ID", "{'chseq-array':['text',{'@id':'2','$':'buffer'},null]}");
        results.put("optimizedPrettyCharSequenceArray+ID", "{'chseq-array': [\n  'text',\n  {\n    '@id': '2',\n    '$': 'buffer'\n  },\n  null\n]}");
        results.put("optimizedCompactCharSequenceArray+ID", "{'chseq-array': [\n  'text',\n  {\n    '@id': '2',\n    '$': 'buffer'\n  },\n  null\n]}");
        results.put("noRootMinimalCharSequenceArray+ID", "['text',{'@id':'2','$':'buffer'},null]");
        results.put("noRootPrettyCharSequenceArray+ID", "[\n  'text',\n  {\n    '@id': '2',\n    '$': 'buffer'\n  },\n  null\n]");
        results.put("noRootCompactCharSequenceArray+ID", "[\n  'text',\n  {\n    '@id': '2',\n    '$': 'buffer'\n  },\n  null\n]");
        results.put("explicitMinimalCharSequenceArray+ID", "{'chseq-array':[[{'id':'1'}],[{'string':[[],['text']]},{'string-buffer':[[{'id':'2'}],['buffer']]},{'null':[[],[null]]}]]}");
        results.put("explicitPrettyCharSequenceArray+ID", "{'chseq-array': [\n  [\n    {\n      'id': '1'\n    }\n  ],\n  [\n    {\n      'string': [\n        [\n        ],\n        [\n          'text'\n        ]\n      ]\n    },\n    {\n      'string-buffer': [\n        [\n          {\n            'id': '2'\n          }\n        ],\n        [\n          'buffer'\n        ]\n      ]\n    },\n    {\n      'null': [\n        [\n        ],\n        [\n          null\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactCharSequenceArray+ID", "{'chseq-array': [\n  [\n    {\n      'id': '1'\n    }\n  ],\n  [\n    {\n      'string': [\n        [],\n        [\n          'text'\n        ]\n      ]\n    },\n    {\n      'string-buffer': [\n        [\n          {\n            'id': '2'\n          }\n        ],\n        [\n          'buffer'\n        ]\n      ]\n    },\n    {\n      'null': [\n        [],\n        [\n          null\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalEmptyStringArray", "{'string-array-array':[[]]}");
        results.put("optimizedPrettyEmptyStringArray", "{'string-array-array': [\n  [\n  ]\n]}");
        results.put("optimizedCompactEmptyStringArray", "{'string-array-array': [\n  []\n]}");
        results.put("noRootMinimalEmptyStringArray", "[[]]");
        results.put("noRootPrettyEmptyStringArray", "[\n  [\n  ]\n]");
        results.put("noRootCompactEmptyStringArray", "[\n  []\n]");
        results.put("explicitMinimalEmptyStringArray", "{'string-array-array':[[],[{'string-array':[[],[]]}]]}");
        results.put("explicitPrettyEmptyStringArray", "{'string-array-array': [\n  [\n  ],\n  [\n    {\n      'string-array': [\n        [\n        ],\n        [\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactEmptyStringArray", "{'string-array-array': [\n  [],\n  [\n    {\n      'string-array': [\n        [],\n        []\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalEmptyStringArray+ID", "{'string-array-array':[[]]}");
        results.put("optimizedPrettyEmptyStringArray+ID", "{'string-array-array': [\n  [\n  ]\n]}");
        results.put("optimizedCompactEmptyStringArray+ID", "{'string-array-array': [\n  []\n]}");
        results.put("noRootMinimalEmptyStringArray+ID", "[[]]");
        results.put("noRootPrettyEmptyStringArray+ID", "[\n  [\n  ]\n]");
        results.put("noRootCompactEmptyStringArray+ID", "[\n  []\n]");
        results.put("explicitMinimalEmptyStringArray+ID", "{'string-array-array':[[{'id':'1'}],[{'string-array':[[{'id':'2'}],[]]}]]}");
        results.put("explicitPrettyEmptyStringArray+ID", "{'string-array-array': [\n  [\n    {\n      'id': '1'\n    }\n  ],\n  [\n    {\n      'string-array': [\n        [\n          {\n            'id': '2'\n          }\n        ],\n        [\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactEmptyStringArray+ID", "{'string-array-array': [\n  [\n    {\n      'id': '1'\n    }\n  ],\n  [\n    {\n      'string-array': [\n        [\n          {\n            'id': '2'\n          }\n        ],\n        []\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalProperties", "{'properties':[{'@name':'one','@value':'1'}]}");
        results.put("optimizedPrettyProperties", "{'properties': [\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]}");
        results.put("optimizedCompactProperties", "{'properties': [\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]}");
        results.put("noRootMinimalProperties", "[{'@name':'one','@value':'1'}]");
        results.put("noRootPrettyProperties", "[\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]");
        results.put("noRootCompactProperties", "[\n  {\n    '@name': 'one',\n    '@value': '1'\n  }\n]");
        results.put("explicitMinimalProperties", "{'properties':[[],[{'property':[[{'name':'one','value':'1'}],[]]}]]}");
        results.put("explicitPrettyProperties", "{'properties': [\n  [\n  ],\n  [\n    {\n      'property': [\n        [\n          {\n            'name': 'one',\n            'value': '1'\n          }\n        ],\n        [\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactProperties", "{'properties': [\n  [],\n  [\n    {\n      'property': [\n        [\n          {\n            'name': 'one',\n            'value': '1'\n          }\n        ],\n        []\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalObject", "{'oss':{'@license':'BSD','vendor':'Codehaus','name':'XStream'}}");
        results.put("optimizedPrettyObject", "{'oss': {\n  '@license': 'BSD',\n  'vendor': 'Codehaus',\n  'name': 'XStream'\n}}");
        results.put("optimizedCompactObject", "{'oss': {\n  '@license': 'BSD',\n  'vendor': 'Codehaus',\n  'name': 'XStream'\n}}");
        results.put("noRootMinimalObject", "{'@license':'BSD','vendor':'Codehaus','name':'XStream'}");
        results.put("noRootPrettyObject", "{\n  '@license': 'BSD',\n  'vendor': 'Codehaus',\n  'name': 'XStream'\n}");
        results.put("noRootCompactObject", "{\n  '@license': 'BSD',\n  'vendor': 'Codehaus',\n  'name': 'XStream'\n}");
        results.put("explicitMinimalObject", "{'oss':[[{'license':'BSD'}],[{'vendor':[[],['Codehaus']]},{'name':[[],['XStream']]}]]}");
        results.put("explicitPrettyObject", "{'oss': [\n  [\n    {\n      'license': 'BSD'\n    }\n  ],\n  [\n    {\n      'vendor': [\n        [\n        ],\n        [\n          'Codehaus'\n        ]\n      ]\n    },\n    {\n      'name': [\n        [\n        ],\n        [\n          'XStream'\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactObject", "{'oss': [\n  [\n    {\n      'license': 'BSD'\n    }\n  ],\n  [\n    {\n      'vendor': [\n        [],\n        [\n          'Codehaus'\n        ]\n      ]\n    },\n    {\n      'name': [\n        [],\n        [\n          'XStream'\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalAttributeOnly", "{'oss':{'@license':'BSD'}}");
        results.put("optimizedPrettyAttributeOnly", "{'oss': {\n  '@license': 'BSD'\n}}");
        results.put("optimizedCompactAttributeOnly", "{'oss': {\n  '@license': 'BSD'\n}}");
        results.put("noRootMinimalAttributeOnly", "{'@license':'BSD'}");
        results.put("noRootPrettyAttributeOnly", "{\n  '@license': 'BSD'\n}");
        results.put("noRootCompactAttributeOnly", "{\n  '@license': 'BSD'\n}");
        results.put("explicitMinimalAttributeOnly", "{'oss':[[{'license':'BSD'}],[]]}");
        results.put("explicitPrettyAttributeOnly", "{'oss': [\n  [\n    {\n      'license': 'BSD'\n    }\n  ],\n  [\n  ]\n]}");
        results.put("explicitCompactAttributeOnly", "{'oss': [\n  [\n    {\n      'license': 'BSD'\n    }\n  ],\n  []\n]}");
        results.put("optimizedMinimalX", "{'x':{'aStr':'X','anInt':42,'innerObj':{'@class':'ys','$':'Y'}}}");
        results.put("optimizedPrettyX", "{'x': {\n  'aStr': 'X',\n  'anInt': 42,\n  'innerObj': {\n    '@class': 'ys',\n    '$': 'Y'\n  }\n}}");
        results.put("optimizedCompactX", "{'x': {\n  'aStr': 'X',\n  'anInt': 42,\n  'innerObj': {\n    '@class': 'ys',\n    '$': 'Y'\n  }\n}}");
        results.put("noRootMinimalX", "{'aStr':'X','anInt':42,'innerObj':{'@class':'ys','$':'Y'}}");
        results.put("noRootPrettyX", "{\n  'aStr': 'X',\n  'anInt': 42,\n  'innerObj': {\n    '@class': 'ys',\n    '$': 'Y'\n  }\n}");
        results.put("noRootCompactX", "{\n  'aStr': 'X',\n  'anInt': 42,\n  'innerObj': {\n    '@class': 'ys',\n    '$': 'Y'\n  }\n}");
        results.put("explicitMinimalX", "{'x':[[],[{'aStr':[[],['X']]},{'anInt':[[],[42]]},{'innerObj':[[{'class':'ys'}],['Y']]}]]}");
        results.put("explicitPrettyX", "{'x': [\n  [\n  ],\n  [\n    {\n      'aStr': [\n        [\n        ],\n        [\n          'X'\n        ]\n      ]\n    },\n    {\n      'anInt': [\n        [\n        ],\n        [\n          42\n        ]\n      ]\n    },\n    {\n      'innerObj': [\n        [\n          {\n            'class': 'ys'\n          }\n        ],\n        [\n          'Y'\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactX", "{'x': [\n  [],\n  [\n    {\n      'aStr': [\n        [],\n        [\n          'X'\n        ]\n      ]\n    },\n    {\n      'anInt': [\n        [],\n        [\n          42\n        ]\n      ]\n    },\n    {\n      'innerObj': [\n        [\n          {\n            'class': 'ys'\n          }\n        ],\n        [\n          'Y'\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalEmptyX", "{'x':{'anInt':0,'innerObj':{}}}");
        results.put("optimizedPrettyEmptyX", "{'x': {\n  'anInt': 0,\n  'innerObj': {\n  }\n}}");
        results.put("optimizedCompactEmptyX", "{'x': {\n  'anInt': 0,\n  'innerObj': {}\n}}");
        results.put("noRootMinimalEmptyX", "{'anInt':0,'innerObj':{}}");
        results.put("noRootPrettyEmptyX", "{\n  'anInt': 0,\n  'innerObj': {\n  }\n}");
        results.put("noRootCompactEmptyX", "{\n  'anInt': 0,\n  'innerObj': {}\n}");
        results.put("explicitMinimalEmptyX", "{'x':[[],[{'anInt':[[],[0]]},{'innerObj':[[],[]]}]]}");
        results.put("explicitPrettyEmptyX", "{'x': [\n  [\n  ],\n  [\n    {\n      'anInt': [\n        [\n        ],\n        [\n          0\n        ]\n      ]\n    },\n    {\n      'innerObj': [\n        [\n        ],\n        [\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactEmptyX", "{'x': [\n  [],\n  [\n    {\n      'anInt': [\n        [],\n        [\n          0\n        ]\n      ]\n    },\n    {\n      'innerObj': [\n        [],\n        []\n      ]\n    }\n  ]\n]}");
        results.put("optimizedMinimalCollections", "{'collections':{'good':['XStream'],'bad':[{'anInt':0}]}}");
        results.put("optimizedPrettyCollections", "{'collections': {\n  'good': [\n    'XStream'\n  ],\n  'bad': [\n    {\n      'anInt': 0\n    }\n  ]\n}}");
        results.put("optimizedCompactCollections", "{'collections': {\n  'good': [\n    'XStream'\n  ],\n  'bad': [\n    {\n      'anInt': 0\n    }\n  ]\n}}");
        results.put("noRootMinimalCollections", "{'good':['XStream'],'bad':[{'anInt':0}]}");
        results.put("noRootPrettyCollections", "{\n  'good': [\n    'XStream'\n  ],\n  'bad': [\n    {\n      'anInt': 0\n    }\n  ]\n}");
        results.put("noRootCompactCollections", "{\n  'good': [\n    'XStream'\n  ],\n  'bad': [\n    {\n      'anInt': 0\n    }\n  ]\n}");
        results.put("explicitMinimalCollections", "{'collections':[[],[{'good':[[{'class':'linked-list'}],[{'string':[[],['XStream']]}]]},{'bad':[[{'class':'sorted-set'}],[{'x':[[],[{'anInt':[[],[0]]}]]}]]}]]}");
        results.put("explicitPrettyCollections", "{'collections': [\n  [\n  ],\n  [\n    {\n      'good': [\n        [\n          {\n            'class': 'linked-list'\n          }\n        ],\n        [\n          {\n            'string': [\n              [\n              ],\n              [\n                'XStream'\n              ]\n            ]\n          }\n        ]\n      ]\n    },\n    {\n      'bad': [\n        [\n          {\n            'class': 'sorted-set'\n          }\n        ],\n        [\n          {\n            'x': [\n              [\n              ],\n              [\n                {\n                  'anInt': [\n                    [\n                    ],\n                    [\n                      0\n                    ]\n                  ]\n                }\n              ]\n            ]\n          }\n        ]\n      ]\n    }\n  ]\n]}");
        results.put("explicitCompactCollections", "{'collections': [\n  [],\n  [\n    {\n      'good': [\n        [\n          {\n            'class': 'linked-list'\n          }\n        ],\n        [\n          {\n            'string': [\n              [],\n              [\n                'XStream'\n              ]\n            ]\n          }\n        ]\n      ]\n    },\n    {\n      'bad': [\n        [\n          {\n            'class': 'sorted-set'\n          }\n        ],\n        [\n          {\n            'x': [\n              [],\n              [\n                {\n                  'anInt': [\n                    [],\n                    [\n                      0\n                    ]\n                  ]\n                }\n              ]\n            ]\n          }\n        ]\n      ]\n    }\n  ]\n]}");
        
        TestSuite suite = new TestSuite(JsonWriterFormatTest.class.getName());
        for (final Iterator iterMode = modes.entrySet().iterator(); iterMode.hasNext();) {
            final Map.Entry entryMode = (Map.Entry)iterMode.next();
            final String modeName = (String)entryMode.getKey();
            final int mode = ((Integer)entryMode.getValue()).intValue();
            for (final Iterator iterFormat = formats.entrySet().iterator(); iterFormat.hasNext();) {
                final Map.Entry entryFormat = (Map.Entry)iterFormat.next();
                final String formatName = (String)entryFormat.getKey();
                final JsonWriter.Format format = (JsonWriter.Format)entryFormat.getValue();
                for (final Iterator iterTarget = targets.entrySet().iterator(); iterTarget.hasNext();) {
                    final Map.Entry entryTarget = (Map.Entry)iterTarget.next();
                    final String targetName = (String)entryTarget.getKey();
                    final Object target = entryTarget.getValue();
                    final String name = modeName + formatName + targetName;
                    final String result = ((String)results.get(name)).replace('\'', '"');
                    
                    suite.addTest(new JsonWriterFormatTest(name, target, result, mode, format));
                }
            }
        }

        return suite;
    }
}
