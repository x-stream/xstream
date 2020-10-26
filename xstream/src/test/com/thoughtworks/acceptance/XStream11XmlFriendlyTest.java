/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. July 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.io.xml.XStream11XmlFriendlyReplacer;


public class XStream11XmlFriendlyTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        final XStream xstream = new XStream(DefaultDriver.create(new XStream11XmlFriendlyReplacer())) {
            @Override
            protected boolean useXStream11XmlFriendlyMapper() {
                return true;
            }

        };
        setupSecurity(xstream);
        return xstream;
    }

    public static class WithDollarCharField extends StandardObject {
        private static final long serialVersionUID = 200407L;
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

        final String expected11 = ""
            + "<dollar>\n"
            + "  <_DOLLAR_field>a</_DOLLAR_field>\n"
            + "  <field_DOLLAR_>b</field_DOLLAR_>\n"
            + "  <fi_DOLLAR_eld>c</fi_DOLLAR_eld>\n"
            + "  <fi_DOLLAR__DOLLAR_eld>d</fi_DOLLAR__DOLLAR_eld>\n"
            + "</dollar>";

        final String expected12 = ""
            + "<dollar>\n"
            + "  <_-field>a</_-field>\n"
            + "  <field_->b</field_->\n"
            + "  <fi_-eld>c</fi_-eld>\n"
            + "  <fi_-_-eld>d</fi_-_-eld>\n"
            + "</dollar>";

        assertWithAsymmetricalXml(in, expected11, expected12);
    }

    public static class WithUnderscoreCharField extends StandardObject {
        private static final long serialVersionUID = 200407L;
        String _field;
        String field_;
        String fi_eld;
        String fi__eld;
    }

    public void testSupportsFieldsWithUnderscoreChar() {
        xstream.alias("underscore", WithUnderscoreCharField.class);

        final WithUnderscoreCharField in = new WithUnderscoreCharField();
        in._field = "a";
        in.field_ = "b";
        in.fi_eld = "c";
        in.fi__eld = "d";

        final String expected11 = ""
            + "<underscore>\n"
            + "  <__field>a</__field>\n"
            + "  <field__>b</field__>\n"
            + "  <fi__eld>c</fi__eld>\n"
            + "  <fi____eld>d</fi____eld>\n"
            + "</underscore>";

        assertWithAsymmetricalXml(in, expected11, expected11);
    }

    public void testSupportsAliasWithDashChar() {
        xstream.alias("under-score", WithUnderscoreCharField.class);

        final WithUnderscoreCharField in = new WithUnderscoreCharField();
        in._field = "a";
        in.field_ = "b";
        in.fi_eld = "c";
        in.fi__eld = "d";

        final String expected11 = ""
            + "<under-score>\n"
            + "  <__field>a</__field>\n"
            + "  <field__>b</field__>\n"
            + "  <fi__eld>c</fi__eld>\n"
            + "  <fi____eld>d</fi____eld>\n"
            + "</under-score>";

        assertWithAsymmetricalXml(in, expected11, expected11);
    }

    public static class A_B extends StandardObject {
        private static final long serialVersionUID = 200409L;
        final int x;

        public A_B(final int x) {
            this.x = x;
        }

    }

    public void testSupportsUnderscoreInShortClassName() {
        final String expected11 = ""
            + "<com.thoughtworks.acceptance.XStream11XmlFriendlyTest-A_B>\n"
            + "  <x>3</x>\n"
            + "</com.thoughtworks.acceptance.XStream11XmlFriendlyTest-A_B>";

        final String expected12 = ""
            + "<com.thoughtworks.acceptance.XStream11XmlFriendlyTest_-A__B>\n"
            + "  <x>3</x>\n"
            + "</com.thoughtworks.acceptance.XStream11XmlFriendlyTest_-A__B>";

        assertWithAsymmetricalXml(new A_B(3), expected11, expected12);
    }

}
