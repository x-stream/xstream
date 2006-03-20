package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class CustomMapperTest extends AbstractAcceptanceTest {

    /**
     * A sample mapper strips the underscore prefix of fieldnames in the XML
     */
    private static class FieldPrefixStrippingMapper extends MapperWrapper {
        public FieldPrefixStrippingMapper(Mapper wrapped) {
            super(wrapped);
        }

        public String serializedMember(Class type, String memberName) {
            if (memberName.startsWith("_")) {
                // _blah -> blah
                memberName = memberName.substring(1); // chop off leading char (the underscore)
            } else if (memberName.startsWith("my")) {
                // myBlah -> blah
                memberName = memberName.substring(2, 3).toLowerCase() + memberName.substring(3);
            }
            return super.serializedMember(type, memberName);
        }

        public String realMember(Class type, String serialized) {
            String fieldName = super.realMember(type, serialized);
            // Not very efficient or elegant, but enough to get the point across.
            // Luckily the CachingMapper will ensure this is only ever called once per field per class.
            try {
                type.getDeclaredField("_" + fieldName);
                return "_" + fieldName;
            } catch (NoSuchFieldException e) {
                try {
                    String myified = "my" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    type.getDeclaredField(myified);
                    return myified;
                } catch (NoSuchFieldException e2) {
                    return fieldName;
                }
            }
        }
    }

    public static class ThingWithStupidNamingConventions extends StandardObject {
        String _firstName;
        String lastName;
        int myAge;

        public ThingWithStupidNamingConventions(String firstname, String lastname, int age) {
            _firstName = firstname;
            this.lastName = lastname;
            myAge = age;
        }
    }

    public void testUserDefinedMappingCanAlterFieldName() {
        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new FieldPrefixStrippingMapper(next);
            }
        };
        xstream.alias("thing", ThingWithStupidNamingConventions.class);

        ThingWithStupidNamingConventions in = new ThingWithStupidNamingConventions("Joe", "Walnes", 10);
        String expectedXml = ""
                + "<thing>\n"
                + "  <firstName>Joe</firstName>\n" // look, no underscores!
                + "  <lastName>Walnes</lastName>\n"
                + "  <age>10</age>\n"
                + "</thing>";

        assertBothWays(in, expectedXml);
    }

    private static class PackageStrippingMapper extends MapperWrapper {
        public PackageStrippingMapper(Mapper wrapped) {
            super(wrapped);
        }

        public String serializedClass(Class type) {
            return type.getName().replaceFirst(".*\\.", "");
        }
    }
    
    public void testStripsPackagesUponDeserialization() {
        // obviously this isn't deserializable!
        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new PackageStrippingMapper(next);
            }
        };

        // NOTE: no aliases defined!

        String expectedXml = "" +
                "<Software>\n" +
                "  <vendor>ms</vendor>\n" +
                "  <name>word</name>\n" +
                "</Software>";
        assertEquals(expectedXml, xstream.toXML(new Software("ms", "word")));
    }
    
    public void testOwnMapperChainCanBeRegistered() {
        Mapper mapper = new DefaultMapper(getClass().getClassLoader(), "impl");
        xstream = new XStream(new PureJavaReflectionProvider(), mapper, new DomDriver());
        
        String expected = "" +
                "<com.thoughtworks.acceptance.objects.Software>\n" +
                "  <vendor>ms</vendor>\n" +
                "  <name>word</name>\n" +
                "</com.thoughtworks.acceptance.objects.Software>";
        assertEquals(expected, xstream.toXML(new Software("ms", "word")));
    }
}
