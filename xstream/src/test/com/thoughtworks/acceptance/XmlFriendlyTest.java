/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2017, 2018, 2019, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 18. April 2006 by Mauro Talevi
 */
package com.thoughtworks.acceptance;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.core.JVM;


public class XmlFriendlyTest extends AbstractAcceptanceTest {

    public static class WithDollarCharField extends StandardObject {
        private static final long serialVersionUID = 200604L;
        String $field;
        String field$;
        String fi$eld;
        String fi$$eld;
    }

    public void testSupportsFieldsWithDollarChar() {
        xstream.alias("dollar", WithDollarCharField.class);

        final WithDollarCharField in = new WithDollarCharField();
        in.$field = "a";
        in.field$ = "b";
        in.fi$eld = "c";
        in.fi$$eld = "d";

        final String expected = ""
            + "<dollar>\n"
            + "  <_-field>a</_-field>\n"
            + "  <field_->b</field_->\n"
            + "  <fi_-eld>c</fi_-eld>\n"
            + "  <fi_-_-eld>d</fi_-_-eld>\n"
            + "</dollar>";
        assertBothWays(in, expected);
    }

    public static class WithUnderscoreCharField extends StandardObject {
        private static final long serialVersionUID = 200604L;
        String _field;
        String field_;
        String fi_eld;
    }

    public void testSupportsFieldsWithUnderscoreChar() {
        xstream.alias("underscore", WithUnderscoreCharField.class);

        final WithUnderscoreCharField in = new WithUnderscoreCharField();
        in._field = "a";
        in.field_ = "b";
        in.fi_eld = "c";

        final String expected = ""
            + "<underscore>\n"
            + "  <__field>a</__field>\n"
            + "  <field__>b</field__>\n"
            + "  <fi__eld>c</fi__eld>\n"
            + "</underscore>";
        assertBothWays(in, expected);
    }

    public static class WithDoubleUnderscoreCharField extends StandardObject {
        private static final long serialVersionUID = 200605L;
        String __field;
        String field__;
        String fi__eld;
    }

    public void testSupportsFieldsWithDoubleUnderscoreChar() {
        xstream.alias("underscore", WithDoubleUnderscoreCharField.class);

        final WithDoubleUnderscoreCharField in = new WithDoubleUnderscoreCharField();
        in.__field = "a";
        in.field__ = "b";
        in.fi__eld = "c";

        final String expected = ""
            + "<underscore>\n"
            + "  <____field>a</____field>\n"
            + "  <field____>b</field____>\n"
            + "  <fi____eld>c</fi____eld>\n"
            + "</underscore>";
        assertBothWays(in, expected);
    }

    public static class WithDollarAndUnderscoreCharField extends StandardObject {
        private static final long serialVersionUID = 200709L;
        String $_$field;
        String field$_$;
        String fi_$_eld;
        String fi_$$_eld;
        String fi$__$eld;
    }

    public void testSupportsFieldsWithDollarAndUnderScoreChar() {
        xstream.alias("dollar", WithDollarAndUnderscoreCharField.class);

        final WithDollarAndUnderscoreCharField in = new WithDollarAndUnderscoreCharField();
        in.$_$field = "a";
        in.field$_$ = "b";
        in.fi_$_eld = "c";
        in.fi_$$_eld = "d";
        in.fi$__$eld = "e";

        final String expected = ""
            + "<dollar>\n"
            + "  <_-___-field>a</_-___-field>\n"
            + "  <field_-___->b</field_-___->\n"
            + "  <fi___-__eld>c</fi___-__eld>\n"
            + "  <fi___-_-__eld>d</fi___-_-__eld>\n"
            + "  <fi_-_____-eld>e</fi_-_____-eld>\n"
            + "</dollar>";
        assertBothWays(in, expected);
    }

    public static class WithUnusualCharacters extends StandardObject {
        private static final long serialVersionUID = 201107L;
        String µ_;
        String _µ;
        String ¢¥€£äöüßᚥ;
    }

    public void testSupportsFieldsWithUnusualChars() {
        xstream.alias("unusual", WithUnusualCharacters.class);

        final WithUnusualCharacters in = new WithUnusualCharacters();
        in.µ_ = "a";
        in._µ = "b";
        in.¢¥€£äöüßᚥ = "c";

        final String expected = ""
            + "<unusual>\n"
            + "  <_.00b5__>a</_.00b5__>\n"
            + "  <___.00b5>b</___.00b5>\n"
            + "  <_.00a2_.00a5_.20ac_.00a3äöüß_.16a5>c</_.00a2_.00a5_.20ac_.00a3äöüß_.16a5>\n"
            + "</unusual>";
        assertBothWays(in, expected);
    }

