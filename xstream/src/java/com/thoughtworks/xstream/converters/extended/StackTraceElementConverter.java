package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converter for StackTraceElement (the lines of a stack trace) - JDK 1.4+ only.
 *
 * @author Joe Walnes
 */
public class StackTraceElementConverter extends AbstractBasicConverter {

    // Regular expression to parse a line of a stack trace. Returns 4 groups.
    //
    // Example:       com.blah.MyClass.doStuff(MyClass.java:123)
    //                |-------1------| |--2--| |----3-----| |4|
    // (Note group 4 is optional is optional and only present if a colon char exists.)

    private static final Pattern PATTERN = Pattern.compile("^(.+)\\.([^\\(]+)\\(([^:]*)(:(\\d+))?\\)$");

    private final StackTraceElementFactory factory = new StackTraceElementFactory();

    public boolean canConvert(Class type) {
        return StackTraceElement.class.equals(type);
    }

    protected Object fromString(String str) {
        Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            String declaringClass = matcher.group(1);
            String methodName = matcher.group(2);
            String fileName = matcher.group(3);
            if (fileName.equals("Unknown Source")) {
                return factory.unknownSourceElement(declaringClass, methodName);
            } else if (fileName.equals("Native Method")) {
                return factory.nativeMethodElement(declaringClass, methodName);
            } else {
                if (matcher.group(4) != null) {
                    int lineNumber = Integer.parseInt(matcher.group(5));
                    return factory.element(declaringClass, methodName, fileName, lineNumber);
                } else {
                    return factory.element(declaringClass, methodName, fileName);
                }
            }
        } else {
            throw new ConversionException("Could not parse StackTraceElement : " + str);
        }
    }

}
