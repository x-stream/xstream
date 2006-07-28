package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext extends DataHolder {

    Object convertAnother(Object current, Class type);

    /**
     * @since 1.2
     */
    Object convertAnother(Object current, Class type, Converter converter);

    Object currentObject();

    Class getRequiredType();

    void addCompletionCallback(Runnable work, int priority);
    
}
