package com.thoughtworks.xstream.alias;

/**
 * @deprecated As of 1.2, use {@link com.thoughtworks.xstream.mapper.CannotResolveClassException} instead
 */
public class CannotResolveClassException extends RuntimeException {
    /**
     * @deprecated As of 1.2
     */
    public CannotResolveClassException(String className) {
        super(className);
    }
}
