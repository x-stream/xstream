package com.thoughtworks.acceptance;

import java.util.Currency;

public class ReadResolveTest extends AbstractAcceptanceTest {

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
}
