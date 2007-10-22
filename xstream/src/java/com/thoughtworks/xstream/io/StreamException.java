package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.XStreamException;

public class StreamException extends XStreamException {
    public StreamException(Throwable e) {
        super(e);
    }

    public StreamException(String message) {
        super(message);
    }
}
