package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StatusEnum;
import com.thoughtworks.acceptance.objects.ColorEnum;

import java.io.*;
import java.util.Currency;

/**
 * @author Chris Kelly
 * @author Joe Walnes
 */ 
public class ReadResolveTest extends AbstractAcceptanceTest {

    public void testReadResolveWithDefaultSerialization() throws IOException, ClassNotFoundException {
        StatusEnum status = StatusEnum.STARTED;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(status);

        byte[] bArray = bout.toByteArray();
        StatusEnum rStatus = null;
        ObjectInputStream in = null;

        ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        rStatus = (StatusEnum) in.readObject();
        assertNotNull(rStatus);

        assertSame(status, rStatus);
    }

    public void testReadResolveWithXStream() {
        StatusEnum status = StatusEnum.STARTED;

        String xml = xstream.toXML(status);
        StatusEnum rStatus = (StatusEnum) xstream.fromXML(xml);

        assertSame(status, rStatus);
    }

    public void testCurrencyIsResolvedToSameInstance() {
        // Currency is a class that contains readResolve()
        Currency currency = Currency.getInstance("USD");
        String expectedXml = "" +
                "<java.util.Currency>\n" +
                "  <currencyCode>USD</currencyCode>\n" +
                "</java.util.Currency>";
        Object result = assertBothWays(currency, expectedXml);
        assertSame(result, currency);
    }

    public void testSupportCommonsLangEnum() {
        Object resultOne = xstream.fromXML(xstream.toXML(ColorEnum.RED));
        Object resultTwo = xstream.fromXML(xstream.toXML(ColorEnum.RED));
        Object resultThree = xstream.fromXML(xstream.toXML(ColorEnum.BLUE));
        assertSame(resultOne, resultTwo);
        assertNotSame(resultOne, resultThree);
    }
}
