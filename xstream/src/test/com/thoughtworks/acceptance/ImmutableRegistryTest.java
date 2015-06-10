package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.xstream.converters.ConversionException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Geoff on 2015-04-22.
 */
public class ImmutableRegistryTest extends AbstractAcceptanceTest{

    /**
     * see {@link com.thoughtworks.xstream.XStream#setupImmutableTypes()} for the list
     * of built-in immutables, note that URL is one of them.
     */
    public static class URLPair extends StandardObject{
        URL source;
        URL destination;

        public URLPair(URL source, URL destination){
            this.source = source;
            this.destination = destination;
        }

        public URLPair(URL both){
            this.source = both;
            this.destination = both;
        }
    }

    public void testDocumentWithImmutableMembersDontUseXPathByDefault() throws MalformedURLException{
        xstream.alias("URLPair", URLPair.class);

        URL empower = new URL("http://www.empoweroperations.com");
        URLPair pair = new URLPair(empower);

        String expectedXml = "" +
                 "<URLPair>\n" +
                 "  <source>http://www.empoweroperations.com</source>\n" +
                 "  <destination>http://www.empoweroperations.com</destination>\n" +
                 "</URLPair>";

        assertBothWays(pair, expectedXml);
    }

    public void testDocumentWithPreviouslyMutableNowImmutableMembersDeserializeOK() throws MalformedURLException{
        xstream.alias("ThingsDocument", WithNamedList.class);
        xstream.alias("X", X.class);

        WithNamedList instance = new WithNamedList("Exes");
        X sharedRef = new X(1);
        instance.things.add(sharedRef);
        instance.things.add(sharedRef);

        String serialized = xstream.toXML(instance);

        //act
        xstream.addImmutableType(X.class);
        WithNamedList deserialized = (WithNamedList) xstream.fromXML(serialized);

        //assert
        assertNotNull(deserialized);
        assertEquals(new X(1), deserialized.things.get(0));
        assertEquals(new X(1), deserialized.things.get(1));
        assertSame(deserialized.things.get(0), deserialized.things.get(1));
        assertEquals(2, deserialized.things.size());
    }

    public void testDeserializingTypeRegisteredAsImmutableThrowsNiceError() throws MalformedURLException {
        xstream.alias("URLPair", URLPair.class);

        String problemDocument = "" +
                 "<URLPair>\n" +
                 "  <source>http://www.empoweroperations.com</source>\n" +
                 "  <destination reference=\"../source\">\n" +
                 "</URLPair>";

        try{
            xstream.fromXML(problemDocument);
        }
        catch(ConversionException e){
            assertEquals(e.get("class"), "java.net.URL");
            assertTrue(e.getMessage().contains("Invalid reference"));
        }
    }

}
