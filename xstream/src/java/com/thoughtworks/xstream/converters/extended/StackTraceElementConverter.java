package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;

/**
 * Converts {@link StackTraceElement} for XStream.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
public class StackTraceElementConverter
        implements Converter {
    private static final String CLASSNAME_TAG = "className";
    private static final String METHODNAME_TAG = "methodName";
    private static final String FILENAME_TAG = "fileName";
    private static final String LINENUMBER_TAG = "lineNumber";

    /**
     * {@inheritDoc}
     */
    public boolean canConvert(final Class type) {
        return StackTraceElement.class.equals(type);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(final Object source, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final StackTraceElement element = (StackTraceElement) source;

        marshalClassName(element, writer, context);
        marshalMethodName(element, writer, context);
        marshalFileName(element, writer, context);
        marshalLineNumber(element, writer, context);
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        final StackTraceElement element = new Throwable().getStackTrace()[0];
        final BackupHierarchicalStreamReader backupReader = new BackupHierarchicalStreamReader(
                reader);

        try {
            unmarshalClassName(element, backupReader);
            unmarshalMethodName(element, backupReader);
            unmarshalFileName(element, backupReader);
            unmarshalLineNumber(element, backupReader);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return element;
    }

    public static void setFileName(final StackTraceElement element,
            final String fileName)
            throws NoSuchFieldException, IllegalAccessException {
        setField(element, "fileName", fileName);
    }

    private static void marshalClassName(final StackTraceElement element,
            final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        writer.startNode(CLASSNAME_TAG);
        context.convertAnother(element.getClassName());
        writer.endNode();
    }

    private static void marshalMethodName(final StackTraceElement element,
            final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        writer.startNode(METHODNAME_TAG);
        context.convertAnother(element.getMethodName());
        writer.endNode();
    }

    private static void marshalFileName(final StackTraceElement element,
            final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final String fileName = element.getFileName();

        // Without debugging information, this may be null
        if (null != fileName) {
            writer.startNode(FILENAME_TAG);
            context.convertAnother(fileName);
            writer.endNode();
        }
    }

    private static void marshalLineNumber(final StackTraceElement element,
            final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        writer.startNode(LINENUMBER_TAG);
        context.convertAnother(new Integer(element.getLineNumber()));
        writer.endNode();
    }

    private static void unmarshalClassName(final StackTraceElement element,
            final BackupHierarchicalStreamReader backupReader)
            throws NoSuchFieldException, IllegalAccessException {
        backupReader.moveDown();
        setField(element, "declaringClass", backupReader.getValue());
        backupReader.moveUp();
    }

    private static void unmarshalMethodName(final StackTraceElement element,
            final BackupHierarchicalStreamReader backupReader)
            throws NoSuchFieldException, IllegalAccessException {
        backupReader.moveDown();
        setField(element, "methodName", backupReader.getValue());
        backupReader.moveUp();
    }

    private static void unmarshalFileName(final StackTraceElement element,
            final BackupHierarchicalStreamReader backupReader)
            throws NoSuchFieldException, IllegalAccessException {
        backupReader.moveDown();

        if (FILENAME_TAG.equals(backupReader.getNodeName())) {
            setFileName(element, backupReader.getValue());
            backupReader.moveUp();

        } else {
            setFileName(element, null);
            backupReader.backUp();
        }
    }

    private static void unmarshalLineNumber(final StackTraceElement element,
            final BackupHierarchicalStreamReader backupReader)
            throws NoSuchFieldException, IllegalAccessException {
        backupReader.moveDown();
        setField(element, "lineNumber", new Integer(backupReader.getValue()));
        backupReader.moveUp();
    }

    private static void setField(final StackTraceElement element,
            final String fieldName, final Object value)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = StackTraceElement.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(element, value);
    }
}
