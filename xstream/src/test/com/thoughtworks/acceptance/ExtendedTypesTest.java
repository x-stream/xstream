package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;

import java.awt.*;
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


}
