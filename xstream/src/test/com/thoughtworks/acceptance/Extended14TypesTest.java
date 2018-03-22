/*
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. January 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;


public class Extended14TypesTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
    }

    @Override
    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testLocaleWithVariant() {
        assertBothWays(new Locale("zh", "CN", "cc"), "<locale>zh_CN_cc</locale>");
        assertBothWays(new Locale("zh", "", "cc"), "<locale>zh__cc</locale>");
    }

    public void testCurrency() {
        assertBothWays(Currency.getInstance("USD"), "<currency>USD</currency>");
    }

    public void testGregorianCalendar() {
        final Calendar in = Calendar.getInstance();
        in.setTimeZone(TimeZone.getTimeZone("AST"));
        in.setTimeInMillis(44444);
        final String expected = ""
            + "<gregorian-calendar>\n"
            + "  <time>44444</time>\n"
            + "  <timezone>AST</timezone>\n"
            + "</gregorian-calendar>";
        final Calendar out = assertBothWays(in, expected);
        assertEquals(in.getTime(), out.getTime());
        assertEquals(TimeZone.getTimeZone("AST"), out.getTimeZone());
    }

    public void testGregorianCalendarCompat() { // compatibility to 1.1.2 and below
        final Calendar in = Calendar.getInstance();
        in.setTimeInMillis(44444);
        final String oldXML = "" + "<gregorian-calendar>\n" + "  <time>44444</time>\n" + "</gregorian-calendar>";
        final Calendar out = xstream.fromXML(oldXML);
        assertEquals(in.getTime(), out.getTime());
        assertEquals(TimeZone.getTimeZone("EST"), out.getTimeZone());
    }

    public void testRegexPattern() {
        // setup
        final Pattern pattern = Pattern.compile("^[ae]*$", Pattern.MULTILINE | Pattern.UNIX_LINES);
        final String expectedXml = ""
            + "<java.util.regex.Pattern>\n"
            + "  <pattern>^[ae]*$</pattern>\n"
            + "  <flags>9</flags>\n"
            + "</java.util.regex.Pattern>";

        // execute
        final String actualXml = xstream.toXML(pattern);
        final Pattern result = xstream.fromXML(actualXml);

        // verify
        assertEquals(expectedXml, actualXml);
        assertEquals(pattern.pattern(), result.pattern());
        assertEquals(pattern.flags(), result.flags());

        assertFalse("regex should not hava matched", result.matcher("oooo").matches());
        assertTrue("regex should have matched", result.matcher("aeae").matches());
    }

    public void testSubject() {
        xstream.allowTypes(Subject.class);
        xstream.allowTypeHierarchy(Principal.class);

        final Subject subject = new Subject();
        final Principal principal = new X500Principal("c=uk, o=Thoughtworks, ou=XStream");
        subject.getPrincipals().add(principal);
        final String expectedXml = ""
            + "<auth-subject>\n"
            + "  <principals>\n"
            + "    <javax.security.auth.x500.X500Principal serialization=\"custom\">\n"
            + "      <javax.security.auth.x500.X500Principal>\n"
            + "        <byte-array>MDYxEDAOBgNVBAsTB1hTdHJlYW0xFTATBgNVBAoTDFRob3VnaHR3b3JrczELMAkGA1UEBhMCdWs=</byte-array>\n"
            + "      </javax.security.auth.x500.X500Principal>\n"
            + "    </javax.security.auth.x500.X500Principal>\n"
            + "  </principals>\n"
            + "  <readOnly>false</readOnly>\n"
            + "</auth-subject>";

        assertBothWays(subject, expectedXml);
    }

    public void testCharset() {
        final Charset charset = Charset.forName("utf-8");
        final String expectedXml = "<charset>UTF-8</charset>";

        assertBothWays(charset, expectedXml);
    }
}
