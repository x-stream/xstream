package com.thoughtworks.acceptance;


import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeMapAndTreeSetTest extends AbstractAcceptanceTest {

    public static class MyComparator implements Comparator {
        private String something = "stuff";

        public int compare(Object o1, Object o2) {
            return ((String) o1).compareTo((String) o2);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("my-comparator", MyComparator.class);
    }

    public void testTreeMapWithComparator() {
        TreeMap map = new TreeMap(new MyComparator());
        map.put("benny", "hill");
        map.put("joe", "walnes");

        String expected = "" +
                "<tree-map>\n" +
                "  <comparator class=\"my-comparator\">\n" +
                "    <something>stuff</something>\n" +
                "  </comparator>\n" +
                "  <entry>\n" +
                "    <string>benny</string>\n" +
                "    <string>hill</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>joe</string>\n" +
                "    <string>walnes</string>\n" +
                "  </entry>\n" +
                "</tree-map>";

        TreeMap result = (TreeMap) assertBothWays(map, expected);
        assertEquals(MyComparator.class, result.comparator().getClass());
    }

    public void testTreeMapWithoutComparator() {
        TreeMap map = new TreeMap();
        map.put("benny", "hill");
        map.put("joe", "walnes");

        String expected = "" +
                "<tree-map>\n" +
                "  <no-comparator/>\n" +
                "  <entry>\n" +
                "    <string>benny</string>\n" +
                "    <string>hill</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>joe</string>\n" +
                "    <string>walnes</string>\n" +
                "  </entry>\n" +
                "</tree-map>";

        TreeMap result = (TreeMap) assertBothWays(map, expected);
        assertNull(result.comparator());
    }

    public void testTreeSetWithComparator() {
        TreeSet set = new TreeSet(new MyComparator());
        set.add("hi");
        set.add("bye");

        String expected = "" +
                "<tree-set>\n" +
                "  <comparator class=\"my-comparator\">\n" +
                "    <something>stuff</something>\n" +
                "  </comparator>\n" +
                "  <string>bye</string>\n" +
                "  <string>hi</string>\n" +
                "</tree-set>";

        TreeSet result = (TreeSet) assertBothWays(set, expected);
        assertEquals(MyComparator.class, result.comparator().getClass());
    }

    public void testTreeSetWithoutComparator() {
        TreeSet set = new TreeSet();
        set.add("hi");
        set.add("bye");

        String expected = "" +
                "<tree-set>\n" +
                "  <no-comparator/>\n" +
                "  <string>bye</string>\n" +
                "  <string>hi</string>\n" +
                "</tree-set>";

        assertBothWays(set, expected);
    }
}
