/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. October 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream;

/**
 * Exception thrown configuring an XStream instance.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class InitializationException extends XStream.InitializationException {
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message) {
        super(message);
    }
}
