/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2012, 2014, 2016, 2017, 2018, 2020, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. October 2003 by Joe Walnes, merged with Extended14TypesTest and Extended17TypesTest
 */
package com.thoughtworks.acceptance;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

import org.jdom.Element;

import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;


public class ExtendedTypesTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.allowTypes(Element.class, Color.class);

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

    public void testAwtColor() {
        final Color color = new Color(0, 10, 20, 30);

        final String expected = ""
            + "<awt-color>\n"
            + "  <red>0</red>\n"
            + "  <green>10</green>\n"
            + "  <blue>20</blue>\n"
            + "  <alpha>30</alpha>\n"
            + "</awt-color>";

        assertBothWays(color, expected);
    }

    public void testSqlTimestamp() {
        assertBothWays(new Timestamp(1000), "<sql-timestamp>1970-01-01 00:00:01</sql-timestamp>");
    }

    public void testSqlTimestampWithFraction() {
        final Timestamp timestamp = new Timestamp(1234);
        timestamp.setNanos(78900);
        assertBothWays(timestamp, "<sql-timestamp>1970-01-01 00:00:01.0000789</sql-timestamp>");
    }

    public void testSqlTimestampWithLocalTimeZone() {
        xstream.registerConverter(new SqlTimestampConverter(TimeZone.getDefault()));
        final Timestamp timestamp = new Timestamp(1234);
        timestamp.setNanos(78900);
        assertBothWays(timestamp, "<sql-timestamp>" + timestamp.toString() + "</sql-timestamp>");
    }

    @SuppressWarnings("deprecation")
    public void testSqlTime() {
        assertBothWays(new Time(14, 7, 33), "<sql-time>14:07:33</sql-time>");
    }

    @SuppressWarnings("deprecation")
    public void testSqlDate() {
        assertBothWays(new Date(78, 7, 25), "<sql-date>1978-08-25</sql-date>");
    }

    public void testFile() throws IOException {
        // using temp file to avoid OS specific or directory layout issues
        final File absFile = File.createTempFile("bleh", ".tmp");
        absFile.deleteOnExit();
        assertTrue(absFile.isAbsolute());
        String expectedXml = "<file>" + absFile.getPath() + "</file>";
        assertFilesBothWay(absFile, expectedXml);

        // test a relative file now
        final File relFile = new File("bloh.tmp");
        relFile.deleteOnExit();
        assertFalse(relFile.isAbsolute());
        expectedXml = "<file>" + relFile.getPath() + "</file>";
        assertFilesBothWay(relFile, expectedXml);
    }

    private void assertFilesBothWay(final File f, final String expectedXml) {
        final String resultXml = xstream.toXML(f);
        assertEquals(expectedXml, resultXml);
        final File resultObj = xstream.fromXML(resultXml);
        assertEquals(f, resultObj);
        // now comes the part that fails without a specific converter
        // in the case of a relative file, this will work, because we run the comparison test from the same working
        // directory
        assertEquals(f.getAbsolutePath(), resultObj.getAbsolutePath());
        // needed because File's equals method only
        // compares the path getAttribute, at least in the
        // win32 implementation
        assertEquals(f.isAbsolute(), resultObj.isAbsolute());
    }

    public void testLocale() {
        assertBothWays(new Locale("zh", "", ""), "<locale>zh</locale>");
        assertBothWays(new Locale("zh", "CN", ""), "<locale>zh_CN</locale>");
    }

    public void testCanHandleJDomElement() {
        final Element element = new Element("JUnit");

        final String expected = ""
            + "<org.jdom.Element serialization=\"custom\">\n"
            + "  <org.jdom.Element>\n"
            + "    <default>\n"
            + "      <attributes>\n"
            + "        <size>0</size>\n"
            + "        <parent reference=\"../../../..\"/>\n"
            + "      </attributes>\n"
            + "      <content>\n"
            + "        <size>0</size>\n"
            + "        <parent class=\"org.jdom.Element\" reference=\"../../../..\"/>\n"
            + "      </content>\n"
            + "      <name>JUnit</name>\n"
            + "    </default>\n"
            + "    <string></string>\n"
            + "    <string></string>\n"
            + "    <byte>0</byte>\n"
            + "  </org.jdom.Element>\n"
            + "</org.jdom.Element>";

        assertBothWays(element, expected);
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

    public void testPathOfDefaultFileSystem() {
        assertBothWays(Paths.get("../a/relative/path"), "<path>../a/relative/path</path>");
        assertBothWays(Paths.get("/an/absolute/path"), "<path>/an/absolute/path</path>");

        final Path absolutePath = Paths.get("target").toAbsolutePath();
        String absolutePathName = absolutePath.toString();
        if (File.separatorChar != '/') {
            absolutePathName = absolutePathName.replace(File.separatorChar, '/');
        }
        final Path path = Paths.get(absolutePath.toUri());
        assertBothWays(path, "<path>" + absolutePathName + "</path>");
    }

    public void testPathWithSpecialCharacters() {
        assertBothWays(Paths.get("with space"), "<path>with space</path>");
        assertBothWays(Paths.get("with+plus"), "<path>with+plus</path>");
        assertBothWays(Paths.get("with&ampersand"), "<path>with&amp;ampersand</path>");
        assertBothWays(Paths.get("with%20encoding"), "<path>with%20encoding</path>");
    }

    public void testPathOfNonDefaultFileSystem() throws IOException {
        final Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        final URI uri = URI
            .create("jar:" + Paths.get("target/lib/proxytoys-0.2.1.jar").toAbsolutePath().toUri().toString());

        try (final FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            final String entry = "/com/thoughtworks/proxy/kit/SimpleReference.class";
            final Path path = zipfs.getPath(entry);
            assertBothWays(path, "<path>" + uri.toString() + "!" + entry + "</path>");
        }
    }

    public void testPathIsImmutable() {
        final Path[] array = new Path[2];
        array[0] = array[1] = Paths.get("same");
        assertBothWays(array, "" //
            + "<path-array>\n" //
            + "  <path>same</path>\n" //
            + "  <path>same</path>\n" //
            + "</path-array>");
    }

    public void testEmptyOptional() {
        final Optional<Object> optional = Optional.empty();
        assertBothWays(optional, "<optional/>");
    }

    public void testOptional() {
        final Optional<String> optional = Optional.of("test");
        assertBothWays(optional, ("" //
            + "<optional>\n" //
            + "  <value class='string'>test</value>\n" //
            + "</optional>").replace('\'', '"'));
    }

    public void testOptionalWithAlias() {
        final Optional<String> optional = Optional.of("test");
        xstream.aliasField("junit", Optional.class, "value");
        assertBothWays(optional, ("" //
            + "<optional>\n" //
            + "  <junit class='string'>test</junit>\n" //
            + "</optional>").replace('\'', '"'));
    }

    public void testOptionalIsRerenceable() {
        @SuppressWarnings("unchecked")
        final Optional<Object>[] array = new Optional[3];
        array[0] = array[2] = Optional.of("test");
        array[1] = Optional.empty();
        assertBothWays(array, ("" //
            + "<optional-array>\n" //
            + "  <optional>\n" //
            + "    <value class='string'>test</value>\n" //
            + "  </optional>\n" //
            + "  <optional/>\n" //
            + "  <optional reference='../optional'/>\n" //
            + "</optional-array>").replace('\'', '"'));
    }

    public void testOptionalWithOldFormat() {
        assertEquals(Optional.of("test"), xstream.fromXML("" //
            + "<java.util.Optional>\n" //
            + "  <value class='string'>test</value>\n" //
            + "</java.util.Optional>"));
    }

    public void testEmptyOptionalDouble() {
        final OptionalDouble optional = OptionalDouble.empty();
        assertBothWays(optional, "<optional-double></optional-double>");
    }

    public void testEmptyOptionalDoubleWithOldFormat() {
        assertEquals(OptionalDouble.empty(), xstream.fromXML("" //
            + "<java.util.OptionalDouble>\n" //
            + "  <isPresent>false</isPresent>\n" //
            + "  <value>NaN</value>\n" //
            + "</java.util.OptionalDouble>"));
    }

    public void testOptionalDouble() {
        final OptionalDouble optional = OptionalDouble.of(1.8);
        assertBothWays(optional, "<optional-double>1.8</optional-double>");
    }

    public void testOptionalDoubleIsImmutable() {
        final OptionalDouble[] array = new OptionalDouble[3];
        array[0] = array[2] = OptionalDouble.of(1.8);
        array[1] = OptionalDouble.empty();
        assertBothWays(array, "" //
            + "<optional-double-array>\n" //
            + "  <optional-double>1.8</optional-double>\n" //
            + "  <optional-double></optional-double>\n" //
            + "  <optional-double>1.8</optional-double>\n" //
            + "</optional-double-array>");
    }

    public void testOptionalDoubleWithOldFormat() {
        assertEquals(OptionalDouble.of(1.8), xstream.fromXML("" //
            + "<java.util.OptionalDouble>\n" //
            + "  <isPresent>true</isPresent>\n" //
            + "  <value>1.8</value>\n" //
            + "</java.util.OptionalDouble>"));
    }

    public void testEmptyOptionalInt() {
        final OptionalInt optional = OptionalInt.empty();
        assertBothWays(optional, "<optional-int></optional-int>");
    }

    public void testEmptyOptionalIntWithOldFormat() {
        assertEquals(OptionalInt.empty(), xstream.fromXML("" //
            + "<java.util.OptionalInt>\n" //
            + "  <isPresent>false</isPresent>\n" //
            + "  <value>0</value>\n" //
            + "</java.util.OptionalInt>"));
    }

    public void testOptionalInt() {
        final OptionalInt optional = OptionalInt.of(42);
        assertBothWays(optional, "<optional-int>42</optional-int>");
    }

    public void testOptionalIntIsImmutable() {
        final OptionalInt[] array = new OptionalInt[3];
        array[0] = array[2] = OptionalInt.of(42);
        array[1] = OptionalInt.empty();
        assertBothWays(array, "" //
            + "<optional-int-array>\n" //
            + "  <optional-int>42</optional-int>\n" //
            + "  <optional-int></optional-int>\n" //
            + "  <optional-int>42</optional-int>\n" //
            + "</optional-int-array>");
    }

    public void testOptionalIntWithOldFormat() {
        assertEquals(OptionalInt.of(42), xstream.fromXML("" //
            + "<java.util.OptionalInt>\n" //
            + "  <isPresent>true</isPresent>\n" //
            + "  <value>42</value>\n" //
            + "</java.util.OptionalInt>"));
    }

    public void testEmptyOptionalLong() {
        final OptionalLong optional = OptionalLong.empty();
        assertBothWays(optional, "<optional-long></optional-long>");
    }

    public void testEmptyOptionalLongWithOldFormat() {
        assertEquals(OptionalLong.empty(), xstream.fromXML("" //
            + "<java.util.OptionalLong>\n" //
            + "  <isPresent>false</isPresent>\n" //
            + "  <value>0</value>\n" //
            + "</java.util.OptionalLong>"));
    }

    public void testOptionalLong() {
        final OptionalLong optional = OptionalLong.of(2344556678888786L);
        assertBothWays(optional, "<optional-long>2344556678888786</optional-long>");
    }

    public void testOptionalLongIsImmutable() {
        final OptionalLong[] array = new OptionalLong[3];
        array[0] = array[2] = OptionalLong.of(2344556678888786L);
        array[1] = OptionalLong.empty();
        assertBothWays(array, "" //
            + "<optional-long-array>\n" //
            + "  <optional-long>2344556678888786</optional-long>\n" //
            + "  <optional-long></optional-long>\n" //
            + "  <optional-long>2344556678888786</optional-long>\n" //
            + "</optional-long-array>");
    }

    public void testOptionalLongWithOldFormat() {
        assertEquals(OptionalLong.of(2344556678888786L), xstream.fromXML("" //
            + "<java.util.OptionalLong>\n" //
            + "  <isPresent>true</isPresent>\n" //
            + "  <value>2344556678888786</value>\n" //
            + "</java.util.OptionalLong>"));
    }

}
