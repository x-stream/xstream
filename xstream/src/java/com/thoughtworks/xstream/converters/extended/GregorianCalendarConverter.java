package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.GregorianCalendar;

/**
 * Converts a java.util.GregorianCalendar to XML. Note that although it currently only contains one field, it nests
 * it inside a child element, to allow for other fields to be stored in the future.
 *
 * @author Joe Walnes
 */
public class GregorianCalendarConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.equals(GregorianCalendar.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        GregorianCalendar calendar = (GregorianCalendar) source;
        writer.startNode("time");
        writer.setValue(String.valueOf(calendar.getTimeInMillis()));
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        long timeInMillis = Long.parseLong(reader.getValue());
        reader.moveUp();

        GregorianCalendar result = new GregorianCalendar();
        result.setTimeInMillis(timeInMillis);
        return result;
    }

}
