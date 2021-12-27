/*
 * Copyright (C) 2021 XStream Committers.
 * All rights reserved.
 *
 * Created on 21. September 2021 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.XStreamException;


/**
 * General base class for a Security Exception in XStream.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public abstract class AbstractSecurityException extends XStreamException {
    private static final long serialVersionUID = 20210921L;

    /**
     * Constructs a SecurityException.
     * @param message the exception message
     * @since upcoming
     */
    public AbstractSecurityException(final String message) {
        super(message);
    }
}
