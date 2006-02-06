package com.thoughtworks.acceptance;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.thoughtworks.xstream.core.JVM;


public class EncodingTest extends AbstractAcceptanceTest {

    public void testCanDealWithUtfText() {
        assertBothWays("J\u00F6rg", "<string>J\u00F6rg</string>");
    }

    public void testCanDealWithNullCharactersInText() {
        assertBothWays("X\0Y", "<string>X&#x0;Y</string>");
    }

    public void testEscapesXmlUnfriendlyChars() {
        assertBothWays("<", "<string>&lt;</string>");
        assertBothWays(">", "<string>&gt;</string>");
        assertBothWays("<>", "<string>&lt;&gt;</string>");
        assertBothWays("<=", "<string>&lt;=</string>");
        assertBothWays(">=", "<string>&gt;=</string>");
        assertBothWays("&", "<string>&amp;</string>");
        assertBothWays("'", "<string>&apos;</string>");
        assertBothWays("\"", "<string>&quot;</string>");
    }

    public void testDecimalFormatSymbols() {
        final String xml;
        if (!JVM.is14()) {
            xml =
                    "<java.text.DecimalFormatSymbols serialization=\"custom\">\n"
                + "  <java.text.DecimalFormatSymbols>\n"
                + "    <default>\n"
                + "      <decimalSeparator>,</decimalSeparator>\n"
                + "      <digit>#</digit>\n"
                + "      <exponential>E</exponential>\n"
                + "      <groupingSeparator>.</groupingSeparator>\n"
                + "      <minusSign>-</minusSign>\n"
                + "      <monetarySeparator>,</monetarySeparator>\n"
                + "      <patternSeparator>;</patternSeparator>\n"
                + "      <perMill>\u2030</perMill>\n"
                + "      <percent>%</percent>\n"
                + "      <serialVersionOnStream>1</serialVersionOnStream>\n"
                + "      <zeroDigit>0</zeroDigit>\n"
                + "      <NaN>\ufffd</NaN>\n"
                + "      <currencySymbol>DM</currencySymbol>\n"
                + "      <infinity>\u221e</infinity>\n"
                + "      <intlCurrencySymbol>DEM</intlCurrencySymbol>\n"
                + "    </default>\n"
                + "  </java.text.DecimalFormatSymbols>\n"
                + "</java.text.DecimalFormatSymbols>";
        } else {
            xml =
                    "<java.text.DecimalFormatSymbols serialization=\"custom\">\n"
                + "  <java.text.DecimalFormatSymbols>\n"
                + "    <default>\n"
                + "      <decimalSeparator>,</decimalSeparator>\n"
                + "      <digit>#</digit>\n"
                + "      <exponential>E</exponential>\n"
                + "      <groupingSeparator>.</groupingSeparator>\n"
                + "      <minusSign>-</minusSign>\n"
                + "      <monetarySeparator>,</monetarySeparator>\n"
                + "      <patternSeparator>;</patternSeparator>\n"
                + "      <perMill>\u2030</perMill>\n"
                + "      <percent>%</percent>\n"
                + "      <serialVersionOnStream>2</serialVersionOnStream>\n"
                + "      <zeroDigit>0</zeroDigit>\n"
                + "      <NaN>\ufffd</NaN>\n"
                + "      <currencySymbol>\u20ac</currencySymbol>\n"
                + "      <infinity>\u221e</infinity>\n"
                + "      <intlCurrencySymbol>EUR</intlCurrencySymbol>\n"
                + "      <locale>de_DE</locale>\n"
                + "    </default>\n"
                + "  </java.text.DecimalFormatSymbols>\n"
                + "</java.text.DecimalFormatSymbols>";
        }
        final DecimalFormatSymbols format = new DecimalFormatSymbols(Locale.GERMANY);
        assertBothWays(format, xml);
    }
}
