package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.TimeZone;

public class ExtendedTypesTest extends AbstractAcceptanceTest {

    public void testAwtColor() {

        xstream.alias("awt-color", Color.class);
        xstream.registerConverter(new ColorConverter());

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

        xstream.alias("sql-timestamp", Timestamp.class);
        xstream.registerConverter(new SqlTimestampConverter());

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        assertBothWays(new Timestamp(1234),
                "<sql-timestamp>1970-01-01 00:00:01.234</sql-timestamp>");                
    }

    public void testFile() throws IOException {
        xstream.alias("file", File.class);
        xstream.registerConverter(new FileConverter());

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
        assertEquals(f.isAbsolute(), ((File)resultObj).isAbsolute()); // needed because File's equals method only compares the path attribute, at least in the win32 implementation
    }
}
