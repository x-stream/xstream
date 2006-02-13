package com.thoughtworks.xstream.mapper;

/**
 * Excpetion thrown if a mapper cannot locate the appropriate class for an element.
 * <p>
 * Note: The base class will be RuntimeException as soon as the deprecated version of this class in
 * the alias package has been removed.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CannotResolveClassException extends com.thoughtworks.xstream.alias.CannotResolveClassException {
    public CannotResolveClassException(String className) {
        super(className);
    }
}
