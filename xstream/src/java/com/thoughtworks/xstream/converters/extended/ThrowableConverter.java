package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter for Throwable (and Exception) that retains stack trace, for JDK1.4 only.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
public class ThrowableConverter
        implements Converter {
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String MESSAGE_TAG = "message";
    private static final String CAUSE_TAG = "cause";
    private static final String STACKTRACE_TAG = "stackTrace";

    /**
     * {@inheritDoc}
     */
    public boolean canConvert(final Class type) {
        return Throwable.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(final Object source, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final Throwable thrown = (Throwable) source;

        marshallClass(thrown, writer);
        marshallMessage(thrown, writer, context);
        marshallCause(thrown, writer, context);
        marshallStackTrace(thrown, writer, context);
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        try {
            final BackupHierarchicalStreamReader backupReader
                    = new BackupHierarchicalStreamReader(reader);

            return createThrowable(unmarshallClass(backupReader),
                    unmarshallMessage(backupReader),
                    unmarshallCause(backupReader, context),
                    unmarshallStackTrace(backupReader, context));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void marshallClass(final Throwable thrown,
            final HierarchicalStreamWriter writer) {
        writer.addAttribute(CLASS_ATTRIBUTE, thrown.getClass().getName());
    }

    private static void marshallMessage(final Throwable thrown,
            final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final String message = thrown.getMessage();

        if (null != message) {
            writer.startNode(MESSAGE_TAG);
            context.convertAnother(message);
            writer.endNode();
        }
    }

    private static void marshallCause(final Throwable thrown,
            final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final Throwable cause = thrown.getCause();

        if (null != cause) {
            writer.startNode(CAUSE_TAG);
            context.convertAnother(cause);
            writer.endNode();
        }
    }

    private static void marshallStackTrace(final Throwable thrown, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        // The tags are redundant if I knew the end boundary
        writer.startNode(STACKTRACE_TAG);
        context.convertAnother(thrown.getStackTrace());
        writer.endNode();
    }

    private static Class unmarshallClass(
            final BackupHierarchicalStreamReader reader) {
        try {
            return Class.forName(reader.getAttribute(CLASS_ATTRIBUTE));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String unmarshallMessage(
            final BackupHierarchicalStreamReader reader) {
        final String message;

        reader.moveDown();

        if (MESSAGE_TAG.equals(reader.getNodeName())) {
            message = reader.getValue();
            reader.moveUp();

        } else {
            message = null;
            reader.backUp();
        }

        return message;
    }

    private static Throwable unmarshallCause(
            final BackupHierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        final Throwable cause;

        reader.moveDown();

        if (CAUSE_TAG.equals(reader.getNodeName())) {
            cause = (Throwable) context.convertAnother(context.currentObject(),
                    Throwable.class);
            reader.moveUp();

        } else {
            cause = null;
            reader.backUp();
        }

        return cause;
    }

    private static StackTraceElement[] unmarshallStackTrace(
            final BackupHierarchicalStreamReader reader,
            final UnmarshallingContext context) {
        final List elements = new ArrayList();

        reader.moveDown();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            elements.add(context.convertAnother(context.currentObject(),
                    StackTraceElement.class));
            reader.moveUp();
        }

        reader.moveUp();

        return (StackTraceElement[]) elements.toArray(
                new StackTraceElement[elements.size()]);
    }

    private static Throwable createThrowable(final Class clazz,
            final String message, final Throwable cause,
            final StackTraceElement[] stackTrace)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        final Throwable thrown = (Throwable) clazz.getConstructor(new Class[]{
            String.class, Throwable.class
        })
                .newInstance(new Object[]{message, cause});

        thrown.setStackTrace(stackTrace);

        return thrown;
    }
}
