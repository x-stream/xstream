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