    public static class __ {
        public static class A_B extends StandardObject {
            private static final long serialVersionUID = 200709L;
            final int x;

            public A_B(final int x) {
                this.x = x;
            }

        }
    }

    public void testSupportsUnderscoreInShortClassName() {
        assertBothWays(new __.A_B(3), ""
            + "<com.thoughtworks.acceptance.XmlFriendlyTest_-_____-A__B>\n"
            + "  <x>3</x>\n"
            + "</com.thoughtworks.acceptance.XmlFriendlyTest_-_____-A__B>");
    }

    public void testSlashRSlashSlashSlashN() {
        final String before = "\r\\\n";
        final String xml = xstream.toXML(before);
        assertEquals(before, xstream.fromXML(xml));
    }

    public void testCanDealWithUtfText() {
        assertBothWays("J\u00F6rg", "<string>J\u00F6rg</string>");
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
        if (!JVM.isVersion(13)) {
            xml = ""
                + "<java.text.DecimalFormatSymbols serialization=\"custom\">\n"
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
                + "      <serialVersionOnStream>3</serialVersionOnStream>\n"
                + "      <zeroDigit>0</zeroDigit>\n"
                + "      <NaN>NaN</NaN>\n"
                + "      <currencySymbol>\u20ac</currencySymbol>\n"
                + "      <exponentialSeparator>E</exponentialSeparator>\n"
                + "      <infinity>\u221e</infinity>\n"
                + "      <intlCurrencySymbol>EUR</intlCurrencySymbol>\n"
                + "      <locale>de_DE</locale>\n"
                + "    </default>\n"
                + "  </java.text.DecimalFormatSymbols>\n"
                + "</java.text.DecimalFormatSymbols>";
        } else if (!JVM.isVersion(15)) {
            xml = ""
                + "<java.text.DecimalFormatSymbols serialization=\"custom\">\n"
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
                + "      <serialVersionOnStream>4</serialVersionOnStream>\n"
                + "      <zeroDigit>0</zeroDigit>\n"
                + "      <NaN>NaN</NaN>\n"
                + "      <currencySymbol>\u20ac</currencySymbol>\n"
                + "      <exponentialSeparator>E</exponentialSeparator>\n"
                + "      <infinity>\u221e</infinity>\n"
                + "      <intlCurrencySymbol>EUR</intlCurrencySymbol>\n"
                + "      <locale>de_DE</locale>\n"
                + "      <minusSignText>-</minusSignText>\n"
                + "      <perMillText>\u2030</perMillText>\n"
                + "      <percentText>%</percentText>\n"
                + "    </default>\n"
                + "  </java.text.DecimalFormatSymbols>\n"
                + "</java.text.DecimalFormatSymbols>";
        } else {
            xml = ""
                + "<java.text.DecimalFormatSymbols serialization=\"custom\">\n"
                + "  <java.text.DecimalFormatSymbols>\n"
                + "    <default>\n"
                + "      <decimalSeparator>,</decimalSeparator>\n"
                + "      <digit>#</digit>\n"
                + "      <exponential>E</exponential>\n"
                + "      <groupingSeparator>.</groupingSeparator>\n"
                + "      <hashCode>0</hashCode>\n"
                + "      <minusSign>-</minusSign>\n"
                + "      <monetaryGroupingSeparator>.</monetaryGroupingSeparator>\n"
                + "      <monetarySeparator>,</monetarySeparator>\n"
                + "      <patternSeparator>;</patternSeparator>\n"
                + "      <perMill>\u2030</perMill>\n"
                + "      <percent>%</percent>\n"
                + "      <serialVersionOnStream>5</serialVersionOnStream>\n"
                + "      <zeroDigit>0</zeroDigit>\n"
                + "      <NaN>NaN</NaN>\n"
                + "      <currencySymbol>\u20ac</currencySymbol>\n"
                + "      <exponentialSeparator>E</exponentialSeparator>\n"
                + "      <infinity>\u221e</infinity>\n"
                + "      <intlCurrencySymbol>EUR</intlCurrencySymbol>\n"
                + "      <locale>de_DE</locale>\n"
                + "      <minusSignText>-</minusSignText>\n"
                + "      <perMillText>\u2030</perMillText>\n"
                + "      <percentText>%</percentText>\n"
                + "    </default>\n"
                + "  </java.text.DecimalFormatSymbols>\n"
                + "</java.text.DecimalFormatSymbols>";
        }
        final DecimalFormatSymbols format = new DecimalFormatSymbols(Locale.GERMANY);
        format.setNaN("NaN");
        assertEquals("EUR", format.getInternationalCurrencySymbol());
        assertBothWays(format, xml);
    }

}
