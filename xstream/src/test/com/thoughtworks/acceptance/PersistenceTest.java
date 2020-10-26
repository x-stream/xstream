/*
 * Copyright (C) 2008, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. November 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlArrayList;


/**
 * Tests the persistence package.
 *
 * @author J&ouml;rg Schaible
 */
public class PersistenceTest extends AbstractAcceptanceTest {

    private File dir;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dir = new File("target/test-storage");
        dir.mkdirs();
        cleanUp();
    }

    @Override
    protected void tearDown() throws Exception {
        cleanUp();
        dir.delete();
        super.tearDown();
    }

    private void cleanUp() {
        final File[] files = dir.listFiles();
        for (final File file : files) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    private final class PersistenceArrayListConverter implements Converter {
        @Override
        public void marshal(final Object source, final HierarchicalStreamWriter writer,
                final MarshallingContext context) {
            final XmlArrayList<Object> list = new XmlArrayList<>(new FilePersistenceStrategy<Integer, Object>(dir,
                xstream));
            context.convertAnother(dir);
            list.addAll((Collection<?>)source);
        }

        @Override
        public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final File directory = (File)context.convertAnother(null, File.class);
            final XmlArrayList<Object> persistentList = new XmlArrayList<>(new FilePersistenceStrategy<Integer, Object>(
                directory, xstream));
            final ArrayList<Object> list = new ArrayList<>(persistentList);
            // persistentList.clear(); // remove files
            return list;
        }

        @Override
        public boolean canConvert(final Class<?> type) {
            return type == ArrayList.class;
        }
    }

    public void testCanUsePersistenceCollectionAsConverter() throws IOException {
        xstream.alias("lists", SampleLists.class);
        xstream.alias("software", Software.class);
        xstream.registerLocalConverter(SampleLists.class, "good", new PersistenceArrayListConverter());

        final SampleLists<Object, ?> lists = new SampleLists<>();
        lists.good.add("Guilherme");
        lists.good.add(Integer.valueOf(1970));
        lists.good.add(new Software("Codehaus", "XStream"));

        final String expected = ""
            + "<lists>\n"
            + "  <good>"
            + dir.getPath()
            + "</good>\n"
            + "  <bad class=\"list\"/>\n"
            + "</lists>";

        // assumes 'lists' is serialized first
        final SampleLists<Object, ?> serialized = assertBothWays(lists, expected);

        // compare original list and list written in separate XML file
        assertEquals(lists.good, serialized.good);

        // retrieve value from external file
        try (final FileInputStream inputStream = new FileInputStream(new File(dir, "int@2.xml"))) {
            assertEquals(lists.good.get(2), xstream.fromXML(inputStream));
        }
    }
}
