package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Converter for Throwable (and Exception) that retains stack trace, for JDK1.4 only.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 */
public class ThrowableConverter implements Converter {

    private final static Method initCause;
    private final static Method getCause;
    static {
        Method init = null;
        Method get = null;
        try {
            init = Throwable.class.getDeclaredMethod("initCause", new Class[]{Throwable.class});
            get = Throwable.class.getDeclaredMethod("getCause", null);
        } catch (NoSuchMethodException e) {
            // JDK 1.3
        }
        initCause = init;
        getCause = get;
    }
    
    private Converter defaultConverter;

    public ThrowableConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter;
    }

    public boolean canConvert(final Class type) {
        return Throwable.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Throwable throwable = (Throwable) source;
        if (getCause != null) {
            try {
                Throwable cause = (Throwable)getCause.invoke(throwable, null);
                if (cause == null) {
                    initCause.invoke(throwable, new Object[]{ null });
                }
            } catch (IllegalArgumentException e) {
                // ignore, initCause failed, cause was already set
            } catch (IllegalAccessException e) {
                // ignore
            } catch (InvocationTargetException e) {
                // ignore
            }
        }
        throwable.getStackTrace(); // Force stackTrace field to be lazy loaded by special JVM native witchcraft (outside our control).
        defaultConverter.marshal(throwable, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return defaultConverter.unmarshal(reader, context);
    }
}
