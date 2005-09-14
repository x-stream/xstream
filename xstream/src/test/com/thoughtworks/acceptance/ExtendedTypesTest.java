package com.thoughtworks.acceptance;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;

public class ExtendedTypesTest extends AbstractAcceptanceTest {

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

    public void testAwtColor() {
        Color color = new Color(0, 10, 20, 30);

        String expected = "" +
                "<awt-color>\n" +
                "  <red>0</red>\n" +
                "  <green>10</green>\n" +
                "  <blue>20</blue>\n" +
                "  <alpha>30</alpha>\n" +
                "</awt-color>";

        assertBothWays(color, expected);
    }

    public void testSqlTimestamp() {
        assertBothWays(new Timestamp(1234),
                "<sql-timestamp>1969-12-31 19:00:01.234</sql-timestamp>");
    }

    public void testSqlTime() {
        assertBothWays(new Time(14, 7, 33),
                "<sql-time>14:07:33</sql-time>");
    }

    public void testSqlDate() {
        assertBothWays(new Date(78, 7, 25),
                "<sql-date>1978-08-25</sql-date>");
    }

    public void testFile() throws IOException {
        // using temp file to avoid os specific or directory layout issues
        File absFile = File.createTempFile("bleh", ".tmp");
        absFile.deleteOnExit();
        assertTrue(absFile.isAbsolute());
        String expectedXml = "<file>" + absFile.getPath() + "</file>";
        assertFilesBothWay(absFile, expectedXml);

        // test a relative file now
        File relFile = new File("bloh.tmp");
        relFile.deleteOnExit();
        assertFalse(relFile.isAbsolute());
        expectedXml = "<file>" + relFile.getPath() + "</file>";
        assertFilesBothWay(relFile, expectedXml);
    }

    private void assertFilesBothWay(File f, String expectedXml) {
        String resultXml = xstream.toXML(f);
        assertEquals(expectedXml, resultXml);
        Object resultObj = xstream.fromXML(resultXml);
        assertEquals(File.class, resultObj.getClass());
        assertEquals(f, resultObj);
        // now comes the part that fails without a specific converter
        // in the case of a relative file, this will work, because we run the comparison test from the same working directory
        assertEquals(f.getAbsolutePath(), ((File)resultObj).getAbsolutePath());
        assertEquals(f.isAbsolute(), ((File)resultObj).isAbsolute()); // needed because File's equals method only compares the path getAttribute, at least in the win32 implementation
    }

    public void testLocale() {
        assertBothWays(new Locale("zh", "", ""), "<locale>zh</locale>");
        assertBothWays(new Locale("zh", "CN", ""), "<locale>zh_CN</locale>");
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
}
