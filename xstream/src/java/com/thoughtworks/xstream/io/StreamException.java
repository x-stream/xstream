package com.thoughtworks.xstream.io;

public class StreamException extends RuntimeException {
    public StreamException(Throwable e) {
        super(e.getMessage());
    }
}
