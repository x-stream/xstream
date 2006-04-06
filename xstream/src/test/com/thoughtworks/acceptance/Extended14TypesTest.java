package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Extended14TypesTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();

        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
    }

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
        Calendar in = Calendar.getInstance();
        in.setTimeZone(TimeZone.getTimeZone("AST"));
        in.setTimeInMillis(44444);
        String expected = "" +
                "<gregorian-calendar>\n" +
                "  <time>44444</time>\n" +
                "  <timezone>AST</timezone>\n" +
                "</gregorian-calendar>";
        Calendar out = (Calendar) assertBothWays(in, expected);
        assertEquals(in.getTime(), out.getTime());
        assertEquals(TimeZone.getTimeZone("AST"), out.getTimeZone());
    }

    public void testGregorianCalendarCompat() { // compatibility to 1.1.2 and below
        Calendar in = Calendar.getInstance();
        in.setTimeInMillis(44444);
        String oldXML = "" +
                "<gregorian-calendar>\n" +
                "  <time>44444</time>\n" +
                "</gregorian-calendar>";
        Calendar out = (Calendar) xstream.fromXML(oldXML);
        assertEquals(in.getTime(), out.getTime());
        assertEquals(TimeZone.getTimeZone("EST"), out.getTimeZone());
    }

    public void testRegexPattern() {
        // setup
        Pattern pattern = Pattern.compile("^[ae]*$", Pattern.MULTILINE | Pattern.UNIX_LINES);
        String expectedXml = "" +
                "<java.util.regex.Pattern>\n" +
                "  <pattern>^[ae]*$</pattern>\n" +
                "  <flags>9</flags>\n" +
                "</java.util.regex.Pattern>";

        // execute
        String actualXml = xstream.toXML(pattern);
        Pattern result = (Pattern) xstream.fromXML(actualXml);

        // verify
        assertEquals(expectedXml, actualXml);
        assertEquals(pattern.pattern(), result.pattern());
        assertEquals(pattern.flags(), result.flags());

        assertFalse("regex should not hava matched", result.matcher("oooo").matches());
        assertTrue("regex should have matched", result.matcher("aeae").matches());
    }
    
    public void testSubject() {
        Subject subject = new Subject();
        Principal principal = new X500Principal("c=uk, o=Thoughtworks, ou=XStream");
        subject.getPrincipals().add(principal);
        String expectedXml = "" +
                "<auth-subject>\n" +
                "  <principals>\n" +
                "    <javax.security.auth.x500.X500Principal serialization=\"custom\">\n" +
                "      <javax.security.auth.x500.X500Principal>\n" +
                "        <byte-array>MDYxEDAOBgNVBAsTB1hTdHJlYW0xFTATBgNVBAoTDFRob3VnaHR3b3JrczELMAkGA1UEBhMCdWs=\n</byte-array>\n" +
                "      </javax.security.auth.x500.X500Principal>\n" +
                "    </javax.security.auth.x500.X500Principal>\n" +
                "  </principals>\n" +
                "  <readOnly>false</readOnly>\n" +
                "</auth-subject>";

        assertBothWays(subject, expectedXml);
    }
    
    public void testCharset() {
        Charset charset = Charset.forName("utf-8");
        String expectedXml = "<charset>UTF-8</charset>";

        assertBothWays(charset, expectedXml);
    }
}
