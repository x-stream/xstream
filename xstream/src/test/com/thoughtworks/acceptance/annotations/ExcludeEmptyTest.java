package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamExcludeEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ruslan Sibgatullin
 */
public class ExcludeEmptyTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    public void testAnnotationForClassWithExcludeEmptyFields() {
        String xml = "<person>\n" +
                "  <name>name</name>\n" +
                "  <ignoredField>2</ignoredField>\n" +
                "  <addresses>\n" +
                "    <string>one</string>\n" +
                "    <string>two</string>\n" +
                "  </addresses>\n" +
                "</person>";
        assertBothWays(new Person(), xml);
    }

    @XStreamAlias("person")
    private static class Person {
        @XStreamExcludeEmpty
        private String name = "name";
        @XStreamExcludeEmpty
        private String emptyField = "";
        @XStreamExcludeEmpty
        private int ignoredField = 2;

        @XStreamExcludeEmpty
        private List<String> addresses = new ArrayList<>(Arrays.asList("one", "two"));
        @XStreamExcludeEmpty
        private List<String> emptyList = new ArrayList<>();
        
        @XStreamExcludeEmpty
        private Map<Integer, String> emptyMap = new HashMap<>();

    }
}
