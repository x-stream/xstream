package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.core.BaseException;

public class StreamException extends BaseException {
    public StreamException(Throwable e) {
        super(e);
    }

    public StreamException(String message) {
        super(message);
    }
}
