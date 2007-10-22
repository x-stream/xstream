/*
 * Copyright (C) 2007 XStream committers.
 * Created on 22.10.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream;

/**
 * Exception thrown configuring an XStream instance.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class InitializationException extends XStream.InitializationException {
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message) {
        super(message);
    }
}