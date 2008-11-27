/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. November 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Tests the persistence package.
 * 
 * @author J&ouml;rg Schaible
 */
public class PersistenceTest extends AbstractAcceptanceTest {

    private File dir;

    protected void setUp() throws Exception {
        super.setUp();
        dir = new File("target/test-storage");
        dir.mkdirs();
        cleanUp();
    }

    protected void tearDown() throws Exception {
        cleanUp();
        dir.delete();
        super.tearDown();
    }

    private void cleanUp() {
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length; ++i) {
            if (files[i].isFile()) {
                files[i].delete();
            }
        }
    }

    private final class PersistenceArrayListConverter implements Converter {
        public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
            final XmlArrayList list = new XmlArrayList(new FilePersistenceStrategy(dir, xstream));
            context.convertAnother(dir);
            list.addAll((Collection)source);
        }

        public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
            final File directory = (File)context.convertAnother(null, File.class);
            final XmlArrayList persistentList = new XmlArrayList(new FilePersistenceStrategy(directory, xstream));
            final ArrayList list = new ArrayList(persistentList);
            //persistentList.clear(); // remove files
            return list;
        }

        public boolean canConvert(Class type) {
            return type == ArrayList.class;
        }
    }

    public void testCanUsePersistenceCollectionAsConverter() throws IOException {
        xstream.alias("lists", SampleLists.class);
        xstream.alias("software", Software.class);
        xstream.registerLocalConverter(SampleLists.class, "good", new PersistenceArrayListConverter());
        
        SampleLists lists = new SampleLists();
        lists.good.add("Guilherme");
        lists.good.add(new Integer(1970));
        lists.good.add(new Software("Codehaus", "XStream"));

        String expected = "" +
                "<lists>\n" +
                "  <good>" + dir.getPath() + "</good>\n" +
                "  <bad class=\"list\"/>\n" + 
                "</lists>";

        // assumes 'lists' is serialized first 
        SampleLists serialized = (SampleLists)assertBothWays(lists, expected);
        
        // compare original list and list written in separate XML file 
        assertEquals(lists.good, serialized.good);
        
        // retrieve value from external file
        FileInputStream inputStream = new FileInputStream(new File(dir, "int@2.xml"));
        try {
            assertEquals(lists.good.get(2), xstream.fromXML(inputStream));
        } finally {
            inputStream.close();
        }
    }
}
