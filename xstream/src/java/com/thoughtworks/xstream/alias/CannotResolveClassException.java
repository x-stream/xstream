package com.thoughtworks.xstream.alias;

import com.thoughtworks.xstream.XStreamException;

/**
 * @deprecated As of 1.2, use {@link com.thoughtworks.xstream.mapper.CannotResolveClassException} instead
 */
public class CannotResolveClassException extends XStreamException {
    /**
     * @deprecated As of 1.2
     */
    public CannotResolveClassException(String className) {
        super(className);
    }
}
