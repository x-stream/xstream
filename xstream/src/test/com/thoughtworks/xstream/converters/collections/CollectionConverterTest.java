package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Ruslan Sibgatullin
 */
public class CollectionConverterTest extends AbstractAcceptanceTest {

    public void testMarshallArrayListOfStrings() {
        Collection<String> input = new ArrayList<>(Arrays.asList("one", "two", "three"));

        String expected = "<list>\n" +
                "  <string>one</string>\n" +
                "  <string>two</string>\n" +
                "  <string>three</string>\n" +
                "</list>";
        assertBothWays(input, expected);
    }

    public void testMarshallEmptyList() {
        Collection<String> input = Collections.emptyList();

        String expected = "<empty-list/>";
        assertBothWays(input, expected);
    }
    
    public void testMarshallEmptyArray() {
        Collection<String> input = new ArrayList<>();

        String expected = "<list/>";
        assertBothWays(input, expected);
    }

}
