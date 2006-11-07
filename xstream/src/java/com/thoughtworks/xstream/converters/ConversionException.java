package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.core.BaseException;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Thrown by {@link Converter} implementations when they cannot convert an object
 * to/from textual data.
 *
 * When this exception is thrown it can be passed around to things that accept an
 * {@link ErrorWriter}, allowing them to add diagnostics to the stack trace.
 *
 * @author Joe Walnes
 *
 * @see ErrorWriter
 */
public class ConversionException extends BaseException implements ErrorWriter {

    private Map stuff = new OrderRetainingMap();

    /**
     * Plays nice with JDK1.3 and JDK1.4
     */
    protected Throwable cause;

    public ConversionException(String msg, Throwable cause) {
        super(msg);
        if (msg != null) {
            add("message", msg);
        }
        if (cause != null) {
            add("cause-exception", cause.getClass().getName());
            add("cause-message", cause instanceof ConversionException ? ((ConversionException)cause).getShortMessage() :  cause.getMessage());
            this.cause = cause;
        }
    }

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public String get(String errorKey) {
        return (String) stuff.get(errorKey);
    }

    public void add(String name, String information) {
        stuff.put(name, information);
    }

    public Iterator keys() {
        return stuff.keySet().iterator();
    }

    public String getMessage() {
        StringBuffer result = new StringBuffer();
        if (super.getMessage() != null) {
            result.append(super.getMessage());
        }
        result.append("\n---- Debugging information ----");
        for (Iterator iterator = keys(); iterator.hasNext();) {
            String k = (String) iterator.next();
            String v = get(k);
            result.append('\n').append(k);
            int padding = 20 - k.length();
            for (int i = 0; i < padding; i++) {
                result.append(' ');
            }
            result.append(": ").append(v);
        }
        result.append("\n-------------------------------");
        return result.toString();
    }

    public Throwable getCause() {
        return cause;
    }

    public String getShortMessage() {
        return super.getMessage();
    }
}
